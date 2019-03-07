package com.authrus.gateway.deploy.build;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
@ToString
public class RouteRule {
   
   private String pattern;
   private String template;
   
   @JsonCreator
   public RouteRule(
         @JsonProperty(value = "pattern", required = true) String pattern,
         @JsonProperty(value = "template", required = true) String template)
   {
      this.pattern = pattern;
      this.template = template;
   }
}