package com.stroganova.reportingapp.reports;


import com.stroganova.reportingapp.enums.ReportStatus;
import lombok.Data;
import lombok.NonNull;

import javax.persistence.Entity;
import javax.persistence.Id;


@Data
@Entity
public class ReportRequest {

    public ReportRequest() {
    }

    @NonNull
    @Id
    private String uuid;


    private ReportRequestInfo reportRequestInfo;

    private String link;
    private ReportStatus status;



}
