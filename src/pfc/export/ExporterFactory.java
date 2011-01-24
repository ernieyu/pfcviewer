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

/**
 *  Class to create an Exporter object.
 *  @author Ernie Yu
 */
public class ExporterFactory {

    private int type;

    /** Constructor.
     *  @param type factory type - see constants in Exporter interface
     */
    public ExporterFactory(int type) {
        this.type = type;
    }

    /** Returns a new Exporter object based on the factory type.  The
     *  Exporter is initialized to write to the specified file.
     */
    public Exporter getExporter(File exportFile) {
        Exporter exporter = null;
        switch (type) {
            case Exporter.MBOX:
                exporter = new MboxMailExporter();
                break;
            case Exporter.MBOX_TOC:
                exporter = new EudoraMailExporter();
                break;
            case Exporter.FAVE_HTML:
                exporter = new FavoriteHtmlExporter();
                break;
            default:
                // Do nothing.
        }
        if (exporter != null) { exporter.setFile(exportFile); }
        return exporter;
    }

}
