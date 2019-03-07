package com.authrus.gateway.deploy.build;

import static com.authrus.http.proxy.core.State.CERTIFICATE_EXPIRED;
import static com.authrus.http.proxy.core.State.CERTIFICATE_INVALID;
import static com.authrus.http.proxy.core.State.CERTIFICATE_REQUIRED;
import static com.authrus.http.proxy.core.State.CERTIFICATE_REVOKED;
import static com.authrus.http.proxy.core.State.CERTIFICATE_UNTRUSTED;
import static com.authrus.http.proxy.core.State.COOKIE_EXPIRED;
import static com.authrus.http.proxy.core.State.COOKIE_SPOOF;
import static com.authrus.http.proxy.core.State.PERMISSION_DENIED;
import static com.authrus.http.proxy.core.State.REMOTE_DISCONNECT;
import static com.authrus.http.proxy.core.State.REMOTE_TIMEOUT;
import static com.authrus.http.proxy.core.State.RESTRICTED_METHOD;
import static com.authrus.http.proxy.core.State.UNABLE_TO_CONNECT;
import static com.authrus.http.proxy.core.State.UNEXPECTED_ERROR;
import static org.simpleframework.http.Status.BAD_GATEWAY;
import static org.simpleframework.http.Status.FORBIDDEN;
import static org.simpleframework.http.Status.GATEWAY_TIMEOUT;
import static org.simpleframework.http.Status.INTERNAL_SERVER_ERROR;
import static org.simpleframework.http.Status.METHOD_NOT_ALLOWED;
import static org.simpleframework.http.Status.SERVICE_UNAVAILABLE;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.authrus.http.proxy.balancer.identity.IdentityExtractor;
import com.authrus.http.proxy.core.intercept.RedirectInterceptor;
import com.authrus.http.proxy.security.AccessManager;
import com.authrus.http.proxy.security.AccessRule;
import com.authrus.http.proxy.security.RuleAccessManager;
import com.sun.scenario.effect.Identity;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.ToString;

import org.simpleframework.http.Scheme;
import org.simpleframework.http.Status;
import org.simpleframework.transport.reactor.Reactor;

import com.authrus.common.ssl.Certificate;
import com.authrus.http.proxy.core.State;
import com.authrus.http.proxy.core.exchange.ReactorController;
import com.authrus.http.proxy.core.intercept.CombinationInterceptor;
import com.authrus.http.proxy.core.intercept.CopyHeaderInterceptor;
import com.authrus.http.proxy.core.intercept.HeaderInterceptor;
import com.authrus.http.proxy.core.intercept.ResponseInterceptor;
import com.authrus.http.proxy.host.Host;
import com.authrus.http.proxy.host.HostResolver;
import com.authrus.http.proxy.host.RegularExpressionResolver;
import com.authrus.http.proxy.host.SecureResolver;
import com.authrus.http.proxy.log.FileLog;
import com.authrus.http.proxy.resource.ResourceReporter;
import com.authrus.http.proxy.resource.host.ResourceHost;
import com.authrus.http.proxy.resource.redirect.Redirect;
import com.authrus.http.proxy.resource.redirect.RedirectResolver;
import com.authrus.http.proxy.resource.redirect.RegularExpressionRedirector;
import com.authrus.http.proxy.resource.redirect.ResourceRedirector;
import com.authrus.http.proxy.security.DirectAccessManager;
import com.authrus.http.resource.FileManager;
import com.authrus.http.resource.FileResource;
import com.authrus.http.resource.Resource;
import com.authrus.http.resource.StringResource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
@ToString
public class HostLayout {

   public static final String PROXY_NODE = "X-Proxy-Node";

   private final List<RedirectRule> redirects;
   private final Map<String, String> headers;
   private final List<String> locations;
   private final List<Route> routes;
   private final String directory;
   private final KeyStore store;
   private final AccessLog log;
   private final String name;
   private final int port;
   
   @JsonCreator
   public HostLayout(
         @JsonProperty("redirects") List<RedirectRule> redirects, // redirects
         @JsonProperty("headers") Map<String, String> headers, // response static headers
         @JsonProperty("locations") List<String> locations, // rewrite Location response headers
         @JsonProperty("routes") List<Route> routes, // backend servers
         @JsonProperty("directory") String directory,
         @JsonProperty("key-store") KeyStore store,
         @JsonProperty("access-log") AccessLog log,
         @JsonProperty("name") String name, // domain
         @JsonProperty("port") int port)
   {
      this.redirects = redirects;
      this.directory = directory;
      this.locations = locations;
      this.headers = headers;
      this.routes = routes;
      this.store = store;
      this.name = name;
      this.port = port;
      this.log = log;
   }

   public Certificate createCertificate() {
      if(store != null) {
         return store.getCertificate();
      }
      return null;
   }
   
   @SneakyThrows
   public RouteTable createSystem(ProxyContext context){
      if(name == null) {
         throw new IllegalStateException("Host must have a name");
      }
      if(directory == null) {
         throw new IllegalStateException("Host must specify a static resources directory");
      }
      Map<State, Resource> resources = createResources();
      Reactor reactor = context.getReactor();
      FileLog fileLog = log.createLog(directory);
      Host invalid = createInvalidHost(context);
      Map<String, String> redirectMap = new LinkedHashMap<>();
      Map<String, AccessRule> ruleMap = new LinkedHashMap<>();
      ReactorController controller = new ReactorController(reactor);
      AccessManager manager = new RuleAccessManager(ruleMap);
      ResourceReporter reporter = new ResourceReporter(resources, fileLog, name);
      Map<String, Host> hosts = new LinkedHashMap<String, Host>();
      RegularExpressionResolver regexResolver = new RegularExpressionResolver(hosts, invalid);
      List<ResponseInterceptor> interceptors = new ArrayList<ResponseInterceptor>();
      CombinationInterceptor interceptor = new CombinationInterceptor(interceptors);
      String local = InetAddress.getLocalHost().getCanonicalHostName();
      IdentityExtractor extractor = context.getExtractor();
      String hostname = local.toLowerCase();
      HostResolver resolver = regexResolver;
      
      interceptors.add(new CopyHeaderInterceptor());
      interceptors.add(new HeaderInterceptor(PROXY_NODE, "proxy@" + hostname));

      if(headers != null) {
         Set<Entry<String, String>> entries = headers.entrySet();

         for (Entry<String, String> entry : entries) {
            String name = entry.getKey();
            String value = entry.getValue();

            interceptors.add(new HeaderInterceptor(name, value));
         }
      }
      if(locations != null) {
         for(String location : locations) {
            String expression = "(http|https)://" + location + "/(.*)";
            String template = "%{1}://" + name + "/%{2}";

            redirectMap.put(expression, template);
         }
      }
      interceptors.add(new RedirectInterceptor(redirectMap));

      if(redirects != null && !redirects.isEmpty()) {
         resolver = createRedirects(context, resolver);
      }
      ruleMap.put("/", new WebTokenRule(extractor));
      fileLog.start();
      
      return RouteTable.builder()
            .controller(controller)
            .interceptor(interceptor)
            .manager(manager)
            .reporter(reporter)
            .registry(regexResolver)
            .resolver(resolver)
            .context(context)
            .log(fileLog)
            .build();
   }
   
   public Host createInvalidHost(ProxyContext context) {
      Resource statusResource = new StringResource("Everything is ok...", "text/plain", "UTF-8", Status.OK);
      Resource errorResource = new StringResource("Could not find resource...", "text/plain", "UTF-8", Status.NOT_FOUND);
      Map<String, Resource> resources = new LinkedHashMap<String, Resource>();
      ResourceHost host = new ResourceHost(resources, errorResource);
      resources.put("/.*", statusResource);
      return host;
   }
   
   public HostResolver createRedirects(ProxyContext context, HostResolver resolver) {
      Map<String, Redirect> secureRedirects = new LinkedHashMap<>();
      Map<String, Redirect> normalRedirects = new LinkedHashMap<>();
      Resource redirectResource = new StringResource("Redirecting ...", "text/plain", "UTF-8", Status.FOUND);
      Resource errorResource = new StringResource("Error ...", "text/plain", "UTF-8", Status.NOT_FOUND);
      ResourceRedirector secureRedirector = new RegularExpressionRedirector(secureRedirects, redirectResource);
      ResourceRedirector normalRedirector = new RegularExpressionRedirector(normalRedirects, redirectResource); 
      RedirectResolver secureResolver = new RedirectResolver(resolver, secureRedirector, errorResource);
      RedirectResolver normalResolver = new RedirectResolver(resolver, normalRedirector, errorResource);
      
      for(RedirectRule rule :redirects){
         try {
            RedirectEntry entry = rule.createRedirect();
            Redirect redirect = entry.getRedirect();
            Scheme scheme = entry.getScheme();
            String path = entry.getPath();
            
            if(scheme == Scheme.HTTP) {
               normalRedirects.put(path, redirect);
            } else {
               secureRedirects.put(path, redirect);
            }
         }catch(Exception e) {
            throw new IllegalStateException("Could not parse redirect expression", e);
         }
      }
      return new SecureResolver(normalResolver, secureResolver);
   }

   
   public Map<State, Resource> createResources() {
      File files = new File(directory);
      Map<State, Resource> resources = new LinkedHashMap<>();
      
      if(!files.exists()) {
         throw new IllegalStateException("Resource directory " + files + " is empty");
      }
      FileManager manager = new FileManager(files);
      resources.put(REMOTE_DISCONNECT, new FileResource(manager, new File(files, "error/remote-disconnect.html"), "text/html", BAD_GATEWAY));
      resources.put(REMOTE_TIMEOUT, new FileResource(manager, new File(files, "error/remote-timeout.html"), "text/html", GATEWAY_TIMEOUT));
      resources.put(UNEXPECTED_ERROR, new FileResource(manager, new File(files, "error/internal-error.html"), "text/html", INTERNAL_SERVER_ERROR));
      resources.put(UNABLE_TO_CONNECT, new FileResource(manager, new File(files, "error/service-unavailable.html"), "text/html", SERVICE_UNAVAILABLE));
      resources.put(COOKIE_SPOOF, new FileResource(manager, new File(files, "error/cookie-spoof.html"), "text/html", FORBIDDEN));
      resources.put(COOKIE_EXPIRED, new FileResource(manager, new File(files, "error/cookie-expired.html"), "text/html", FORBIDDEN));
      resources.put(PERMISSION_DENIED, new FileResource(manager, new File(files, "error/permission-denied.html"), "text/html", FORBIDDEN));
      resources.put(CERTIFICATE_INVALID, new FileResource(manager, new File(files, "error/certificate-invalid.html"), "text/html", FORBIDDEN));
      resources.put(CERTIFICATE_REVOKED, new FileResource(manager, new File(files, "error/certificate-revoked.html"), "text/html", FORBIDDEN));
      resources.put(CERTIFICATE_UNTRUSTED, new FileResource(manager, new File(files, "error/certificate-not-trusted.html"), "text/html", FORBIDDEN));
      resources.put(CERTIFICATE_EXPIRED, new FileResource(manager, new File(files, "error/certificate-expired.html"), "text/html", FORBIDDEN));
      resources.put(CERTIFICATE_REQUIRED, new FileResource(manager, new File(files, "error/certificate-required.html"), "text/html", FORBIDDEN));
      resources.put(RESTRICTED_METHOD, new FileResource(manager, new File(files, "error/method-not-allowed.html"), "text/html", METHOD_NOT_ALLOWED));
      return resources;
   }
}