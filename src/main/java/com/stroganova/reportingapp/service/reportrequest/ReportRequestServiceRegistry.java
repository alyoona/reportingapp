package com.stroganova.reportingapp.service.reportrequest;

import com.stroganova.reportingapp.reports.ReportRequestInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportRequestServiceRegistry {

    private Map<ReportRequestInfo, ReportRequestService> registry  = new HashMap<>();
    private final List<ReportRequestService> list;
    public ReportRequestServiceRegistry(List<ReportRequestService> list) {
        this.list = list;
    }

    @PostConstruct
    public void init() {
        for (ReportRequestService service : list) {
            registry.put(service.getReportRequestInfo(), service);
        }
    }

    public ReportRequestService getService(ReportRequestInfo reportRequestInfo) {
        log.info("reportRequestInfo: {}", reportRequestInfo);
        log.info("Getting ReportRequestService from registry {}", registry);
        return registry.get(reportRequestInfo);
    }



}
