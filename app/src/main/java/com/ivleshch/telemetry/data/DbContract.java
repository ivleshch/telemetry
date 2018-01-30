package com.ivleshch.telemetry.data;

/**
 * Created by Ivleshch on 11.01.2018.
 */

 public class DbContract {

    static final String DATABASE_SERVER                         = "10.1.1.5";
    static final String DATABASE_NAME                           = "telemetry";
    static final String DATABASE_USER                           = "ivlesch";
    static final String DATABASE_PASSWORD                       = "Io1UfDA6N";

    static final String DATABASE_DRIVER                         = "com.mysql.jdbc.Driver";

    public static final String DATABASE_TABLE_REASONS                   = "reasons";
    public static final Integer DATABASE_TABLE_REASONS_ID               = 1;
    public static final String DATABASE_TABLE_REASONS_COLUMN_ID         = "id";
    public static final String DATABASE_TABLE_REASONS_COLUMN_CODE       = "code";
    public static final String DATABASE_TABLE_REASONS_COLUMN_REASON     = "reason";

    public static final String DATABASE_TABLE_RAW_EVENTS                = "aggregated_events";
    public static final Integer DATABASE_TABLE_RAW_EVENTS_ID            = 2;
    public static final String DATABASE_TABLE_RAW_EVENTS_COLUMN_ID      = "id";
    public static final String DATABASE_TABLE_RAW_EVENTS_COLUMN_DATE    = "date";
    public static final String DATABASE_TABLE_RAW_EVENTS_COLUMN_TIME    = "time";
    public static final String DATABASE_TABLE_RAW_EVENTS_COLUMN_COUNT   = "count";
    public static final String DATABASE_TABLE_RAW_EVENTS_COLUMN_DEVICE       = "device";


    public static final String DATABASE_TABLE_STOPS                     = "stops";
    public static final Integer DATABASE_TABLE_STOPS_ID                 = 3;
    public static final String DATABASE_TABLE_STOPS_COLUMN_ID           = "id";
    public static final String DATABASE_TABLE_STOPS_COLUMN_MOMENT       = "moment";
    public static final String DATABASE_TABLE_STOPS_COLUMN_DATE         = "date";
    public static final String DATABASE_TABLE_STOPS_COLUMN_DURATION     = "duration";
    public static final String DATABASE_TABLE_STOPS_COLUMN_REASON       = "reason";
    public static final String DATABASE_TABLE_STOPS_COLUMN_DEVICE       = "device";

    public static final String DATABASE_TABLE_DEVICES                   = "devices";
    public static final Integer DATABASE_TABLE_DEVICES_ID               = 4;
    public static final String DATABASE_TABLE_DEVICES_COLUMN_ID          = "id";
    public static final String DATABASE_TABLE_DEVICES_COLUMN_DEVICE_NAME = "device_name";
    public static final String DATABASE_TABLE_DEVICES_COLUMN_UUID        = "uuid";



    static final String DATABASE_TABLE_RAW_EVENTS_GET           =   "" +
                                                                    " Select " +
                                                                        " aggregated_events.date as date, "+
                                                                        " aggregated_events.time as time, " +
                                                                        " aggregated_events.id as id, "+
                                                                        " aggregated_events.device as device, "+
                                                                        " aggregated_events.count as count "+
                                                                    " From "+
                                                                        " telemetry.aggregated_events as aggregated_events "+
                                                                    " Where "+
                                                                        " aggregated_events.timestamp between '%1$s' and '%2$s';";


    static final String DATABASE_TABLE_REASONS_GET              = "" +
                                                                    " Select " +
                                                                        " reasons.id, " +
                                                                        " reasons.code, " +
                                                                        " reasons.reason " +
                                                                    " From " +
                                                                        " telemetry.reasons as reasons;";


    static final String DATABASE_TABLE_STOPS_GET                = "" +
                                                                    " Select "+
                                                                        " stops.moment as moment, "+
                                                                        " stops.id as id, "+
                                                                        " stops.duration as duration, "+
                                                                        " stops.device as device, "+
                                                                        " stops.reason as reason "+
                                                                    " From "+
                                                                        " telemetry.stops as stops "+
                                                                    " Where "+
                                                                        " stops.moment between %1$s and %2$s;";

    static final String DATABASE_TABLE_DEVISES_GET              = "" +
                                                                     " Select " +
                                                                        " devices.id as id, "+
                                                                        " devices.device_name as device_name, "+
                                                                        " devices.device_uuid as uuid " +
                                                                     " From " +
                                                                        " telemetry.devices as devices;";

}
