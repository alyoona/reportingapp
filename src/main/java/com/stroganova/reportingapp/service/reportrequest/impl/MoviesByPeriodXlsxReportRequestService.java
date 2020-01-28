package com.stroganova.reportingapp.service.reportrequest.impl;


import com.stroganova.reportingapp.entity.Movie;
import com.stroganova.reportingapp.enums.ReportFormat;
import com.stroganova.reportingapp.enums.ReportRequestType;
import com.stroganova.reportingapp.enums.ReportType;
import com.stroganova.reportingapp.reports.*;
import com.stroganova.reportingapp.repository.MovieRepository;
import com.stroganova.reportingapp.service.reportgenerator.MovieExcelReportGenerator;
import com.stroganova.reportingapp.service.reportrequest.ReportRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MoviesByPeriodXlsxReportRequestService implements ReportRequestService {


    private final MovieRepository movieRepository;
    private final MovieExcelReportGenerator reportGenerator;

    public MoviesByPeriodXlsxReportRequestService(MovieRepository movieRepository, MovieExcelReportGenerator reportGenerator) {
        this.movieRepository = movieRepository;
        this.reportGenerator = reportGenerator;
    }


    @Override
    public ReportRequest handle(ReportRequest reportRequest) {

        log.info("handle requestByPeriod {}", reportRequest);

        ReportRequestByPeriod requestByPeriod = (ReportRequestByPeriod) reportRequest;
        DatePeriod datePeriod = requestByPeriod.getDatePeriod();

        if (Thread.currentThread().isInterrupted()) {
            log.info("Thread.currentThread().isInterrupted()");
            return reportRequest;
        }
        log.info("movieRepository {}", movieRepository);
        List<Movie> movies = movieRepository.findAllByYearBetween(datePeriod.getForm(), datePeriod.getTo());
        log.info("movies {}", movies.size());

        log.info("reportGenerator {}", reportGenerator);
        ReportRequest generatedReportRequest = reportGenerator.generate(reportRequest, movies);
        log.info("generatedReportRequest {}", generatedReportRequest);
        return generatedReportRequest;
    }

    @Override
    public ReportRequestInfo getReportRequestInfo() {
        ReportTypeAndFormat typeAndFormat = new ReportTypeAndFormat(ReportType.MOVIES_BY_PERIOD, ReportFormat.XLSX);
        return new ReportRequestInfo(ReportRequestType.REPORT_ADD, typeAndFormat);
    }



}
