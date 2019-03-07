package com.authrus.gateway.deploy;

import java.net.URI;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;

import com.authrus.gateway.deploy.build.EndPoint;
import com.authrus.gateway.deploy.build.FirewallRule;
import com.authrus.gateway.deploy.build.RouteTable;

@Data
@AllArgsConstructor
public class Deployment {

   private final Set<RouteTable> tables;
   private final Set<FirewallRule> rules;
   private final Set<EndPoint> servers;
   private final Set<URI> addresses;
}
