package com.authrus.gateway.deploy.build;

import com.authrus.common.collections.Cache;
import com.authrus.common.collections.LeastRecentlyUsedCache;

public class AddressPrefixBuilder {

   private static final String IP4_CIDR_PREFIX = "\\d+\\.\\d+\\.\\d+\\.\\d+\\/\\d+";
   private static final String IP6_CIDR_PREFIX = "[a-fA-F0-9]+:[a-fA-F0-9]+:[a-fA-F0-9]+:[a-fA-F0-9]+:[a-fA-F0-9]+:[a-fA-F0-9]+:[a-fA-F0-9]+:[a-fA-F0-9]+";

   private final Cache<String, AddressPrefix> cache;

   public AddressPrefixBuilder() {
      this.cache = new LeastRecentlyUsedCache<>();
   }

   public AddressPrefix create(String token) {
      AddressPrefix prefix = cache.fetch(token);

      if(prefix == null) {
         prefix = parse(token);
         cache.cache(token, prefix);
      }
      return prefix;
   }

   private AddressPrefix parse(String token) {
      if(token != null) {
         if(token.matches(IP4_CIDR_PREFIX)) {
            String[] parts = token.split("\\.");
            byte[] prefix = new byte[parts.length];

            for(int i = 0; i < parts.length; i++) {
               prefix[i] = Byte.parseByte(parts[i]);
            }
            return new AddressPrefix(prefix);
         }
         if(token.matches(IP6_CIDR_PREFIX)) {
            String[] parts = token.split(":");
            byte[] prefix = new byte[parts.length];

            for(int i = 0; i < parts.length; i++) {
               prefix[i] = Byte.parseByte(parts[i]);
            }
            return new AddressPrefix(prefix);

         }
      }
      return new AddressPrefix(new byte[]{});
   }
}
