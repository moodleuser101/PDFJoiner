/*
 * Copyright (C) 2026 pmreid
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

import com.reid.pdfjoiner.primitive.ConversionResult;
import java.io.File;

/**
 * Interface for detection and conversion of file formats to add to the PDF
 * queue
 *
 * @author pmreid
 */
public interface DocDetectorConverter {

    /**
     * Helper method to ascertain whether a given file is a PDF or not
     *
     * @param file Populated Java File reference
     * @return true if file is a PDF
     */
    public boolean checkFileIsPDF(File file);

    /**
     * Will detect what type of file is supplied and attempt to convert to a PDF
     * if possible
     *
     * @param file Populated Java File reference of source document
     * @return ConversionResult DTO with the success result and file handle of
     * the temporary PDF generated, if successful
     */
    public ConversionResult attemptConversion(File file);

}
