package com.stroganova.reportingapp.jms;

import com.stroganova.reportingapp.enums.ReportFormat;
import com.stroganova.reportingapp.enums.ReportRequestType;
import com.stroganova.reportingapp.enums.ReportType;
import com.stroganova.reportingapp.reports.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.time.LocalDate;
import java.util.Map;


@Slf4j
@Component
public class ReportRequestMessageConverter implements MessageConverter {

    @Override
    public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {

        log.info("Start converting toMessage, object: {}", object);

        ReportRequest request = (ReportRequest) object;

        Message message = session.createMessage();
        message.setStringProperty(ReportRequestProperties.REPORT_REQUEST_IDENTIFIER, request.getUuid());

        ReportRequestType requestType = request.getReportRequestInfo().getReportRequestType();

        if (ReportRequestType.GET_LINK == requestType) {
            message.setStringProperty(ReportRequestProperties.REPORT_LINK, request.getLink());
        } else {
            message.setStringProperty(ReportRequestProperties.REPORT_STATUS, request.getStatus().name());
        }

        log.info("message = {}", message);

        return message;
    }

    @Override
    public Object fromMessage(Message message) throws JMSException, MessageConversionException {

        log.info("Start converting fromMessage, message: {}", message);

        String uuid = message.getStringProperty(ReportRequestProperties.REPORT_REQUEST_IDENTIFIER);
        ReportRequestType reportRequestType = ReportRequestType.getByName(message.getStringProperty(ReportRequestProperties.REPORT_REQUEST_TYPE));

        if (ReportRequestType.REPORT_ADD == reportRequestType) {

            String reportType = message.getStringProperty(ReportRequestProperties.REPORT_TYPE);
            String reportFormat = message.getStringProperty(ReportRequestProperties.REPORT_FORMAT);

            ReportTypeAndFormat typeAndFormat = getTypeAndFormat(reportType, reportFormat);

            if (ReportType.MOVIES_BY_PERIOD == typeAndFormat.getReportType()) {
                @SuppressWarnings("unchecked")
                Map<String, String> datePeriodMap = (Map<String, String>) message.getObjectProperty(ReportRequestProperties.REPORT_DATE_PERIOD_MAP);

                return createReportRequestByPeriod(uuid, reportRequestType, typeAndFormat, datePeriodMap);
            }
            return createReportRequest(uuid, reportRequestType, typeAndFormat);
        } else {
            return createReportRequest(uuid, reportRequestType);
        }


    }

    private ReportRequest createReportRequest(String uuid, ReportRequestType reportRequestType) {
        log.info("creating ReportRequest: {}, {}, {}", reportRequestType);
        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setUuid(uuid);
        reportRequest.setReportRequestInfo(new ReportRequestInfo(reportRequestType, null));
        return reportRequest;
    }


    private ReportRequest createReportRequest(String uuid, ReportRequestType reportRequestType, ReportTypeAndFormat typeAndFormat) {
        log.info("creating ReportRequest: {}, {}, {}", reportRequestType, typeAndFormat);
        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setUuid(uuid);
        reportRequest.setReportRequestInfo(new ReportRequestInfo(reportRequestType, typeAndFormat));
        return reportRequest;
    }

    private ReportRequest createReportRequestByPeriod(String uuid, ReportRequestType reportRequestType, ReportTypeAndFormat typeAndFormat, Map<String, String> datePeriodMap) {

        log.info("creating ReportRequest: {}, {}, {}", reportRequestType, typeAndFormat, datePeriodMap);

        if (datePeriodMap == null || datePeriodMap.size() == 0) {
            throw new IllegalArgumentException("MOVIES_BY_PERIOD report type requires date period info");
        }
        LocalDate dateFrom = LocalDate.parse(datePeriodMap.get("dateFrom"));
        LocalDate dateTo = LocalDate.parse(datePeriodMap.get("dateTo"));
        DatePeriod datePeriod = new DatePeriod(dateFrom, dateTo);

        ReportRequestByPeriod reportRequest = new ReportRequestByPeriod();
        reportRequest.setUuid(uuid);
        reportRequest.setReportRequestInfo(new ReportRequestInfo(reportRequestType, typeAndFormat));
        reportRequest.setDatePeriod(datePeriod);

        log.info("created ReportRequestByPeriod = {}", reportRequest);
        return reportRequest;
    }

    private ReportTypeAndFormat getTypeAndFormat(String reportType, String reportFormat) {
        ReportType type = ReportType.getByName(reportType);
        ReportFormat format = ReportFormat.getByName(reportFormat);
        return new ReportTypeAndFormat(type, format);
    }


}
