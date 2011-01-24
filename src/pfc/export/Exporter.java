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

package pfc.export;

import java.io.File;
import java.io.IOException;
import pfc.cab.CabinetItem;

/**
 *  Generic interface to export cabinet items to a file.  An object
 *  instantiated from a class implementing Exporter must be passed to the
 *  Cabinet.export() method to perform export operations.  Most of the
 *  methods for an Exporter are called to handle events during cabinet
 *  folder traversal.
 *
 *  @author Ernie Yu
 */
public interface Exporter {

    public static final int MBOX = 1;
    public static final int MBOX_TOC = 2;
    public static final int FAVE_HTML = 3;

    /** Sets file to receive exported items.
     */
    public void setFile(File exportFile);
    
    /** Returns true if cabinet item is valid for export.
     */
    public boolean isExportable(CabinetItem item);
    
    /** Opens export file.
     */
    public void open() throws IOException;

    /** Opens folder in export file.
     */
    public void openFolder(CabinetItem item);
    
    /** Exports cabinet item to export file.
     */
    public void export(CabinetItem envelope, CabinetItem item) 
        throws IOException;

    /** Closes folder in export file.
     */
    public void closeFolder();
    
    /** Closes export file.
     */
    public void close();

}
