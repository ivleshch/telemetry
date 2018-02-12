package com.ivleshch.telemetry.data;

import org.kobjects.base64.Base64;

/**
 * Created by Ivleshch on 11.01.2018.
 */

public class DbContract {

    public static final String CHECK_URL = "http://10.1.1.11:8080/ERP_Telemetry/ws/ExchangeMobile.1cws?wsdl";
    public static final String URL = "http://10.1.1.11:8080/ERP_Telemetry/ws/ExchangeMobile.1cws";
    public static final String SOAP_ACTION = "10.1.1.11:8080/ERP_Telemetry/ws/ExchangeMobile.1cws?wsdl/";
    public static final String NAMESPACE = "ExchangeMobile";
    public static final String HEADER_TYPE = "Authorization";
    public static final String METHOD_NAME_REPORT = "GetReport";
    public static final String HEADER_DATA = "Basic " + Base64.encode("ExchangeMobile:P@ssw0rd".getBytes());

    public static final String REQUEST_PROPERTY_CONNECTION = "Connection";
    public static final String REQUEST_PROPERTY_CLOSE      = "close";
    public static final int    REQUEST_TIMEOUT             = 2000;

    public static final String DATABASE_TYPE_CATALOG_NOMENCLATURE = "Catalog_Nomenclature";
    public static final String DATABASE_TYPE_CATALOG_INDIVIDUAL = "Catalog_Individual";
    public static final String DATABASE_TYPE_CATALOG_WORK_CENTER = "Catalog_Work_Center";
    public static final String DATABASE_TYPE_DOCUMENT_REPORT_FOR_SHIFT = "Document_Report_For_Shift";
    public static final String DATABASE_TYPE_CATALOG_SHIFT = "Catalog_Shift";
    public static final String DATABASE_TYPE_INFOREG_RAW_EVENT = "InfoReg_Raw_Event";
    public static final String DATABASE_TYPE_INFOREG_STOP = "InfoReg_Stop";


    public static final String DATABASE_TYPE = "TYPE";
    public static final String DATABASE_DATA = "DATA";


    public static final String DATABASE_TABLE_REASONS = "reasons";
    public static final Integer DATABASE_TABLE_REASONS_ID = 1;
    public static final String DATABASE_TABLE_REASONS_COLUMN_ID = "id";
    public static final String DATABASE_TABLE_REASONS_COLUMN_CODE = "code";
    public static final String DATABASE_TABLE_REASONS_COLUMN_REASON = "reason";

    public static final String DATABASE_TABLE_RAW_EVENTS = "aggregated_events";
    public static final Integer DATABASE_TABLE_RAW_EVENTS_ID = 2;
    public static final String DATABASE_TABLE_RAW_EVENTS_COLUMN_ID = "id";
    public static final String DATABASE_TABLE_RAW_EVENTS_COLUMN_DATE = "date";
    public static final String DATABASE_TABLE_RAW_EVENTS_COLUMN_TIME = "time";
    public static final String DATABASE_TABLE_RAW_EVENTS_COLUMN_COUNT = "count";
    public static final String DATABASE_TABLE_RAW_EVENTS_COLUMN_DEVICE = "device";


    public static final String DATABASE_TABLE_STOPS = "stops";
    public static final Integer DATABASE_TABLE_STOPS_ID = 3;
    public static final String DATABASE_TABLE_STOPS_COLUMN_ID = "id";
    public static final String DATABASE_TABLE_STOPS_COLUMN_MOMENT = "moment";
    public static final String DATABASE_TABLE_STOPS_COLUMN_DATE = "date";
    public static final String DATABASE_TABLE_STOPS_COLUMN_DURATION = "duration";
    public static final String DATABASE_TABLE_STOPS_COLUMN_REASON = "reason";
    public static final String DATABASE_TABLE_STOPS_COLUMN_DEVICE = "device";

    public static final String DATABASE_TABLE_DEVICES = "devices";
    public static final Integer DATABASE_TABLE_DEVICES_ID = 4;
    public static final String DATABASE_TABLE_DEVICES_COLUMN_ID = "id";
    public static final String DATABASE_TABLE_DEVICES_COLUMN_DEVICE_NAME = "device_name";
    public static final String DATABASE_TABLE_DEVICES_COLUMN_UUID = "uuid";


    public static final String DATABASE_TABLE_LINE_INFORMATION_WORK_CENTER_UID      = "work_center_uid";
    public static final String DATABASE_TABLE_LINE_INFORMATION_NOMENCLATURE_UID      = "nomenclature_uid";
    public static final String DATABASE_TABLE_LINE_INFORMATION_START_OF_SHIFT       = "start_of_shift";
    public static final String DATABASE_TABLE_LINE_INFORMATION_END_OF_SHIFT         = "end_of_shift";
    public static final String DATABASE_TABLE_LINE_INFORMATION_WORK_CENTER          = "work_center";
    public static final String DATABASE_TABLE_LINE_INFORMATION_INDIVIDUAL           = "individual";
    public static final String DATABASE_TABLE_LINE_INFORMATION_NOMENCLATURE         = "nomenclature";
    public static final String DATABASE_TABLE_LINE_INFORMATION_QUANTITY_PLAN        = "quantity_plan";
    public static final String DATABASE_TABLE_LINE_INFORMATION_QUANTITY_FACT        = "quantity_fact";
    public static final String DATABASE_TABLE_LINE_INFORMATION_QUANTITY_DEFECT      = "quantity_defect";
    public static final String DATABASE_TABLE_LINE_INFORMATION_QUANTITY_WASTE       = "quantity_waste";
    public static final String DATABASE_TABLE_LINE_INFORMATION_AVAIBILITY_PERCENT   = "avaibility_percent";
    public static final String DATABASE_TABLE_LINE_INFORMATION_PERFORMANCE_PERCENT  = "performance_percent";
    public static final String DATABASE_TABLE_LINE_INFORMATION_OEE_PERCENT          = "oee_percent";
    public static final String DATABASE_TABLE_LINE_INFORMATION_QUANTITY_STOPS       = "quantity_stops";
    public static final String DATABASE_TABLE_LINE_INFORMATION_QUALITY_PERCENT      = "quality_percent";
    public static final String DATABASE_TABLE_LINE_INFORMATION_DURATION_STOPS       = "duration_stops";
    public static final String DATABASE_TABLE_LINE_INFORMATION_REASON_STOP          = "reason_stop";





    static final String DATABASE_TABLE_RAW_EVENTS_GET = "" +
            " Select " +
            " aggregated_events.date as date, " +
            " aggregated_events.time as time, " +
            " aggregated_events.id as id, " +
            " aggregated_events.device as device, " +
            " aggregated_events.count as count " +
            " From " +
            " telemetry.aggregated_events as aggregated_events " +
            " Where " +
            " aggregated_events.timestamp between '%1$s' and '%2$s';";


    static final String DATABASE_TABLE_REASONS_GET = "" +
            " Select " +
            " reasons.id, " +
            " reasons.code, " +
            " reasons.reason " +
            " From " +
            " telemetry.reasons as reasons;";


    static final String DATABASE_TABLE_STOPS_GET = "" +
            " Select " +
            " stops.moment as moment, " +
            " stops.id as id, " +
            " stops.duration as duration, " +
            " stops.device as device, " +
            " stops.reason as reason " +
            " From " +
            " telemetry.stops as stops " +
            " Where " +
            " stops.moment between %1$s and %2$s;";

    static final String DATABASE_TABLE_DEVISES_GET = "" +
            " Select " +
            " devices.id as id, " +
            " devices.device_name as device_name, " +
            " devices.device_uuid as uuid " +
            " From " +
            " telemetry.devices as devices;";

}
