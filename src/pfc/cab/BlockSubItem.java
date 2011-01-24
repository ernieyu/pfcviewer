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

import java.util.*;

/**
 *  Represents a subitem in a mail message block.
 *  @author Ernie Yu
 */
public class BlockSubItem {

    private short id;
    private byte type;
    private byte[] content;

    /**
     *  Constructor.
     */
    public BlockSubItem(short id, byte type, byte[] array) {
        this.id = id;
        this.type = type;
        this.content = array;
    }

    public short getId() {
        return id;
    }

    public byte getType() {
        return type;
    }

    public byte[] getContent() {
        return content;
    }

    /** 
     *  Parses block content into an array list of BlockSubItem objects.  
     *  This is a static method to allow content parsing for different 
     *  blocks.
     */
    public static ArrayList parseBlockContent(byte[] content) {
        
        ArrayList subItems = new ArrayList();
        int pos = 0;

        while (pos < content.length) {
            // Next two bytes should be subitem id.
            short subId = IntUtil.toShort(content[pos], content[pos + 1]);
            // Next byte is subitem type.
            byte subType = content[pos + 2];
            // Determine subitem position and length.
            int subOffset;
            int subLength;
            switch (subType) {
                case 1:
                    subOffset = 3;
                    subLength = 1;
                    break;
                case 2: case 3:
                    subOffset = 3;
                    subLength = 2;
                    break;
                case 4:
                    subOffset = 3;
                    subLength = 4;
                    break;
                default:
                    subOffset = 7;
                    subLength = IntUtil.toInt(content[pos + 3],
                        content[pos + 4], content[pos + 5], content[pos + 6]);
            }
            // Get subitem content.
            byte[] subContent = new byte[subLength];
            for (int i = 0; i < subLength; i++) {
                subContent[i] = content[pos + subOffset + i];
            }

            // Create subitem object and add to array.
            BlockSubItem subItem = new BlockSubItem(
                subId, subType, subContent);
            subItems.add(subItem);

            // Advance to next subitem.
            pos += (subOffset + subLength);
        }
        
        return subItems;
    }
    
}
