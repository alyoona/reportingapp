package com.stroganova.reportingapp.service.reportwriter;

import com.stroganova.reportingapp.reports.ReportRequestInfo;

import java.io.IOException;
import java.io.OutputStream;


public interface ReportStoreService {

    OutputStream getOutputStream(ReportRequestInfo info, String uuid) throws IOException;

    String getReportLink(ReportRequestInfo info, String uuid);

}
