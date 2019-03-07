package com.authrus.gateway.resource.health;

import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import org.springframework.stereotype.Component;

import com.authrus.gateway.deploy.Deployment;
import com.authrus.gateway.deploy.build.EndPoint;
import com.google.common.collect.Maps;
import com.authrus.http.proxy.balancer.status.StatusMonitor;

@Component
@AllArgsConstructor
public class HealthService {
   
   private final Deployment plan;
   
   @SneakyThrows
   public Map<String, String> health() {
      Map<String, String> results = Maps.newHashMap();
      Set<EndPoint> servers = plan.getServers();
      
      for(EndPoint entry : servers) {
         StatusMonitor monitor = entry.getMonitor();
         String state = monitor.getLastStatus();
         String address = String.valueOf(entry.getAddress());
         
         results.put(address, state);
      }
      return results;
   }
}
