package com.authrus.agent.process;

import java.io.File;
import java.util.List;

import com.authrus.rest.registry.system.TailResult;

public interface Process {
   File getDirectory();
   String getName();
   String getHost();
   List<TailResult> tail();
   CommandResult start(long wait);
   CommandResult stop(long wait);
}
