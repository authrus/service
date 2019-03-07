package com.authrus.gateway.deploy.trace;

import javax.management.ObjectName;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.authrus.gateway.deploy.parse.SourceReader;
import com.authrus.http.proxy.trace.TraceAgent;
import com.authrus.http.proxy.trace.TraceCollector;
import com.authrus.http.proxy.trace.TraceDistributor;
import com.authrus.http.proxy.trace.search.SearchRecorder;
import com.authrus.http.proxy.trace.search.Searcher;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertyResolver;
import org.springframework.jmx.export.MBeanExporter;

@Slf4j
@Configuration
public class TraceConfiguration {

   private final MBeanExporter exporter;
   private final ObjectMapper mapper;
   private final SourceReader reader;
   private final String path;

   @SneakyThrows
   public TraceConfiguration(
           MBeanExporter exporter,
           PropertyResolver resolver,
           @Value("${gateway.trace.file}") String path,
           @Value("${gateway.trace.enable:false}") boolean enable)
   {
      this.reader = new SourceReader(resolver);
      this.mapper = new ObjectMapper();
      this.exporter = exporter;
      this.path = path;
   }

   @Bean
   @SneakyThrows
   public TraceContext traceContext() {
      Reader source = reader.readDeployment(path);
      TraceSchema schema = mapper.readValue(source, TraceSchema.class);

      return schema.getContext();
   }

   @Bean
   @Qualifier("client")
   public TraceDistributor clientDistributor(TraceContext context) {
      List<TraceCollector> collectors = Lists.newArrayList();
      Map<String, SearchRecorder> recorders = context.getClient();
      Set<String> names = recorders.keySet();

      for (String name : names) {
         SearchRecorder recorder = recorders.get(name);
         collectors.add(recorder);
      }
      return new TraceDistributor(collectors);
   }

   @Bean
   @Qualifier("proxy")
   public TraceDistributor proxyDistributor(TraceContext context) {
      List<TraceCollector> collectors = Lists.newArrayList();
      Map<String, SearchRecorder> recorders = context.getProxy();
      Set<String> names = recorders.keySet();

      for (String name : names) {
         SearchRecorder recorder = recorders.get(name);
         collectors.add(recorder);
      }
      return new TraceDistributor(collectors);
   }

   @Qualifier("client")
   @Bean(initMethod = "start", destroyMethod = "stop")
   public TraceAgent clientAgent(TraceContext context, @Qualifier("client") TraceDistributor distributor)  {
      Map<String, SearchRecorder> recorders = context.getClient();
      Set<String> names = recorders.keySet();
      Package parent = TraceConfiguration.class.getPackage();
      String module = parent.getName();
      
      for(String name : names) {
         SearchRecorder recorder = recorders.get(name);

         try {
            ObjectName identifier = new ObjectName(module + ":type=Searcher,name=" + name);
            Searcher searcher = new Searcher(recorder);

            exporter.registerManagedResource(searcher, identifier);
         } catch(Exception e) {
            log.info("Could not register searcher for {}", name, e);
         }
      }
      return new TraceAgent(distributor);
   }

   @Qualifier("proxy")
   @Bean(initMethod = "start", destroyMethod = "stop")
   public TraceAgent proxyAgent(TraceContext context, @Qualifier("proxy") TraceDistributor distributor) {
      Map<String, SearchRecorder> recorders = context.getProxy();
      Set<String> names = recorders.keySet();
      Package parent = TraceConfiguration.class.getPackage();
      String module = parent.getName();
      
      for(String name : names) {
         SearchRecorder recorder = recorders.get(name);

         try {
            ObjectName identifier = new ObjectName(module + ":type=Searcher,name=" + name);
            Searcher searcher = new Searcher(recorder);

            exporter.registerManagedResource(searcher, identifier);
         } catch(Exception e) {
            log.info("Could not register searcher for {}", name, e);
         }
      }
      return new TraceAgent(distributor);
   }
}
