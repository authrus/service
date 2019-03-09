package com.authrus.agent.process;

import static com.authrus.agent.process.CommandTemplate.BASH;
import static com.authrus.agent.process.CommandTemplate.SYSTEM_CTL;
import static com.authrus.agent.process.CommandTemplate.WORKSPACE;

import com.authrus.common.command.Environment;
import com.authrus.rest.registry.RegistryNode;

public enum OperatingSystem {
   LINUX {
      @Override
      public Process create(RegistryNode node, Environment environment) {
         String directory = node.getDirectory();
         return new UnixProcess(SYSTEM_CTL, node, environment, directory);
      }
   },
   WINDOWS {
      @Override
      public Process create(RegistryNode node, Environment environment) {
         String directory = node.getDirectory();
         return new WindowsProcess(WORKSPACE, node, environment, directory);
      }
   },
   MAC {
      @Override
      public Process create(RegistryNode node, Environment environment) {
         String directory = node.getDirectory();
         return new UnixProcess(BASH, node, environment, directory);
      }
   };
   
   
   public abstract Process create(RegistryNode node, Environment environment);
   
   public static OperatingSystem resolveSystem() {
      OperatingSystem[] values = OperatingSystem.values();
      String system = System.getProperty("os.name");
      String token = system.toLowerCase();
      
      for(OperatingSystem os : values) {
         if(token.startsWith(os.name().toLowerCase())) {
            return os;
         }
      }
      return LINUX;
   }
}
