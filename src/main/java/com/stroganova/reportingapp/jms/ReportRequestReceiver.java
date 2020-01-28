package com.stroganova.reportingapp.jms;

import com.stroganova.reportingapp.enums.ReportRequestType;
import com.stroganova.reportingapp.enums.ReportStatus;
import com.stroganova.reportingapp.reports.ReportRequest;
import com.stroganova.reportingapp.service.reportrequest.ReportRequestService;
import com.stroganova.reportingapp.service.reportrequest.ReportRequestServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.support.JmsHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class ReportRequestReceiver {

    private ReportRequestServiceRegistry registry;

    private final Map<String, ReportRequest> cache = new ConcurrentHashMap<>();

    public ReportRequestReceiver(ReportRequestServiceRegistry registry) {
        this.registry = registry;
    }

    @JmsListener(destination = "${jms.queue.destination.request}")
    @SendTo(value = "${jms.queue.destination.response}")
    public ReportRequest receive(ReportRequest reportRequest, @Header(JmsHeaders.REDELIVERED) Boolean isRedelivered) {

        log.info("START processing reportRequest", reportRequest);

        ReportRequestService reportRequestService = registry.getService(reportRequest.getReportRequestInfo());
        log.info("current reportRequestService: {} ", reportRequestService);

        if (Thread.currentThread().isInterrupted()) {
            log.info("Thread.currentThread().isInterrupted()");
            return null;
        }

        if (isRedelivered) {
            ReportRequest cachedReportRequest = cache.get(reportRequest.getUuid());
            if (ReportStatus.DONE == reportRequest.getStatus()) {
                log.info("redelivered request has already been processed and saved to db");
                return resolveSendingToResponseQueue(cachedReportRequest);
            } else {
                log.info("process redelivered request for creating report");
                return process(reportRequest, reportRequestService);
            }
        } else {
            log.info("new reportRequest");
            return process(reportRequest, reportRequestService);
        }

    }

    private ReportRequest process(ReportRequest reportRequest, ReportRequestService reportRequestService) {
        log.info("process request for creating report");
        ReportRequest processedReportRequest = reportRequestService.handle(reportRequest);
        log.info("processed request: {}", processedReportRequest);

        cache.put(processedReportRequest.getUuid(), processedReportRequest);

        return resolveSendingToResponseQueue(processedReportRequest);
    }


    private ReportRequest resolveSendingToResponseQueue(ReportRequest reportRequest) {
        ReportRequestType requestType = reportRequest.getReportRequestInfo().getReportRequestType();
        if (ReportRequestType.REPORT_ADD == requestType) {
            log.info("return null allows not to send message to response queue. toMessage won't be invoked by converter");
            return null;
        } else {
            log.info("process requests for getting report link or status");
            log.info("send message to response queue, report will be processed by toMessage converter method");
            return reportRequest;
        }
    }


}
