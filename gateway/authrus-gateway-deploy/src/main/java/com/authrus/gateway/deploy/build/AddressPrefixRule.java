package com.authrus.gateway.deploy.build;

import java.net.InetSocketAddress;
import java.util.List;

import com.authrus.http.proxy.security.AccessRule;
import org.simpleframework.http.Request;

public class AddressPrefixRule implements AccessRule {

   private final AddressPrefixBuilder builder;
   private final List<String> tokens;

   public AddressPrefixRule(List<String> tokens) {
      this.builder = new AddressPrefixBuilder();
      this.tokens = tokens;
   }

   @Override
   public boolean isAllowed(Request request) {
      InetSocketAddress address = request.getClientAddress();

      for(String token : tokens) {
         AddressPrefix prefix = builder.create(token);

         if(prefix.match(address)) {
            return true;
         }
      }
      return false;
   }
}
