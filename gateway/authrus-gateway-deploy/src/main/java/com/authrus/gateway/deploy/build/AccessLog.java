package com.authrus.gateway.deploy.build;

import java.io.File;

import lombok.Builder;

import com.authrus.http.proxy.log.FileLog;
import com.authrus.http.proxy.log.Formatter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Builder
public class AccessLog {
   
   private String path;
   private String format;
   private int threshold;
   
   @JsonCreator
   public AccessLog(
         @JsonProperty(value = "path", required = true) String path,
         @JsonProperty(value = "format", required = true) String format,
         @JsonProperty(value = "threshold", defaultValue = "10000") int threshold)
   {
      this.path = path;
      this.format = format;
      this.threshold = threshold;
   }
   
   public FileLog createLog(String directory) {
      File file = new File(directory, path);
      Formatter formatter = new Formatter(format);
      FileLog log = new FileLog(file, formatter, threshold);
      
      return log;
   }
}