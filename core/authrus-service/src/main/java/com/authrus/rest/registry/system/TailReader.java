package com.authrus.rest.registry.system;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@AllArgsConstructor
public class TailReader {
   
   private final int limit;  

   @SneakyThrows
   public String read(File file) {
      long length = file.length();
      
      if(length > limit) {
         RandomAccessFile reader = new RandomAccessFile(file, "r");
         byte[] content = new byte[limit];
         int seek = 0;
         
         try {
            reader.seek(length - limit);
            
            int count = reader.read(content, 0, limit);
            int actual = Math.min(limit, count);
            
            while(seek < actual) {
               byte current = content[seek++];
               
               if(current == '\n') {
                  return new String(content, seek, actual -seek, StandardCharsets.UTF_8);
               }
               if(current == '\r' && seek < actual) {
                  byte next = content[seek];
                  
                  if(next != '\n') {
                     return new String(content, seek, actual -seek, StandardCharsets.UTF_8);
                  }                  
               }
            }
            return new String(content, StandardCharsets.UTF_8);
         } finally {
            reader.close();
         }
      }
      return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
   }
}
