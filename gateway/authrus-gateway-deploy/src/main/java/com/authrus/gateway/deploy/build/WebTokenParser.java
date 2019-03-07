package com.authrus.gateway.deploy.build;

import java.util.Base64;
import java.util.Base64.Decoder;

public class WebTokenParser {

   private final Decoder decoder;

   public WebTokenParser() {
      this.decoder = Base64.getDecoder();
   }

   public WebToken parse(String token) {
      if(token != null) {
         int first = token.indexOf('.');

         if (first != -1) {
            int length = token.length();
            int last = token.indexOf('.', first + 1);

            if (last > first) {
               String header = token.substring(0, first);
               String body = token.substring(first + 1, last);
               String signature = token.substring(last + 1, length);
               byte[] headerData = decoder.decode(header);

               if(headerData[0] == '{') {
                  byte[] bodyData = decoder.decode(body);
                  byte[] signatureData = decoder.decode(signature);

                  return new WebToken(headerData, bodyData, signatureData);
               }
            }
         }
      }
      return null;
   }
}
