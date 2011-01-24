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
import javax.swing.tree.*;
import javax.swing.event.*;

/**
 *  Implements TreeModel interface for use in displaying cabinet contents.  
 *  CabinetTreeModel is initialized with a Cabinet, which is usually 
 *  created using a CabinetMaker.  All tree elements are CabinetItem 
 *  objects.
 *
 *  @author Ernie Yu
 *  <br>02 Mar 2003 Calls Cabinet.getCabinetItem() method to retrieve items.
 */
public class CabinetTreeModel implements TreeModel {

    private Cabinet cabinet;

    /**
     *  Constructor.
     *  @param cab cabinet object
     */
    public CabinetTreeModel(Cabinet cab) {
        cabinet = cab;
    }

    /**
     *  Returns the root item of the tree.
     */
    public Object getRoot() {
        return cabinet.getCabinetItem(1);
    }

    /**
     *  Returns the child at the specified index in the parent's child
     *  array.  Returns null only if parent has no children.
     */
    public Object getChild(Object parent, int index) {
        CabinetItem node = null;
        int count = -1;
        // Get pointer to first child.
        int idx = ((CabinetItem)parent).getChild();
        // Loop through all children.
        while (idx != 0) {
            // Get tree element; count if it is folder.
            CabinetItem item = cabinet.getCabinetItem(idx);
            if (item.isFolder()) {
                count++;
                if (count == index) {
                    return item;        // return item found
                }
                else {
                    node = item;        // save last folder found
                }
            }
            // Get pointer to next element.
            idx = item.getNext();
        }
        return node;    // return last folder found, or null.
    }

    /**
     *  Returns the number of children of parent.  Only cabinet folders
     *  count as tree elements.  Returns 0 if parent has no children.
     */
    public int getChildCount(Object parent) {
        int count = 0;
        // Get pointer to first child.
        int idx = ((CabinetItem)parent).getChild();
        // Loop through all children.
        while (idx != 0) {
            // Get tree element; count if it is folder.
            CabinetItem item = cabinet.getCabinetItem(idx);
            if (item.isFolder()) {
                count++;
            }
            // Get pointer to next element.
            idx = item.getNext();
        }
        return count;
    }

    /**
     *  Returns the index of child in parent.  If parent or child is null,
     *  returns -1.
     */
    public int getIndexOfChild(Object parent, Object child) {
        if ((parent == null) || (child == null)) { return -1; }
        int childIdx = ((CabinetItem)child).getIndex();
        int count = -1;
        // Get pointer to first child.
        int idx = ((CabinetItem)parent).getChild();
        // Loop through all children.
        while (idx != 0) {
            // Get tree element; count if it is folder.
            CabinetItem item = cabinet.getCabinetItem(idx);
            if (item.isFolder()) {
                count++;
                if (item.getIndex() == childIdx) { return count; }
            }
            // Get pointer to next element.
            idx = item.getNext();
        }
        return -1;      // Child not found, so return -1.
    }

    /**
     *  Returns true if node is a leaf.
     */
    public boolean isLeaf(Object node) {
        CabinetItem item = (CabinetItem)node;
        if (item.isFolder()) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     *  Messaged when the user has altered the value for the item
     *  identified by path to newValue.
     */
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    /**
     *  Adds a listener for the TreeModelEvent posted after the tree changes.
     */
    public void addTreeModelListener(TreeModelListener l) {
    }

    /**
     *  Removes a listener previously added with addTreeModelListener.
     */
    public void removeTreeModelListener(TreeModelListener l) {
    }
}
