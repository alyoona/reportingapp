package com.stroganova.reportingapp.service.reportgenerator;

import com.stroganova.reportingapp.entity.Movie;
import com.stroganova.reportingapp.enums.ReportStatus;
import com.stroganova.reportingapp.reports.ReportRequest;
import com.stroganova.reportingapp.reports.ReportRequestInfo;
import com.stroganova.reportingapp.repository.ReportRequestRepository;
import com.stroganova.reportingapp.service.reportwriter.ReportStoreService;
import com.stroganova.reportingapp.service.reportwriter.XlsxReportWriterService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Service
public class DefaultMovieExcelReportGenerator implements MovieExcelReportGenerator {

    private final ReportStoreService storeService;
    private final ReportRequestRepository reportRequestRepository;
    private final XlsxReportWriterService<Movie> writer;

    @Value("${report.directory.format.excel:\\excel}")
    private String reportPathByFormat;


    public DefaultMovieExcelReportGenerator(ReportStoreService storeService
            , ReportRequestRepository reportRequestRepository
            , XlsxReportWriterService<Movie> writer) {
        this.storeService = storeService;
        this.reportRequestRepository = reportRequestRepository;
        this.writer = writer;
    }

    @Override
    public ReportRequest generate(ReportRequest reportRequest, List<Movie> movies) {

        String uuid = reportRequest.getUuid();
        ReportRequestInfo reportRequestInfo = reportRequest.getReportRequestInfo();

        try (OutputStream outputStream = storeService.getOutputStream(reportRequestInfo, uuid);
             Workbook workbook = new HSSFWorkbook()) {

            if (Thread.currentThread().isInterrupted()) {
                return reportRequest;
            }

            writer.writeIntoExcel(movies, outputStream, workbook);

            reportRequest.setLink(storeService.getReportLink(reportRequestInfo, uuid));
            reportRequest.setStatus(ReportStatus.DONE);

            return reportRequestRepository.save(reportRequest);

        } catch (IOException e) {
            reportRequest.setStatus(ReportStatus.ERROR);
            reportRequestRepository.save(reportRequest);
            throw new RuntimeException("Report generation error", e);
        }
    }
}

