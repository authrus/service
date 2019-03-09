package com.authrus.agent.process;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CommandResult {

   private String directory;
   private String command;
   private String log;
}
