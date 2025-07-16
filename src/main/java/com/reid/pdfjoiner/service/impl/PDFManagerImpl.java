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
package com.reid.pdfjoiner.service.impl;

import com.reid.pdfjoiner.PDFJoiner;
import com.reid.pdfjoiner.service.PDFManager;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.RandomAccessStreamCache;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

/**
 *
 * @author pmreid
 */
public class PDFManagerImpl implements PDFManager {

// Potentially used to vary procedure for launching desktop PDF viewing:
    private static String OS = System.getProperty("os.name").toLowerCase();

    @Override
    public boolean joinPDFs(File dest, List<File> sources) {
        boolean status = false;
        PDFMergerUtility pmu = new PDFMergerUtility();
        pmu.setDestinationFileName(dest.getAbsolutePath());

        sources.forEach(file -> {
            try {
                pmu.addSource(file);
            } catch (FileNotFoundException e) {
                PDFJoiner.outputExceptionToUser(e);
            }
        });

        RandomAccessStreamCache.StreamCacheCreateFunction streamCache = IOUtils.createMemoryOnlyStreamCache();
        try {
            pmu.mergeDocuments(streamCache);
            status = true;
        } catch (IOException ex) {
            PDFJoiner.outputExceptionToUser(ex);
        }
        return status;
    }

    @Override
    public void loadPDFToView() {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(PDFJoiner.destinationFile);
            } catch (IOException ex) {
                PDFJoiner.outputExceptionToUser(ex);
            }
        } else {
            PDFJoiner.outputExceptionToUser(new Exception("Desktop viewing of the new PDF is not available because the Java Desktop API is not installed."));
        }
    }

    /*
    A series of helper methods to determine which OS is in use:
     */
    private boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    private boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    private boolean isUnix() {
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);
    }

    private boolean isSolaris() {
        return (OS.indexOf("sunos") >= 0);
    }

}
