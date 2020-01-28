package com.stroganova.reportingapp.enums;

import lombok.Getter;

@Getter
public enum  ReportFormat {
    PDF("pdf", ".pdf"), XLSX("xlsx", ".xlsx");

    private final String name;
    private final String fileExtension;


    ReportFormat(String name, String fileExtension) {
        this.name = name;
        this.fileExtension = fileExtension;
    }

    public static ReportFormat getByName(String name) {
        ReportFormat[] formats = values();
        for (ReportFormat format : formats) {
            if (format.getName().equalsIgnoreCase(name)) {
                return format;
            }
        }
        throw new IllegalArgumentException("Not supported report format: " + name);
    }
}
