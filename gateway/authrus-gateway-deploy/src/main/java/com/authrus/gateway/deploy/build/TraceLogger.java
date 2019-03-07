package com.authrus.gateway.deploy.build;

import lombok.extern.slf4j.Slf4j;

import com.authrus.common.time.DateTime;
import com.authrus.http.proxy.trace.TraceCollector;
import com.authrus.http.proxy.trace.TraceEvent;
import com.authrus.http.proxy.trace.search.EventFormatter;

@Slf4j
public class TraceLogger implements TraceCollector {
   
   private final EventFormatter formatter;
   private final boolean enabled;
   
   public TraceLogger(boolean enabled) {
      this.formatter = new EventFormatter();
      this.enabled = enabled;
   }

   @Override
   public void collect(TraceEvent event) {
      if(enabled) {
         String thread = event.getThread();
         Object value = event.getValue();
         Object type = event.getEvent();
         String text = formatter.format(value);
         DateTime timeStamp = event.getDateTime();
         String time = timeStamp.formatDate("HH:mm:ss");
      
         if(text != null && !text.isEmpty()) {
            log.info("{} [{}] {}: {}", time, thread, type, text);
         } else {
            log.info("{} [{}] {}", time, thread, type);
         }
      }
   }
}