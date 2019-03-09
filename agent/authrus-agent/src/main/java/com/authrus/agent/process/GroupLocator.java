package com.authrus.agent.process;

import lombok.AllArgsConstructor;

import com.authrus.agent.group.GroupResolver;
import com.authrus.common.command.Environment;
import com.authrus.rest.registry.RegistryNode;

@AllArgsConstructor
public class GroupLocator implements ProcessLocator {

   private final GroupResolver resolver;
   private final Environment environment;
   
   public ProcessManager locate(String name) {
      OperatingSystem system = OperatingSystem.resolveSystem();
      RegistryNode node = resolver.findLocal(name);
      
     if(node != null) { 
        Process process = system.create(node, environment);        
        return new ProcessManager(process);
     }
     return null;
   }
   
}
