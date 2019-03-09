package com.authrus.agent.group;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.EMPTY_MAP;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import com.authrus.gateway.deploy.Deployment;
import com.authrus.gateway.deploy.DeploymentEngine;
import com.authrus.gateway.deploy.build.AccessLog;
import com.authrus.gateway.deploy.build.HostLayout;
import com.authrus.gateway.deploy.build.ProxyPlan;
import com.authrus.gateway.deploy.build.RouteTable;
import com.authrus.rest.registry.RegistryNode;
import lombok.SneakyThrows;

public class GroupServer {
	
	private static final String LOG_PATTERN = "%T [%P] %h [%t] '%r' %s %b %{Host} '%{User-Agent}' %{Content-Type} @{Content-Type} @{Content-Encoding} %X @{X-Time} ms %d ms";
	
	private final AtomicReference<RouteTable> reference;
	private final GroupServiceRegistry registry;
	private final DeploymentEngine engine;
	private final HostLayout layout;
	private final AccessLog access;
	
	public GroupServer(DeploymentEngine engine, String directory, String host, String file, int port) throws IOException {
		this.access = new AccessLog(file, LOG_PATTERN, 10000);
		this.layout = new HostLayout(EMPTY_LIST, EMPTY_MAP, EMPTY_LIST, EMPTY_LIST, directory, null, access, host, port);
		this.registry = new GroupServiceRegistry(host, port);
		this.reference = new AtomicReference<>();
		this.engine = engine;
	}
  
  @SneakyThrows
  public void register(RegistryNode node) {
     RouteTable table = reference.get();
     
     if(table == null) {
        throw new IllegalStateException("Group server has not started");
     }
     registry.register(table, node);
  }  
	
	@SneakyThrows
   public Deployment start() {
      ProxyPlan plan = ProxyPlan.builder()
            .hosts(Collections.singletonList(layout))
            .properties(Collections.emptyMap())
            .build();
      Deployment deployment = engine.deploy(plan);
      RouteTable table = deployment.getTables()
            .stream()
            .findFirst()
            .get();
   
      reference.set(table);
      return deployment;
   }
}

