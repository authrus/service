package com.authrus.gateway.deploy.trace;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

import com.authrus.http.proxy.trace.search.SearchRecorder;

@Data
@AllArgsConstructor
public class TraceContext {

   private final Map<String, SearchRecorder> proxy;
   private final Map<String, SearchRecorder> client;


}
