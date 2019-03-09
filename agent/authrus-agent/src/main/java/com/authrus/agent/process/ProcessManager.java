package com.authrus.agent.process;

import java.io.File;
import java.util.List;

import com.authrus.rest.registry.system.TailResult;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

@AllArgsConstructor
public class ProcessManager {

   private final Process process;

   public String getName() {
      return process.getName();
   }
   
   public File getDirectory() {
      return process.getDirectory();
   }
   
   @SneakyThrows
   public CommandResult start(long wait) {
      return process.start(wait);
   }
   
   @SneakyThrows
   public CommandResult stop(long wait) {
      return process.stop(wait);
   }
   
   @SneakyThrows
   public List<TailResult> tail() {
      return process.tail();   
   }   
}
