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

import java.text.*;
import java.util.*;
import java.util.zip.*;

/** 
 *  Contains all of the components of an email message.  The contents of
 *  a cabinet item are parsed by the main constructor to retrieve the
 *  mail components.
 *
 *  @author Ernie Yu
 */
public class MailMessage {

    public static final String HEADER_LINE = 
        "----------------------- Headers --------------------------------";
    
    // standard fields
    private String dateString;
    private String from;
    private String to;
    private String cc;
    private String bcc;
    private String replyTo;
    private String recipient;
    private String subject;
    private String body;

    // AOL fields
    private byte[] check;
    private String screenname;
    private boolean external;
    private boolean embedded;
    private int dateTime;
    private String attachment;
    private String postHeader;
    private String mailHeader;
    private String oldAddress;
    private String oldData;
    private String picture;
    private String attLocation;
    private String binLocation;

    /** 
     *  Constructor.
     *  @param content contents of CabinetItem for mail message.
     */
    public MailMessage(byte[] content) {
        // Parse message content into blocks.
        ArrayList blockList = ItemBlock.parseItemContent(content);
        
        StringBuffer bodyBuffer = new StringBuffer();
        StringBuffer headBuffer = new StringBuffer();
        Inflater inflater = new Inflater(true);
        boolean v7Message = false;
        boolean oldHeader = false;
        boolean firstData = true;

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
                      case 3:
                        check = data;
                        break;
                      case 5:
                        dateString = new String(data);
                        break;
                      case 6:
                        from = new String(data);
                        break;
                      case 7:
                        to = new String(data);
                        break;
                      case 8:
                        cc = new String(data);
                        break;
                      case 9:
                        bcc = new String(data);
                        break;
                      case 10:
                        subject = new String(data);
                        break;
                      case 11:
                        screenname = new String(data);
                        break;
                      case 16:
                        replyTo = new String(data);
                        break;
                      case 17:
                        recipient = new String(data);
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
                    switch (extType) {
                      case 0:
                        if (!v7Message) {
                            if (isHeaderString(data)) {
                                oldHeader = true;
                                // Sometimes, header text is immediately
                                // after the header line.
                                headBuffer.append(getHeaderText(data));
                            }
                            else if (oldHeader) {
                                // Sometimes, additional header text is 
                                // contained in a separate block.
                                headBuffer.append(new String(data));
                            }
                            else {
                                // pre-V7 body text
                                bodyBuffer.append(new String(data));
                            }
                        }
                        break;
                      case 1:
                        // Attachment file name
                        StringBuffer buffer = new StringBuffer();
                        for (int k = 14; k < data.length; k++) {
                            if (data[k] == '\000') { break; }
                            buffer.append((char)data[k]);
                        }
                        attachment = buffer.toString();
                        break;
                      case 5:
                        // V7 header
                        headBuffer.append(toV7String(data));
                        break;
                      case 256:
                        // V7 body text - skip first piece
                        if (!firstData) {
                            bodyBuffer.append(unpackData(data, inflater));
                        }
                        firstData = false;
                        break;
                      case 257:
                        // Start of V7 header
                        v7Message = true;
                        break;
                      case 260:
                        // End of V7 header
                        v7Message = false;
                        break;
                      default:
                        // Throw other extended data away.
                    }
                    extType = -1;   // Reset extended type indicator
                    break;
                  default:
                    // Throw other data away.
                }
            }   // end of loop through subitems.
        }       // end of loop through item blocks.
        
        inflater.end();
        body = bodyBuffer.toString();
        mailHeader = headBuffer.toString();
        if (from == null) { from = screenname; }
    }

    // Accessor methods.
    public String getDateString() {
        return dateString;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getCc() {
        return cc;
    }

    public String getBcc() {
        return bcc;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }
    
    public String getAttachment() {
        return attachment;
    }

    /** 
     *  Returns true if body text includes &lt;html&gt; tag.
     */
    public boolean isHtml() {
        int htmlStart = body.indexOf("<html>");
        if (htmlStart < 0) { htmlStart = body.indexOf("<HTML>"); }
        if (htmlStart < 0) {
            return false;
        } else {
            return true;
        }
    }
    
    /**
     *  Returns mail header.  If the message content does not include a
     *  header, then the header is manufactured from other fields.
     */
    public String getMailHeader() {
        if ((mailHeader != null) && (mailHeader.trim().length() > 0)) {
            return mailHeader;
        }
        else {
            StringBuffer buffer = new StringBuffer();
            String separator = System.getProperty("line.separator");
            buffer.append("Date: ").append(formatHeaderDate()).append(separator);
            buffer.append("To: ").append(to).append(separator);
            buffer.append("From: ").append(from).append(separator);
            buffer.append("Subject: ").append(subject).append(separator);
            return buffer.toString();
        }
    }
    
    /**
     *  Returns body text of mail message.  Converts &lt;BR&gt; tags and
     *  character entities to text if they are not within an actual HTML
     *  document.  AOL inserts these for display in its HTML-enabled window.
     */
    public String getBodyText() {
        StringBuffer buffer = new StringBuffer(body);
        String separator = System.getProperty("line.separator");
        boolean htmlDoc = false;
        int pos = 0;
        // Move through body text.
        while (pos < buffer.length()) {
            String token = nextToken(buffer, pos);
            if (token != null) {
                if (htmlDoc) {
                    if (token.equalsIgnoreCase("</html>")) {
                        htmlDoc = false;
                    }
                }
                else {
                    if (token.equalsIgnoreCase("<html>")) {
                        htmlDoc = true;
                    }
                    else if (token.equalsIgnoreCase("<BR>")) {
                        buffer.delete(pos, pos + token.length());
                        buffer.insert(pos, separator);
                    }
                    else if (token.equalsIgnoreCase("&nbsp;")) {
                        buffer.delete(pos, pos + token.length());
                        buffer.insert(pos, " ");
                    }
                    else if (token.equalsIgnoreCase("&amp;")) {
                        buffer.delete(pos, pos + token.length());
                        buffer.insert(pos, "&");
                    }
                    else if (token.equalsIgnoreCase("&lt;")) {
                        buffer.delete(pos, pos + token.length());
                        buffer.insert(pos, "<");
                    }
                    else if (token.equalsIgnoreCase("&gt;")) {
                        buffer.delete(pos, pos + token.length());
                        buffer.insert(pos, ">");
                    }
                }
            }
            pos++;
        }
        // Return converted text.
        return buffer.toString();
    }
    
    /**
     *  Returns next token if it is an HTML tag or character entity that we
     *  are searching for.
     */
    private String nextToken(StringBuffer buffer, int pos) {
        try {
            char chr = buffer.charAt(pos);
            if (chr == '<') {
                if (buffer.substring(pos, pos + 4).equalsIgnoreCase("<BR>")) {
                    return buffer.substring(pos, pos + 4);
                }
                else if (buffer.substring(pos, pos + 6).equalsIgnoreCase("<html>")) {
                    return buffer.substring(pos, pos + 6);
                }
                else if (buffer.substring(pos, pos + 7).equalsIgnoreCase("</html>")) {
                    return buffer.substring(pos, pos + 7);
                }
            }
            else if (chr == '&') {
                if (buffer.substring(pos, pos + 6).equalsIgnoreCase("&nbsp;")) {
                    return buffer.substring(pos, pos + 6);
                }
                if (buffer.substring(pos, pos + 5).equalsIgnoreCase("&amp;")) {
                    return buffer.substring(pos, pos + 5);
                }
                if (buffer.substring(pos, pos + 4).equalsIgnoreCase("&lt;")) {
                    return buffer.substring(pos, pos + 4);
                }
                if (buffer.substring(pos, pos + 4).equalsIgnoreCase("&gt;")) {
                    return buffer.substring(pos, pos + 4);
                }
            }
        } 
        catch (StringIndexOutOfBoundsException ex) { 
            // Do nothing; same as no token found.
        }
        // Nothing found so return null.
        return null;
    }
    
    /**
     *  Parses AOL date string and returns Date object.  Returns null if
     *  the date string cannot be parsed.  The date string is known to take
     *  one of three forms: <BR>
     *  a)  12/2/2001 6:18:53 PM Eastern Standard Time <BR>
     *  b)  12/2/01 <BR>
     *  c)  01-12-02 18:18:53 EST
     */
    public Date getDate() {
        Date date = null;
        // Create formatter with format pattern.
        SimpleDateFormat format = new SimpleDateFormat("M/d/yy h:m:s a z");

        // Try parsing with first date format pattern.
        try {
            date = format.parse(dateString);
        }
        catch (ParseException pex) { date = null; }

        if (date == null) {
            // Try parsing with second date format pattern.
            try {
                format.applyPattern("M/d/yy");
                date = format.parse(dateString);
            }
            catch (ParseException pex) { date = null; }

            // Try parsing with third date format pattern.
            if (date == null) {
                try {
                    format.applyPattern("yy-MM-dd HH:mm:ss z");
                    date = format.parse(dateString);
                }
                catch (ParseException pex) { date = null; }
            }
        }

        return date;
    }

    /**
     *  Converts date to mail header date format.  The result string is of 
     *  the form: Sun, 8 Dec 2002 13:59:59 -0500.
     */
    private String formatHeaderDate() {
        // Create formatter with output date pattern.
        SimpleDateFormat format = new SimpleDateFormat(
            "EEE, d MMM yyyy HH:mm:ss Z");
        Date date = getDate();
        if (date != null) {
            return format.format(date);
        } else {
            return dateString;
        }
    }

    /**
     *  Returns four-line header string for display in the viewer.
     */
    public String toHeadString(boolean outgoing) {
        StringBuffer buf = new StringBuffer();
        String separator = System.getProperty("line.separator");
        buf.append("Date: ").append(dateString).append(separator);
        if (outgoing) {
            buf.append("To: ").append(to).append(separator);
        } else {
            buf.append("From: ").append(from).append(separator);
        }
        buf.append("Subject: ").append(subject).append(separator);
        if (attachment != null) {
            buf.append("Attachment: ").append(attachment);
        }
        return buf.toString();
    }
    
    /**
     *  Returns mail body text for display in the viewer.  If selected,
     *  the complete mail header is appended to the end of the returned 
     *  string.
     */
    public String toTextString(boolean showHeader) {
        StringBuffer buf = new StringBuffer();
        String separator = System.getProperty("line.separator");
        buf.append(getBodyText()).append(separator);
        if (showHeader) {
            buf.append(separator);
            buf.append(HEADER_LINE).append(separator);
            buf.append(getMailHeader()).append(separator);
        }
        return buf.toString();
    }
    
    /**
     *  Returns true if subitem data contains standard header line.
     */
    private boolean isHeaderString(byte[] data) {
        String text = new String(data);
        int pos = text.indexOf("----- Headers -----");
        if (pos >= 0) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     *  Strips off header separator string in subitem data, and returns 
     *  remaining text.
     */
    private String getHeaderText(byte[] data) {
        StringBuffer buffer = new StringBuffer();
        // Find header line.
        String text = new String(data);
        int pos = text.indexOf("----- Headers -----");
        if (pos >= 0) {
            // Find end of header line.
            pos = text.indexOf("-----\015\012", pos);
            if (pos >= 0) {
                buffer.append(text.substring(pos + 7));
            }
        }
        return buffer.toString();
    }

    /**
     *  Converts byte array with V7 header data to a character string.  
     *  Replaces all occurences of Del (0x7f) with system line separator.
     */
    private String toV7String(byte[] data) {
        StringBuffer buffer = new StringBuffer();
        String separator = System.getProperty("line.separator");
        for (int i = 0; i < data.length; i++) {
            if (data[i] == 0x7f) {
                buffer.append(separator);
            }
            else {
                buffer.append((char)data[i]);
            }
        }
        return buffer.toString();
    }

    /**
     *  Uncompresses byte array into a character string.  This is applied
     *  to mail content saved in later versions.
     */
    private String unpackData(byte[] data, Inflater inflater) {
        StringBuffer result = new StringBuffer();
        String separator = System.getProperty("line.separator");
        // Set input byte array, and create output buffer.
        inflater.setInput(data);
        byte[] buffer = new byte[(10 * data.length)];
        try {
            // Uncompress bytes into buffer, and convert to text.
            int length = inflater.inflate(buffer, 0, (10 * data.length));
            result.append(new String(buffer, 0, length));
        }
        catch (DataFormatException dfx) {
            result.append(separator).append(dfx.toString());
        }
        return result.toString();
    }

}
