package com.authrus.rest.registry.system;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(value = TerminateResource.RESOURCE_PATH)
@Api(value = TerminateResource.RESOURCE_NAME, produces = MediaType.APPLICATION_JSON)
public class TerminateResource {
   
   public static final String RESOURCE_PATH = "/v1/terminate";
   public static final String RESOURCE_NAME = "terminate";
   
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Stop the service")
   public Response terminate() {
      System.exit(0);
      return Response.noContent().build();
   }
}

