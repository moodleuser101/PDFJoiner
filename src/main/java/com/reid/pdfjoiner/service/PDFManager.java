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
package com.reid.pdfjoiner.service;

import java.io.File;
import java.util.List;

/**
 *
 * @author pmreid
 */
public interface PDFManager {

    public static final int TYPE_PPTX = 1;
    public static final int TYPE_PPT = 2;
    public static final int TYPE_DOCX = 3;
    public static final int TYPE_DOC = 4;
    public static final int TYPE_ODP = 5;
    public static final int TYPE_ODT = 6;

    /**
     * Simple helper method to actually do the joining of two or more PDFs. This
     * method assumes all compliance checks have already occurred.
     *
     * @param dest Validated File object to use as destination
     * @param sources Validated List of File objects representing the source
     * PDFs, in page order.
     * @return true on success
     */
    public boolean joinPDFs(File dest, List<File> sources);

    /**
     * Simple helper method to launch the desktop's default PDF viewer program
     * and view the newly-created PDF file
     */
    public void loadPDFToView();

    /**
     * Converts an office file such as Word or PowerPoint to a temporary PDF
     * prior to it being joined
     *
     * @param f populated Java file reference to source document
     * @return populated Java File reference to the temporary PDF file
     */
    public File convertDocument(File f);


    /*
    Helper methods to establish OS
     */
    public boolean isWindows();

    public boolean isMac();

    public boolean isUnix();

    public boolean isSolaris();

}
