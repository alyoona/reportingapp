package com.stroganova.reportingapp.reports;

import com.stroganova.reportingapp.enums.ReportFormat;
import com.stroganova.reportingapp.enums.ReportType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportTypeAndFormat {

    private ReportType reportType;
    private ReportFormat reportFormat;


}
