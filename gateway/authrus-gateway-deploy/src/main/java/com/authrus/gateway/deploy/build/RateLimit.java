package com.authrus.gateway.deploy.build;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class RateLimit {

   public static final RateLimit LIMIT_100 = new RateLimit(100L, 60000L);
   public static final RateLimit LIMIT_1000 = new RateLimit(1000L, 60000L);
   public static final RateLimit LIMIT_10000 = new RateLimit(10000L, 60000L);
   public static final RateLimit LIMIT_100000 = new RateLimit(100000L, 60000L);

   private final long count;
   private final long duration;

   @JsonCreator
   public RateLimit(
           @JsonProperty(value = "count") Long count,
           @JsonProperty(value = "duration") Long duration)
   {
      this.duration = duration == null ? 60000L : duration;
      this.count = count == null ? 10000L : count;
   }
}
