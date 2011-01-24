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
import pfc.cab.*;

/**
 *  Class to export mail messages for Eudora.  In addition to creating an 
 *  mbox mail file, this generates two companion files:
 *  <ul>
 *    <li>&lt;mailbox&gt;.toc index file</li>
 *    <li>descmap.pce file containing an entry for the mbox file</li>
 *  </ul>
 *  This is a flat file export; the directory tree structure is not preserved 
 *  in the file.
 *
 * @author  Ernie Yu
 */
public class EudoraMailExporter extends MboxMailExporter {

    private TocFile toc;
    private int mboxSize;
    
    /** Creates a new instance of EudoraMailExporter */
    public EudoraMailExporter() {
        super();
        toc = null;
        mboxSize = 0;
    }
    
    /** Sets file to receive exported items.
     */
    public void setFile(File exportFile) {
        super.setFile(exportFile);
        toc = new TocFile(exportFile);
    }

    /** Opens export file.
     */
    public void open() throws IOException {
        super.open();
        toc.writeHeader();
    }
    
    /** Exports cabinet item to export file.  All mail information is
     *  taken from the mail item; the envelope item is ignored.
     */
    public void export(CabinetItem envelope, CabinetItem item) 
        throws IOException {
        super.export(envelope, item);
        // Get initial and new file sizes in bytes.
        int offset = mboxSize;
        mboxSize = mbox.getSize();
        // Write message entry to TOC file.
        toc.writeMessage(item, offset, (mboxSize - offset));
    }
    
    /** Closes export file.
     */
    public void close() {
        super.close();
        toc.close();
        PceFile.addMailbox(mbox.getFile(), false);
    }
    
}
