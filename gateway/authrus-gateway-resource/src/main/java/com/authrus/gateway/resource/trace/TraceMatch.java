package com.authrus.gateway.resource.trace;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TraceMatch {

   private final List<TraceEntry> entries;
   private final TraceCategory category;
   private final String name;
   private final long id;
}
