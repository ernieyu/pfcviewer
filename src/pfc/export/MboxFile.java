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
 *  Represents Unix mbox file for emails.
 *
 * @author  Ernie Yu
 */
public class MboxFile {
    
    private File mboxFile;
    private DataOutputStream dos;
    private PrintWriter out;
    
    /** Creates a new instance of MboxFile */
    public MboxFile(File mboxFile) {
        this.mboxFile = mboxFile;
        dos = null;
        out = null;
    }

    /** Returns File object representing mbox path and file name.
     */
    public File getFile() {
        return mboxFile;
    }
    
    /** Returns print writer for character output to mbox file.  Output
     *  is filtered through a DataOutputStream so we can access the number
     *  of bytes written to the file.
     */
    public PrintWriter getPrintWriter() throws IOException {
        // Create output stream and print writer if necessary.
        if (out == null) {
            dos = new DataOutputStream(new FileOutputStream(mboxFile));
            out = new PrintWriter(
                new BufferedWriter(new OutputStreamWriter(dos)));
        }
        return out;
    }
    
    /** Returns number of bytes written to mbox file.
     */
    public int getSize() {
        if (dos != null) {
            return dos.size();
        } else {
            return 0;
        }
    }
    
    /** Closes writer and output stream for mbox file.
     */
    public void close() {
        if (out != null) {
            out.close();
        }
        if (dos != null) {
            try { dos.close(); }
            catch (IOException iox) {
                System.out.println(iox);
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
    
}
