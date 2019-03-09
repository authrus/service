package com.authrus.agent.terminal;

public interface TerminalListener {
   void onTerminalInit();
   void onTerminalReady();
   void onTerminalCommand(String command) throws InterruptedException;
   void onTerminalResize(String columns, String rows);
   void onTerminalClose();
}
