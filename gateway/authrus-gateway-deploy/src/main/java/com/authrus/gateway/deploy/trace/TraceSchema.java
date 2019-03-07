package com.authrus.gateway.deploy.trace;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.authrus.http.proxy.trace.TraceAgent;
import com.authrus.http.proxy.trace.TraceCollector;
import com.authrus.http.proxy.trace.TraceDistributor;
import com.authrus.http.proxy.trace.search.SearchRecorder;
import com.authrus.http.proxy.trace.search.Searcher;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class TraceSchema {

   private final Map<String, TraceDefinition> client;
   private final Map<String, TraceDefinition> proxy;

   @JsonCreator
   public TraceSchema(
	     @JsonProperty("client") Map<String, TraceDefinition> client,
        @JsonProperty("proxy") Map<String, TraceDefinition> proxy)
   {
      this.client = client;
      this.proxy = proxy;
   }

   public TraceContext getContext() {
      Map<String, SearchRecorder> proxies = getRecorders(proxy);
      Map<String, SearchRecorder> clients = getRecorders(client);

      return new TraceContext(proxies, clients);
   }

   private Map<String, SearchRecorder> getRecorders(Map<String, TraceDefinition> definitions) {
      Set<String> names = definitions.keySet();
      Map<String, SearchRecorder> recorders = Maps.newHashMap();

      for(String name : names) {
         TraceDefinition definition = definitions.get(name);
         SearchRecorder recorder = definition.getRecorder();

         recorders.put(name, recorder);
      }
      return recorders;
   }
}
