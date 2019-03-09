package com.authrus.agent.group;

import lombok.SneakyThrows;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.authrus.gateway.deploy.Deployment;
import com.authrus.gateway.resource.health.HealthService;
import com.authrus.gateway.resource.status.StatusService;

@Configuration
public class StatusConfiguration {

   @Bean
   @SneakyThrows
   public Deployment deployment(GroupServer server) {
      return server.start();
   }
   
   @Bean
   @SneakyThrows
   public StatusService statusService(Deployment deployment) {
      return new StatusService(deployment);
   }
   
   @Bean
   @SneakyThrows
   public HealthService healthService(Deployment deployment) {
      return new HealthService(deployment);
   }
}
