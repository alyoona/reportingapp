package com.stroganova.reportingapp.service.reportwriter;

import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface XlsxReportWriterService <T> {

     void writeIntoExcel(List<T> rows, OutputStream outputStream, Workbook workbook) throws IOException;
}
