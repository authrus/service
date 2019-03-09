package com.authrus.agent.group;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;

import com.authrus.rest.registry.RegistryNode;

@Slf4j
@AllArgsConstructor
public class GroupServiceLocator {

	private final GroupResolver resolver;
	private final GroupServer server;

	@Scheduled(initialDelay = 1000, fixedDelay = 10000)
	public void schedule() {
		List<RegistryNode> nodes = resolver.findAll();
		
		for(RegistryNode node : nodes) {
			try {
			   server.register(node);
			} catch(Exception e) {
				log.info("Could not register {}", node, e);
			}
		}
	}
}
