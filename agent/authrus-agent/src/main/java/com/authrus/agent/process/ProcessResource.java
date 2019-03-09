package com.authrus.agent.process;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path(value = ProcessResource.RESOURCE_PATH)
@Api(value = ProcessResource.RESOURCE_NAME, produces = MediaType.APPLICATION_JSON)
public class ProcessResource {
   
   public static final String RESOURCE_PATH = "/v1/process";
   public static final String RESOURCE_NAME = "process";

   private final ProcessLocator locator;
   
   @Inject
   public ProcessResource(@Context ProcessLocator locator) {
      this.locator = locator;
   }
   
   @GET
   @Path("/{name}/start")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Start a process")
   public Response start(@PathParam("name") String name, @DefaultValue("5000") @QueryParam("wait") long wait) {
      ProcessManager manager = locator.locate(name);
   
      if(manager != null) {
         CommandResult result = manager.start(wait);
         return Response.ok(result).build();
      }
      return Response.status(Status.NOT_FOUND).build();
   }
   
   @GET
   @Path("/{name}/stop")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Stop a process")
   public Response stop(@PathParam("name") String name, @DefaultValue("5000") @QueryParam("wait") long wait) {
      ProcessManager manager = locator.locate(name);
   
      if(manager != null) {
         CommandResult result = manager.stop(wait);
         return Response.ok(result).build();
      }
      return Response.status(Status.NOT_FOUND).build();
   }
}
