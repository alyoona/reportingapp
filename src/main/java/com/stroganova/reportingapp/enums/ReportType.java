package com.stroganova.reportingapp.enums;

public enum ReportType {
    ALL_MOVIES("AllMovies"),
    MOVIES_BY_PERIOD("MoviesByPeriod"),
    USERS("Users");

     private final String name;


    ReportType(String name) {
        this.name = name;
    }

    public static ReportType getByName(String name) {
        ReportType[] types = values();
        for (ReportType type : types) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Not supported report type: " + name);
    }

    public String getName() {
        return name;
    }

}
