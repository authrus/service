package com.authrus.gateway.deploy;

import java.io.Reader;

import com.authrus.gateway.deploy.parse.SourceReader;
import com.authrus.gateway.deploy.trace.TraceConfiguration;
import com.authrus.http.proxy.trace.TraceAgent;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.PropertyResolver;

@Configuration
@Import(TraceConfiguration.class)
@ComponentScan(basePackageClasses = DeploymentConfiguration.class)
public class DeploymentConfiguration {

   private final DeploymentEngine engine;
   private final SourceReader reader;
   private final String path;
   
   @SneakyThrows
   public DeploymentConfiguration(
       PropertyResolver resolver,
		 @Qualifier("proxy") TraceAgent proxy,
       @Qualifier("client") TraceAgent client,
       @Value("${gateway.plan}") String path,
       @Value("${gateway.debug:false}") boolean debug)
   {
      this.engine = new DeploymentEngine(proxy, client);
      this.reader = new SourceReader(resolver);
      this.path = path;
   }
   
   @Bean
   @SneakyThrows
   public Deployment proxyPlan() {
      Reader source = reader.readDeployment(path);
      return engine.deploy(source);
   }
}

