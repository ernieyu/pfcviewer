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

import java.io.*;
import pfc.cab.*;

/**
 *  Class implementing Exporter to export favorites to an HTML file.
 *  @author Ernie Yu
 */
public class FavoriteHtmlExporter implements Exporter {

    private File htmlFile;
    private PrintWriter out;
    private StringBuffer indent;
    
    /** Creates a new instance of FavoriteHtmlExporter 
     */
    public FavoriteHtmlExporter() {
        htmlFile = null;
        out = null;
        indent = new StringBuffer();
    }
    
    /** Sets file to receive exported items.
     */
    public void setFile(File exportFile) {
        htmlFile = exportFile;
    }
    
    /** Returns true if cabinet item is valid for export.  For Favorites,
     *  the item must be a favorites envelope.
     */
    public boolean isExportable(CabinetItem item) {
        if ((item.getType() == CabinetItem.FAVE_ENVELOPE) &&
            (item.getData() != 0)) {
            return true;
        } else {
            return false;
        }
    }
    
    /** Opens export file.
     */
    public void open() throws IOException {
        out = new PrintWriter(
            new BufferedWriter(new FileWriter(htmlFile)));
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Favorite Places</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<dl><p>");
    }
    
    /** Opens folder in export file.
     */
    public void openFolder(CabinetItem item) {
        indent.append("  ");
        out.println(indent.toString() + "<dt><h3>" + item.toString() + "</h3>");
        out.println(indent.toString() + "<dl><p>");
    }
    
    /** Exports cabinet item to export file.
     */
    public void export(CabinetItem envelope, CabinetItem item) 
        throws IOException {
        Favorite favorite = new Favorite(item.getContent());
        out.println(indent.toString() + "  <dt><a href='" + 
            favorite.getUrl() + "'>" + 
            envelope.toString() + "</a>");
    }
    
    /** Closes folder in export file.
     */
    public void closeFolder() {
        out.println(indent.toString() + "</p></dl>");
        indent.delete(0, 2);
    }
    
    /** Closes export file.
     */
    public void close() {
        if (out != null) {
            out.println("</p></dl>");
            out.println("</body>");
            out.println("</html>");
            out.close();
        }
    }
    
}
