package com.authrus.gateway.resource.trace;

import com.authrus.gateway.deploy.trace.TraceContext;
import com.authrus.http.proxy.trace.search.SearchRecorder;

public enum TraceCategory {
   PROXY {
      @Override
      public SearchRecorder getRecorder(TraceContext context, String name) {
         return context.getProxy().get(name);
      }
   },
   CLIENT {
      @Override
      public SearchRecorder getRecorder(TraceContext context, String name) {
         return context.getClient().get(name);
      }
   };

   public abstract SearchRecorder getRecorder(TraceContext context, String name);
}
