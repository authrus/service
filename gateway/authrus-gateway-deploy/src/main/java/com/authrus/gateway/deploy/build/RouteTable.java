package com.authrus.gateway.deploy.build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.authrus.http.proxy.core.intercept.HeaderInterceptor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import org.simpleframework.http.Protocol;
import org.springframework.util.CollectionUtils;

import com.authrus.http.proxy.balancer.LoadBalancer;
import com.authrus.http.proxy.balancer.LoadBalancerHost;
import com.authrus.http.proxy.cache.CacheHost;
import com.authrus.http.proxy.cache.CompositeFilter;
import com.authrus.http.proxy.cache.ContentTypeFilter;
import com.authrus.http.proxy.cache.MemoryLowFilter;
import com.authrus.http.proxy.core.ResponseReporter;
import com.authrus.http.proxy.core.exchange.TransactionController;
import com.authrus.http.proxy.core.intercept.CombinationInterceptor;
import com.authrus.http.proxy.core.intercept.CompressionInterceptor;
import com.authrus.http.proxy.core.intercept.FilterInterceptor;
import com.authrus.http.proxy.core.intercept.ResponseInterceptor;
import com.authrus.http.proxy.filter.ReplaceTextFilter;
import com.authrus.http.proxy.host.Host;
import com.authrus.http.proxy.host.HostResolver;
import com.authrus.http.proxy.host.RegularExpressionResolver;
import com.authrus.http.proxy.log.FileLog;
import com.authrus.http.proxy.security.AccessManager;

@Data
@Builder
@AllArgsConstructor
public class RouteTable {
   
   private static final String[] COMPRESS_TYPES = {
      "text/css",
      "text/plain",
      "text/html",
      "text/js",
      "text/json", 
      "application/javascript"
   };

   private final RegularExpressionResolver registry;
   private final TransactionController controller;
   private final ResponseInterceptor interceptor;
   private final ResponseReporter reporter;
   private final AccessManager manager;
   private final HostResolver resolver;
   private final ProxyContext context;
   private final FileLog log;
   private final String name;
   
   public Host registerRoute(Route route) { 
      List<String> patterns = route.getPatterns();
      Host host = createHost(route);
      
      for(String match : patterns) {
         registry.registerHost(host, match);
      }
      return host;
   }
   
   private Host createHost(Route route) {
      CacheControl control = route.getControl();
      ResponseInterceptor interceptor = createInterceptor(route, control);
      LoadBalancer balancer = route.createBalancer(context, name);
      Host host = new LoadBalancerHost(balancer, 
            manager, 
            controller, 
            reporter, 
            interceptor);
      
      return createCacheHost(host, control);
   }
   
   private Host createCacheHost(Host host, CacheControl control) {
      if(control != null && !control.isDisabled()) {
         long duration = control.getDuration();
         List<String> types = control.getTypes();
         Map<String, Long> durations = new LinkedHashMap<>();
         ContentTypeFilter typeFilter = new ContentTypeFilter(durations);
         MemoryLowFilter memFilter = new MemoryLowFilter(0.2f);
         CompositeFilter comFilter = new CompositeFilter(Arrays.asList(memFilter, typeFilter));
         CacheHost wrapper = new CacheHost(comFilter, host, log);
         
         for(String type : types) {
            durations.put(type, duration);
         }
         wrapper.setCacheEnabled(true);
         host = wrapper;
      }
      return host;
   }

   private ResponseInterceptor createInterceptor(Route route, CacheControl control) {
      List<ResponseInterceptor> interceptors = new ArrayList<>();
      Map<String, Long> types = new LinkedHashMap<>();
      CompressionInterceptor compressor = new CompressionInterceptor(types);
      CombinationInterceptor combination = new CombinationInterceptor(interceptors);
      long threshold = 8192;
      
      for(String type : COMPRESS_TYPES) {
         types.put(type, threshold);
      }
      interceptors.add(interceptor);
      interceptors.add(compressor);

      if(control != null && control.isDisabled()) {
         interceptors.add(new HeaderInterceptor(Protocol.CACHE_CONTROL, Protocol.NO_CACHE));
         interceptors.add(new HeaderInterceptor(Protocol.PRAGMA, Protocol.NO_CACHE));
      }
      Map<String, String> filters = route.getFilters(); 
      
      if(!CollectionUtils.isEmpty(filters)) {
         ReplaceTextFilter replace = new ReplaceTextFilter(filters);
         FilterInterceptor wrapper = new FilterInterceptor(Collections.singletonMap("/", replace), "text");

        interceptors.add(wrapper);
      }
      return combination;
   }
}