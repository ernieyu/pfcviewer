/*
 * Copyright (c) 2002 Ernest Yu. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to 
 * deal in the Software without restriction, including without limitation the 
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or 
 * sell copies of the Software, and to permit persons to whom the Software is 
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package pfc.cab;

/**
 *  Collection of static utility methods for working with int and short 
 *  variables.
 *  @author Ernie Yu
 */
public class IntUtil {

    /**
     *  Reverses byte order in 4-byte int for little-endian values.
     */
    public static int reverseInt(int i) {
        int byte0 = i << 24;
        int byte1 = ((i >>> 8) << 24) >>> 8;
        int byte2 = ((i >>> 16) << 24) >>> 16;
        int byte3 = i >>> 24;
        return (byte0 | byte1 | byte2 | byte3);
    }

    /**
     *  Reverses byte order in 2-byte short for little-endian values.
     */
    public static short reverseShort(short i) {
        int hiByte = (i << 24) >> 16;
        int loByte = (i << 16) >>> 24;
        return (short)(hiByte | loByte);
    }

    /**
     *  Constructs int given four bytes.  Byte order is little-endian.
     */
    public static int toInt(byte byte0, byte byte1, byte byte2, byte byte3) {
        int b0 = byte3 << 24;
        int b1 = (byte2 << 24) >>> 8;
        int b2 = (byte1 << 24) >>> 16;
        int b3 = (byte0 << 24) >>> 24;
        return (b0 | b1 | b2 | b3);
    }

    /**
     *  Constructs short given two bytes.  Byte order is little-endian.
     */
    public static short toShort(byte byte0, byte byte1) {
        int hiByte = byte1 << 8;
        int loByte = (byte0 << 24) >>> 24;
        return (short)(hiByte | loByte);
    }

    /**
     *  Converts int value to string.  Result is padded with leading
     *  zeros to fill out length.
     */
    public static String padIntString(int value, int length) {
        String valueText = Integer.toString(value);
        StringBuffer result = new StringBuffer(valueText);
        for (int i = 0; i < (length - valueText.length()); i++) {
            result.insert(0, " ");
        }
        return result.toString();
    }

    /**
     *  Converts int value to hex string.  Result is padded with leading
     *  zeros to fill out digits.
     */
    public static String toHexString(int i, int digits) {
        String hexString = Integer.toHexString(i);
        int len = hexString.length();

        StringBuffer result = new StringBuffer();
        for (int j = 0; j < digits; j++) {
            if (len < (digits - j))
                result.append("0");
            else
                result.append(hexString.charAt(len - digits + j));
        }
        return result.toString();
    }
}
