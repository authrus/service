package com.authrus.agent.group;

import java.io.IOException;
import java.util.List;

import lombok.SneakyThrows;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.authrus.gateway.deploy.DeploymentEngine;
import com.authrus.gateway.deploy.build.TraceLogger;
import com.authrus.http.proxy.trace.TraceAgent;
import com.authrus.http.proxy.trace.TraceCollector;
import com.authrus.rest.host.HostNameResolver;
import com.authrus.rest.registry.RegistryConfiguration;
import com.authrus.rest.registry.RegistryNode;
import com.authrus.rest.registry.RegistryService;

@Configuration
@EnableConfigurationProperties(StaticRegistry.class)
@Import({StatusConfiguration.class, RegistryConfiguration.class})
public class GroupConfiguration {
	
   private final HostNameResolver resolver;
   private final DeploymentEngine engine;
   private final RegistryService service;
   private final StaticRegistry registry;
   private final TraceCollector collector;
   private final TraceAgent agent;
   private final String directory;
	private final String log;
	private final int port;
	
	public GroupConfiguration(
	      StaticRegistry registry, 
	      RegistryService service,
			@Value("${group.directory}") String directory, 
			@Value("${group.log:log/access.log}") String log,
         @Value("${group.host:}") String host, 
			@Value("${group.port}")int port) throws IOException 
	{
	   this.resolver = new HostNameResolver(host);
	   this.collector = new TraceLogger(false);
	   this.agent = new TraceAgent(collector);       
	   this.engine = new DeploymentEngine(agent, agent);
	   this.registry = registry;
		this.directory = directory;
	   this.service = service;
		this.log = log;
		this.port = port;
	}
	
	@Bean
	@SneakyThrows
	public GroupServer groupServer() {
      String host = resolver.resolveHost();        
		return new GroupServer(engine, directory, host, log, port);
	}
	
	@Bean
   @SneakyThrows
   public GroupResolver groupResolver() {  
      String host = resolver.resolveHost();  
      List<RegistryNode> nodes = registry.getNodes();
      
      if(nodes.isEmpty()) {
         return new RegistryResolver(service);
      }
      return new StaticResolver(registry, host);
   }
	
	@Bean
	@SneakyThrows
	public GroupServiceLocator groupLocator(GroupServer server, GroupResolver resolver) {		
		return new GroupServiceLocator(resolver, server);
	}
	
	
}
