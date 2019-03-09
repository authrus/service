package com.authrus.agent.group;

import java.util.List;

import lombok.AllArgsConstructor;

import com.authrus.rest.registry.RegistryNode;

@AllArgsConstructor
public class StaticResolver implements GroupResolver {

   private final StaticRegistry registry;
   private final String host;
   
   @Override
   public List<RegistryNode> findAll() {
      return registry.getNodes();
   }

   @Override
   public List<RegistryNode> findLocals() {
      return registry.getNodes(host);
   }

   @Override
   public RegistryNode findLocal(String name) {
      return registry.getNode(host, name);
   }

}

