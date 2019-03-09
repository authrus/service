package com.authrus.agent.process;

import java.util.Collections;

import lombok.SneakyThrows;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.authrus.agent.group.GroupConfiguration;
import com.authrus.agent.group.GroupResolver;
import com.authrus.common.command.Environment;
import com.authrus.common.command.MapEnvironment;
import com.authrus.rest.registry.RegistryConfiguration;

@Configuration
@Import({RegistryConfiguration.class, GroupConfiguration.class})
@ComponentScan(basePackageClasses = ProcessConfiguration.class)
public class ProcessConfiguration {
   
   private final GroupResolver resolver;
   private final Environment environment;
   
   public ProcessConfiguration(GroupResolver resolver) {
      this.environment = new MapEnvironment(Collections.EMPTY_MAP);
      this.resolver = resolver;
   }

   @Bean
   @SneakyThrows
   public ProcessLocator processLocator() {
      return new GroupLocator(resolver, environment);
   }
}
