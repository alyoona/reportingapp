package com.stroganova.reportingapp.service.reportwriter.impl;

import com.stroganova.reportingapp.enums.ReportFormat;
import com.stroganova.reportingapp.enums.ReportRequestType;
import com.stroganova.reportingapp.enums.ReportType;
import com.stroganova.reportingapp.reports.ReportRequestInfo;
import com.stroganova.reportingapp.reports.ReportTypeAndFormat;
import org.junit.jupiter.api.Test;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

class DefaultReportStoreServiceTest {
    @Test
    void getReportLink() throws NoSuchFieldException {

        DefaultReportStoreService storeService = new DefaultReportStoreService();
        Field field = DefaultReportStoreService.class.getDeclaredField("rootPath");
        field.setAccessible(true);
        String root = Paths.get(".\\reportStorage").toAbsolutePath().toString();
        ReflectionUtils.setField(field, storeService, root);
        field.setAccessible(false);
        ReportTypeAndFormat typeAndFormat = new ReportTypeAndFormat(ReportType.ALL_MOVIES, ReportFormat.XLSX);
        ReportRequestInfo info = new ReportRequestInfo(ReportRequestType.REPORT_ADD, typeAndFormat);
        String uuid = "da9b821f-e3e0-4c0e-b6dc-79b38952596d";

        System.out.println("storeService.getReportLink() = " + storeService.getReportLink(info, uuid));
        String expected = root + "\\XLSX\\ALL_MOVIES\\ALL_MOVIES_da9b821f-e3e0-4c0e-b6dc-79b38952596d.xlsx";

        assertEquals(expected, storeService.getReportLink(info,uuid));
    }


}