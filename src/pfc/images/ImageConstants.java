/*
 * Copyright (c) 2005 Ernest Yu. All rights reserved.
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

package pfc.images;

import java.net.*;
import java.awt.*;
import javax.swing.*;

/**
 *  Constants for application images.
 *
 * @author Ernie Yu
 */
public abstract class ImageConstants {

    /** Protected constructor prevents unintended instantiation. */
    protected ImageConstants() {
    }

    /** Blank icon. */
    public static final String ICON_BLANK = "Blank16.gif";
    /** Copy icon. */
    public static final String ICON_COPY = "Copy16.gif";
    /** Open icon. */
    public static final String ICON_OPEN = "Open16.gif";
    /** Preferences icon. */
    public static final String ICON_PREFERENCES = "Preferences16.gif";
    /** Program logo. */
    public static final String PROGRAM_LOGO = "Logo48.png";

    /** Returns the image object for the specified image path relative to the
     *  package containing this class.  If the image cannot be found, then a
     *  null value is returned.
     * @param imagePath String
     * @return Image
     */
    public static Image createImage(String imagePath) {
        URL imageUrl = ImageConstants.class.getResource(imagePath);
        if (imageUrl != null) {
            return Toolkit.getDefaultToolkit().createImage(imageUrl);
        } else {
            return null;
        }
    }

    /** Returns the icon object for the specified icon path relative to the
     *  package containing this class.  If the icon cannot be found, then a 
     *  blank icon is returned.
     * @param iconPath String
     * @return Icon
     */
    public static Icon createImageIcon(String iconPath) {
        URL iconUrl = ImageConstants.class.getResource(iconPath);
        if (iconUrl != null) {
            return new ImageIcon(iconUrl);
        } else {
            return new ImageIcon(ImageConstants.class.getResource(ICON_BLANK));
        }
    }
}
