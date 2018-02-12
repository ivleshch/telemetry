package com.ivleshch.telemetry.data;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Ivleshch on 31.01.2018.
 */

public class WorkCenter extends RealmObject {

    @PrimaryKey
    private String uid;
    private String description;
    private Boolean deletionMark;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getDeletionMark() {
        return deletionMark;
    }

    public void setDeletionMark(Boolean deletionMark) {
        this.deletionMark = deletionMark;
    }

    public static WorkCenter findObject(String uid){

        WorkCenter newObject = null;

        Realm realm;
        realm = Realm.getDefaultInstance();
        WorkCenter object = realm.where(WorkCenter.class).
                equalTo("uid", uid).
                findFirst();

        if (object==null) {
            newObject = new WorkCenter();
            newObject.setDescription("");
            newObject.setUid(uid);

            realm.beginTransaction();
            realm.insertOrUpdate(newObject);
            realm.commitTransaction();

        }else{
            newObject = realm.copyFromRealm(object);
        }

        realm.close();

        return newObject;
    }

}
