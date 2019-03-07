package com.authrus.gateway.deploy.build;

import java.util.concurrent.atomic.AtomicInteger;

import com.authrus.http.proxy.balancer.identity.IdentityExtractor;
import com.authrus.http.proxy.security.AccessRule;
import lombok.AllArgsConstructor;
import org.simpleframework.http.Request;

@AllArgsConstructor
public class WebTokenRule implements AccessRule {

   private final IdentityExtractor extractor;
   private final WebTokenParser parser;
   private final AtomicInteger permits;

   public WebTokenRule(IdentityExtractor extractor) {
      this(extractor, 10);
   }

   public WebTokenRule(IdentityExtractor extractor, int limit) {
      this.permits = new AtomicInteger(limit);
      this.parser = new WebTokenParser();
      this.extractor = extractor;
   }

   @Override
   public boolean isAllowed(Request request) {
      try {
         String identity = extractor.extractIdentity(request);
         WebToken token = parser.parse(identity);

         if(token != null) {
            if(!token.isValid()) {
               permits.decrementAndGet();
            }
         }
      } catch(Exception e) {
         permits.decrementAndGet();
      }
      return permits.get() > 0;
   }
}
