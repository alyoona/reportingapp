package com.stroganova.reportingapp.reports;


import com.stroganova.reportingapp.enums.ReportRequestType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import javax.persistence.Embeddable;


@Data
@AllArgsConstructor
@Getter

@Embeddable
public class ReportRequestInfo {

    private ReportRequestType reportRequestType;
    private ReportTypeAndFormat reportTypeAndFormat;
}
