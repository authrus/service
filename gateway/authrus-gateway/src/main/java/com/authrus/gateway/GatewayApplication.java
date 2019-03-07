package com.authrus.gateway;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import com.authrus.gateway.deploy.EnableGatewayDeployment;
import com.authrus.rest.EnableResourceServer;

@EnableResourceServer
@EnableGatewayDeployment
@SpringBootApplication
public class GatewayApplication {

   public static void main(String[] list) throws Exception {
      SpringApplicationBuilder builder = new SpringApplicationBuilder(GatewayApplication.class);
      builder.web(WebApplicationType.NONE).run(list);
   }
}
