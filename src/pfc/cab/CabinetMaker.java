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

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

/**
 *  Creates a Cabinet by reading records from a cabinet file.  This class
 *  implements Runnable and may be passed into a thread for execution.
 *
 *  @author Ernie Yu
 *  <br>02 Mar 2003 Creates dummy cabinet item for zero entries.
 */
public class CabinetMaker implements Runnable {
    
    public static final String CABFILE_ID = "AOLVM100";

    private String fileName;
    private Cabinet cabinet;
    private int progressPct;
    private JProgressBar progressBar;
    private JDialog progressDialog;
    private Exception exception;

    /**
     *  Constructor.
     *  @param name cabinet file name
     */
    public CabinetMaker(String name) {
        fileName = name;
        cabinet = null;
        progressPct = 0;
        progressBar = null;
        progressDialog = null;
        exception = null;
    }

    /** Sets JProgressBar object.  If set, the run() method will use the
     *  EventQueue to update the displayed percentage while it reads the 
     *  cabinet file.
     */
    public void setProgressBar(JProgressBar bar) {
        progressBar = bar;
        progressBar.setValue(progressPct);
    }

    /** Sets JDialog object.  If set, the run() method will use the
     *  EventQueue to close the dialog box after it finishes reading the 
     *  cabinet file.
     */
    public void setProgressDialog(JDialog dialog) {
        progressDialog = dialog;
    }

    /**
     *  Returns cabinet.
     */
    public Cabinet getCabinet() {
        return cabinet;
    }

    /**
     *  Returns cabinet build progress as percentage of index length.
     */
    public int getProgressPct() {
        return progressPct;
    }

    /**
     *  Returns cabinet build exception, if any.  Returns null if process
     *  completed successfully.
     */
    public Exception getException() {
        return exception;
    }

    /**
     *  Executes when thread is started.  Reads cabinet file and builds
     *  Cabinet object.
     */
    public void run() {

        RandomAccessFile pfcFile = null;

        try {
            // Create new Cabinet object.
            cabinet = new Cabinet();

            // Open personal filing cabinet for read-only access
            pfcFile = new RandomAccessFile(fileName, "r");

            // Read first 8 bytes; should = AOLVM100.
            pfcFile.seek(0);
            StringBuffer buf = new StringBuffer();
            for (int j = 0; j < 8; j++) {
                buf.append((char)pfcFile.readByte());
            }
            if (!buf.toString().equals(CABFILE_ID)) {
                throw new CabinetException("Selected file is not a Filing Cabinet.");
            }
            
            // Find start of index
            pfcFile.seek(16L);
            int idxStart = IntUtil.reverseInt(pfcFile.readInt());
            cabinet.setIdxStart(idxStart);

            // Get index length
            pfcFile.seek(idxStart + 4);
            int idxLength = IntUtil.reverseInt(pfcFile.readInt());
            cabinet.setIdxLength(idxLength);

            // Get index count
            pfcFile.seek(idxStart + 8);
            int idxCount = IntUtil.reverseInt(pfcFile.readInt());
            cabinet.setIdxCount(idxCount);

            // Get start of cabinet
            pfcFile.seek(idxStart + 16);
            cabinet.setCabStart(IntUtil.reverseInt(pfcFile.readInt()));

            // Go to first message index
            int idxMsg0 = idxStart + 12;
            int itemCount = 0;
            ArrayList itemList = new ArrayList();

            // Loop through all index entries.
            for (int i = idxMsg0; i < (idxStart + 8 + idxLength); i += 4) {

                // Get cabinet address for item.
                pfcFile.seek(i);
                int cabAddr = IntUtil.reverseInt(pfcFile.readInt());

                byte[] byteBuffer;
                if (cabAddr != 0) {
                    // Get item length.
                    pfcFile.seek(cabAddr + 4);
                    int length = IntUtil.reverseInt(pfcFile.readInt());

                    // Read entire item into byte array.
                    byteBuffer = new byte[length];
                    pfcFile.read(byteBuffer);
                }
                else {
                    // Create empty byte array for zero entry.
                    byteBuffer = new byte[4];
                }

                // Create cabinet item.
                CabinetItem item = new CabinetItem(byteBuffer);
                item.setIndex(itemCount++);
                item.setAddress(cabAddr);

                // Add item to array list.
                itemList.add(item);

                // Set progress meter as percentage of index length.
                progressPct = (int)((float)(i - idxStart - 8) / (float)idxLength * 100f);

                // Check if thread interrupted.
                if (Thread.interrupted()) {
                    throw new CabinetException("Cabinet file read interrupted.");
                }

                // Set progress bar if available.
                if ((progressBar != null) && 
                    ((progressPct % 5) == 0)) {
                    EventQueue.invokeLater(
                        new Runnable() {
                            public void run() {
                                progressBar.setValue(progressPct);
                            }
                        });
                }
            }

            // Set item count and item list in cabinet.
            cabinet.setItemCount(itemCount);
            cabinet.setItemList(itemList);
            progressPct = 100;
        }
        catch (IOException iox) {
            exception = iox;
        }
        catch (CabinetException cex) {
            exception = cex;
        }
        finally {
            // Close cabinet file.
            if (pfcFile != null) {
                try { pfcFile.close(); }
                catch (IOException iox) { exception = iox; }
            }
        }

        // Set progress bar to completion, and close dialog.
        if ((progressBar != null) || (progressDialog != null)) {
            EventQueue.invokeLater(
                new Runnable() {
                    public void run() {
                        if (progressBar != null) {
                            progressBar.setValue(progressPct);
                        }
                        if (progressDialog != null) {
                            progressDialog.dispatchEvent(
                                new java.awt.event.WindowEvent(
                                    progressDialog, 
                                    java.awt.event.WindowEvent.WINDOW_CLOSING));
                        }
                    }
                });
        } 
    }

}
