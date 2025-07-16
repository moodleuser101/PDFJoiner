/*
 * Copyright (C) 2025 pmreid
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.reid.pdfjoiner;

import com.reid.pdfjoiner.service.PDFManager;
import com.reid.pdfjoiner.service.impl.PDFManagerImpl;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * A simple desktop utility application to join two or more source PDF files
 * into a single output PDF file.
 *
 * @author pmreid
 */
public class PDFJoiner {

    public static MainWindow mw;
    public static List<File> sourceFiles;
    public static File destinationFile;
    private static final String SUFFIX = "pdf";
    public static PDFManager pdfMan;
    public static final int DIR_UP = 1;
    public static final int DIR_DOWN = -1;

    /**
     * The main method is the entry point to this program.
     *
     * @param args command-line arguments; assumed to be empty for this program.
     */
    public static void main(String[] args) {
        initializeSettings();
        PDFJoiner.sourceFiles = new ArrayList<>();
        pdfMan = new PDFManagerImpl();
        mw = new MainWindow();
        mw.setLocationRelativeTo(null);
        mw.setVisible(true);
    }

    /**
     * After checking that all necessary parameters are set, this will execute
     * the PDF join process
     *
     * @return true on successful join
     */
    public static boolean executeJoin() {
        boolean status = false;
        if (destinationFile.getParentFile().canWrite() && !sourceFiles.isEmpty()) {
            for (File f : sourceFiles) {
                if (f.exists() && f.canRead()) {
                    return pdfMan.joinPDFs(destinationFile, sourceFiles);
                } else {
                    PDFJoiner.outputExceptionToUser(new Exception("One or more source files cannot be read."));
                }
            }
        } else {
            PDFJoiner.outputExceptionToUser(new Exception("Destination file unwritable or source files list empty."));
        }
        return status;
    }

    /**
     * Helper method that will change the order of source files in the ArrayList
     * and therefore the order of files in the final output PDF
     *
     * @param index reference to the current location in the ArrayList of the
     * item to move
     * @param direction reference to PDFJoiner.DIR_ to move up or down
     * @return true on success
     */
    public static boolean reOrderSourceFiles(int index, int direction) {
        // first, check if index exists:
        if (PDFJoiner.sourceFiles.size() <= index) {
            return false;
        }
        boolean rtn;
        switch (direction) {
            case (PDFJoiner.DIR_DOWN):
                if (PDFJoiner.sourceFiles.size() - index > 1) {
                    Collections.rotate(PDFJoiner.sourceFiles.subList(index, index + 2), -1);
                    rtn = true;
                } else {
                    rtn = false;
                }
                break;
            case (PDFJoiner.DIR_UP):
                if (index > 0) {
                    Collections.rotate(PDFJoiner.sourceFiles.subList(index - 1, index + 1), 1);
                    rtn = true;
                } else {
                    rtn = false;
                }
                break;
            default:
                return false;
        }

        return rtn;
    }

    /**
     * Helper method to select the output file to save to
     *
     * @return Absolute path of desired output file
     */
    public static String selectOutputFile() {
        destinationFile = PDFJoiner.fileBrowse("Select Source PDF", FileDialog.SAVE, PDFJoiner.SUFFIX, false)[0]; // show dialog to select output file
        if (destinationFile == null) {
            PDFJoiner.outputExceptionToUser(new Exception("Cancel was clicked on destination file selection; it will not be possible to proceed until an output file is selected..."));
            return null;
        } else {
            return destinationFile.getAbsolutePath();
        }
    }

    /**
     * Helper method to select the source files to process
     *
     * @return true on success
     */
    public static boolean selectSourceFiles() {
        List<File> newSourceFiles;
        newSourceFiles = Arrays.asList(PDFJoiner.fileBrowse("Select Source PDF", FileDialog.LOAD, ".pdf", true)); // show dialog to select source file
        if (newSourceFiles == null) {
            PDFJoiner.outputExceptionToUser(new Exception("Cancel was clicked on source files selection; it will not be possible to proceed until a source is selected..."));
            return false;
        } else {
            // merge the existing list of source files with these new ones
            PDFJoiner.sourceFiles.addAll(newSourceFiles);
            return true;
        }
    }

    public static void removeFileFromSources(File f) {
        PDFJoiner.sourceFiles.removeIf(sourceFile -> sourceFile.getAbsolutePath().equals(f.getAbsolutePath()));
    }

    /**
     * Simple helper method to output an exception to the user in a pop-up
     * message box
     *
     * @param ex Populated Java exception
     */
    public static void outputExceptionToUser(Exception ex) {
        JOptionPane.showMessageDialog(null, "An error occurred: \n\n" + ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Simple helper method to output a message to the user in a pop-up message
     * box
     *
     * @param msg Textual representation of the message to display
     */
    public static void outputMessageToUser(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Message", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Simple helper method to output a Yes/No question to the user in a pop-up
     * message box
     *
     * @param msg Textual representation of the message to display
     * @param title Text box title text
     * @return true for 'Yes'
     */
    public static boolean outputYesNoQuestionToUser(String msg, String title) {
        int reply = JOptionPane.showConfirmDialog(null, msg, title, JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.YES_OPTION) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Shows a "save as"/"open" file dialog
     *
     * @param dialogTitle Title of the dialog box
     * @param dialogType a FileDialog.{SAVE, LOAD} reference
     * @param fileFilter filename filter, eg ".csv" or ".sdc"; must have a dot
     * and no star
     * @return Java File reference
     */
    private static File[] fileBrowse(String dialogTitle, int dialogType, String fileFilter, boolean multipleSelect) {
        try {
            FileDialog fileDialog = new FileDialog(new Frame(), dialogTitle, dialogType);
            fileDialog.setDirectory(System.getProperty("user.home"));
            fileDialog.setMultipleMode(multipleSelect);
            String fileFilter2;
            if (!fileFilter.contains(".")) {
                fileFilter2 = "." + fileFilter;
            } else {
                fileFilter2 = fileFilter;
            }
            fileDialog.setFilenameFilter(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(fileFilter2);
                }
            });
            fileDialog.setVisible(true);

            File[] files = fileDialog.getFiles();
            File[] processedFiles = new File[files.length];
            if (files != null && files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    String fn = files[i].getName();
                    if (!fn.endsWith(fileFilter)) {
                        fn = fn + "." + fileFilter;
                    }
                    processedFiles[i] = new File(fileDialog.getDirectory(), fn);
                }
                return processedFiles;
            }
        } catch (Exception ex) {
            Exception ex2 = new Exception("Error processing file: \n" + ex.getLocalizedMessage());
            PDFJoiner.outputExceptionToUser(ex2);
        }
        return null;
    }

    /**
     * Helper method to display an error message to the user, and then exit
     * application.
     *
     * @param msg
     */
    private static void triggerExit(String msg) {
        PDFJoiner.outputExceptionToUser(new Exception(msg));
        System.exit(0);
    }

    /**
     * Simple helper method to set up the UI look and feel so it's native to the
     * user's Operating System platform and avoids the Java Swing default
     * interface
     */
    private static void initializeSettings() {
        System.setProperty("sun.java2d.uiScale.enabled", "true");
        System.setProperty("sun.java2d.uiScale", "1.0"); // Adjust scale factor
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            PDFJoiner.outputExceptionToUser(ex);
        }
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog"); // prevents superfluous logging by Apache PDFBox
    }
}
