package com.ivleshch.telemetry.data;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Ivleshch on 10.01.2018.
 */

public class Stop extends RealmObject {

    @PrimaryKey
    @SerializedName("UID")
    private String uid;
    @SerializedName("DATE")
    private Long date;
    @SerializedName("DURATION")
    private Integer duration;
    @SerializedName("REASON")
    private Integer reason;
    @SerializedName("REASON_DESCRIPTION")
    private String reasonDescription;
    @SerializedName("WORK_CENTER")
    private String workCenter;

    public Stop(String uid, String date, String duration, String reason, String reasonDescription, String workCenter) {
        this.uid = uid;
        this.date = Long.parseLong(date);;
        this.duration = Integer.parseInt(duration);
        this.reason = Integer.parseInt(reason);
        this.reasonDescription = reasonDescription;
        this.workCenter = workCenter;
    }

    public Stop() {

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getReason() {
        return reason;
    }

    public void setReason(Integer reason) {
        this.reason = reason;
    }

    public String getReasonDescription() {
        return reasonDescription;
    }

    public void setReasonDescription(String reasonDescription) {
        this.reasonDescription = reasonDescription;
    }

    public String getWorkCenter() {
        return workCenter;
    }

    public void setWorkCenter(String workCenter) {
        this.workCenter = workCenter;
    }
}
