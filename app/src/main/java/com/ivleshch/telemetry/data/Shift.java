package com.ivleshch.telemetry.data;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Ivleshch on 31.01.2018.
 */

public class Shift extends RealmObject {

    @PrimaryKey
    private String uid;
    private Date date;
    private Date startOfShift;
    private Date endOfShift;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
}
