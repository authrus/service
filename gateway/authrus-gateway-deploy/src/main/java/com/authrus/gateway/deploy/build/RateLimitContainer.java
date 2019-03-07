package com.authrus.gateway.deploy.build;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

import com.authrus.http.proxy.balancer.identity.IdentityExtractor;
import lombok.AllArgsConstructor;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;

@AllArgsConstructor
public class RateLimitContainer implements Container {

   private final ScheduledThreadPoolExecutor executor;
   private final ResetScheduler scheduler;
   private final RateLimitFilter filter;
   private final AtomicBoolean active;
   private final Container container;
   private final long duration;

   public RateLimitContainer(IdentityExtractor extractor, Container container, long duration, long limit) {
      this.executor = new ScheduledThreadPoolExecutor(10);
      this.filter = new RateLimitFilter(extractor, limit);
      this.scheduler = new ResetScheduler(filter);
      this.active = new AtomicBoolean();
      this.container = container;
      this.duration = duration;
   }

   @Override
   public void handle(Request req, Response resp) {
      long delay = filter.throttle(req);

      if(delay > 0) {
         if(active.compareAndSet(false, true)) {
            executor.scheduleWithFixedDelay(scheduler, duration, duration, MILLISECONDS);
         }
         executor.schedule(() -> {
            container.handle(req, resp);
         }, delay, MILLISECONDS);
      } else {
         container.handle(req, resp);
      }
   }

   @AllArgsConstructor
   private static class ResetScheduler implements Runnable {

      private final RateLimitFilter filter;

      @Override
      public void run() {
         filter.reset();
      }
   }
}
