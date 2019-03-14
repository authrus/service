package com.authrus.gateway.deploy.build;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AddressPrefix {

   private final byte[] prefix;

   public boolean match(InetSocketAddress address) {
      InetAddress source = address.getAddress();
      byte[] parts = source.getAddress();

      if(parts.length >= prefix.length) {
         for(int i = 0; i < prefix.length; i++) {
            if(parts[i] != prefix[i]) {
               return false;
            }
         }
      }
      return true;
   }
}
