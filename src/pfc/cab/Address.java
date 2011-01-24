/*
 * Copyright (c) 2003 Ernest Yu. All rights reserved.
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

import java.util.*;

/**
 *  Represents an address book item.
 *  @author  Ernie Yu
 */
public class Address {
    
    private String firstName = null;
    private String lastName = null;
    private String email1 = null;
    private String remarks = null;
    
    /** Creates a new instance of Address 
     *  @param content contents of CabinetItem for address
     */
    public Address(byte[] content) {
        // Parse item content into blocks.
        ArrayList blockList = ItemBlock.parseItemContent(content);

        // Loop through all item blocks.
        for (int i = 0; i < blockList.size(); i++) {
            ItemBlock block = (ItemBlock)blockList.get(i);
            int blockLen = block.getLength();

            // Get all subitems.
            ArrayList subItems = BlockSubItem.parseBlockContent(
                block.getContent());

            short extType = -1;

            // Loop through all subitems.
            for (int j = 0; j < subItems.size(); j++) {
                BlockSubItem subItem = (BlockSubItem)subItems.get(j);
                byte[] data = subItem.getContent();

                if (data.length > 0) {
                    // Process standard subitem types.
                    switch (subItem.getId()) {
                      case 1:
                        firstName = new String(data);
                        break;
                      case 2:
                        lastName = new String(data);
                        break;
                      case 3:
                        email1 = new String(data);
                        break;
                      case 4:
                        remarks = new String(data);
                        break;
                      default:
                        // Throw other data away.
                    }
                }
                // Process extended subitem types.
                switch (subItem.getId()) {
                  case 12:
                    extType = IntUtil.toShort(data[0], data[1]);
                    break;
                  case 13:
                    // Extended data unknown, not processed.
                    extType = -1;   // Reset extended type indicator
                    break;
                  default:
                    // Throw other data away.
                }
            }   // end of loop through subitems.
        }       // end of loop through item blocks.
    }

    // Accessor methods.
    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return firstName;
    }
    
    public String getEmail1() {
        return email1;
    }
    
    public String getRemarks() {
        return remarks;
    }

    /**
     *  Returns header string for display in the viewer.
     */
    public String toHeadString() {
        StringBuffer buf = new StringBuffer();
        String separator = System.getProperty("line.separator");
        buf.append("First Name: ").append(firstName).append(separator);
        buf.append("Last Name: ").append(lastName);
        return buf.toString();
    }
    
    /**
     *  Returns body text for display in the viewer.
     */
    public String toTextString() {
        return email1;
    }
    
}
