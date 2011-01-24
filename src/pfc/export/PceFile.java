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

/**
 *  Represents PCE file for Eudora.  This is a text file listing the mailbox
 *  files and folders in the current directory.
 *  @author  Ernie Yu
 */
public class PceFile {
    
    public static final String PCE_NAME = "descmap.pce";
    
    /** Creates a new instance of PceFile */
    public PceFile() {
    }
    
    /** Adds PCE file entry for mailbox file or folder.
     */
    public static void addMailbox(File mboxFile, boolean folder) {
        boolean nameFound = false;
        
        File pceFile = new File(mboxFile.getParent(), PCE_NAME);
        if (pceFile.exists()) {
            BufferedReader in = null;
            try {
                // Open existing PCE file and read entries.
                in = new BufferedReader(new FileReader(pceFile));
                String mboxName = mboxFile.getName();
                String entry = in.readLine();
                while (entry != null) {
                    // Get mailbox name, and compare with mailbox.
                    int c1 = entry.indexOf(",");
                    int c2 = entry.indexOf(",", c1 + 1);
                    if (entry.substring(c1 + 1, c2).equalsIgnoreCase(mboxName)) {
                        nameFound = true;
                        break;
                    }
                    // Read next line.
                    entry = in.readLine();
                }
            }
            catch (FileNotFoundException fnx) {
                System.out.println(fnx);
            }
            catch (IOException iox) {
                System.out.println(iox);
            }
            finally {
                if (in != null) { 
                    try { in.close(); }
                    catch (IOException iox) {}
                }
            }
        }
        else {
            try {
                // Create new PCE file.
                pceFile.createNewFile();
            }
            catch (IOException iox) {
                System.out.println(iox);
            }
            catch (SecurityException sex) {
                System.out.println(sex);
            }
        }

        // If mailbox entry not found, then add.
        if (!nameFound) {
            PrintWriter out = null;
            try {
                // Open PCE file to append data.
                out = new PrintWriter(
                    new BufferedWriter(new FileWriter(pceFile, true)));
                // Remove extension and get mailbox ID.
                String mboxName = mboxFile.getName();
                String mboxId = mboxName;
                int lastDot = mboxName.lastIndexOf(".");
                if (lastDot > 0) {
                    mboxId = mboxName.substring(0, lastDot);
                }
                // Add entry for mailbox file.
                out.println(mboxId + "," + mboxName + ",M,N");
            }
            catch (IOException iox) {
                System.out.println(iox);
            }
            finally {
                if (out != null) { out.close(); }
            }
        }
    }
    
}
