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
package com.reid.pdfjoiner.service.impl;

import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.conversion.msoffice.MicrosoftPowerpointBridge;
import com.documents4j.job.LocalConverter;
import com.reid.pdfjoiner.PDFJoiner;
import com.reid.pdfjoiner.primitive.ConversionResult;
import com.reid.pdfjoiner.service.DocDetectorConverter;
import com.reid.pdfjoiner.service.PDFManager;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pmreid
 */
public class DocDetectorConverterImpl implements DocDetectorConverter {

    @Override
    public boolean checkFileIsPDF(File file) {
        /*
        Needs more work to do better PDF detection, like reading headers or meta data...
        minimal working mechanism:
         */
        return file.getName().toLowerCase().endsWith(".pdf");
    }

    @Override
    public ConversionResult attemptConversion(File file) {
        int type = getFileType(file);
        ConversionResult result = new ConversionResult();
        result.setSource_file_type(type);
        if (type > -1) {
            try {
                File tmp = File.createTempFile("PDFJoiner_", "." + PDFJoiner.SUFFIX);
                tmp.deleteOnExit();
                result.setTempFile(tmp);
                switch (type) {
                    case PDFManager.TYPE_PPT:
                        result.setSuccess(doc4JConverter(file, tmp, DocumentType.PPT));
                        break;
                    case PDFManager.TYPE_PPTX:
                        result.setSuccess(doc4JConverter(file, tmp, DocumentType.PPTX));
                        break;
                    case PDFManager.TYPE_DOC:
                        result.setSuccess(doc4JConverter(file, tmp, DocumentType.DOC));
                        break;
                    case PDFManager.TYPE_DOCX:
                        result.setSuccess(doc4JConverter(file, tmp, DocumentType.DOCX));
                        break;
                    case PDFManager.TYPE_ODT:
                        // not yet implemented
                        break;
                    case PDFManager.TYPE_ODP:
                        // not yet implemented
                        break;

                }
            } catch (IOException ex) {
                return result;
            }
        }
        return result;
    }

    /**
     * Performs a basic check on the given file to extract its type from the
     * extension
     *
     * @param file Populated Java File reference
     * @return PDFManager.TYPE_ reference, or -1 if unknown
     */
    private int getFileType(File file) {
        int type = -1;
        /*
        Needs updating with more rigorous document detection methodology...
         */
        String f = file.getName().toLowerCase();
        if (f.endsWith(".pptx")) {
            type = PDFManager.TYPE_PPTX;
        } else if (f.endsWith(".ppt")) {
            type = PDFManager.TYPE_PPT;
        } else if (f.endsWith(".docx")) {
            type = PDFManager.TYPE_DOCX;
        } else if (f.endsWith(".doc")) {
            type = PDFManager.TYPE_DOC;
        } else if (f.endsWith(".odt")) {
            type = PDFManager.TYPE_ODT;
        } else if (f.endsWith(".odp")) {
            type = PDFManager.TYPE_ODP;
        }
        return type;
    }

    /**
     * Helper method that calls the document4j API. This API, which only works
     * on a native MS Windows environment with a working Microsoft Office
     * implementation, will call MS Office's background API, and do a "proper"
     * Microsoft Office conversion from DOCX, PPTX etc into PDF, which is
     * captured as a temporary file
     *
     * @param inputFile Populated Java File object of the input file to be
     * converted
     * @param tmpFile Populated Java File object of an (empty) temporary file
     * that will store the generated PDF
     * @param docType Reference to the type of source document, eg
     * DocumentType.PPTX
     * @return true on success
     */
    private boolean doc4JConverter(File inputFile, File tmpFile, DocumentType docType) {

        boolean result = false;
        try {
            FileInputStream inputStream = new FileInputStream(inputFile);
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            InputStream in = new BufferedInputStream(inputStream);
            IConverter converter = LocalConverter.builder()
                    .baseFolder(new File(tmpFile.getParent()))
                    .workerPool(20, 25, 2, TimeUnit.SECONDS)
                    .enable(MicrosoftPowerpointBridge.class)
                    .processTimeout(5, TimeUnit.SECONDS)
                    .build();

            Future<Boolean> conversion = converter
                    .convert(in).as(docType)
                    .to(bo).as(DocumentType.PDF)
                    .prioritizeWith(1000) // optional
                    .schedule();

            {
                try {
                    conversion.get();
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(DocDetectorConverterImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            OutputStream outputStream = new FileOutputStream(tmpFile);
            bo.writeTo(outputStream);
            in.close();
            bo.close();
            result = true;
        } catch (IOException ex) {
            return result;

        }

        return result;
    }
}
