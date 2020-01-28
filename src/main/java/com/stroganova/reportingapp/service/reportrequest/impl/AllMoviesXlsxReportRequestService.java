package com.stroganova.reportingapp.service.reportrequest.impl;


import com.stroganova.reportingapp.entity.Movie;
import com.stroganova.reportingapp.enums.ReportFormat;
import com.stroganova.reportingapp.enums.ReportRequestType;
import com.stroganova.reportingapp.enums.ReportType;
import com.stroganova.reportingapp.reports.ReportRequest;
import com.stroganova.reportingapp.reports.ReportRequestInfo;
import com.stroganova.reportingapp.reports.ReportTypeAndFormat;
import com.stroganova.reportingapp.repository.MovieRepository;
import com.stroganova.reportingapp.service.reportgenerator.MovieExcelReportGenerator;
import com.stroganova.reportingapp.service.reportrequest.ReportRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AllMoviesXlsxReportRequestService implements ReportRequestService {

    private final MovieRepository movieRepository;
    private final MovieExcelReportGenerator reportGenerator;

    public AllMoviesXlsxReportRequestService(MovieRepository movieRepository, MovieExcelReportGenerator reportGenerator) {
        this.movieRepository = movieRepository;
        this.reportGenerator = reportGenerator;
    }

    @Override
    public ReportRequest handle(ReportRequest reportRequest) {
        log.info("handle request {}", reportRequest);

        if (Thread.currentThread().isInterrupted()) {
            log.info("Thread.currentThread().isInterrupted()");
            return reportRequest;
        }

        List<Movie> movies = movieRepository.findAll();
        return reportGenerator.generate(reportRequest, movies);
    }

    @Override
    public ReportRequestInfo getReportRequestInfo() {
        log.info("Getting ReportRequestInfo ...");
        ReportTypeAndFormat typeAndFormat = new ReportTypeAndFormat(ReportType.ALL_MOVIES, ReportFormat.XLSX);
        return new ReportRequestInfo(ReportRequestType.REPORT_ADD, typeAndFormat);
    }
}
