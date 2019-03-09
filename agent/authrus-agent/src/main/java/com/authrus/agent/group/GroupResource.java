package com.authrus.agent.group;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.authrus.rest.registry.RegistryNode;

@Path(value = GroupResource.RESOURCE_PATH)
@Api(value = GroupResource.RESOURCE_NAME, produces = MediaType.APPLICATION_JSON)
public class GroupResource {
   
   public static final String RESOURCE_PATH = "/v1/group";
   public static final String RESOURCE_NAME = "group";

   private final GroupResolver resolver;
   
   @Inject
   public GroupResource(@Context GroupResolver resolver) {
      this.resolver = resolver;
   }
   
   @GET  
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Show group services")
   public Response list() {
      List<RegistryNode> result = resolver.findAll();
      return Response.ok(result).build();
   }
   
   @GET  
   @Path("/{host}")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Show group services")
   public Response list(@PathParam("host") String host) {
      List<RegistryNode> result = resolver.findAll()
            .stream()
            .filter(Objects::nonNull)
            .filter(node -> {
               String location = node.getHost();
               return location.equals(host);
            })
            .collect(Collectors.toList());
      
      return Response.ok(result).build();
   }
}
