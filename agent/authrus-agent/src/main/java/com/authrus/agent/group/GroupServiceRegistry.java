package com.authrus.agent.group;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

import com.authrus.gateway.deploy.build.HealthCheck;
import com.authrus.gateway.deploy.build.Route;
import com.authrus.gateway.deploy.build.RouteRule;
import com.authrus.gateway.deploy.build.RouteTable;
import com.authrus.gateway.deploy.build.ServerGroup;
import com.authrus.rest.registry.RegistryNode;
import com.google.common.collect.Maps;

@Slf4j
public class GroupServiceRegistry {

   private final Map<String, RegistryNode> nodes;
   private final String local;
   private final int port;

   public GroupServiceRegistry(String local, int port) throws IOException {
      this.nodes = new ConcurrentHashMap<>();
      this.local = local;
      this.port = port;
   }

   public void register(RouteTable table, RegistryNode node) throws Exception {
      String name = node.getName();
      String host = node.getHost();
      String normal = host.toLowerCase();
      String target = String.format("http://.*/%s/%s/.*", normal, name);
      RegistryNode current = nodes.get(target);
      
      if (current == null) {
         String manage = node.getManage();
         String address = node.getAddress();
         
         if(!StringUtils.isBlank(manage)) {                    
            registerJMX(table, node);
         }
         if(!StringUtils.isBlank(address)) {  
            registerServer(table, node);
         }
      }
   }
   
   private void registerServer(RouteTable table, RegistryNode node) throws Exception {
      String name = node.getName();
      String host = node.getHost();
      String health = node.getHealth();
      String address = node.getAddress();
      String normal = host.toLowerCase();
      String match = String.format("/%s/%s/(.*)", normal, name);
      String target = String.format("http://.*/%s/%s/.*", normal, name);
      HealthCheck check = HealthCheck.builder()
            .path(health)
            .frequency(5000L)
            .build();
      
      ServerGroup group = ServerGroup.builder()
            .check(check)
            .timeout(10000)
            .keepAlive(60000)
            .servers(Collections.singletonList(address))
            .build();
      
      RouteRule rule = RouteRule.builder()
            .pattern(match)
            .template("/%{1}")
            .build();

      Route route = Route.builder()
            .group(group)
            .rules(Collections.singletonList(rule))
            .patterns(Collections.singletonList(target))
            .build();

      log.info("Registered '{}' at '{}'", name, target);
      table.registerRoute(route);
      nodes.put(target, node);
   }
   
   private void registerJMX(RouteTable table, RegistryNode node) throws Exception {
      String name = node.getName();
      String host = node.getHost();
      String health = node.getHealth();
      String address = node.getManage();
      String normal = host.toLowerCase();
      String prefix = String.format("/%s/%s/jmx/", normal, name);
      String match = String.format("%s(.*)", prefix);
      String target = String.format("http://.*%s.*", prefix);
      Map<String, String> map = Maps.newLinkedHashMap();
      
      map.put("HREF=\"/", "HREF=\"/manage" + prefix);
      map.put("HREF='/", "HREF='/manage" + prefix);
      map.put("href=\"/", "href=\"/manage" + prefix);
      map.put("href=';/", "href='/manage" + prefix);
      map.put("ACTION=\"/", "ACTION=\"/manage" + prefix);
      map.put("action=';/", "action='/manage" + prefix);
      map.put("ACTION=/", "ACTION=/manage" + prefix);
      map.put("action=/", "action=/manage" + prefix);
      
      HealthCheck check = HealthCheck.builder()
            .path(health)
            .frequency(5000L)
            .build();
      
      ServerGroup group = ServerGroup.builder()
            .check(check)
            .timeout(10000)
            .keepAlive(60000)
            .servers(Collections.singletonList(address))
            .build();
      
      RouteRule rule = RouteRule.builder()
            .pattern(match)
            .template("/%{1}")
            .build();

      Route route = Route.builder()
            .group(group)
            .filters(map)
            .rules(Collections.singletonList(rule))
            .patterns(Collections.singletonList(target))
            .build();

      log.info("Registered '{}' at '{}'", name, target);
      table.registerRoute(route);
      nodes.put(target, node);
   }
}
