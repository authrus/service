package com.authrus.gateway.deploy.build;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WebToken {

   private final byte[] header;
   private final byte[] body;
   private final byte[] signature;

   public boolean isValid() {
      return header != null &&
             signature != null &&
             body != null;
   }

   public String getHeader() {
      return new String(header);
   }

   public String getBody() {
      return new String(body);
   }

   public String getSignature() {
      return new String(signature);
   }

   @Override
   public String toString() {
      return getBody();
   }
}
