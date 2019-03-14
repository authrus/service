package com.authrus.gateway.deploy.build;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.authrus.common.collections.Cache;
import com.authrus.common.collections.LeastRecentlyUsedCache;

public class AddressPrefixBuilder {

   private static final String IP4_CIDR_PREFIX = "(\\.\\d+\\.\\d+\\.\\d+)\\/(\\d+)";
   private static final String IP6_CIDR_PREFIX = "([a-fA-F0-9]*:[a-fA-F0-9]*:[a-fA-F0-9]*:[a-fA-F0-9]*:[a-fA-F0-9]*:[a-fA-F0-9]*:[a-fA-F0-9]*:[a-fA-F0-9]*)\\/(\\d+)";

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
      AddressPrefix prefix = parse(token, IP4_CIDR_PREFIX, "\\.", false);

      if(prefix == null) {
         return parse(token, IP6_CIDR_PREFIX, ":", true);
      }
      return null;
   }

   private AddressPrefix parse(String token, String expression, String delimeter, boolean decode) {
      if(token != null) {
         Pattern pattern = Pattern.compile(expression);
         Matcher matcher = pattern.matcher(token);

         if(matcher.matches()) {
            String text = matcher.group(1); // the I.P address prefix
            String size = matcher.group(2); // the number of bits in the prefix
            String[] parts = text.split(delimeter);
            int[] prefix = new int[parts.length]; // consider using a BitSet....
            int count = Integer.parseInt(size);

            for(int i = 0; i < parts.length; i++) {
               if(decode) {
                  prefix[i] = Integer.parseInt(parts[i]);
               } else {
                  prefix[i] = Integer.decode("0x" + parts[i]);
               }
            }
            return new AddressPrefix(prefix, count);
         }
      }
      return null;
   }
}
