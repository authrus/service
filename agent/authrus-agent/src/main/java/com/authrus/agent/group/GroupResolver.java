package com.authrus.agent.group;

import java.util.List;

import com.authrus.rest.registry.RegistryNode;

public interface GroupResolver {
   List<RegistryNode> findAll();
   List<RegistryNode> findLocals();
   RegistryNode findLocal(String name);
}
