package com.ivleshch.telemetry.webService;

import java.util.Date;

/**
 * Created by Ivleshch on 01.02.2018.
 */

public class GetDataParams {

    Date startOfShift;
    Date endOfShift;
    boolean timer;
    boolean updateReport;
    String server;
    String webService;
    Date lastUpdate;

    public Date getStartOfShift() {
        return startOfShift;
    }

    public void setStartOfShift(Date startOfShift) {
        this.startOfShift = startOfShift;
    }

    public Date getEndOfShift() {
        return endOfShift;
    }

    public void setEndOfShift(Date endOfShift) {
        this.endOfShift = endOfShift;
    }

    public boolean isTimer() {
        return timer;
    }

    public void setTimer(boolean timer) {
        this.timer = timer;
    }

    public boolean isUpdateReport() {
        return updateReport;
    }

    public void setUpdateReport(boolean updateReport) {
        this.updateReport = updateReport;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getWebService() {
        return webService;
    }

    public void setWebService(String webService) {
        this.webService = webService;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public GetDataParams(Date startOfShift, Date endOfShift, boolean timer, boolean updateReport, String server, String webService, Date lastUpdate) {
        this.startOfShift = startOfShift;
        this.endOfShift = endOfShift;
        this.timer = timer;
        this.updateReport = updateReport;
        this.server = server;
        this.webService = webService;
        this.lastUpdate = lastUpdate;
    }
}
