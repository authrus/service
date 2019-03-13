package com.authrus.gateway.resource.trace;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Path(value = TraceResource.RESOURCE_PATH)
@Api(value = TraceResource.RESOURCE_NAME, produces = MediaType.APPLICATION_JSON)
public class TraceResource {

   public static final String RESOURCE_PATH = "/v1/trace";
   public static final String RESOURCE_NAME = "trace";

   private final TraceService service;

   @Inject
   public TraceResource(@Context TraceService service) {
      this.service = service;
   }

   @GET
   @Path("/all/{category}/{name}")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Find all records")
   public List<TraceMatch> findAll(
           @PathParam("category") TraceCategory category,
           @PathParam("name") String name)
   {
      return service.findAll(category, name);
   }

   @GET
   @Path("/active/{category}/{name}")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Find active records")
   public List<TraceMatch> findActive(
           @PathParam("category") TraceCategory category,
           @PathParam("name") String name)
   {
      return service.findActive(category, name);
   }

   @GET
   @Path("/all/{category}/{name}/{seconds}")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Find all records within the specified time")
   public List<TraceMatch> findAll(
           @PathParam("category") TraceCategory category,
           @PathParam("name") String name,
           @PathParam("seconds") long seconds)
   {
      return service.findAll(category, name, seconds);
   }

   @GET
   @Path("/active/{category}/{name}/{seconds}")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Find active records within the specified time")
   public List<TraceMatch> findActive(
           @PathParam("category") TraceCategory category,
           @PathParam("name") String name,
           @PathParam("seconds") long seconds)
   {
      return service.findActive(category, name, seconds);
   }

   @GET
   @Path("/all-by-id/{category}/{name}/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Find all records with the specified id")
   public List<TraceMatch> findAllById(
           @PathParam("category") TraceCategory category,
           @PathParam("name") String name,
           @PathParam("id") String id)
   {
      return service.findAllById(category, name, id);
   }

   @GET
   @Path("/active-by-id/{category}/{name}/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Find active records with the specified id")
   public List<TraceMatch> findActiveById(
           @PathParam("category") TraceCategory category,
           @PathParam("name") String name,
           @PathParam("id") String id)
   {
      return service.findActiveById(category, name, id);
   }

   @GET
   @Path("/all-by-value/{category}/{name}/{pattern}")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Find all records matching the specified pattern")
   public List<TraceMatch> findAllByValue(
           @PathParam("category") TraceCategory category,
           @PathParam("name") String name,
           @PathParam("pattern") String pattern)
   {
      return service.findAllByValue(category, name, pattern);
   }

   @GET
   @Path("/active-by-value/{category}/{name}/{pattern}")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Find all records matching the specified pattern")
   public List<TraceMatch> findActiveByValue(
           @PathParam("category") TraceCategory category,
           @PathParam("name") String name,
           @PathParam("pattern") String pattern)
   {
      return service.findActiveByValue(category, name, pattern);
   }

   @GET
   @Path("/all-by-value/{category}/{name}/{pattern}/{seconds}")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Find all records matching the specified pattern")
   public List<TraceMatch> findAllByValue(
           @PathParam("category") TraceCategory category,
           @PathParam("name") String name,
           @PathParam("pattern") String pattern,
           @PathParam("seconds") long seconds)
   {
      return service.findAllByValue(category, name, pattern, seconds);
   }

   @GET
   @Path("/active-by-value/{category}/{name}/{pattern}/{seconds}")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Find active records matching the specified pattern")
   public List<TraceMatch> findActiveByValue(
           @PathParam("category") TraceCategory category,
           @PathParam("name") String name,
           @PathParam("pattern") String pattern,
           @PathParam("seconds") long seconds)
   {
      return service.findActiveByValue(category, name, pattern, seconds);
   }

   @GET
   @Path("/all-by-event/{category}/{name}/{pattern}")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Find all records matching the specified pattern")
   public List<TraceMatch> findAllByEvent(
           @PathParam("category") TraceCategory category,
           @PathParam("name") String name,
           @PathParam("pattern") String pattern)
   {
      return service.findAllByEvent(category, name, pattern);
   }

   @GET
   @Path("/active-by-event/{category}/{name}/{pattern}")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Find all records matching the specified pattern")
   public List<TraceMatch> findActiveByEvent(
           @PathParam("category") TraceCategory category,
           @PathParam("name") String name,
           @PathParam("pattern") String pattern)
   {
      return service.findActiveByEvent(category, name, pattern);
   }

   @GET
   @Path("/all-by-event/{category}/{name}/{pattern}/{seconds}")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Find all records matching the specified pattern")
   public List<TraceMatch> findAllByEvent(
           @PathParam("category") TraceCategory category,
           @PathParam("name") String name,
           @PathParam("pattern") String pattern,
           @PathParam("seconds") long seconds)
   {
      return service.findAllByEvent(category, name, pattern, seconds);
   }

   @GET
   @Path("/active-by-event/{category}/{name}/{pattern}/{seconds}")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Find all records matching the specified pattern")
   public List<TraceMatch> findActiveByEvent(
           @PathParam("category") TraceCategory category,
           @PathParam("name") String name,
           @PathParam("pattern") String pattern,
           @PathParam("seconds") long seconds)
   {
      return service.findActiveByEvent(category, name, pattern, seconds);
   }

   @GET
   @Path("/all-by-address/{category}/{name}/{pattern}")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Find all records matching the specified pattern")
   public List<TraceMatch> findAllByAddress(
           @PathParam("category") TraceCategory category,
           @PathParam("name") String name,
           @PathParam("pattern") String pattern)
   {
      return service.findAllByAddress(category, name, pattern);
   }

   @GET
   @Path("/active-by-address/{category}/{name}/{pattern}")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Find all records matching the specified pattern")
   public List<TraceMatch> findActiveByAddress(
           @PathParam("category") TraceCategory category,
           @PathParam("name") String name,
           @PathParam("pattern") String pattern)
   {
      return service.findActiveByAddress(category, name, pattern);
   }

   @GET
   @Path("/all-by-address/{category}/{name}/{pattern}/{seconds}")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Find all records matching the specified pattern")
   public List<TraceMatch> findAllByAddress(
           @PathParam("category") TraceCategory category,
           @PathParam("name") String name,
           @PathParam("pattern") String pattern,
           @PathParam("seconds") long seconds)
   {
      return service.findAllByAddress(category, name, pattern, seconds);
   }

   @GET
   @Path("/active-by-address/{category}/{name}/{pattern}/{seconds}")
   @Produces(MediaType.APPLICATION_JSON)
   @ApiOperation(value = "Find all records matching the specified pattern")
   public List<TraceMatch> findActiveByAddress(
           @PathParam("category") TraceCategory category,
           @PathParam("name") String name,
           @PathParam("pattern") String pattern,
           @PathParam("seconds") long seconds)
   {
      return service.findActiveByAddress(category, name, pattern, seconds);
   }

   @DELETE
   @Path("/all/{category}/{name}")
   @ApiOperation(value = "Delete all records")
   public void deleteAll(
           @PathParam("category") TraceCategory category,
           @PathParam("name") String name)
   {
      service.clear(category, name);
   }
}



