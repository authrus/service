package com.authrus.agent.process;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.SneakyThrows;

import com.authrus.common.command.Environment;
import com.authrus.rest.registry.RegistryNode;
import com.authrus.rest.registry.system.TailParser;
import com.authrus.rest.registry.system.TailResult;

@Builder
@AllArgsConstructor
public class WindowsProcess implements Process {  
   
   private final CommandTemplate template;
   private final ProcessRunner runner;
   private final RegistryNode node;
   private final TailParser parser;
   
   public WindowsProcess(CommandTemplate template, RegistryNode node, Environment environment, String directory) {
      this.runner = new ProcessRunner(environment);
      this.parser = new TailParser(directory);
      this.template = template;
      this.node = node;
   }
   
   @Override
   public String getName() {
      return node.getName();
   }

   @Override
   public String getHost() {
      return node.getHost();
   }
   
   @Override
   public File getDirectory() {
      String directory = node.getDirectory();
      Path path = Paths.get(directory);
      
      return path.toFile();
   }
   
   @Override
   @SneakyThrows
   public CommandResult start(long wait) {
      String name = node.getName();
      String directory = node.getDirectory();
      String environment = node.getEnvironment();
      Path path = Paths.get(directory);
      File file = path.toFile();
      String command = template.start(name, directory, environment);
      String log = runner.execute(command, file, wait);
      
      return new CommandResult(directory, command, log);
   }
   
   @Override
   public CommandResult stop(long wait) {
      String name = node.getName();
      String directory = node.getDirectory();
      String environment = node.getEnvironment();
      Path path = Paths.get(directory);
      File file = path.toFile();
      String command = template.stop(name, directory, environment);
      String log = runner.execute(command, file, wait);
      
      return new CommandResult(directory, command, log);
   }
   
   @Override
   public List<TailResult> tail() {
      return parser.tail(".*.log");
   }
}
