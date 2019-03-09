package com.authrus.agent.group;

import java.util.List;

import lombok.AllArgsConstructor;

import com.authrus.rest.registry.RegistryNode;
import com.authrus.rest.registry.RegistryService;

@AllArgsConstructor
public class RegistryResolver implements GroupResolver {
   
   private final RegistryService service;
   
   @Override
   public List<RegistryNode> findAll() {
      return service.getNodes();
   }
   
   @Override
   public List<RegistryNode> findLocals() {
      return service.getNodes();
   }

   @Override
   public RegistryNode findLocal(String name) {
      return service.getNode(name);
   }

}
