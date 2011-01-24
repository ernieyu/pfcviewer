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
import javax.swing.table.*;

/**
 *  Extends AbstractTableModel class to display folder items in a JTable
 *  object.  This class is contains a Cabinet and CabinetItem, which must
 *  be set to display data in the table.
 *  @author Ernie Yu
 */
public class CabinetTableModel extends AbstractTableModel {

    private static final String TAB = "\011";

    private Cabinet cabinet;
    private CabinetItem folder;

    /**
     *  Constructor.
     */
    public CabinetTableModel() {
        cabinet = null;
        folder = null;
    }

    /**
     *  Sets cabinet, and fires table structure change event to repaint
     *  JTable display.
     */
    public void setCabinet(Cabinet cab) {
        cabinet = cab;
        folder = null;
        fireTableStructureChanged();
    }

    /**
     *  Sets current folder, and fires table structure change event to
     *  repaint JTable display.
     */
    public void setFolder(CabinetItem folder) {
        this.folder = folder;
        fireTableStructureChanged();
    }
    
    /**
     *  Returns the number of rows in the table.  Only cabinet envelopes
     *  that are NOT folders count as rows.  Returns 0 if folder has no
     *  children.
     */
    public int getRowCount() {
        int count = 0;
        if ((cabinet == null) || (folder == null)) { return count; }
        // Get pointer to first child.
        int idx = folder.getChild();
        // Loop through all children.
        while (idx != 0) {
            // Get tree element; count if NOT a folder.
            CabinetItem item = cabinet.getCabinetItem(idx);
            if (!item.isFolder()) {
                count++;
            }
            // Get pointer to next element.
            idx = item.getNext();
        }
        return count;
    }

    /**
     *  Returns the number of columns in the table.
     */
    public int getColumnCount() {
        return 4;
    }

    /**
     *  Returns the column name at the specified column index.
     */
    public String getColumnName(int column) {
        if ((folder != null) && 
            (folder.getType() == CabinetItem.FILE_FOLDER)) {
            switch (column) {
                case 0:
                    return "File";
                case 1:
                    return "Description";
                case 2:
                    return "Size";
                case 3:
                    return "Index";
                default:
                    return "Index";
            }
        }
        else {
            switch (column) {
                case 0:
                    return "Date";
                case 1:
                    return "From/To";
                case 2:
                    return "Subject";
                case 3:
                    return "Index";
                default:
                    return "Index";
            }
        }
    }

    /**
     *  Returns the item at the specified row and column.
     *  Returns null if no object is available.
     */
    public Object getValueAt(int row, int column) {
        int count = -1;
        if ((cabinet == null) || (folder == null)) { return null; }
        // Get pointer to first child.
        int idx = folder.getChild();
        // Loop through all children.
        while (idx != 0) {
            // Get tree element; count if NOT a folder.
            CabinetItem item = cabinet.getCabinetItem(idx);
            if (!item.isFolder()) {
                count++;
                if (count == row) {
                    String result = getEnvelopeField(item, column);
                    return result;
                }
            }
            // Get pointer to next element.
            idx = item.getNext();
        }
        return null;            // return null if not found
    }

    /**
     *  Parses envelope content, and returns the field for the specified
     *  table column.  The columns defined below.
     *  For messages: 0 = date, 1 = from/to, 2 = subject, 3 = data pointer.
     *  For files: 0 = file, 1 = description, 2 = size, 3 = data pointer.
     *  For addresses: 1 = name, 2 = email address, 3 = data pointer.
     */
    private String getEnvelopeField(CabinetItem item, int column) {
        String result = null;
        StringBuffer buffer = new StringBuffer();
        byte[] content = item.getContent();
        int itemType = item.getType();

        // Get envelope label.
        for (int i = 18; i < 98; i++) {
            if (content[i] == '\000') break;
            buffer.append((char)content[i]);
        }

        if ((itemType == CabinetItem.FAVE_ENVELOPE) ||
            (itemType == CabinetItem.GROUP_ENVELOPE)) {
            try {
                switch (column) {
                    case 1:
                        result = buffer.toString();
                        break;
                    case 3:
                        result = String.valueOf(item.getIndex());
                        break;
                    default:
                        // Return null.
                }
            }
            catch (IndexOutOfBoundsException iex) {}
        }
        if (itemType == CabinetItem.ADDR_ENVELOPE) {
            // Find 1st tab.
            int tab1 = buffer.indexOf(TAB);
            // Return label piece delimited by tabs
            try {
                switch (column) {
                    case 1:
                        result = buffer.substring(0, tab1);
                        break;
                    case 2:
                        result = buffer.substring(tab1 + 1, buffer.length());
                        break;
                    case 3:
                        result = String.valueOf(item.getIndex());
                        break;
                    default:
                        // Return null.
                }
            }
            catch (IndexOutOfBoundsException iex) {}
        }
        else if ((itemType == CabinetItem.MAIL_ENVELOPE) ||
                 (itemType == CabinetItem.FILE_ENVELOPE)) {
            // Find 1st and 2nd tabs.
            int tab1 = buffer.indexOf(TAB);
            int tab2 = buffer.indexOf(TAB, tab1 + 1);
            // Return label piece delimited by tabs
            try {
                switch (column) {
                    case 0:
                        result = buffer.substring(0, tab1);
                        break;
                    case 1:
                        result = buffer.substring(tab1 + 1, tab2);
                        break;
                    case 2:
                        result = buffer.substring(tab2 + 1, buffer.length());
                        break;
                    case 3:
                        result = String.valueOf(item.getIndex());
                        break;
                    default:
                        // Return null.
                }
            }
            catch (IndexOutOfBoundsException iex) {}
        }
        return result;
    }

}
