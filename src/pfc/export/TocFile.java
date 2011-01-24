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

package pfc.export;

import java.io.*;
import java.util.Date;
import pfc.cab.*;

/**
 *  Represents mbox table of contents file for Eudora.
 *
 * @author  Ernie Yu
 */
public class TocFile {

    private File tocFile;
    private FileOutputStream out;
    private String mboxName;
    private short msgCount;
    
    /** Creates a new instance of TocFile.
     *  @param mboxFile mailbox file
     */
    public TocFile(File mboxFile) {
        mboxName = mboxFile.getName();
        // Remove mailbox file extension.
        int lastDot = mboxName.lastIndexOf(".");
        if (lastDot > 0) {
            mboxName = mboxName.substring(0, lastDot);
        }
        
        // Set File object for toc file.
        tocFile = new File(mboxFile.getParent(), mboxName + ".toc");
        out = null;
        msgCount = 0;
    }
    
    /** Writes TOC file header.  The header is always 104 bytes long.
     */
    public void writeHeader() {
        try {
            // Create output stream if necessary.
            if (out == null) {
                out = new FileOutputStream(tocFile);
            }

            // Write all header bytes.
            out.write(0x30);                    // 0-7: file id
            out.write(0x00);
            out.write(0x00);
            out.write(0x00);
            out.write(0x00);
            out.write(0x00);
            out.write(0x00);
            out.write(0x00);
        
            byte[] byteBuf = mboxName.getBytes();   // 8-39: mailbox name
            for (int i = 0; i < 32; i++) {
                if (i < byteBuf.length) {
                    out.write(byteBuf[i]);
                } else {
                    out.write(0x00);
                }
            }
        
            out.write(0x03);                    // 40-41: mailbox type
            out.write(0x00);
            out.write(0x00);
            out.write(0x00);

            out.write(0x00);                    // 44: mailbox class
            out.write(0x00);

            out.write(0x00);                    // 46: mailbox coordinates
            out.write(0x00);
            out.write(0x00);
            out.write(0x00);
            out.write(0x00);
            out.write(0x00);
            out.write(0x00);
            out.write(0x00);
        
            for (int i = 54; i < 70; i++) {
                out.write(0xff);
            }

            out.write(0x02);                    // 70-73:
            out.write(0x00);
            out.write(0x02);
            out.write(0x00);
        
            for (int i = 74; i < 102; i++) {
                out.write(0x00);
            }

            out.write(shortToByte(msgCount));   // 102-103: message count
        }
        catch (IOException iox) {
            System.out.println(iox);
        }
    }
    
    /** Writes TOC message entry.  The entry is always 218 bytes long.
     */
    public void writeMessage(CabinetItem item, int offset, int length) {
        int dateTime = 0;
        short status = 1;       // mark message read
        byte option1 = 0;
        byte option2 = 0;
        short priority = 3;
        String ascTime;
        
        // Create mail message to parse item contents.
        MailMessage message = new MailMessage(item.getContent());
        // Create date string in ASCII time format.
        Date date = message.getDate();
        if (date != null) {
            dateTime = (int)(date.getTime() / 1000);
            ascTime = MboxMailExporter.toAsctime(date);
        } else {
            ascTime = message.getDateString();
        }
        
        String from = message.getFrom();
        String subject = message.getSubject();
        
        // Write message entry to TOC file.
        try {
            out.write(intToByte(offset));           // 0-3: mbox start
            out.write(intToByte(length));           // 4-7: message length
            out.write(intToByte(dateTime));         // 8-11: date/time
            out.write(shortToByte(status));         // 12-13: status
            out.write(option1);
            out.write(option2);
            out.write(shortToByte(priority));       // 16-17: priority
        
            byte[] byteBuf = ascTime.getBytes();    // 18-49: ascii time
            for (int i = 0; i < 32; i++) {
                if (i < byteBuf.length) {
                    out.write(byteBuf[i]);
                } else {
                    out.write(0x00);
                }
            }
        
            byteBuf = from.getBytes();              // 50-113: from field
            for (int i = 0; i < 64; i++) {
                if (i < byteBuf.length) {
                    out.write(byteBuf[i]);
                } else {
                    out.write(0x00);
                }
            }
        
            byteBuf = subject.getBytes();           // 114-177: subject field
            for (int i = 0; i < 64; i++) {
                if (i < byteBuf.length) {
                    out.write(byteBuf[i]);
                } else {
                    out.write(0x00);
                }
            }

            out.write(0xff);                        // 178-185
            out.write(0xff);
            out.write(0xff);
            out.write(0xff);
            out.write(0xff);
            out.write(0xff);
            out.write(0xff);
            out.write(0xff);
            
            for (int i = 186; i < 218; i++) {
                out.write(0x00);
            }
            
            // Increment message count.
            msgCount++;
        }
        catch (IOException iox) {
            System.out.println(iox);
        }
    }
    
    /** Closes TOC file.
     */
    public void close() {
        // Close file output stream.
        try {
            out.close();
        }
        catch (IOException iox) {
            System.out.println(iox);
        }
        
        // Reopen file for random access; save message count.
        RandomAccessFile toc = null;
        try {
            toc = new RandomAccessFile(tocFile, "rw");
            toc.seek(102);
            toc.write(shortToByte(msgCount));
        }
        catch (IOException iox) {
            System.out.println(iox);
        }
        finally {
            if (toc != null) { 
                try { toc.close(); } 
                catch (IOException iox) {}
            }
        }
    }
        
    /** Closes output stream if still open.  Overrides method in Object 
     *  class.
     */
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }
    
    /** Converts short value to little-endian byte array.
     */
    private byte[] shortToByte(short value) {
        byte[] result = new byte[2];
        result[0] = (byte)((value << 8) >>> 8);
        result[1] = (byte)(value >>> 8);
        return result;
    }
    
    /** Converts int value to little-endian byte array.
     */
    private byte[] intToByte(int value) {
        byte[] result = new byte[4];
        result[0] = (byte)((value << 24) >>> 24);
        result[1] = (byte)((value << 16) >>> 24);
        result[2] = (byte)((value << 8) >>> 24);
        result[3] = (byte)(value >>> 24);
        return result;
    }
}

