package com.authrus.gateway.deploy.build;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class CacheControl {

   private static final String[] CACHE_TYPES = {
           "text/css",
           "text/plain",
           "text/html",
           "text/js",
           "image/png",
           "image/gif",
           "image/jpeg",
           "application/javascript"
   };

   private final List<String> types;
   private final long duration;
   private final boolean disabled;

   @JsonCreator
   public CacheControl(
           @JsonProperty(value = "content-types") List<String> types,
           @JsonProperty(value = "duration") Long duration,
           @JsonProperty(value = "disabled") Boolean disabled)
   {
      this.types = types == null ? Arrays.asList(CACHE_TYPES) : types;
      this.duration = duration == null ? TimeUnit.MINUTES.toMillis(10) : duration;
      this.disabled = disabled == null ? false : disabled;
   }
}
