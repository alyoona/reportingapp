package com.stroganova.reportingapp.service.reportgenerator;

import com.stroganova.reportingapp.entity.Movie;
import com.stroganova.reportingapp.reports.ReportRequest;

import java.util.List;

public interface MovieExcelReportGenerator {

    ReportRequest generate(ReportRequest reportRequest, List<Movie> movies);
}
