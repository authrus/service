package com.authrus.rest.registry.system;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TailParser {

   private final TailReader reader;
   private final File directory;
   
   public TailParser(String directory) {
      this(directory, 8192);
   }
   
   public TailParser(String directory, int limit) {
      this.directory = new File(directory);
      this.reader = new TailReader(limit);
   }
   
   public List<TailResult> tail(String pattern) {
      String[] suffixes = { "log", "logs", "." };
      
      for(String suffix : suffixes) {
         File root = new File(directory, suffix);
         
         if(root.exists() && root.isDirectory()) {
            File[] files = root.listFiles(file -> {
               String name = file.getName();
               
               if(name.matches(pattern) || name.endsWith(pattern)) {
                  return true;
               }
               return false;
            });
            return Arrays.asList(files)
                  .stream()
                  .map(file -> {
                     try {
                        String name = file.getName();
                        String log = reader.read(file);
                        
                        return TailResult.builder()
                              .name(name)
                              .log(log)
                              .build();
                     } catch(Exception e) {
                        return null;
                     }
                  })
                  .filter(Objects::nonNull)
                  .collect(Collectors.toList());
         }
      }
      return Collections.emptyList();
   }
}
