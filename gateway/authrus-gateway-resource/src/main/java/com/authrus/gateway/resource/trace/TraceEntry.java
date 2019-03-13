package com.authrus.gateway.resource.trace;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TraceEntry {

   private final String thread;
   private final String event;
   private final String message;
   private final String elapsed;
   private final String time;
   private final long eventDuration;
   private final long totalDuration;
   private final long id;
}
