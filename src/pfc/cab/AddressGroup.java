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
 *  Represents an address group item.
 *  @author  Ernie Yu
 */
public class AddressGroup {
    
    private String groupName = null;
    private ArrayList emailList = new ArrayList();
    
    /** Creates a new instance of AddressGroup 
     *  @param content contents of CabinetItem for address group
     */
    public AddressGroup(byte[] content) {
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
                        groupName = new String(data);
                        break;
                      case 2:
                        // Parse data into list of email addresses.
                        String emails = new String(data);
                        String delimiter = "\015\012";
                        int start = 0;
                        int end = emails.indexOf(delimiter, start);
                        while (end >= 0) {
                            emailList.add(emails.substring(start, end));
                            start = end + delimiter.length();
                            if (start >= emails.length()) { break; }
                            end = emails.indexOf(delimiter, start);
                        }
                        if (start < emails.length()) {
                            emailList.add(emails.substring(start));
                        }
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
    public String getGroupName() {
        return groupName;
    }

    public ArrayList getEmailList() {
        return emailList;
    }

    /**
     *  Returns header string for display in the viewer.
     */
    public String toHeadString() {
        StringBuffer buf = new StringBuffer();
        String separator = System.getProperty("line.separator");
        buf.append("Group Name: ").append(groupName).append(separator);
        return buf.toString();
    }
    
    /**
     *  Returns body text for display in the viewer.
     */
    public String toTextString() {
        StringBuffer buf = new StringBuffer();
        String separator = System.getProperty("line.separator");
        for (int i = 0; i < emailList.size(); i++) {
            buf.append((String)emailList.get(i)).append(separator);
        }
        return buf.toString();
    }
    
}
