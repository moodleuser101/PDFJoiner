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
package com.reid.pdfjoiner.primitive;

import java.io.File;

/**
 * A data transfer object to store the result of a conversion/detection event
 *
 * @author pmreid
 */
public class ConversionResult {

    private boolean success;
    private File tempFile;
    private int source_file_type;

    public ConversionResult() {
        this.success = false;
    }

    public ConversionResult(boolean s, File f, int t) {
        this.success = s;
        this.tempFile = f;
        this.source_file_type = t;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public File getTempFile() {
        return tempFile;
    }

    public void setTempFile(File file) {
        this.tempFile = file;
    }

    public int getSource_file_type() {
        return source_file_type;
    }

    public void setSource_file_type(int source_file_type) {
        this.source_file_type = source_file_type;
    }

}
