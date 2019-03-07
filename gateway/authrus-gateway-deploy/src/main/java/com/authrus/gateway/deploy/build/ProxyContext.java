package com.authrus.gateway.deploy.build;

import java.io.IOException;
import java.net.URI;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;

import com.authrus.gateway.deploy.Deployment;
import com.authrus.http.proxy.analyser.SampleRecorder;
import com.authrus.http.proxy.balancer.identity.CookieExtractor;
import com.authrus.http.proxy.balancer.identity.IdentityExtractor;
import com.authrus.http.proxy.core.exchange.RequestExchanger;
import com.authrus.http.proxy.trace.TraceAgent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.simpleframework.common.thread.ConcurrentExecutor;
import org.simpleframework.transport.reactor.ExecutorReactor;
import org.simpleframework.transport.reactor.Reactor;

@Data
public class ProxyContext {
   
   private final Set<RouteTable> tables;
   private final Set<FirewallRule> rules;
   private final Set<EndPoint> monitors;
   private final Set<URI> addresses;  
   private final SampleRecorder recorder;
   private final IdentityExtractor extractor;
   private final ObjectMapper mapper;
   private final TraceAgent proxy;
   private final TraceAgent client;
   private final Executor executor;
   private final Reactor reactor; 

   public ProxyContext(TraceAgent proxy, TraceAgent client, SampleRecorder recorder, String cookie) throws IOException {
      this(proxy, client, recorder, cookie, 40);
   }
   
   public ProxyContext(TraceAgent proxy, TraceAgent client, SampleRecorder recorder, String cookie, int threads) throws IOException {
      this(proxy, client, recorder, cookie, threads, 4);
   }
   
   public ProxyContext(TraceAgent proxy, TraceAgent client, SampleRecorder recorder, String cookie, int threads, int selectors) throws IOException {
      this.executor = new ConcurrentExecutor(RequestExchanger.class, threads);
      this.reactor = new ExecutorReactor(executor, selectors);
      this.extractor = new CookieExtractor(cookie);
      this.monitors = new CopyOnWriteArraySet<>();
      this.rules = new CopyOnWriteArraySet<>();
      this.addresses = new CopyOnWriteArraySet<>();
      this.tables = new CopyOnWriteArraySet<>();
      this.mapper = new ObjectMapper();
      this.recorder = recorder;
      this.client = client;
      this.proxy = proxy;
   }
   
   public Deployment getPlan() {      
      return new Deployment(tables, rules, monitors, addresses);
   }
}