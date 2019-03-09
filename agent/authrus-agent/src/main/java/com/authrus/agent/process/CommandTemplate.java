package com.authrus.agent.process;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public enum CommandTemplate {
   WORKSPACE {
      @Override
      public String start(String name, String directory, String environment) {
         Path path = Paths.get(directory, "target");
         File root = path.toFile();
         
         if(root.exists()) {
            String prefix = root.getAbsolutePath();
            Path archive = Paths.get(prefix, name + ".jar");
            File file = archive.toFile();         
            
            if(file.exists()) {
               return String.format("java -cp %s/src/main/resources%s%s/target/%s.jar -Dspring.profiles.active=%s org.springframework.boot.loader.JarLauncher", directory, File.pathSeparator, directory, name, environment);
            }
         }
         return null;
      }

      @Override
      public String stop(String name, String directory, String environment) {
         return null;
      }      
   },
   LOCAL {
      @Override
      public String start(String name, String directory, String environment) {
         String[] suffixes = {".", "target", "lib", "libs", "bin"};
         
         for(String suffix : suffixes) {
            Path path = Paths.get(directory, suffix);
            File root = path.toFile();
            
            if(root.exists()) {
               String prefix = root.getAbsolutePath();
               Path archive = Paths.get(prefix, name + ".jar");
               File file = archive.toFile();         
               
               if(file.exists()) {
                  return String.format("java -jar -Dspring.profiles.active=%s %s/%s.jar", environment, prefix, name);
               }
            }
         }
         return null;
      }

      @Override
      public String stop(String name, String directory, String environment) {
         return null;
      }      
   },
   SYSTEM_CTL{
      @Override
      public String start(String name, String directory, String environment) {
         return String.format("/usr/bin/sudo -u root /sbin/service %s start", name);
      }

      @Override
      public String stop(String name, String directory, String environment) {
         return String.format("/usr/bin/sudo -u root /sbin/service %s stop", name);
      }      
   },
   BASH{
      @Override
      public String start(String name, String directory, String environment) {
         return String.format("%s/bin/start.sh", directory);
      }

      @Override
      public String stop(String name, String directory, String environment) {
         return String.format("%s/bin/stop.sh", directory);
      }       
   },
   DOS{
      @Override
      public String start(String name, String directory, String environment) {
         return String.format("%s/bin/start.bat", directory);
      }

      @Override
      public String stop(String name, String directory, String environment) {
         return String.format("%s/bin/stop.bat", directory);
      }       
   };
   
   public abstract String start(String name, String directory, String environment);
   public abstract String stop(String name, String directory, String environment);
}
