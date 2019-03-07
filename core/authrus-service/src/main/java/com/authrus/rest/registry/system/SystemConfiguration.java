package com.authrus.rest.registry.system;

import java.io.File;

import lombok.SneakyThrows;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = SystemConfiguration.class)
public class SystemConfiguration {

   private final File path;

   public SystemConfiguration(@Value("${server.directory}") File path) {
      this.path = path;
   }
   
   @Bean
   @SneakyThrows
   public TailService tailService() {
      String directory = path.getCanonicalPath();
      return new TailService(directory);
   }

}
