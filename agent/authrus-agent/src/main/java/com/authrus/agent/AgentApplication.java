package com.authrus.agent;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.authrus.agent.group.EnableGroupServer;
import com.authrus.agent.zookeeper.EnableZooKeeperServer;
import com.authrus.rest.EnableResourceServer;

@EnableScheduling
@EnableGroupServer
@EnableResourceServer
@EnableZooKeeperServer
@SpringBootApplication
public class AgentApplication {

	public static void main(String[] list) {
      SpringApplicationBuilder builder = new SpringApplicationBuilder(AgentApplication.class);
      builder.web(WebApplicationType.NONE).run(list);
	}
}
