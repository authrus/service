package com.authrus.store.tuple;

import com.authrus.store.Schema;
import com.authrus.store.SchemaCompiler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import org.springframework.context.annotation.Bean;

import com.authrus.tuple.frame.FrameDistributor;
import com.authrus.tuple.frame.FrameTracer;
import com.authrus.tuple.frame.SessionRegistry;
import com.authrus.tuple.frame.SessionRegistryListener;
import com.authrus.tuple.grid.Catalog;
import com.authrus.tuple.grid.ChangeSubscriber;
import com.authrus.tuple.grid.GridServer;
import com.authrus.tuple.grid.Structure;
import com.authrus.tuple.grid.StructureBuilder;
import com.authrus.tuple.subscribe.SubscriptionDistributor;
import com.authrus.tuple.subscribe.SubscriptionListener;

@AllArgsConstructor
class CatalogBuilder {

   private final List<SubscriptionListener> listeners;
   private final int port;
   
   @Bean
   @SneakyThrows
   public Catalog create(SessionRegistry registry, Set<Class<?>> types) {
      SchemaCompiler compiler = new SchemaCompiler();
      Executor executor = new ScheduledThreadPoolExecutor(2);
      ChangeSubscriber subscriber = new ChangeSubscriber(executor);
      SubscriptionListener listener = new SubscriptionDistributor(listeners);
      List<FrameTracer> tracers = new ArrayList<FrameTracer>();
      FrameDistributor distributor = new FrameDistributor(tracers);
      FrameTracer tracer = new SessionRegistryListener(registry);
      GridServer server = new GridServer(subscriber, listener, distributor, port);
      Map<String, Structure> structures = new LinkedHashMap<String, Structure>();
      StructureBuilder builder = new StructureBuilder(structures, subscriber);
     
      for(Class<?> type : types) {
         Schema<?> schema = compiler.compile(type);
         Structure structure = schema.getStructure();
         String entity = schema.getEntity();
         
         structures.put(entity, structure);
      }
      tracers.add(tracer);
      server.start();
      
      return builder.createCatalog(); 
   }
}
