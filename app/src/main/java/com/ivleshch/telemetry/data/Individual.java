package com.ivleshch.telemetry.data;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Ivleshch on 31.01.2018.
 */

public class Individual extends RealmObject {

    @PrimaryKey
    private String uid;
    private String description;
    private String descriptionShort;
    private Boolean deletionMark;

    public String getDescriptionShort() {
        return descriptionShort;
    }

    public void setDescriptionShort(String descriptionShort) {
        this.descriptionShort = descriptionShort;
    }

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

    public static Individual findObject(String uid){

        Individual newObject = null;

        Realm realm;
        realm = Realm.getDefaultInstance();
        Individual object = realm.where(Individual.class).
                equalTo("uid", uid).
                findFirst();

        if (object==null) {
            newObject = new Individual();
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
