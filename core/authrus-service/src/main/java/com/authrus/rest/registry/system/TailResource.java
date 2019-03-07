package com.authrus.rest.registry.system;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(value = TailResource.RESOURCE_PATH)
@Api(value = TailResource.RESOURCE_NAME, produces = MediaType.APPLICATION_JSON)
public class TailResource {
   
   public static final String RESOURCE_PATH = "/v1/tail";
   public static final String RESOURCE_NAME = "tail";

   private final TailService service;
   
   @Inject
   public TailResource(@Context TailService service) {
      this.service = service;
   }
   
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Tail this process")
   public Response tail() {
      List<TailResult> results = service.tail();
      return Response.ok(results).build();
   }
}

