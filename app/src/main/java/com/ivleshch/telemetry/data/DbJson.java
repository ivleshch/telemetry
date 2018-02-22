package com.ivleshch.telemetry.data;

/**
 * Created by Ivleshch on 09.02.2018.
 */

public class DbJson {

    private Long startOfShift;
    private Long endOfShift;
    private boolean updateReport;
    private Long lastUpdate;

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

    public Long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }


    public DbJson(Long startOfShift, Long endOfShift, boolean updateReport, Long lastUpdate) {
        this.startOfShift = startOfShift;
        this.endOfShift = endOfShift;
        this.updateReport = updateReport;
        this.lastUpdate = lastUpdate;
    }
}
