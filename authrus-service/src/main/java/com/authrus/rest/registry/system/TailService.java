package com.authrus.rest.registry.system;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class TailService {

   private final TailParser parser;
   
   public TailService(String directory) {
      this.parser = new TailParser(directory);
   }
   
   public List<TailResult> tail() {
      return parser.tail(".*.log");
   }
}
