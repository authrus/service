package com.authrus.gateway.deploy.build;

import java.util.concurrent.atomic.AtomicLong;

import com.authrus.common.collections.Cache;
import com.authrus.common.collections.CopyOnWriteCache;
import com.authrus.http.proxy.balancer.identity.IdentityExtractor;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

public class RateLimitFilter {

   private final Cache<String, AtomicLong> rates;
   private final IdentityExtractor extractor;
   private final long limit;
   private final long delay;

   public RateLimitFilter(IdentityExtractor extractor, long limit) {
      this(extractor, limit, 100);
   }

   public RateLimitFilter(IdentityExtractor extractor, long limit, long delay) {
      this.rates = new CopyOnWriteCache<String, AtomicLong>();
      this.extractor = extractor;
      this.limit = limit;
      this.delay = delay;
   }

   public long throttle(Request request) {
      String identity = extractor.extractIdentity(request);

      if(identity != null) {
         AtomicLong rate = rates.fetch(identity);

         if(rate == null) {
            rate = new AtomicLong(limit);
            rates.cache(identity, rate);
         }
         long result = rate.getAndDecrement();

         if(result <= 0) {
            return delay;
         }
      }
      return 0;
   }

   public void reset() {
      rates.clear();
   }
}
