package com.ivleshch.telemetry.data;

import android.os.AsyncTask;
import android.util.Log;

import com.ivleshch.telemetry.AsyncResponse;
import com.ivleshch.telemetry.Constants;
import com.ivleshch.telemetry.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;

/**
 * Created by Ivleshch on 11.01.2018.
 */
public class DbHelper extends AsyncTask<Date, String, Integer> {

    private Connection connect  = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private Realm realm;
    private String startDateOfShiftSQL, endDateOfShiftSQL,startTimeOfShiftSQL,endTimeOfShiftSQL;
    private Long startDateOfShiftUnix, endDateOfShiftUnix;
    public AsyncResponse delegate = null;

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(Integer r) {
        delegate.processFinish(r);
    }

    @Override
    protected Integer doInBackground(Date... params) {

        boolean asyncTaskFinished = false;
        boolean isTimer = false;
        try {

            if(params.length==Constants.TIMER_RUN_ASYNC){
                isTimer = true;
            }
            startDateOfShiftSQL = Utils.dateSQL(params[0]);
            startTimeOfShiftSQL = Utils.timeSQL(params[0]);

            endDateOfShiftSQL = Utils.dateSQL(params[1]);
            endTimeOfShiftSQL = Utils.timeSQL(params[1]);

            startDateOfShiftUnix = params[0].getTime()/1000L;
            endDateOfShiftUnix   = params[1].getTime()/1000L;

            Class.forName(DbContract.DATABASE_DRIVER);

            DriverManager.setLoginTimeout(2);
            connect = DriverManager.getConnection("jdbc:mysql://"
                                                    + DbContract.DATABASE_SERVER+"/"
                                                    + DbContract.DATABASE_NAME+"?"
                                                    + "user="+DbContract.DATABASE_USER
                                                    +"&password="+DbContract.DATABASE_PASSWORD);

            statement = connect.createStatement();

            String query;

            query = DbContract.DATABASE_TABLE_REASONS_GET;
            resultSet = statement.executeQuery(query);
            writeResultSet(resultSet,DbContract.DATABASE_TABLE_REASONS_ID);

            query = DbContract.DATABASE_TABLE_DEVISES_GET;
            resultSet = statement.executeQuery(query);
            writeResultSet(resultSet,DbContract.DATABASE_TABLE_DEVICES_ID);

            query = DbContract.DATABASE_TABLE_RAW_EVENTS_GET;
            resultSet = statement.executeQuery(String.format(query,startDateOfShiftSQL+startTimeOfShiftSQL,endDateOfShiftSQL+endTimeOfShiftSQL));
            writeResultSet(resultSet,DbContract.DATABASE_TABLE_RAW_EVENTS_ID);

            query = DbContract.DATABASE_TABLE_STOPS_GET;
            resultSet = statement.executeQuery(String.format(query,startDateOfShiftUnix,endDateOfShiftUnix));
            writeResultSet(resultSet,DbContract.DATABASE_TABLE_STOPS_ID);

            asyncTaskFinished = true;

        } catch (Exception e) {
            Log.w("Error connection", "" + e.getMessage());
            close();
        } finally {
            close();
        }
        if(!isTimer){
            if (asyncTaskFinished){
                return Constants.ASYNC_TASK_RESULT_SUCCESSFUL;
            } else{
                return Constants.ASYNC_TASK_RESULT_FAILED;
            }
        } else{
            if (asyncTaskFinished){
                return Constants.TIMER_ASYNC_TASK_RESULT_SUCCESSFUL;
            } else{
                return Constants.TIMER_ASYNC_TASK_RESULT_FAILED;
            }
        }

    }

    private void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }

            if (connect != null) {
                connect.close();
            }
        } catch (Exception e) {

        }
    }

    private void writeResultSet(ResultSet resultSet, Integer tableId) throws SQLException {
        switch (tableId){
            case 1: //DATABASE_TABLE_REASONS
                saveToRealmReasons(resultSet);
                break;
            case 2://DATABASE_TABLE_RAW_EVENTS
                saveToRealmEvents(resultSet);
                break;
            case 3://DATABASE_TABLE_STOPS
                saveToRealmStops(resultSet);
                break;
            case 4://DATABASE_TABLE_DEVICES
                saveToRealmDevices(resultSet);
                break;
            default:
                break;
        }
    }

    private void saveToRealmReasons(ResultSet resultSet)  throws SQLException{

        ArrayList<Reason> objectArrayList = new ArrayList<>();

        while (resultSet.next()) {
            Reason reason = new Reason();
            reason.setId(resultSet.getInt(DbContract.DATABASE_TABLE_REASONS_COLUMN_ID));
            reason.setCode(resultSet.getInt(DbContract.DATABASE_TABLE_REASONS_COLUMN_CODE));
            reason.setReason(resultSet.getString(DbContract.DATABASE_TABLE_REASONS_COLUMN_REASON));

            objectArrayList.add(reason);
        }

        if(objectArrayList.size()>0) {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.insertOrUpdate(objectArrayList);
            realm.commitTransaction();
            realm.close();
        }
    }

    private void saveToRealmDevices(ResultSet resultSet)  throws SQLException{

        ArrayList<Device> objectArrayList = new ArrayList<>();

        while (resultSet.next()) {
            Device device = new Device();
            device.setDevice_uuid(resultSet.getString(DbContract.DATABASE_TABLE_DEVICES_COLUMN_UUID));
            device.setId(resultSet.getInt(DbContract.DATABASE_TABLE_DEVICES_COLUMN_ID));
            device.setDevice_name(resultSet.getString(DbContract.DATABASE_TABLE_DEVICES_COLUMN_DEVICE_NAME));

            objectArrayList.add(device);
        }

        if(objectArrayList.size()>0) {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.insertOrUpdate(objectArrayList);
            realm.commitTransaction();
            realm.close();
        }
    }

    private void saveToRealmEvents(ResultSet resultSet)  throws SQLException{

        ArrayList<Event> objectArrayList = new ArrayList<>();

        while (resultSet.next()) {
            Event event = new Event();
            event.setId(resultSet.getInt(DbContract.DATABASE_TABLE_RAW_EVENTS_COLUMN_ID));
            event.setDate(Utils.dateFromDateTime(resultSet.getDate(DbContract.DATABASE_TABLE_RAW_EVENTS_COLUMN_DATE),
                                                resultSet.getTime(DbContract.DATABASE_TABLE_RAW_EVENTS_COLUMN_TIME)));
            event.setCount(resultSet.getInt(DbContract.DATABASE_TABLE_RAW_EVENTS_COLUMN_COUNT));
            event.setDevice(Device.findObject(resultSet.getInt(DbContract.DATABASE_TABLE_RAW_EVENTS_COLUMN_DEVICE)));
            event.setDeviceCode(resultSet.getInt(DbContract.DATABASE_TABLE_RAW_EVENTS_COLUMN_DEVICE));

            objectArrayList.add(event);
        }

        if(objectArrayList.size()>0) {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.insertOrUpdate(objectArrayList);
            realm.commitTransaction();
            realm.close();
        }
    }

    private void saveToRealmStops(ResultSet resultSet)  throws SQLException{

        ArrayList<Stop> objectArrayList = new ArrayList<>();

        while (resultSet.next()) {
            Stop stop = new Stop();
            stop.setId(resultSet.getInt(DbContract.DATABASE_TABLE_STOPS_COLUMN_ID));
            stop.setDuration(resultSet.getInt(DbContract.DATABASE_TABLE_STOPS_COLUMN_DURATION));
            stop.setReason(Reason.findObject(resultSet.getInt(DbContract.DATABASE_TABLE_STOPS_COLUMN_REASON)));
            stop.setReasonCode(resultSet.getInt(DbContract.DATABASE_TABLE_STOPS_COLUMN_REASON));
            stop.setDate(Utils.dateFromUnix(resultSet.getLong(DbContract.DATABASE_TABLE_STOPS_COLUMN_MOMENT)));
            stop.setDevice(Device.findObject(resultSet.getInt(DbContract.DATABASE_TABLE_STOPS_COLUMN_DEVICE)));
            stop.setDeviceCode(resultSet.getInt(DbContract.DATABASE_TABLE_STOPS_COLUMN_DEVICE));


            objectArrayList.add(stop);
        }

        if(objectArrayList.size()>0) {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.insertOrUpdate(objectArrayList);
            realm.commitTransaction();
            realm.close();
        }
    }



}
