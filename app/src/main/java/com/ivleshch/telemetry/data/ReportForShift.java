package com.ivleshch.telemetry.data;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Ivleshch on 31.01.2018.
 */

public class ReportForShift extends RealmObject{

    @PrimaryKey
    private String uid;
    private Boolean deletionMark;
    private Boolean finished;
    private Individual shiftMaster;
    private WorkCenter workCenter;
    private Date startOfShift;
    private Date endOfShift;
    private Boolean conducted;


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Boolean getDeletionMark() {
        return deletionMark;
    }

    public void setDeletionMark(Boolean deletionMark) {
        this.deletionMark = deletionMark;
    }

    public Boolean getFinished() {
        return finished;
    }

    public void setFinished(Boolean finished) {
        this.finished = finished;
    }

    public Individual getShiftMaster() {
        return shiftMaster;
    }

    public void setShiftMaster(Individual shiftMaster) {
        this.shiftMaster = shiftMaster;
    }

    public WorkCenter getWorkCenter() {
        return workCenter;
    }

    public void setWorkCenter(WorkCenter workCenter) {
        this.workCenter = workCenter;
    }

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

    public Boolean getConducted() {
        return conducted;
    }

    public void setConducted(Boolean conducted) {
        this.conducted = conducted;
    }
}
