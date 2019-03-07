package com.authrus.gateway.deploy.build;

import static com.authrus.gateway.deploy.build.RateLimit.LIMIT_100000;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import com.authrus.http.proxy.balancer.identity.AddressExtractor;
import com.authrus.http.proxy.balancer.identity.IdentityExtractor;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.simpleframework.http.Status;
import org.simpleframework.transport.trace.TraceAnalyzer;

import com.authrus.common.ssl.Certificate;
import com.authrus.http.proxy.ProxyContainer;
import com.authrus.http.proxy.host.DomainResolver;
import com.authrus.http.proxy.host.HostResolver;
import com.authrus.http.proxy.resource.host.ResourceHost;
import com.authrus.http.resource.StringResource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Slf4j
@Builder
public class ProxyPlan {
   
   private final Map<String, String> properties;
   private final List<HostLayout> hosts;
   private final RateLimit limit;
   
   @JsonCreator
   public ProxyPlan(
         @JsonProperty("properties") Map<String, String> properties,
         @JsonProperty("hosts") List<HostLayout> hosts,
         @JsonProperty("rate-limit") RateLimit limit)
   {
      this.limit = limit == null ? LIMIT_100000 : limit;
      this.properties = properties;
      this.hosts = hosts;
   }
   
   @SneakyThrows
   public Set<RouteTable> process(ProxyContext context){
      Executor executor = context.getExecutor();
      TraceAnalyzer analyzer = context.getClient();
      Map<String, HostResolver> domains = new LinkedHashMap<String, HostResolver>();
      StringResource resource = new StringResource("No host matched", "text/plain", "UTF-8", Status.OK);
      ResourceHost error = new ResourceHost(Collections.EMPTY_MAP, resource);
      HostResolver domainResolver = new DomainResolver(domains, error);
      Set<Integer> ports = new HashSet<Integer>();
      List<Destination> details = new ArrayList<Destination>();
      Set<RouteTable> systems = new HashSet<RouteTable>();
      int count = hosts.size();
      
      for(HostLayout host : hosts) {
         List<Route> routes = host.getRoutes();
         RouteTable system = host.createSystem(context);
         
         for(Route route : routes) {
            system.registerRoute(route);
         }
         HostResolver resolver = system.getResolver();
         int port = host.getPort();
         InetSocketAddress hostAddress = new InetSocketAddress(port);
         KeyStore store = host.getStore();
         String name = host.getName();
         
         if(!ports.add(port)) {
            throw new IllegalArgumentException("Port " + port + " already used");
         }
         domains.put(name + ":" + port, resolver);
         domains.put(name, resolver);
         
         if(store != null) {   
            Certificate certificate = store.getCertificate();
            Destination virtualDetails = new Destination(hostAddress, certificate, name);
            details.add(virtualDetails);
            context.getAddresses().add(URI.create("https://" + name));
         } else {
            Destination virtualDetails = new Destination(hostAddress, null, name);
            details.add(virtualDetails);
         }
         if(count <= 1) {
            domainResolver = resolver;
         }
         log.info("Domain {} is listening on {}", name, port);
         context.getAddresses().add(URI.create("http://" + name)); // HTTP will always be supported
         context.getTables().add(system);
      }
      long duration = limit.getDuration();
      long requests = limit.getCount();
      IdentityExtractor extractor = context.getExtractor();
      ProxyContainer container = new ProxyContainer(domainResolver, executor);
      RateLimitContainer limiter = new RateLimitContainer(extractor, container, duration, requests);
      ProxyServer processor = new ProxyServer(limiter, analyzer, 40);
      
      for(Destination host : details) {
         Certificate certificate = host.getCertificate();
         InetSocketAddress address = host.getAddress();
         
         if(certificate != null) {
            processor.start(address, certificate);
         }else {
            processor.start(address);
         }
      }
      return systems;
   }
}