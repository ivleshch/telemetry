package com.ivleshch.telemetry.data;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Ivleshch on 22.01.2018.
 */

public class Device extends RealmObject {

    @PrimaryKey
    private String device_uuid;
    private String device_name;
    private int id;

    public String getDevice_uuid() {
        return device_uuid;
    }

    public void setDevice_uuid(String device_uuid) {
        this.device_uuid = device_uuid;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static Device findObject(Integer id){

        Device newObject = null;

        Realm realm;
        realm = Realm.getDefaultInstance();
        RealmResults<Device> object = realm.where(Device.class).
                equalTo("id", id).
                findAll();

        if(object.size()>0){
            newObject = object.get(0);
        }


        return newObject;
    }
}
