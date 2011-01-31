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

import java.io.*;
import java.util.*;
import pfc.cab.*;
import pfc.export.*;

/**
 *  Exports all mail messages in the Filing Cabinet to an mbox mail file.  
 *  This program checks all items in the cabinet, and ignores the folder 
 *  structure.
 *
 *  @author Ernie Yu
 *  <br>02 Mar 2003 - Checks cabinet maker for exceptions, and halts on error.
 */
public class MailToMbox {
    
    private Cabinet cabinet;
    
    /** Creates a new instance of MailToMbox */
    public MailToMbox() {
    }
    
    /**
     *  Main program method.  Accepts two command line arguments, PFC 
     *  filename and mbox filename.
     *  @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: MailToMbox <pfcFile> <mboxFile>");
            return;
        }

        // Compare file names.
        File pfcFile = new File(args[0]);
        File mboxFile = new File(args[1]);
        if (pfcFile.getName().equalsIgnoreCase(mboxFile.getName())) {
            System.out.println("Pfc filename and Mbox filename must be different.");
            return;
        }
        
        // Create main object, open cabinet file, and export mails.
        MailToMbox converter = new MailToMbox();
        converter.openCabinet(args[0]);
        converter.exportMail(args[1]);
    }

    /**
     *  Opens specified cabinet file, and reads all items into Cabinet 
     *  object.
     */
    private void openCabinet(String pfcName) {
        // Open PFC file for reading
        RandomAccessFile pfcFile = null;
        try {
            pfcFile = new RandomAccessFile(pfcName, "r");
        } catch (FileNotFoundException ex) {
            System.err.println("Could not read cabinet file " + pfcName);
            System.exit(1);
        }

        // Create cabinet maker and start thread to read file.
        CabinetMaker maker = new CabinetMaker(pfcFile);
        Thread thread = new Thread(maker);
        thread.start();
        
        // Wait for thread to finish.
        System.out.print("Reading cabinet file " + pfcName);
        while (thread.isAlive()) {
            System.out.print(".");
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException iex) {}
        }
        System.out.println();
        
        // Check exception and progress for errors.
        Exception exception = maker.getException();
        int percent = maker.getProgressPct();
        if ((exception == null) && (percent == 100)) {
            // Get finished cabinet.
            cabinet = maker.getCabinet();
        }
        else {
            // Report error and halt.
            System.out.println("Stopped at " + percent + "%");
            if (exception != null) {
                System.out.println(exception.toString());
            }
            System.exit(1);
        }
    }
    
    /**
     *  Exports all mail messages in cabinet to mbox file.
     */
    private void exportMail(String mboxName) {
        System.out.println("Exporting to mbox file " + mboxName);
        int itemCount = cabinet.getItemCount();
        int mailCount = 0;

        // Create factory and get exporter object.
        ExporterFactory factory = new ExporterFactory(Exporter.MBOX);
        Exporter exporter = factory.getExporter(new File(mboxName));
        try {
            exporter.open();
            // Loop through items, and export mail messages.
            for (int i = 0; i < itemCount; i++) {
                CabinetItem item = cabinet.getCabinetItem(i);
                if (item.getType() == CabinetItem.MAIL_DATA) {
                    exporter.export(null, item);
                    mailCount++;
                }
            }
        }
        catch (IOException iox) {
            System.out.println(iox);
        }
        finally {
            exporter.close();
            System.out.println(String.valueOf(mailCount) + 
                " mail messages exported.");
            System.out.println("Done.");
        }
    }
    
}
