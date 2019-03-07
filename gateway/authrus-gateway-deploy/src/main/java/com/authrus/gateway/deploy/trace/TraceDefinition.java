package com.authrus.gateway.deploy.trace;

import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.authrus.http.proxy.trace.EventFilter;
import com.authrus.http.proxy.trace.search.SearchRecorder;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TraceDefinition {

   private final List<String> creates;
   private final List<String> updates;
   private final List<String> commits;
   
   @JsonCreator
   public TraceDefinition(
         @JsonProperty("create") List<String> creates,
         @JsonProperty("update") List<String> updates,
         @JsonProperty("commit") List<String> commits)
   {
      this.creates = creates;
      this.updates = updates;
      this.commits = commits;
   }

   public SearchRecorder getRecorder() {
	   EventFilter create = new EventFilter((Collection)creates);
	   EventFilter update = new EventFilter((Collection)updates);
	   EventFilter commit = new EventFilter((Collection)commits);
	   
	   return new SearchRecorder(create, update, commit);
   }
}
