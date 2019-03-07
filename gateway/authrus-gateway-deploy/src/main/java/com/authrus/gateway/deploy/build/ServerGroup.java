package com.authrus.gateway.deploy.build;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import com.authrus.http.proxy.balancer.LoadBalancer;
import com.authrus.http.proxy.balancer.MasterSlaveSelector;
import com.authrus.http.proxy.balancer.SelectorLoadBalancer;
import com.authrus.http.proxy.balancer.ServerActivitySelector;
import com.authrus.http.proxy.balancer.ServerConnector;
import com.authrus.http.proxy.balancer.ServerSelector;
import com.authrus.http.proxy.balancer.identity.IdentityExtractor;
import com.authrus.http.proxy.route.Router;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
@ToString 
public class ServerGroup {
   
   private final HealthCheck check;
   private final List<String> servers;
   private final String selector;
   private final int keepAlive;
   private final int timeout;
   private final int buffer; 
   
   @JsonCreator
   public ServerGroup(
         @JsonProperty(value = "health-check", required = true) HealthCheck check,
         @JsonProperty(value = "servers", required = true) List<String> servers,
         @JsonProperty(value = "selector", defaultValue = "master-slave") String selector,
         @JsonProperty(value = "keep-alive", defaultValue = "20000") int keepAlive,
         @JsonProperty(value = "timeout", defaultValue = "10000") int timeout,
         @JsonProperty(value = "buffer", defaultValue = "8192") int buffer)
   {
      this.keepAlive = Math.max(keepAlive, 20000);
      this.timeout = Math.max(timeout, 10000);
      this.buffer = Math.max(buffer, 8192);
      this.servers = servers;
      this.selector = selector;
      this.check = check;
   }     
   
   public int getKeepAlive() {
      return (keepAlive <= 0 ? 20000 : keepAlive) + 10000;
   }
   
   public LoadBalancer createBalancer(ProxyContext context, Router router, List<String> patterns) {
      ServerSelector selector = createSelector(context, patterns);
      IdentityExtractor extractor = context.getExtractor();
      
      return new SelectorLoadBalancer(selector, extractor, router);
   }

   public ServerSelector createSelector(ProxyContext context, List<String> patterns) {
      List<ServerConnector> connectors = new ArrayList<ServerConnector>();
      
      if(check == null) {
         throw new IllegalStateException("Health check details required");
      }
      check.validate(servers);
      
      String path = check.getPath();
      Map<String, String> headers = check.getHeaders();
      long frequency = check.getFrequency();
      
      for(String server : servers) {
         if(server == null) {
            throw new IllegalStateException("Illegal null address in " + servers);
         }
         ServerAddress address = new ServerAddress(server, path);
         ServerDefinition uri = new ServerDefinition(headers, address, keepAlive + 10000, timeout, frequency, buffer);
         ServerConnector connector = uri.createConnector(context, patterns);
         connectors.add(connector);
      }
      SelectorType type = SelectorType.resolve(selector);
      
      if(type == SelectorType.DYNAMIC) {
         return new ServerActivitySelector(connectors, (identity) -> null);
      }
      return new MasterSlaveSelector(connectors);
   }
   
}