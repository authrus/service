package com.authrus.store.tuple;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import lombok.SneakyThrows;

import org.simpleframework.transport.reactor.ExecutorReactor;
import org.simpleframework.transport.reactor.Reactor;

import com.authrus.transport.DirectSocketBuilder;
import com.authrus.transport.DirectTransportBuilder;
import com.authrus.transport.SocketBuilder;
import com.authrus.transport.TransportBuilder;
import com.authrus.tuple.frame.SessionRegistry;
import com.authrus.tuple.frame.SessionRegistryListener;
import com.authrus.tuple.grid.Catalog;
import com.authrus.tuple.grid.GridSubscriber;
import com.authrus.tuple.query.Origin;

class TupleStoreSubscriber {
   
   private final File path;
   private final String remote;
   private final String name;
   private final int port;

   public TupleStoreSubscriber(File path, String name, String remote, int port) {
      this.remote = remote;
      this.name = name;
      this.port = port;
      this.path = path;
   }

   @SneakyThrows
   public TupleStoreBuilder subscribe(SessionRegistry registry, Catalog catalog, String host) {
      Origin origin = new Origin(name, host, port);
      Executor executor = new ScheduledThreadPoolExecutor(2);
      Reactor reactor = new ExecutorReactor(executor);
      TupleStoreTracer tracer = new TupleStoreTracer();
      SocketBuilder socket = new DirectSocketBuilder(tracer, remote, port);
      TransportBuilder transport = new DirectTransportBuilder(socket, reactor);
      SessionRegistryListener listener = new SessionRegistryListener(registry);
      GridSubscriber subscriber = new GridSubscriber(listener, transport);

      return new TupleStoreBuilder(subscriber, catalog, origin, path, remote);
   }
}
