package com.stroganova.reportingapp.enums;

public enum ReportRequestType {

    REPORT_ADD("report"),

    GET_LINK("link"),

    CHECK_STATUS("status");

    private final String name;

    ReportRequestType(String name) {
        this.name = name;
    }

    public static ReportRequestType getByName(String name) {
        ReportRequestType[] reportRequestTypes = values();
        for (ReportRequestType reportRequestType : reportRequestTypes) {
            if (reportRequestType.getName().equalsIgnoreCase(name)) {
                return reportRequestType;
            }
        }
        throw new IllegalArgumentException("Not supported report request type: " + name);
    }

    public String getName() {
        return name;
    }



}
