package org.jboss.seam.example.webassoc.util;

import java.io.IOException;

public class ObjectSerializer
{

   static char[] s_hexDigit =
      {
         '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
         'e', 'f'
      };

   static public String byteToHex(byte b)
   {

      // Returns hex String representation of byte b
      char[] array = {s_hexDigit[(b >> 4) & 0x0f], s_hexDigit[b & 0x0f]};

      return new String(array);

   }

   static public byte hexToByte(String hexStr, int pos)
   {

      char leadChar = hexStr.charAt(pos);
      char secChar = hexStr.charAt(pos + 1);
      byte leadByte = Byte.decode("0x0" + leadChar).byteValue();
      byte secByte = Byte.decode("0x0" + secChar).byteValue();
      leadByte = (byte)((leadByte << 4) | secByte);

      return leadByte;

   }

   public static String serializeToString(String obj)
           throws IOException
   {

      byte[] bytes = obj.getBytes();
      StringBuffer buf = new StringBuffer();

      for (int i = 0; i < bytes.length; ++i)
      {

         buf.append(byteToHex(bytes[i]));

      }

      return buf.toString();

   }

}