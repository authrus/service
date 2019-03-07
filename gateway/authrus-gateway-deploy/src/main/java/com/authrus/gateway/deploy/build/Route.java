package com.authrus.gateway.deploy.build;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;

import com.authrus.http.proxy.balancer.LoadBalancer;
import com.authrus.http.proxy.route.MessageAppender;
import com.authrus.http.proxy.route.RegularExpressionRouter;
import com.authrus.http.proxy.route.Router;
import com.authrus.http.proxy.route.append.ClientAddressAppender;
import com.authrus.http.proxy.route.append.HeaderAppender;
import com.authrus.http.proxy.route.append.ServerAddressAppender;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
public class Route {
   
   private final Map<String, String> filters;
   private final Map<String, String> headers;
   private final List<String> patterns;
   private final List<RouteRule> rules;
   private final CacheControl control;
   private final ServerGroup group;
   
   @JsonCreator
   public Route(
         @JsonProperty(value = "response-filters", required = false) Map<String, String> filters,            
         @JsonProperty(value = "route-headers", required = false) Map<String, String> headers,        
         @JsonProperty(value = "route-patterns", required = true) List<String> patterns,
         @JsonProperty(value = "route-rules", required = false) List<RouteRule> rules,
         @JsonProperty(value = "cache-control", required = false) CacheControl control,
         @JsonProperty(value = "server-group", required = true) ServerGroup group)
   {
      this.control = control;
      this.filters = filters;
      this.headers = headers;
      this.patterns = patterns;
      this.rules = rules;
      this.group = group;
   }

   @SneakyThrows
   public LoadBalancer createBalancer(ProxyContext context, String name){
      Map<String, String> map = new LinkedHashMap<>();
      
      if(patterns == null || patterns.isEmpty()) {
         throw new IllegalStateException("Route must have a at least one match pattern");
      }
      if(rules != null) {
         for(RouteRule rule : rules) {
            String pattern = rule.getPattern();
            String template = rule.getTemplate();
            
            if(pattern == null) {
               throw new IllegalStateException("Rule pattern not defined");
            }
            if(template == null) {
               throw new IllegalStateException("Rule pattern not defined");
            }
            map.put(pattern, template);
         }
      }
      Router router = createRouter(name, map);
      
      if(group == null) {
         throw new IllegalStateException("Server group not definied");
      }
      return group.createBalancer(context, router, patterns);
   }

   private Router createRouter(String name, Map<String, String> map) throws Exception {
      int keepAlive = group.getKeepAlive();
      List<MessageAppender> appenders = new ArrayList<>();
      Router router = new RegularExpressionRouter(map, appenders);
      ServerAddressAppender serverAddress = new ServerAddressAppender(name);
      ClientAddressAppender clientAddress = new ClientAddressAppender();
      MessageAppender keepAliveHeader = new HeaderAppender("Keep-Alive", "timeout=" + Math.abs(keepAlive / 1000));
      
      if(headers != null && !headers.isEmpty()) {
         for(Map.Entry<String, String> entry : headers.entrySet()){
            String key = entry.getKey();
            String value = entry.getValue();
            MessageAppender appender = new HeaderAppender(key, value);
            appenders.add(appender);
         }
      }
      appenders.add(keepAliveHeader);
      appenders.add(serverAddress);
      appenders.add(clientAddress);
      
      return router;
   }
   
}