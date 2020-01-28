package com.stroganova.reportingapp.reports;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DatePeriod {
    private LocalDate form;
    private LocalDate to;
}
