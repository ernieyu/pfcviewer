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
 *  Represents a Favorite.  Contains the URL for a web page.
 *  @author Ernie Yu
 */
public class Favorite {
    
    private String url = null;
    
    /** Constructor.  Creates a new instance of Favorite.
     *  @param content contents of CabinetItem for favorite
     */
    public Favorite(byte[] content) {
        StringBuffer buffer = new StringBuffer();
        // Content is URL string.
        for (int i = 0; i < content.length; i++) {
            if (content[i] == 0) { break; }
            buffer.append((char)content[i]);
        }
        url = buffer.toString();
    }

    /** Returns the URL for the favorite.
     */
    public String getUrl() {
        return url;
    }
    
}
