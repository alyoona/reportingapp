package com.stroganova.reportingapp.reports;

import lombok.*;



@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ReportRequestByPeriod extends ReportRequest {

    private DatePeriod datePeriod;
}
