package com.ivleshch.telemetry.data;

/**
 * Created by Ivleshch on 09.02.2018.
 */

public class DbJson {

    Long startOfShift;
    Long endOfShift;
    boolean updateReport;

    public Long getStartOfShift() {
        return startOfShift;
    }

    public void setStartOfShift(Long startOfShift) {
        this.startOfShift = startOfShift;
    }

    public Long getEndOfShift() {
        return endOfShift;
    }

    public void setEndOfShift(Long endOfShift) {
        this.endOfShift = endOfShift;
    }

    public boolean isUpdateReport() {
        return updateReport;
    }

    public void setUpdateReport(boolean updateReport) {
        this.updateReport = updateReport;
    }

    public DbJson(Long startOfShift, Long endOfShift, boolean updateReport) {
        this.startOfShift = startOfShift;
        this.endOfShift = endOfShift;
        this.updateReport = updateReport;
    }
}
