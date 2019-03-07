package com.authrus.gateway.resource.status;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import org.springframework.stereotype.Component;

import com.authrus.gateway.deploy.Deployment;
import com.authrus.gateway.deploy.build.EndPoint;
import com.google.common.collect.Lists;
import com.authrus.http.proxy.balancer.connect.ConnectionPoolConnector;
import com.authrus.http.proxy.balancer.status.StatusMonitor;
import com.authrus.http.proxy.balancer.status.StatusReport;
import com.authrus.http.proxy.core.State;

@Component
@AllArgsConstructor
public class StatusService {

   private final Deployment plan;

   @SneakyThrows
   public List<StatusResult> status(Predicate<State> filter) {
      List<StatusResult> results = Lists.newArrayList();
      Set<EndPoint> servers = plan.getServers();
      
      for(EndPoint entry : servers) {
         ConnectionPoolConnector connector = entry.getConnector();
         State state = connector.getState();
         
         if(filter.test(state)) {
            StatusResult.ConnectionPool pool = StatusResult.ConnectionPool.builder()
                  .state(state)
                  .active(connector.getActiveCount())
                  .failures(connector.getFailureCount())
                  .idle(connector.getIdleCount())
                  .alive(connector.isAlive())
                  .age(connector.getElapsedTime())
                  .build();
            
            StatusResult result = StatusResult.builder()
                  .pool(pool)
                  .address(String.valueOf(entry.getAddress()))
                  .patterns(entry.getPatterns())
                  .build();
            
            results.add(result);
         }
      }
      return results;
   }
   
   @SneakyThrows
   public List<StatusResult> statusComplete(Predicate<State> filter) {
      List<StatusResult> results = Lists.newArrayList();
      Set<EndPoint> servers = plan.getServers();
      
      for(EndPoint entry : servers) {
         ConnectionPoolConnector connector = entry.getConnector();
         State state = connector.getState();
         
         if(filter.test(state)) {
            StatusResult.ConnectionPool pool = StatusResult.ConnectionPool.builder()
                  .state(state)
                  .active(connector.getActiveCount())
                  .failures(connector.getFailureCount())
                  .idle(connector.getIdleCount())
                  .alive(connector.isAlive())
                  .age(connector.getElapsedTime())
                  .build();
            
            StatusMonitor monitor = entry.getMonitor();
            StatusReport report = monitor.checkStatus();                  
            StatusResult result = StatusResult.builder()
                  .pool(pool)
                  .state(report.getState())
                  .local(String.valueOf(report.getLocal()))
                  .remote(String.valueOf(report.getRemote()))
                  .address(String.valueOf(entry.getAddress()))
                  .patterns(entry.getPatterns())
                  .build();
            
            results.add(result);
         }
      }
      return results;
   }
}
