package com.ivleshch.telemetry;

import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * Created by Ivleshch on 16.02.2018.
 */

public class Migration implements RealmMigration {

    @Override
    public void migrate(final DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        if (oldVersion == 1) {
            UpdateV2(schema);
            oldVersion++;
        }
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Migration);
    }

    private void UpdateV2(RealmSchema schema){
        RealmObjectSchema ReportForShiftSchema = schema.get("ReportForShift");
        ReportForShiftSchema.addField("conducted",Boolean.class);

        RealmObjectSchema ReportForShiftProductSchema = schema.get("ReportForShiftProduct");
        ReportForShiftProductSchema.addField("start",Date.class);
        ReportForShiftProductSchema.addField("end",Date.class);
    }

}
