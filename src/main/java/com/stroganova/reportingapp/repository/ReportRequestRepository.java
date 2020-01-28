package com.stroganova.reportingapp.repository;

import com.stroganova.reportingapp.reports.ReportRequest;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ReportRequestRepository extends JpaRepository<ReportRequest, String> {

}
