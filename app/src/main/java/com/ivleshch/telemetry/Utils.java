package com.ivleshch.telemetry;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ivleshch.telemetry.data.Constants;
import com.ivleshch.telemetry.data.Event;
import com.ivleshch.telemetry.data.LineInformation;
import com.ivleshch.telemetry.data.ReportForShift;
import com.ivleshch.telemetry.data.ReportForShiftProduct;
import com.ivleshch.telemetry.data.Shift;
import com.ivleshch.telemetry.data.Stop;

import org.joda.time.Duration;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by Ivleshch on 11.01.2018.
 */

public class Utils {

    public static boolean booleanFromInt(int value){
        return (value != 0);
    }

    public static Date dateFromDateTime(Date date, Time time){
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        cal.setTime(time);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        cal.set(year,month,day,hour,minute,0);

        return cal.getTime();

    }

    public static Date dateFromDayMonthYear(int day, int month, int year){
        Calendar cal = Calendar.getInstance();
//        if(month>0){
//            month = month-1;
//        }

        cal.set(year, month, day,0,0,0);

        return cal.getTime();
    }

    public static Date startOfDay(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);

        return cal.getTime();
    }

    public static Date endOfDay(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE,1);

        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);

        cal.add(Calendar.MILLISECOND,-1);

        return cal.getTime();
    }



    public static Date dateFromUnix(Long datetime) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(datetime*1000L);
//        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);

        return cal.getTime();

    }

    public static Date dateFromUnixWithOut1000(Long datetime) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(datetime);
//        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
        return cal.getTime();

    }

    public static Date startOfShift(Date date) {

        int year, month, day, hour, minute;

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        if(cal.get(Calendar.HOUR_OF_DAY)>=8 && cal.get(Calendar.HOUR_OF_DAY)<=20){
            hour   = 8;
            minute = 0;
        } else{
            hour   = 20;
            minute = 0;

        }

        cal.set(year,month,day,hour,minute,0);
        cal.set(Calendar.MILLISECOND,0);

        return cal.getTime();

    }

    public static Date startOfShift(Date date, String shiftName) {

        int year, month, day, hour, minute;

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        if(shiftName.equals(Constants.FIRST_SHIFT_NAME)){
            hour   = 8;
            minute = 0;
        } else{
            hour   = 20;
            minute = 0;
        }

        cal.set(year,month,day,hour,minute,0);
        cal.set(Calendar.MILLISECOND,0);

        return cal.getTime();

    }

    public static Date endOfShift(Date date) {
        Calendar cal = Calendar.getInstance();

        int year, month, day, hour, minute;
        boolean oneDauShift = true;

        cal.setTime(date);
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        if(cal.get(Calendar.HOUR_OF_DAY)>=8 && cal.get(Calendar.HOUR_OF_DAY)<=20){
            hour   = 20;
            minute = 0;
            if (cal.get(Calendar.HOUR_OF_DAY)<hour){
                hour = cal.get(Calendar.HOUR_OF_DAY);
                minute = cal.get(Calendar.MINUTE);
            }
        } else{
            hour   = 8;
            minute = 0;
            oneDauShift = false;
        }

        cal.set(year,month,day,hour,minute,0);

        if(!oneDauShift){
            cal.add(Calendar.DATE,1);
        }

        cal.set(Calendar.MILLISECOND,0);

        return cal.getTime();

    }

    public static Date endOfShift(Date date, String shiftName) {

        Calendar cal = Calendar.getInstance();

        Calendar currentTime = Calendar.getInstance();

        int year, month, day, hour, minute;
        boolean oneDauShift = true;

        cal.setTime(date);

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        if(shiftName.equals(Constants.FIRST_SHIFT_NAME)){
            hour   = 20;
            minute = 0;
            if(dateToday(date)){
                if (currentTime.get(Calendar.HOUR_OF_DAY)<hour){
                    hour = currentTime.get(Calendar.HOUR_OF_DAY);
                    minute = currentTime.get(Calendar.MINUTE);
                }
            }
        } else{
            Calendar calendarEnd = Calendar.getInstance();
            calendarEnd.set(year,month,day,8,0,0);
            calendarEnd.set(Calendar.MILLISECOND,0);
            calendarEnd.add(Calendar.DATE,1);
            if(!calendarEnd.getTime().before(currentTime.getTime())){
                year = currentTime.get(Calendar.YEAR);
                month = currentTime.get(Calendar.MONTH);
                day = currentTime.get(Calendar.DAY_OF_MONTH);

                hour = currentTime.get(Calendar.HOUR_OF_DAY);
                minute = currentTime.get(Calendar.MINUTE);
            }else{
                hour   = 8;
                minute = 0;
                oneDauShift = false;
            }

        }

        cal.set(year,month,day,hour,minute,0);

        if(!oneDauShift){
            cal.add(Calendar.DATE,1);
        }

        cal.set(Calendar.MILLISECOND,0);

        return cal.getTime();

    }

//    public static Date getEndOfShift(Date date){
//
////        int year, month, day, hour, minute;
////
////        Calendar calendarEnd = Calendar.getInstance();
////        Calendar currentTime = Calendar.getInstance();
////
////        calendarEnd.setTime(date);
////        if(!calendarEnd.getTime().before(currentTime.getTime())){
////                year = currentTime.get(Calendar.YEAR);
////                month = currentTime.get(Calendar.MONTH);
////                day = currentTime.get(Calendar.DAY_OF_MONTH);
////
////                hour = currentTime.get(Calendar.HOUR_OF_DAY);
////                minute = currentTime.get(Calendar.MINUTE);
////            }else{
////                hour   = 8;
////                minute = 0;
////                oneDauShift = false;
////            }
////
////        }
////
////        cal.set(year,month,day,hour,minute,0);
////
////        if(!oneDauShift){
////            cal.add(Calendar.DATE,1);
////        }
////
////        cal.set(Calendar.MILLISECOND,0);
////
////        return cal.getTime();
//    }

    public static boolean dateToday(Date date){
        Calendar cal = Calendar.getInstance();

        // set the calendar to start of today
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        if (date.before(cal.getTime()))
            return false;
        else{
            return true;
        }
    }

    public static Date dateStartOfDate(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        // set the calendar to start of today
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();

    }

    public static Date currentDate() {
        Calendar cal = Calendar.getInstance();
        return cal.getTime();

    }

    public static String dateSQL(Date date){
        Calendar cal = Calendar.getInstance();

        int year, month, day;

        cal.setTime(date);

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH)+1;
        day = cal.get(Calendar.DAY_OF_MONTH);

        return ""+year+"/"+month+"/"+day;
    }

    public static String timeSQL(Date date){
        Calendar cal = Calendar.getInstance();

        int hour, minute;

        cal.setTime(date);

        hour   = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);

        return " "+hour+":"+minute;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }

    public static String determineShift(Date date){

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        if(cal.get(Calendar.HOUR_OF_DAY)>=8 && cal.get(Calendar.HOUR_OF_DAY)<20){
            return Constants.FIRST_SHIFT_NAME;
        } else{
            return Constants.SECOND_SHIFT_NAME;
        }
    }

    public static HashMap sortByValues(HashMap map) {

        List list = new LinkedList(map.entrySet());
        // Defined Custom Comparator here
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        Collections.reverse(list);

        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }

    public static String timeConversion(int totalSeconds) {

        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;

        int seconds = totalSeconds % SECONDS_IN_A_MINUTE;
        int totalMinutes = totalSeconds / SECONDS_IN_A_MINUTE;
        int minutes = totalMinutes % MINUTES_IN_AN_HOUR;
        int hours = totalMinutes / MINUTES_IN_AN_HOUR;

        return hours + "ч." + minutes + " м.";
    }

    public static String formatShift(Date date, Date startOfShift, Date endOfShift){

//        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("dd MMM EE");
        SimpleDateFormat simpleDateFormatHourMinute = new SimpleDateFormat("HH:mm");

        return simpleDateFormatDate.format(date)
                + " "
                + simpleDateFormatHourMinute.format(startOfShift);
//                + " - "
//                + simpleDateFormatHourMinute.format(endOfShift);

    }

    public static String formatShiftStartEnd(Date startOfShift, Date endOfShift){

        SimpleDateFormat simpleDateFormatHourMinute = new SimpleDateFormat("HH:mm");

        return simpleDateFormatHourMinute.format(startOfShift)
                + "-"
                + simpleDateFormatHourMinute.format(endOfShift);
//                + " - "
//                + simpleDateFormatHourMinute.format(endOfShift);

    }

    public static String formatDateRange(Date date){

        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("dd MMM EE");

        return simpleDateFormatDate.format(date);
//                + "-"
//                + simpleDateFormatHourMinute.format(endOfShift);

    }

    public static Shift findCurrentShift(List<Shift> shifts, boolean datePickerSelect, Date currentDateTime){

        Date  key;
        Shift value;
        Shift shift;

        value = null;
        shift = null;

        for(Shift shiftItem: shifts) {
            value = shiftItem;
            if(!datePickerSelect){
                if(currentDateTime.after(value.getStartOfShift())
                        && currentDateTime.before(value.getEndOfShift())){
                    shift = value;
                    break;
                }
            } else{
                if(dateBetween(currentDateTime,value.getDate())){
                    shift = value;
                    break;
                }
            }
        }
        if(!datePickerSelect){
            if (shift == null) {
                shift = value;
            }
        }
        return shift;

    }

    public static boolean istimerNeedToUpdate(Date startOfShifr, Date endOfShift){

        return (currentDate().after(startOfShifr) && currentDate().before(endOfShift));

    }

    public static boolean dateBetween(Date date, Date dateStart){
        Calendar calendarDate = Calendar.getInstance();
        Calendar calendarDateStart = Calendar.getInstance();

        calendarDate.setTime(date);
        calendarDate.set(Calendar.HOUR_OF_DAY,0);
        calendarDate.set(Calendar.MINUTE,0);
        calendarDate.set(Calendar.SECOND,0);
        calendarDate.set(Calendar.MILLISECOND,0);


        calendarDateStart.setTime(dateStart);
        calendarDateStart.set(Calendar.HOUR_OF_DAY,0);
        calendarDateStart.set(Calendar.MINUTE,0);
        calendarDateStart.set(Calendar.SECOND,0);
        calendarDateStart.set(Calendar.MILLISECOND,0);

        if(calendarDateStart.getTime().equals(calendarDate.getTime())){
            return true;
        } else{
            return false;
        }

    }

    public static int findCurrentShiftIndex(Shift shift,List<Shift> shifts){

        if(shift==null){
            return 0;
        }

        Shift value;
        int index;

        index = 0;
        value=null;

        for(Shift shiftItem:shifts) {
            value = shiftItem;

            if(shift.equals(value)){
                break;
            }
            index++;
        }

        return index;

    }

    public static int percentColor(int percent){
        if(percent>=Constants.PERCENT_COLOR_GRREN){
            return R.color.colorGreen400;
        } else if(Constants.PERCENT_COLOR_YELLOW <=percent && percent<Constants.PERCENT_COLOR_GRREN){
            return R.color.colorYellow400;
        }else if(0<=percent && percent<Constants.PERCENT_COLOR_YELLOW){
            return R.color.colorRed400;
        } else{
            return R.color.colorGrey400;
        }
    }

    public static ArrayList<LineInformation> getLines(Date startOfShift, Date endOfShift, String uidWorkCenter, String uidNomenclature){

        HashMap<Integer,Integer> hmDuration;
        HashMap<Integer,Integer> hmDurationSorted;
        HashMap<Integer,String> hmStops;
        Duration shiftDuration;
        long shiftDurationSeconds,shiftDurationHours;
        int quantityEvents;
        int stopDuration, quantityStops, mainStopDuration;
        String durationStops, mainReasonStop;
        double avaibilityPercent, performancePercent, qualityPercent, oeePercent, stopsPersent, mainStopPercent;
        ArrayList<LineInformation> lines;

        lines = new ArrayList<LineInformation>();
        lines.clear();

        hmDuration = new HashMap<>();
        hmDuration.clear();

        hmDurationSorted = new HashMap<>();
        hmDurationSorted.clear();

        hmStops = new HashMap<>();
        hmStops.clear();

        Realm lineRealm;
        lineRealm = Realm.getDefaultInstance();
        lineRealm.refresh();

        hmDuration.clear();
        hmDurationSorted.clear();
        hmStops.clear();


        RealmQuery<ReportForShift> reportForShiftRealmQuery = lineRealm.where(ReportForShift.class).beginGroup();
        if (uidWorkCenter!=null) {
            reportForShiftRealmQuery.equalTo("workCenter.uid",uidWorkCenter);
        } else {
            reportForShiftRealmQuery.isNotNull("workCenter.uid");
        }

        reportForShiftRealmQuery.equalTo("startOfShift",startOfShift);
        reportForShiftRealmQuery.equalTo("endOfShift",endOfShift);
        reportForShiftRealmQuery.equalTo("conducted",true);

        RealmResults<ReportForShift> reportForShifts = reportForShiftRealmQuery.endGroup().findAll();

        for(ReportForShift reportForShift:reportForShifts){


            RealmQuery<ReportForShiftProduct> reportForShiftProductRealmQuery = lineRealm.where(ReportForShiftProduct.class).beginGroup();
            if (uidNomenclature!=null) {
                reportForShiftProductRealmQuery.equalTo("nomenclature.uid",uidNomenclature);
            } else {
                reportForShiftProductRealmQuery.isNotNull("nomenclature.uid");
            }

            reportForShiftProductRealmQuery.equalTo("uidDocument",reportForShift.getUid());

            RealmResults<ReportForShiftProduct> reportForShiftsProducts = reportForShiftProductRealmQuery.endGroup().findAll();

            RealmResults<Stop> stops = lineRealm.where(Stop.class)
                    .greaterThanOrEqualTo("date",UnixTime(startOfShift))
                    .lessThan("date",UnixTime(endOfShift))
                    .equalTo("workCenter",reportForShift.getWorkCenter().getUid())
                    .findAll();

            RealmResults<Event> events = lineRealm.where(Event.class)
                    .greaterThanOrEqualTo("date",UnixTime(startOfShift))
                    .lessThan("date",UnixTime(endOfShift))
                    .equalTo("workCenter",reportForShift.getWorkCenter().getUid())
                    .findAll();

            quantityEvents = events.sum("count").intValue();
            quantityStops = stops.size();

            shiftDuration = new Duration(startOfShift.getTime(), endOfShift.getTime());
            shiftDurationSeconds = shiftDuration.getStandardSeconds();
//            shiftDurationHours = shiftDuration.getStandardHours();

            stopDuration = stops.sum("duration").intValue();

            stopsPersent = Math.round((double) stopDuration/shiftDurationSeconds*100);

            durationStops = Utils.timeConversion(stopDuration)+"/"+(int) stopsPersent+"%";

//            if(stops.size() == 0 ){
//                avaibilityPercent = -1;
//            } else{
//                avaibilityPercent = 100 - Math.round((double) stopDuration / shiftDurationSeconds * 100);
//            }


            for (Stop stop : stops) {
                if(hmDuration.get(stop.getReason())==null){
                    hmDuration.put(stop.getReason(), stop.getDuration());
                } else{
                    hmDuration.put(stop.getReason(), hmDuration.get(stop.getReason())+stop.getDuration());
                }

                if(hmStops.get(stop.getReason())==null){
                    hmStops.put(stop.getReason(), stop.getReasonDescription());
                }
            }

            hmDurationSorted = Utils.sortByValues(hmDuration);
            if (hmDurationSorted.size()>0){
                mainReasonStop = hmStops.get(hmDurationSorted.entrySet().iterator().next().getKey());

                mainStopDuration = hmDurationSorted.entrySet().iterator().next().getValue();
                mainStopPercent =Math.round((double) mainStopDuration/shiftDurationSeconds*100);

                if(mainReasonStop.equals("")){
                    mainReasonStop = "Отсутствует описание";
                }

                mainReasonStop = mainReasonStop + " "+Utils.timeConversion(mainStopDuration)+"/"+(int) mainStopPercent+"%";
            } else{
                mainReasonStop = "";
            }



            for(ReportForShiftProduct reportForShiftProduct:reportForShiftsProducts){

                avaibilityPercent = reportForShiftProduct.getAvailability();
                performancePercent = reportForShiftProduct.getPerformance();

                if(reportForShift.getFinished()){
                    qualityPercent = reportForShiftProduct.getQuality();
                    oeePercent = reportForShiftProduct.getOee();
                }else{
                    qualityPercent = -1;
                    oeePercent = -1;
//                    if(quantityEvents==0){
//                        performancePercent = -1;
//                    } else{
//                        performancePercent = Math.round((double) quantityEvents / (reportForShiftProduct.getStandardSpeed()*shiftDurationHours) * 100);
//                    }
                }

                lines.add(new LineInformation(reportForShiftProduct.getUid(),
                        lineRealm.copyFromRealm(reportForShift.getWorkCenter()),
                        lineRealm.copyFromRealm(reportForShift.getShiftMaster()),
                        lineRealm.copyFromRealm(reportForShiftProduct.getNomenclature()),
                        reportForShiftProduct.getQuantityPlan(),
                        (reportForShift.getFinished())? reportForShiftProduct.getQuantityFact()  : Constants.NO_DATA,
//                        (reportForShift.getFinished())? quantityEvents: Constants.NO_DATA,
                        (reportForShift.getFinished())? reportForShiftProduct.getQuantityDefect()  : Constants.NO_DATA,

                        reportForShiftProduct.getQuantityWaste(),
                        reportForShiftProduct.getUnitQuantity(),
                        reportForShiftProduct.getUnitWeight(),
                        reportForShiftProduct.getStandardSpeed(),
                        (int)avaibilityPercent,
                        (int) performancePercent,
                        (int) qualityPercent,
                        (int) oeePercent,
                        quantityStops,
                        durationStops,
                        mainReasonStop));
            }
        }

        lineRealm.close();
    return lines;
    }

    public static Long UnixTime(Date date){
        return date.getTime()/1000L;
    }

    public static void fillLineInformation(View view, LineInformation lineInformation){

        TextView tvMaster,tvNomenclature,
                tvQuantityPlan,tvQuantityFact,
                tvQuantityDefect,tvQuantityWaste,
                tvWorkCenter, tvAvailability,
                tvPerformancePercent,tvQualityPercent,
                tvOeePercent, tvQuantityStop,
                tvDurationStop, tvReasonStop;
        ImageView ivClockOee, ivClockQuality,ivClockQuantityDefect,ivClockQuantityFact,
                ivClockPerformance, ivClockAvailability;
        LinearLayout llOee, llavailability, llPerformance, llQuality;

        tvMaster = (TextView) view.findViewById(R.id.tv_master);
        tvNomenclature = (TextView) view.findViewById(R.id.tv_nomenclature);
        tvQuantityPlan = (TextView) view.findViewById(R.id.tv_quantity_plan);
        tvQuantityFact = (TextView) view.findViewById(R.id.tv_quantity_fact);
        tvQuantityDefect = (TextView) view.findViewById(R.id.tv_quantity_defect);
//        tvQuantityWaste = (TextView) view.findViewById(R.id.tv_quantity_waste);
        tvWorkCenter = (TextView) view.findViewById(R.id.tv_work_center);
        tvAvailability = (TextView) view.findViewById(R.id.tv_availability_percent);
        tvPerformancePercent = (TextView) view.findViewById(R.id.tv_performance_percent);
        tvQualityPercent = (TextView) view.findViewById(R.id.tv_quality_percent);
        tvOeePercent = (TextView) view.findViewById(R.id.tv_oee_percent);
        tvQuantityStop = (TextView) view.findViewById(R.id.tv_quantity_stop);
        tvDurationStop = (TextView) view.findViewById(R.id.tv_duration_stop);
        tvReasonStop = (TextView) view.findViewById(R.id.tv_reason_stop);

        ivClockOee = (ImageView) view.findViewById(R.id.iv_clock_oee);
        ivClockQuality = (ImageView) view.findViewById(R.id.iv_clock_quality);
        ivClockQuantityDefect =(ImageView) view.findViewById(R.id.iv_clock_quantity_defect);
        ivClockQuantityFact =(ImageView) view.findViewById(R.id.iv_clock_quantity_fact);
        ivClockPerformance =(ImageView) view.findViewById(R.id.iv_clock_performance);
        ivClockAvailability =(ImageView) view.findViewById(R.id.iv_clock_availability);

        llOee = (LinearLayout) view.findViewById(R.id.ll_line_oee);
        llavailability = (LinearLayout) view.findViewById(R.id.ll_line_availability);
        llPerformance = (LinearLayout) view.findViewById(R.id.ll_line_performance);
        llQuality = (LinearLayout) view.findViewById(R.id.ll_line_quality);

        tvMaster.setText(lineInformation.getMaster().getDescriptionShort());
        tvNomenclature.setText(lineInformation.getNomenclature().getDescription());
        tvQuantityPlan.setText(lineInformation.getQuantityPlan().toString());
        tvQuantityFact.setText(lineInformation.getQuantityFact().toString());
        if(lineInformation.getQuantityFact().equals(Constants.NO_DATA)){
            ivClockQuantityFact.setVisibility(View.VISIBLE);
            tvQuantityFact.setVisibility(View.GONE);
        }else{
            ivClockQuantityFact.setVisibility(View.GONE);
            tvQuantityFact.setVisibility(View.VISIBLE);
        }
        tvQuantityDefect.setText(lineInformation.getQuantityDefect().toString());
        if(lineInformation.getQuantityDefect().equals(Constants.NO_DATA)){
            ivClockQuantityDefect.setVisibility(View.VISIBLE);
            tvQuantityDefect.setVisibility(View.GONE);
        }else{
            ivClockQuantityDefect.setVisibility(View.GONE);
            tvQuantityDefect.setVisibility(View.VISIBLE);
        }
//        tvQuantityWaste.setText(lineInformation.getQuantityWaste().toString());
        tvWorkCenter.setText(lineInformation.getWorkCenter().getDescription());
        tvAvailability.setText(lineInformation.getAvailabilityPercent().toString()+"%");
        if(lineInformation.getAvailabilityPercent().equals(Constants.NO_DATA)){
            ivClockAvailability.setVisibility(View.VISIBLE);
            tvAvailability.setVisibility(View.GONE);
        }else{
            ivClockAvailability.setVisibility(View.GONE);
            tvAvailability.setVisibility(View.VISIBLE);
        }
        tvPerformancePercent.setText(lineInformation.getPerformancePercent().toString()+"%");
        if(lineInformation.getPerformancePercent().equals(Constants.NO_DATA)){
            ivClockPerformance.setVisibility(View.VISIBLE);
            tvPerformancePercent.setVisibility(View.GONE);
        }else{
            ivClockPerformance.setVisibility(View.GONE);
            tvPerformancePercent.setVisibility(View.VISIBLE);
        }
        tvQualityPercent.setText(lineInformation.getQualityPercent().toString()+"%");
        if(lineInformation.getQualityPercent().equals(Constants.NO_DATA)){
            ivClockQuality.setVisibility(View.VISIBLE);
            tvQualityPercent.setVisibility(View.GONE);
        }else{
            ivClockQuality.setVisibility(View.GONE);
            tvQualityPercent.setVisibility(View.VISIBLE);
        }
        tvOeePercent.setText(lineInformation.getOeePercent().toString()+"%");
        if(lineInformation.getOeePercent().equals(Constants.NO_DATA)){
            ivClockOee.setVisibility(View.VISIBLE);
            tvOeePercent.setVisibility(View.GONE);
        }else{
            ivClockOee.setVisibility(View.GONE);
            tvOeePercent.setVisibility(View.VISIBLE);
        }

        tvQuantityStop.setText(lineInformation.getQuantityStops().toString());
        tvDurationStop.setText(lineInformation.getDurationStops().toString());
        tvReasonStop.setText(lineInformation.getReasonDescription());

        llOee.setBackgroundColor(ContextCompat.getColor(llOee.getContext(), Utils.percentColor(lineInformation.getOeePercent())));
        llavailability.setBackgroundColor(ContextCompat.getColor(llOee.getContext(), Utils.percentColor(lineInformation.getAvailabilityPercent())));
        llPerformance.setBackgroundColor(ContextCompat.getColor(llOee.getContext(), Utils.percentColor(lineInformation.getPerformancePercent())));
        llQuality.setBackgroundColor(ContextCompat.getColor(llOee.getContext(), Utils.percentColor(lineInformation.getQualityPercent())));
    }

    public static boolean isTimer(int response){
        boolean isTimer = false;
        switch (response){
            case Constants.ASYNC_TASK_RESULT_SUCCESSFUL:
                isTimer = false;
                break;
            case Constants.ASYNC_TASK_RESULT_FAILED:
                isTimer = false;
                break;
            case Constants.TIMER_ASYNC_TASK_RESULT_SUCCESSFUL:
                isTimer = true;
                break;
            case Constants.TIMER_ASYNC_TASK_RESULT_FAILED:
                isTimer = true;
                break;
            default:
                break;
        }

        return isTimer;
    }

    public static boolean isTimerSuccesful(int response){
        boolean isTimerSuccesful = false;
        switch (response){
            case Constants.TIMER_ASYNC_TASK_RESULT_SUCCESSFUL:
                isTimerSuccesful = true;
                break;
            default:
                break;
        }

        return isTimerSuccesful;
    }

    public static boolean dateBetweenStartEndShift(Date startDateShift, Date endDateShift,Long longDate, Long[][] longStartEndShift){

        boolean filterAccept = false;

//        if(longStartEndShift ==null){
//            filterAccept=true;
//        }else{
//            if(longStartEndShift.length==0){
//                filterAccept = false;
//            }else{
//
//                Calendar calendarDate = Calendar.getInstance();
//                calendarDate.setTime(Utils.dateFromUnix(longDate));
//
//                Calendar calendarDay = Calendar.getInstance();
//                Calendar calendarStart = Calendar.getInstance();
//                Calendar calendarEnd = Calendar.getInstance();
//
//                Duration shiftDuration = new Duration(startDateShift.getTime(), endDateShift.getTime());
//                long shiftDurationDays = shiftDuration.getStandardDays();
//
//                int indexDay;
//                if(shiftDurationDays==0){
//                    filterAccept = false;
//                } else if(shiftDurationDays==1){
//                    indexDay = -1;
//                }else{
//                    indexDay=0;
//                }
//
//
//                for (indexDay;indexDay<shiftDurationDays;indexDay++){
//
//                    for(int indexShift = 0;indexShift<longStartEndShift.length;indexShift++){
//
//                        boolean addDay = false;
//
//                        calendarStart.setTime(Utils.dateFromUnixWithOut1000(longStartEndShift[indexShift][0]));
//                        calendarEnd.setTime(Utils.dateFromUnixWithOut1000(longStartEndShift[indexShift][1]));
//
//                        if(Utils.startOfDay(calendarEnd.getTime()).after(calendarStart.getTime())){
//                            addDay = true;
//                        }
//
//
//                        calendarStart.set(Calendar.YEAR,calendarDate.get(Calendar.YEAR));
//                        calendarStart.set(Calendar.MONTH,calendarDate.get(Calendar.MONTH));
//                        calendarStart.set(Calendar.DAY_OF_MONTH,calendarDate.get(Calendar.DAY_OF_MONTH));
//                        calendarStart.set(Calendar.SECOND,0);
//                        calendarStart.set(Calendar.MILLISECOND,0);
//
//                        calendarEnd.set(Calendar.YEAR,calendarDate.get(Calendar.YEAR));
//                        calendarEnd.set(Calendar.MONTH,calendarDate.get(Calendar.MONTH));
//                        calendarEnd.set(Calendar.DAY_OF_MONTH,calendarDate.get(Calendar.DAY_OF_MONTH));
//                        calendarEnd.set(Calendar.SECOND,0);
//                        calendarEnd.set(Calendar.MILLISECOND,0);
//
//                        if(addDay){
//                            calendarEnd.add(Calendar.DATE,1);
//                        }
//
//                        if(calendarDate.getTime().after(calendarStart.getTime()) && calendarDate.getTime().before(calendarEnd.getTime())){
//                            filterAccept = true;
//                            break;
//                        }
//                    }
//                }
//            }
//        }

        return filterAccept;
    }

}
