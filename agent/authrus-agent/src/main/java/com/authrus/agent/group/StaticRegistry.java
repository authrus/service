package com.authrus.agent.group;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.authrus.rest.registry.RegistryNode;

@Data
@ConfigurationProperties(prefix = "group")
public class StaticRegistry {

   private List<StaticNode> definitions;
   private String environment;
   
   public StaticRegistry() {
      this.definitions = new ArrayList<>();
   }
   
   public List<RegistryNode> getNodes() {
      return definitions.stream()
            .filter(Objects::nonNull)
            .map(node -> node.create(environment))
            .collect(Collectors.toList());
   }
   
   public List<RegistryNode> getNodes(String host) {
      return definitions.stream()
            .filter(Objects::nonNull)
            .filter(node -> {
               return node.host.equals(host);
            })
            .map(node -> node.create(environment))
            .collect(Collectors.toList());
   }
   
   public RegistryNode getNode(String host, String name) {
      return definitions.stream()
            .filter(Objects::nonNull)            
            .filter(node -> {
               return node.name.equals(name) && node.host.equals(host);
            })
            .map(node -> node.create(environment))
            .findFirst()
            .orElse(null);
   }
   
   
   @Data
   public static class StaticNode {
      
      private String description;
      private String directory;
      private String address;
      private String manage;      
      private String host;
      private String name;
      private String health;
      
      public RegistryNode create(String environment) {
         return RegistryNode.builder()
               .environment(environment)
               .description(description)
               .directory(directory)               
               .manage(manage)               
               .address(address)
               .name(name)
               .host(host)
               .health(health)
               .build();
      }
   }
}
