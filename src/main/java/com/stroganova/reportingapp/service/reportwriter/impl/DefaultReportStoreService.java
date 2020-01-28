package com.stroganova.reportingapp.service.reportwriter.impl;

import com.stroganova.reportingapp.enums.ReportFormat;
import com.stroganova.reportingapp.enums.ReportType;
import com.stroganova.reportingapp.reports.ReportRequestInfo;
import com.stroganova.reportingapp.reports.ReportTypeAndFormat;
import com.stroganova.reportingapp.service.reportwriter.ReportStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class DefaultReportStoreService implements ReportStoreService {


    @Value("${report.store.root.path:.\\reportStorage}")
    private String rootPath;

    @Override
    public OutputStream getOutputStream(ReportRequestInfo info, String uuid) throws IOException {

        Path reportStoragePath = getReportStoragePath(info.getReportTypeAndFormat());
        log.info("check if parent dir exists then create");
        Files.createDirectories(reportStoragePath);
        Path reportFileNamePath = getReportFileNamePath(uuid, info.getReportTypeAndFormat());
        Path filePath = Paths.get(reportStoragePath.toString(), reportFileNamePath.toString());
        return new BufferedOutputStream(Files.newOutputStream(filePath));
    }

    @Override
    public String getReportLink(ReportRequestInfo info, String uuid) {
        Path reportStoragePath = getReportStoragePath(info.getReportTypeAndFormat());
        Path reportFileNamePath = getReportFileNamePath(uuid, info.getReportTypeAndFormat());
        Path filePath = Paths.get(reportStoragePath.toString(), reportFileNamePath.toString());
        return filePath.toAbsolutePath().toString();
    }


    private Path getReportStoragePath(ReportTypeAndFormat typeAndFormat) {
        ReportFormat format = typeAndFormat.getReportFormat();
        ReportType type = typeAndFormat.getReportType();
        return Paths.get(rootPath, File.separator + format + File.separator + type);
    }

    private Path getReportFileNamePath(String uuid, ReportTypeAndFormat typeAndFormat) {
        ReportFormat format = typeAndFormat.getReportFormat();
        ReportType type = typeAndFormat.getReportType();
        return Paths.get(File.separator + type + "_" + uuid + format.getFileExtension());
    }

}
