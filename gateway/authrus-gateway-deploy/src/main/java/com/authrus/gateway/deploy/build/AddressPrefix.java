package com.authrus.gateway.deploy.build;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AddressPrefix {

   private final int[] prefix;

   public boolean match(InetSocketAddress address) {
      InetAddress source = address.getAddress();
      byte[] parts = source.getAddress();

      if(parts.length >= prefix.length) {
         for(int i = 0; i < prefix.length; i++) {
            int part = parts[i] & 0xff;

            if(part != prefix[i]) {
               return false;
            }
         }
      }
      return true;
   }
}
