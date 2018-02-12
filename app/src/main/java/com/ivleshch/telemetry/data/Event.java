package com.ivleshch.telemetry.data;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Ivleshch on 10.01.2018.
 */

public class Event extends RealmObject {

    @PrimaryKey
    @SerializedName("UID")
    private String uid;
    @SerializedName("DATE")
    private Long date;
    @SerializedName("QUANTITY")
    private Integer count;
    @SerializedName("WORK_CENTER")
    private String workCenter;

    public Event(){

    }

    public Event(String uid, String date, String count, String workCenter) {
        this.uid = uid;
        this.date = Long.parseLong(date);
        this.count = Integer.parseInt(count);
        this.workCenter = workCenter;
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

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getWorkCenter() {
        return workCenter;
    }

    public void setWorkCenter(String workCenter) {
        this.workCenter = workCenter;
    }
}
