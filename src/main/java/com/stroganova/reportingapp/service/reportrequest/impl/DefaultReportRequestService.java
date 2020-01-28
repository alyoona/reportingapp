package com.stroganova.reportingapp.service.reportrequest.impl;

import com.stroganova.reportingapp.enums.ReportRequestType;
import com.stroganova.reportingapp.reports.ReportRequest;
import com.stroganova.reportingapp.reports.ReportRequestInfo;
import com.stroganova.reportingapp.reports.ReportTypeAndFormat;
import com.stroganova.reportingapp.service.reportrequest.ReportRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class DefaultReportRequestService implements ReportRequestService {

//    private final ReportRequestRepository reportRepository;
//
//    public DefaultReportRequestService(ReportRequestRepository reportRepository) {
//        this.reportRepository = reportRepository;
//    }


    @Override
    public ReportRequest handle(ReportRequest reportRequest) {
        //reportRepository.findById(reportRequest.getUuid()).get();
        return null;
    }

    @Override
    public ReportRequestInfo getReportRequestInfo() {
        return new ReportRequestInfo(ReportRequestType.GET_LINK, new ReportTypeAndFormat(null, null));
    }
}
