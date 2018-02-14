package com.ivleshch.telemetry.data;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ivleshch.telemetry.AsyncResponse;
import com.ivleshch.telemetry.Constants;
import com.ivleshch.telemetry.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.HeaderProperty;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

import static org.apache.commons.lang3.math.NumberUtils.max;

/**
 * Created by Ivleshch on 11.01.2018.
 */
public class DbHelper extends AsyncTask<GetDataParams, String, Integer> {

    private Realm realm;
    private String startDateOfShiftSQL, endDateOfShiftSQL,startTimeOfShiftSQL,endTimeOfShiftSQL;
    private Long startDateOfShiftUnix, endDateOfShiftUnix;
    private Date startOfShift, endOfShift, currentDate;
    public AsyncResponse delegate = null;
    private boolean isEventsStopsLoaded, updateReport;
    private Long lastDateEvent, lastDateStop;
    private String server, webService;

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(Integer r) {
        delegate.processFinish(r);
    }

    @Override
    protected Integer doInBackground(GetDataParams... params) {

        boolean asyncTaskFinished = false;
        boolean isTimer = params[0].timer;
        updateReport = params[0].updateReport;
        server = params[0].server;
        webService = params[0].webService;

        isEventsStopsLoaded = false;

        checkWebServiceAvailability();
        try {

            currentDate = Utils.currentDate();

            startOfShift = params[0].startOfShift;
            endOfShift = params[0].endOfShift;

            startDateOfShiftSQL = Utils.dateSQL(params[0].startOfShift);
            startTimeOfShiftSQL = Utils.timeSQL(params[0].startOfShift);

            endDateOfShiftSQL = Utils.dateSQL(params[0].endOfShift);
            endTimeOfShiftSQL = Utils.timeSQL(params[0].endOfShift);

            startDateOfShiftUnix = params[0].startOfShift.getTime()/1000L;
            endDateOfShiftUnix   = params[0].endOfShift.getTime()/1000L;

            Gson gson = new Gson();

            DbJson dbJson = new DbJson(startDateOfShiftUnix,endDateOfShiftUnix,updateReport);

            String json = gson.toJson(dbJson);

            if(params.length==Constants.TIMER_RUN_ASYNC){
                isTimer = true;
            }

            if (!checkWebServiceAvailability()){
                return returnResult(isTimer,asyncTaskFinished);
            }

            SoapPrimitive response = null;

            SoapObject request = new SoapObject(DbContract.NAMESPACE, DbContract.METHOD_NAME_REPORT);
            request.addProperty("Date", json);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

//            HttpTransportSE httpse = new HttpTransportSE(Constants.URL,300000);
            HttpTransportSE httpse = new HttpTransportSE(String.format(DbContract.URL,server,webService));
            ArrayList<HeaderProperty> headerList = new ArrayList<HeaderProperty>();
            headerList.add(new HeaderProperty(DbContract.HEADER_TYPE, DbContract.HEADER_DATA));
            try {
                httpse.call(String.format(DbContract.SOAP_ACTION,server,webService) + DbContract.METHOD_NAME_REPORT, envelope, headerList);
                response = (SoapPrimitive) envelope.getResponse();

                if (response == null) {
                    return returnResult(isTimer,asyncTaskFinished);
                }

                loadData(response);

            } catch (Exception e) {
                int a= 2;
                a=5;
            }

            asyncTaskFinished = true;

        } catch (Exception e) {
            Log.w("Error connection", "" + e.getMessage());
        } finally {
        }

         return returnResult(isTimer,asyncTaskFinished);

    }

    private void loadData(SoapPrimitive response){

        JSONArray exchangeObjects;
        exchangeObjects = null;

        try {
            exchangeObjects = (new JSONArray(response.getValue().toString()));

            for (int i = 0; i < exchangeObjects.length(); i++) {
//                try{
                    JSONObject exchangeObject = exchangeObjects.getJSONObject(i);

                    switch (exchangeObject.getString(DbContract.DATABASE_TYPE)) {
                        case DbContract.DATABASE_TYPE_CATALOG_NOMENCLATURE:
                            loadNomenclature(exchangeObject.getJSONArray(DbContract.DATABASE_DATA));
                            break;
                        case DbContract.DATABASE_TYPE_CATALOG_INDIVIDUAL:
                            loadIndividual(exchangeObject.getJSONArray(DbContract.DATABASE_DATA));
                            break;
                        case DbContract.DATABASE_TYPE_CATALOG_WORK_CENTER:
                            loadWorkCenter(exchangeObject.getJSONArray(DbContract.DATABASE_DATA));
                            break;
                        case DbContract.DATABASE_TYPE_DOCUMENT_REPORT_FOR_SHIFT:
                            loadReportForShift(exchangeObject.getJSONArray(DbContract.DATABASE_DATA));
                            break;
                        case DbContract.DATABASE_TYPE_CATALOG_SHIFT:
                            loadShift(exchangeObject.getJSONArray(DbContract.DATABASE_DATA));
                            break;
                        case DbContract.DATABASE_TYPE_INFOREG_RAW_EVENT:
                            loadRawEvent(exchangeObject.getJSONArray(DbContract.DATABASE_DATA));
                            break;
                        case DbContract.DATABASE_TYPE_INFOREG_STOP:
                            loadStop(exchangeObject.getJSONArray(DbContract.DATABASE_DATA));
                            break;
                        default:
                            break;
                    }
//                } catch (JSONException e){
//                }

            }
            lastUpdateSave();

        } catch (JSONException e) {
        }

    }

    private void lastUpdateSave(){

        if(isEventsStopsLoaded && (lastDateStop!=null || lastDateEvent!=null) ){
            if(lastDateStop==null){
                lastDateStop = (long) 0;
            }
            if(lastDateEvent==null){
                lastDateEvent = (long) 0;
            }

            ShiftUpdate shiftUpdate = new ShiftUpdate();
            shiftUpdate.setUid(Utils.dateStartOfDate(startOfShift).getTime());
            shiftUpdate.setDate(Utils.dateStartOfDate(startOfShift));
            shiftUpdate.setLastUpdate(Utils.dateFromUnix(max(lastDateEvent,lastDateStop)));

            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.insertOrUpdate(shiftUpdate);
            realm.commitTransaction();
            realm.close();
        }
    }

    private void loadStop(JSONArray objectsArray)  throws JSONException{

        final ArrayList<Stop> objectArrayList;

        Type listType = new TypeToken<ArrayList<Stop>>(){}.getType();
        GsonBuilder gsonBuilder = new GsonBuilder();
        objectArrayList = gsonBuilder.create().fromJson(objectsArray.toString(), listType);

        if (objectArrayList.size()>0){

            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.insertOrUpdate(objectArrayList);
            realm.commitTransaction();
            realm.close();

            isEventsStopsLoaded = true;
            lastDateStop = objectArrayList.get(objectArrayList.size()-1).getDate();
        }

    }

    private void loadRawEvent(JSONArray objectsArray)  throws JSONException{

        final ArrayList<Event> objectArrayList;

        Type listType = new TypeToken<ArrayList<Event>>(){}.getType();
        GsonBuilder gsonBuilder = new GsonBuilder();
        objectArrayList = gsonBuilder.create().fromJson(objectsArray.toString(), listType);

        if (objectArrayList.size()>0){

            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.insertOrUpdate(objectArrayList);
            realm.commitTransaction();
            realm.close();

            isEventsStopsLoaded = true;
            lastDateEvent = objectArrayList.get(objectArrayList.size()-1).getDate();
        }

    }

    private void loadShift(JSONArray objectsArray) throws JSONException{

        final ArrayList<Shift> objectArrayList;

        JSONObject exchangeObject;

        objectArrayList = new ArrayList<>();

        exchangeObject = null;

        for (int i = 0; i < objectsArray.length(); i++) {
            exchangeObject = objectsArray.getJSONObject(i);

            Shift shift = new Shift();

            shift.setUid           (exchangeObject.getString("UID"));
            shift.setDate          (Utils.dateFromUnix(exchangeObject.getLong("DATE")));
            shift.setStartOfShift  (Utils.dateFromUnix(exchangeObject.getLong("START_OF_SHIFT")));
            shift.setEndOfShift    (Utils.dateFromUnix(exchangeObject.getLong("END_OF_SHIFT")));

            objectArrayList.add(shift);

            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            RealmResults<Shift> result = realm.where(Shift.class)
                    .equalTo("date",shift.getDate())
                    .findAll();
            result.deleteAllFromRealm();
            realm.commitTransaction();
            realm.close();
        }

        if (objectArrayList.size()>0){
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.insertOrUpdate(objectArrayList);
            realm.commitTransaction();
            realm.close();
        }

    }

    private void loadNomenclature(JSONArray objectsArray) throws JSONException{
        final ArrayList<Nomenclature> objectArrayList;
        objectArrayList = new ArrayList<>();
        JSONObject exchangeObject;
        exchangeObject = null;
        for (int i = 0; i < objectsArray.length(); i++) {
            exchangeObject = objectsArray.getJSONObject(i);

            Nomenclature nomenclature = new Nomenclature();

            nomenclature.setUid             (exchangeObject.getString("UID"));
            nomenclature.setDeletionMark    (Utils.booleanFromInt(exchangeObject.getInt("DELETION_MARK")));
            nomenclature.setDescription     (exchangeObject.getString("DESCRIPTION"));


            objectArrayList.add(nomenclature);
        }

        if (objectArrayList.size()>0){
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.insertOrUpdate(objectArrayList);
            realm.commitTransaction();
            realm.close();
        }

    }

    private void loadIndividual(JSONArray objectsArray) throws JSONException{
        final ArrayList<Individual> objectArrayList;
        objectArrayList = new ArrayList<>();
        JSONObject exchangeObject;
        exchangeObject = null;
        for (int i = 0; i < objectsArray.length(); i++) {
            exchangeObject = objectsArray.getJSONObject(i);

            Individual individual = new Individual();

            individual.setUid             (exchangeObject.getString("UID"));
            individual.setDeletionMark    (Utils.booleanFromInt(exchangeObject.getInt("DELETION_MARK")));
            individual.setDescription     (exchangeObject.getString("DESCRIPTION"));
            individual.setDescriptionShort(exchangeObject.getString("DESCRIPTION_SHORT"));


            objectArrayList.add(individual);
        }

        if (objectArrayList.size()>0){
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.insertOrUpdate(objectArrayList);
            realm.commitTransaction();
            realm.close();
        }

    }

    private void loadWorkCenter(JSONArray objectsArray) throws JSONException{
        final ArrayList<WorkCenter> objectArrayList;
        objectArrayList = new ArrayList<>();
        JSONObject exchangeObject;
        exchangeObject = null;
        for (int i = 0; i < objectsArray.length(); i++) {
            exchangeObject = objectsArray.getJSONObject(i);

            WorkCenter workCenter = new WorkCenter();

            workCenter.setUid             (exchangeObject.getString("UID"));
            workCenter.setDeletionMark    (Utils.booleanFromInt(exchangeObject.getInt("DELETION_MARK")));
            workCenter.setDescription     (exchangeObject.getString("DESCRIPTION"));


            objectArrayList.add(workCenter);
        }

        if (objectArrayList.size()>0){
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.insertOrUpdate(objectArrayList);
            realm.commitTransaction();
            realm.close();
        }

    }

    private void loadReportForShift(JSONArray objectsArray) throws JSONException{

        final ArrayList<ReportForShift> objectArrayList;
        final ArrayList<ReportForShiftProduct> objectArrayListProducts;

        JSONObject exchangeObject, exchangeObjectTable;
        JSONArray JSONArrayProducts;

        objectArrayList = new ArrayList<>();
        objectArrayListProducts = new ArrayList<>();


        exchangeObject = null;
        exchangeObjectTable = null;

        for (int i = 0; i < objectsArray.length(); i++) {
            exchangeObject = objectsArray.getJSONObject(i);

            ReportForShift reportForShift = new ReportForShift();

            reportForShift.setUid             (exchangeObject.getString("UID"));
            reportForShift.setDeletionMark    (Utils.booleanFromInt(exchangeObject.getInt("DELETION_MARK")));
            reportForShift.setFinished        (Utils.booleanFromInt(exchangeObject.getInt("FINISHED")));
            reportForShift.setShiftMaster     (Individual.findObject(exchangeObject.getString("INDIVIDUAL")));
            reportForShift.setWorkCenter      (WorkCenter.findObject(exchangeObject.getString("WORK_CENTER")));

            reportForShift.setStartOfShift    (Utils.dateFromUnix(exchangeObject.getLong("START_OF_SHIFT")));
            reportForShift.setEndOfShift      (Utils.dateFromUnix(exchangeObject.getLong("END_OF_SHIFT")));

            objectArrayList.add(reportForShift);

            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            RealmResults<ReportForShiftProduct> result = realm.where(ReportForShiftProduct.class)
                    .equalTo("uidDocument",reportForShift.getUid())
                    .findAll();
            result.deleteAllFromRealm();
            realm.commitTransaction();
            realm.close();

            JSONArrayProducts = exchangeObject.getJSONArray("PRODUCTS");
            for (int id = 0; id < JSONArrayProducts.length(); id++) {
                exchangeObjectTable = JSONArrayProducts.getJSONObject(id);

                ReportForShiftProduct reportForShiftProduct = new ReportForShiftProduct();

                reportForShiftProduct.setUid            (exchangeObjectTable.getString("UID"));
                reportForShiftProduct.setUidDocument    (exchangeObject.getString("UID"));
                reportForShiftProduct.setNomenclature   (Nomenclature.findObject(exchangeObjectTable.getString("NOMENCLATURE")));
                reportForShiftProduct.setQuantityPlan   (exchangeObjectTable.getInt("QUANTITY_PLAN"));
                reportForShiftProduct.setQuantityFact   (exchangeObjectTable.getInt("QUANTITY_FACT"));
                reportForShiftProduct.setQuantityDefect (exchangeObjectTable.getInt("QUANTITY_DEFECT"));
                reportForShiftProduct.setQuantityWaste  (exchangeObjectTable.getInt("QUANTITY_WASTE"));
                reportForShiftProduct.setStandardSpeed  (exchangeObjectTable.getInt("STANDARD_SPEED"));
                reportForShiftProduct.setUnitQuantity   (exchangeObjectTable.getString("UNIT_QUANTITY"));
                reportForShiftProduct.setUnitWeight     (exchangeObjectTable.getString("UNIT_WEIGHT"));

                reportForShiftProduct.setAvailability   (exchangeObjectTable.getInt("AVAILABILITY"));
                reportForShiftProduct.setPerformance    (exchangeObjectTable.getInt("PERFORMANCE"));
                reportForShiftProduct.setQuality        (exchangeObjectTable.getInt("QUALITY"));
                reportForShiftProduct.setOee            (exchangeObjectTable.getInt("OEE"));

//                reportForShiftProduct.setStart(Utils.dateFromUnix(exchangeObjectTable.getLong("START")));
//                reportForShiftProduct.setEnd(Utils.dateFromUnix(exchangeObjectTable.getLong("END")));

                objectArrayListProducts.add(reportForShiftProduct);

            }

        }

        if (objectArrayList.size()>0){
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.insertOrUpdate(objectArrayList);
            realm.insertOrUpdate(objectArrayListProducts);
            realm.commitTransaction();
            realm.close();
        }

    }

    private Integer returnResult(boolean timer, boolean finished){
        if(!timer){
            if (finished){
                return Constants.ASYNC_TASK_RESULT_SUCCESSFUL;
            } else{
                return Constants.ASYNC_TASK_RESULT_FAILED;
            }
        } else{
            if (finished){
                return Constants.TIMER_ASYNC_TASK_RESULT_SUCCESSFUL;
            } else{
                return Constants.TIMER_ASYNC_TASK_RESULT_FAILED;
            }
        }
    }

    private boolean checkWebServiceAvailability(){

        try {
            URL url = new URL(String.format(DbContract.CHECK_URL,server,webService));
            HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();
            urlConnect.setRequestProperty(DbContract.REQUEST_PROPERTY_CONNECTION, DbContract.REQUEST_PROPERTY_CLOSE);
            urlConnect.setConnectTimeout(DbContract.REQUEST_TIMEOUT); // Timeout 2 seconds.
            urlConnect.connect();

            urlConnect.disconnect();

            return true;

        }catch (Exception e){
            return false;
        }

    }


}
