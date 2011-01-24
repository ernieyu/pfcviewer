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
 *  Represents a mail message block.  Cabinet items that hold mail messages
 *  are composed of a series of blocks.  Each block is marked by the string 
 *  "AOLH" at the start, and "AOLF" at the end.  The ItemBlock constructor 
 *  further parses the block into an array of subitems.
 *  @author Ernie Yu
 */
public class ItemBlock {

    public static final String START_MARK = "AOLH";
    public static final String END_MARK = "AOLF";

    private byte[] content;         // block content

    /**
     *  Constructor.
     *  @param array block contents
     */
    public ItemBlock(byte[] array) {
        content = array;
    }

    public byte[] getContent() {
        return content;
    }

    public int getLength() {
        return content.length;
    }

    /** 
     *  Parses item content into an array list of ItemBlock objects.  The
     *  block data excludes the start and end markers.  This is a static 
     *  method to allow content parsing for different item types.
     */
    public static ArrayList parseItemContent(byte[] content) {

        ArrayList blockList = new ArrayList();
        int pos = 0;

        while (pos < content.length) {
            StringBuffer startBuffer = new StringBuffer();

            // Next four bytes should be start of next block.
            for (int i = 0; i < 4; i++) {
                startBuffer.append((char)content[pos + i]);
            }

            if (startBuffer.toString().equals(START_MARK)) {
                // Next four bytes should be block length.
                int blockLen = IntUtil.toInt(content[pos + 4],
                    content[pos + 5], content[pos + 6], content[pos + 7]);

                // Create new block excluding start and end markers.
                byte[] block = new byte[blockLen - 12];

                // Read block data into separate byte array.
                for (int i = 0; i < (blockLen - 12); i++) {
                    block[i] = content[pos + 8 + i];
                }

                // Add ItemBlock object to list.
                blockList.add(new ItemBlock(block));

                // Advance to next block.
                pos += blockLen;
            }
            else {
                // Something is wrong.
                break;
            }
        }

        return blockList;
    }

}
