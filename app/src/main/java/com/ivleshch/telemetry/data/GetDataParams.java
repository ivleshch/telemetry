package com.ivleshch.telemetry.data;

import java.util.Date;

/**
 * Created by Ivleshch on 01.02.2018.
 */

public class GetDataParams {

    Date startOfShift;
    Date endOfShift;
    boolean timer;
    boolean updateReport;

    public GetDataParams(Date startOfShift, Date endOfShift, boolean timer, boolean updateReport) {
        this.startOfShift = startOfShift;
        this.endOfShift = endOfShift;
        this.timer = timer;
        this.updateReport = updateReport;
    }
}
