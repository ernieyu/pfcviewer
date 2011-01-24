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
 *  Represents an entry in the Filing Cabinet.  All entries are either
 *  envelopes or data records.  All envelopes are 134 bytes long, and 
 *  contain several fields and pointers, including a pointer to its
 *  corresponding data record.  There are two basic envelope types:
 *  folders with a data pointer of zero, and message envelopes that 
 *  serve as links to their corresponding data items.  (Exception:
 *  the root folder has a non-zero data pointer.)  Data records hold
 *  actual content like mail message, favorites, and address book 
 *  information.
 *  @author Ernie Yu
 */
public class CabinetItem {

    // type constants
    public static final int UNKNOWN = 0;
    public static final int FOLDER = 1;
    public static final int FAVE_ENVELOPE = 2;
    public static final int FILE_FOLDER = 3;
    public static final int FILE_ENVELOPE = 6;
    public static final int FLASH_ENVELOPE = 9;
    public static final int MAIL_ENVELOPE = 12;
    public static final int ADDR_ENVELOPE = 17;
    public static final int GROUP_ENVELOPE = 18;
    public static final int POST_ENVELOPE = 20;
    public static final int MAIL_DATA = 100;
    public static final int ADDR_DATA = 101;
    
    // flag constants
    public static final byte MAIL_SEEN = 0x01;
    public static final byte MAIL_SENT = 0x04;

    private int index;              // position in index table
    private int address;            // record address in cabinet
    private int type;               // record type
    private boolean envelope;       // true if record is envelope
    private boolean folder;         // true if record is folder
    private boolean sysFolder;      // true if record is system folder
    private byte[] content;         // record content
    private byte flags;             // mail message flags

    // Pointers to other records.
    private int data;               // pointer to data record
    private int next;               // pointer to next envelope
    private int prev;               // pointer to previous envelope
    private int parent;             // pointer to parent envelope
    private int child;              // pointer to child envelope

    /**
     *  Constructor.
     *  @param array contents of cabinet item
     */
    public CabinetItem(byte[] array) {
        content = array;

        // Parse content, and set type and pointers.
        type = UNKNOWN;
        envelope = false;
        folder = false;
        sysFolder = false;

        // Get first four bytes as characters.
        // Check content length since AOL 9 contains short items. (12/30/06)
        StringBuffer header = new StringBuffer();
        for (int k = 0; k < Math.min(content.length, 4); k++) {
            header.append((char)content[k]);
        }

        // Check for data subrecord header
        if (header.toString().equals("AOLH")) {
            // Get first subitem; try to determine if mail or address
            short a = IntUtil.toShort(content[8], content[9]);
            byte b = content[10];
            short c = IntUtil.toShort(content[11], content[12]);
            if (a == 1) {
                if ((b == '\002') && (c == 0)) {
                    type = MAIL_DATA;
                }
                else if (b == '\005') {
                    type = ADDR_DATA;
                }
            }
        }
        // Check for envelope record
        else if (content.length == 126) {
            envelope = true;

            short a = IntUtil.toShort(content[0], content[1]);
            short b = IntUtil.toShort(content[2], content[3]);

            if ((b & 0x0001) == 0x0001) {
                folder = true;
                type = FOLDER;
            }
            if ((b & 0x0100) == 0x0100) {
                sysFolder = true;
            }
            
            switch (a) {
                case 2:
                    type = FAVE_ENVELOPE;
                    break;
                case 3: case 4:
                    type = FILE_FOLDER;
                    break;
                case 5: case 6:
                    type = FILE_ENVELOPE;
                    break;
                case 9:
                    type = FLASH_ENVELOPE;
                    break;
                case 7: case 8: case 12:
                    type = MAIL_ENVELOPE;
                    break;
                case 14: case 15: case 20:
                    type = POST_ENVELOPE;
                    break;                    
                case 17:
                    type = ADDR_ENVELOPE;
                    break;
                case 18:
                    type = GROUP_ENVELOPE;
                    break;
                default:
                    // not significant for now
            }

            // Get mail message flags.
            flags = content[14];

            // Get pointers to other records in index.
            data = IntUtil.toInt(content[106], content[107],
                content[108], content[109]);
            next = IntUtil.toInt(content[110], content[111],
                content[112], content[113]);
            prev = IntUtil.toInt(content[114], content[115],
                content[116], content[117]);
            parent = IntUtil.toInt(content[118], content[119],
                content[120], content[121]);
            child = IntUtil.toInt(content[122], content[123],
                content[124], content[125]);
        }
    }

    public void setIndex(int idx) {
        index = idx;
    }

    public int getIndex() {
        return index;
    }

    public void setAddress(int addr) {
        address = addr;
    }

    public int getAddress() {
        return address;
    }

    public int getLength() {
        return content.length;
    }

    public int getType() {
        return type;
    }

    public boolean isEnvelope() {
        return envelope;
    }

    public boolean isFolder() {
        return folder;
    }

    public boolean isSysFolder() {
        return sysFolder;
    }

    public byte[] getContent() {
        return content;
    }

    public byte getFlags() {
        return flags;
    }
    
    public boolean isOutgoing() {
        if ((flags & MAIL_SENT) == MAIL_SENT) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *  Returns index pointer to data record.
     */
    public int getData() {
        return data;
    }

    public int getNext() {
        return next;
    }

    public int getPrev() {
        return prev;
    }

    public int getParent() {
        return parent;
    }

    public int getChild() {
        return child;
    }

    /**
     *  Returns envelope label for item, or empty string if not an envelope.
     */
    public String toString() {
        if (envelope) {
            StringBuffer label = new StringBuffer();
            for (int i = 18; i < (content.length - 18); i++) {
                if (content[i] != 0)
                    label.append((char)content[i]);
                else
                    break;
            }
            return label.toString();
        }
        else {
            return (new String(""));
        }
    }
}
