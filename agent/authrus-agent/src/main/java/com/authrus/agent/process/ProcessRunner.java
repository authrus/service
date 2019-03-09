package com.authrus.agent.process;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.File;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import com.authrus.common.command.Console;
import com.authrus.common.command.Environment;
import com.authrus.common.command.Script;

@Slf4j
public class ProcessRunner {
   
   private final Environment environment;
   private final int limit;
   
   public ProcessRunner(Environment environment) {
      this(environment, 200);
   }
   
   public ProcessRunner(Environment environment, int limit) {
      this.environment = environment;
      this.limit = limit;
   }

   @SneakyThrows
   public String execute(String command, File directory, long wait) {
      Script script = new Script(command, directory, true, true, wait);
      ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
      StringBuilder builder = new StringBuilder();
      
      try {
         Console console = script.execute(environment);              
         Future<String> future = executor.submit(() -> {
            int count = 0;
            
            while (count < limit) {
               String line = console.readLine();
   
               if (line != null) {
                  builder.append("\r\n");
                  builder.append(line);
               } else {
                  break;
               }
            }
            return builder.toString();
         });
         return future.get(wait, MILLISECONDS);
      } catch(Exception e) {
         log.info("Could not execute [{}] in '{}'", command, directory, e);
      }
      return builder.toString();
   }
}
