package com.stroganova.reportingapp.service.reportrequest;

import com.stroganova.reportingapp.reports.ReportRequest;
import com.stroganova.reportingapp.reports.ReportRequestInfo;

public interface ReportRequestService {

    ReportRequest handle(ReportRequest reportRequest);


    ReportRequestInfo getReportRequestInfo();
}
