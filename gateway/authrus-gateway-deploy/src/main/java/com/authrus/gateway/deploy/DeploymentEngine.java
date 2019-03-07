package com.authrus.gateway.deploy;

import java.io.IOException;
import java.io.Reader;
import java.util.Set;

import com.authrus.gateway.deploy.build.ProxyContext;
import com.authrus.gateway.deploy.build.FirewallRule;
import com.authrus.gateway.deploy.build.ProxyPlan;
import com.authrus.gateway.deploy.parse.SourceInterpolator;
import com.authrus.gateway.deploy.parse.SourceProcessor;
import com.authrus.http.proxy.analyser.SampleRecorder;
import com.authrus.http.proxy.trace.TraceAgent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeploymentEngine {
   
   public static final String DEFAULT_COOKIE = "SSOID";
   
   private final ProxyContext context;
   
   public DeploymentEngine(TraceAgent proxy, TraceAgent client) throws IOException {
      this(proxy, client, (sample) -> {});
   }
   
   public DeploymentEngine(TraceAgent proxy, TraceAgent client, SampleRecorder recorder) throws IOException {
      this(proxy, client, recorder, DEFAULT_COOKIE);
   }

   public DeploymentEngine(TraceAgent proxy, TraceAgent client, SampleRecorder recorder, String cookie) throws IOException {
      this.context  = new ProxyContext(proxy, client, recorder, cookie, 40, 4);
   }

   public Deployment deploy(Reader reader) throws Exception {
      ObjectMapper mapper = context.getMapper();
      String text = SourceProcessor.process(reader);     
      String source = SourceInterpolator.interpolate(context, text);
      ProxyPlan plan = mapper.readValue(source, ProxyPlan.class);
      
      return deploy(plan);
   }
   
   public Deployment deploy(ProxyPlan plan) throws Exception {
      TraceAgent proxy = context.getProxy();
      TraceAgent client = context.getClient();

      proxy.start();
      client.start();
      plan.process(context);
      
      Set<FirewallRule> rules = context.getRules();
      
      for(FirewallRule rule : rules) {
         String host = rule.getHost();
         String address = rule.getAddress();
         String type = rule.getType();
         int port = rule.getPort();
         
         log.info("Firewall: open {} port {} on host {}/{}", type, port, host, address);
      }
      return context.getPlan();
   }
}
