package com.ivleshch.telemetry.data;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Ivleshch on 11.01.2018.
 */

public class Reason extends RealmObject {

    @PrimaryKey
    private Integer id;
    private Integer code;
    private String  reason;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public static Reason findObject(Integer id){

        Reason newObject = null;

        Realm realm;
        realm = Realm.getDefaultInstance();
        RealmResults<Reason> object = realm.where(Reason.class).
                equalTo("id", id).
                findAll();

        if(object.size()>0){
            newObject = object.get(0);
        }


        return newObject;
    }
}
