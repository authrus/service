package com.authrus.rest.registry.system;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TailResult {

   private String name;
   private String log;
}
