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

import java.io.IOException;
import java.util.*;
import pfc.export.*;

/**
 *  Contains all information in the Filing Cabinet.  The data in a Cabinet 
 *  object is usually initialized by creating a CabinetMaker object and 
 *  calling its run() method.
 *
 *  @author Ernie Yu
 *  <br>02 Mar 2003 Changed item index to zero-based.
 */
public class Cabinet {

    private int idxStart;
    private int idxLength;
    private int idxCount;
    private int cabStart;
    private int itemCount;
    private ArrayList itemList;
    private int exportCount;

    /**
     *  Constructor.
     */
    public Cabinet() {
        itemList = new ArrayList();
    }

    /** Sets byte address of cabinet index.
     */
    public void setIdxStart(int i) {
        idxStart = i;
    }

    /** Returns byte address of cabinet index.
     */
    public int getIdxStart() {
        return idxStart;
    }

    /** Sets cabinet index length in bytes.
     */
    public void setIdxLength(int i) {
        idxLength = i;
    }

    /** Returns cabinet index length in bytes.
     */
    public int getIdxLength() {
        return idxLength;
    }

    /** Sets number of entries in cabinet index.
     */
    public void setIdxCount(int i) {
        idxCount = i;
    }

    /** Returns number of entries in cabinet index.
     */
    public int getIdxCount() {
        return idxCount;
    }

    /** Sets byte address of cabinet root item.
     */
    public void setCabStart(int i) {
        cabStart = i;
    }

    /** Returns byte address of cabinet root item.
     */
    public int getCabStart() {
        return cabStart;
    }

    /** Sets number of items found when cabinet was loaded.
     */
    public void setItemCount(int i) {
        itemCount = i;
    }

    /** Returns number of items found when cabinet was loaded.
     */
    public int getItemCount() {
        return itemCount;
    }

    /** Sets array list of cabinet items.
     */
    public void setItemList(ArrayList list) {
        itemList = list;
    }

    /** Returns array list of cabinet items.
     */
    public ArrayList getItemList() {
        return itemList;
    }

    /**
     *  Returns the item for the specified index.  Input index for this 
     *  method is zero-based.
     */
    public CabinetItem getCabinetItem(int index) {
        return (CabinetItem)itemList.get(index);
    }
    
    /**
     *  Exports the specified cabinet item using an Exporter.  The item
     *  is usually a folder whose children will be exported.  Returns
     *  the number of items exported.
     */
    public int export(CabinetItem item, Exporter exporter) 
        throws ExportException {
        exportCount = 0;
        try {
            exporter.open();
            if (item.isFolder()) {
                exportChildren(item, exporter);
            }
            else if (exporter.isExportable(item)) {
                CabinetItem dataItem = getCabinetItem(item.getData());
                exporter.export(item, dataItem);
                exportCount++;
            }
        }
        catch (IOException iox) {
            throw new ExportException(iox.toString());
        }
        finally {
            exporter.close();
        }
        return exportCount;
    }
    
    /**
     *  Exports all child items of the specified parent.  This method
     *  will recurse through all sub-folders of the parent, and export
     *  their child items.
     */
    private void exportChildren(CabinetItem parent, Exporter exporter) 
        throws ExportException {
        try {
            exporter.openFolder(parent);
            int idx = parent.getChild();
            while (idx != 0) {
                CabinetItem child = getCabinetItem(idx);
                if (child.isFolder()) {
                    exportChildren(child, exporter);
                }
                else if (exporter.isExportable(child)) {
                    CabinetItem dataItem = getCabinetItem(child.getData());
                    exporter.export(child, dataItem);
                    exportCount++;
                }
                idx = child.getNext();
            }
        }
        catch (IOException iox) {
            throw new ExportException(iox.toString());
        }
        finally {
            exporter.closeFolder();
        }
    }
    
}
