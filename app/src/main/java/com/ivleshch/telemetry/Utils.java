package com.ivleshch.telemetry;

import java.sql.Time;
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

/**
 * Created by Ivleshch on 11.01.2018.
 */

public class Utils {

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

        return hours + "h." + minutes + " m.";
    }


}
