package com.authrus.gateway.resource.rules;

import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import org.springframework.stereotype.Component;

import com.authrus.gateway.deploy.Deployment;
import com.authrus.gateway.deploy.build.FirewallRule;
import com.google.common.collect.Lists;

@Component
@AllArgsConstructor
public class AccessRuleService {

   private final Deployment plan;

   @SneakyThrows
   public List<AccessRule> rules() {
      List<AccessRule> results = Lists.newArrayList();
      Set<FirewallRule> rules = plan.getRules();
      
      for(FirewallRule rule : rules) {
         String host = rule.getHost();
         String address = rule.getAddress();
         String type = rule.getType();
         int port = rule.getPort();
         AccessRule result = AccessRule.builder()
               .type(type)
               .host(host)
               .address(address)
               .port(port)
               .build();
            
         results.add(result);
      }      
      return results;
   }
}
