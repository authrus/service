package com.authrus.gateway.deploy.build;

import java.net.URI;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import com.authrus.http.proxy.balancer.connect.ConnectionPoolConnector;
import com.authrus.http.proxy.balancer.status.StatusMonitor;

@Data
@Builder
@AllArgsConstructor
public class EndPoint {

   private final ConnectionPoolConnector connector;
   private final StatusMonitor monitor;
   private final List<String> patterns;
   private final URI address;
}
