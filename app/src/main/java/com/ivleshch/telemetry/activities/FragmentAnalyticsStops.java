package com.ivleshch.telemetry.activities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.ivleshch.telemetry.R;
import com.ivleshch.telemetry.Utils;
import com.ivleshch.telemetry.data.DbContract;
import com.ivleshch.telemetry.data.Shift;
import com.ivleshch.telemetry.data.Stop;

import org.joda.time.Duration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Ivleshch on 28.02.2018.
 */

public class FragmentAnalyticsStops extends Fragment{

    private Realm realm;
    private RealmResults<Stop> stops;
    private HashMap<Integer,Integer> hmBarStop, hmBarStopSorted;
    private HashMap<Integer,String> hmReasonsDescription;
    private HashMap<Integer,Integer> hmBarDuration;
    private HashMap<Integer,Integer> hmBarDurationSorted;
    private HashMap<Integer,Integer> hmDurationIndex,hmQuantityIndex;

    private HashMap<PieEntry,Integer> hmReasonsQuantity, hmReasonsDuration;

    private List<PieEntry> yValuesStops, yValuesStopsDuration;
    private PieChart pieChartStopsQuantity,pieChartStopsDuration;
    private PieDataSet dataSetStopsQuantity,dataSetStopsDuration;
    private PieData dataStopsQuantity,dataStopsDuration;
    private String uidWorkCenter;
    private Date startDate,endDate;
    private boolean isUpdaditing;
    private LinearLayout llEmptyData,llAnalyticsStops;
    private boolean autoHighLight;
    private Long[][] shiftFilterChecked;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_analytics, parent, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pieChartStopsQuantity = (PieChart) view.findViewById(R.id.piechartStopsQuantity);
        pieChartStopsDuration = (PieChart) view.findViewById(R.id.piechartStopsDuration);

        pieChartStopsQuantity.animateY(1500, Easing.EasingOption.EaseInOutQuad);
        pieChartStopsDuration.animateY(1500, Easing.EasingOption.EaseInOutQuad);

        IMarker markerQuantity = new PieChartCustomMarkerView(getContext(),R.layout.marker_content_line_chart,pieChartStopsQuantity);
        pieChartStopsQuantity.setMarker(markerQuantity);

        IMarker markerDuration = new PieChartCustomMarkerView(getContext(),R.layout.marker_content_line_chart,pieChartStopsDuration);
        pieChartStopsDuration.setMarker(markerDuration);

        pieChartStopsQuantity.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onNothingSelected() {
                pieChartStopsDuration.highlightValues(null);
                pieChartStopsQuantity.highlightValues(null);
            }

            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if(!autoHighLight){
                    if(hmReasonsQuantity.get(e)!=null){
                        if(hmDurationIndex.get(hmReasonsQuantity.get(e))!=null){
                            autoHighLight = true;
                            pieChartStopsDuration.highlightValue(hmDurationIndex.get(hmReasonsQuantity.get(e)),0);
                            autoHighLight = false;
                        }
                    }
                }
            }
        });

        pieChartStopsDuration.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onNothingSelected() {
                pieChartStopsDuration.highlightValues(null);
                pieChartStopsQuantity.highlightValues(null);
            }

            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if(!autoHighLight){
                    if(hmReasonsDuration.get(e)!=null){
                        if(hmQuantityIndex.get(hmReasonsDuration.get(e))!=null){
                            autoHighLight = true;
                            pieChartStopsQuantity.highlightValue(hmQuantityIndex.get(hmReasonsDuration.get(e)),0);
                            autoHighLight = false;
                        }
                    }
                }
            }
        });

        isUpdaditing = false;

        llEmptyData = (LinearLayout) view.findViewById(R.id.ll_empty);
//        llAnalyticsStops = (LinearLayout) view.findViewById(R.id.ll_analytics_stops);

        if(getArguments()!=null){
            startDate = Utils.dateFromUnixWithOut1000(getArguments().getLong(DbContract.DATABASE_TABLE_LINE_INFORMATION_START_OF_SHIFT));
            endDate = Utils.dateFromUnixWithOut1000(getArguments().getLong(DbContract.DATABASE_TABLE_LINE_INFORMATION_END_OF_SHIFT));
            uidWorkCenter = getArguments().getString(DbContract.DATABASE_TABLE_LINE_INFORMATION_WORK_CENTER_UID);

            updatePieChart(startDate,endDate,uidWorkCenter,null);
        }

    }

    public void updatePieChart(Date start, Date end, String workCenter, Long[][] shiftFilter){
        startDate = start;
        endDate = end;
        uidWorkCenter = workCenter;
        shiftFilterChecked = shiftFilter;

        if(!isUpdaditing){

            isUpdaditing = true;
            GraphHelper graphHelper = new GraphHelper();
            graphHelper.execute();

        }

    }

    public void initDataSet(PieChart pieChart,PieDataSet pieDataSet,PieData pieData, List<PieEntry> pieEntry){

        pieDataSet = new PieDataSet(pieEntry, "");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setSliceSpace(1f);
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setValueLineVariableLength(true);

        pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(13f);
        pieData.setValueTextColor(Color.DKGRAY);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setDrawValues(true);

        initPieChart(pieChart,pieData);

    }

    public void initPieChart(final PieChart pieChart, PieData pieData){

        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.getLegend().setEnabled(false);

        pieChart.getDescription().setText("");



        pieChart.setExtraOffsets(20,0,20,0);
        pieChart.setUsePercentValues(true);
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setDrawEntryLabels(true);
        pieChart.setData(pieData);
//        pieChart.invalidate();

    }

    public class PieChartCustomMarkerView extends MarkerView {

        private TextView tvContent;
        private PieChart pieChart;

        public PieChartCustomMarkerView(Context context, int layoutResource, PieChart pieChartLocal) {
            super(context, layoutResource);

            tvContent = (TextView) findViewById(R.id.tvContent);
            pieChart = pieChartLocal;
        }

        @Override
        public void refreshContent(Entry entry, Highlight highlight) {

            switch (pieChart.getId()){
                case R.id.piechartStopsQuantity:
                    if(hmReasonsQuantity.get(entry)!=null){
                        if(hmReasonsDescription.get(hmReasonsQuantity.get(entry))!=null){
                            tvContent.setText(hmReasonsDescription.get(hmReasonsQuantity.get(entry)));
                        } else{
                            tvContent.setText(getString(R.string.emptyDescription));
                        }
                    }else{
                        tvContent.setText(getString(R.string.emptyDescription));
                    }
                    break;
                case R.id.piechartStopsDuration:
                    if(hmReasonsDuration.get(entry)!=null){
                        if(hmReasonsDescription.get(hmReasonsDuration.get(entry))!=null){
                            tvContent.setText(hmReasonsDescription.get(hmReasonsDuration.get(entry)));
                        } else{
                            tvContent.setText(getString(R.string.emptyDescription));
                        }
                    }else{
                        tvContent.setText(getString(R.string.emptyDescription));
                    }
                    break;
                default:
                    tvContent.setText(getString(R.string.emptyDescription));
                    break;
            }

//            if(hmReasonsDescription.get((int )highlight.getX())!=null){
//                tvContent.setText(hmReasonsDescription.get((int )highlight.getX()));
//            } else{
//                 tvContent.setText(getString(R.string.emptyDescription));
//            }
            super.refreshContent(entry, highlight);
        }

        private MPPointF mOffset;

        @Override
        public MPPointF getOffset() {

            if(mOffset == null) {
                mOffset = new MPPointF(-(getWidth() / 2), -getHeight()*2);
            }

            return mOffset;
        }

        @Override
        public void draw(Canvas canvas, float posX, float posY) {
            int lineChartWidth = 0;
            int lineChartHeight = 0;
            float offsetX = getOffset().getX();
            float offsetY = getOffset().getX();
            float width = getWidth();
            float height = getHeight();

            if(pieChart != null) {
                lineChartWidth = pieChart.getWidth();
                lineChartHeight = pieChart.getHeight();
            }
            if(posX + offsetX < 0) {
                offsetX = - posX;
            } else if(posX + width + offsetX > lineChartWidth) {
                offsetX = lineChartWidth - posX - width;
            }
            posX += offsetX;

            if(posY + offsetY < 0) {
                offsetY = - posY;
            } else if(posY + height + offsetY > lineChartHeight) {
                offsetY = lineChartHeight - posY - height;
            }
            posY += offsetY;

            canvas.translate(posX, posY);
            draw(canvas);
            canvas.translate(-posX, -posY);
        }

    }

    public void invalidateCharts(PieChart pieChart, List<PieEntry> pieEntries){
        if (pieEntries.size()>0) {
            pieChart.highlightValues(null);
            pieChart.invalidate();
        } else{
            pieChart.clear();
        }
    }

    public void updateGraphs(){
        realm = Realm.getDefaultInstance();

        hmBarStop = new HashMap<>();
        hmBarStop.clear();

        hmBarStopSorted = new HashMap<>();
        hmBarStopSorted.clear();

        hmReasonsDescription = new HashMap<>();
        hmReasonsDescription.clear();

        hmBarDuration = new HashMap<>();
        hmBarDuration.clear();

        hmBarDurationSorted = new HashMap<>();
        hmBarDurationSorted.clear();

        hmReasonsDuration = new HashMap<>();
        hmReasonsDuration.clear();

        hmReasonsQuantity = new HashMap<>();
        hmReasonsQuantity.clear();

        hmQuantityIndex = new HashMap<>();
        hmQuantityIndex.clear();

        hmDurationIndex = new HashMap<>();
        hmDurationIndex.clear();


        yValuesStops = new ArrayList<>();
        yValuesStopsDuration = new ArrayList<>();

        yValuesStops.clear();
        yValuesStopsDuration.clear();

        RealmResults<Shift> shifts = realm.where(Shift.class).
                lessThanOrEqualTo("date",Utils.startOfDay(endDate)).
                greaterThanOrEqualTo("date",Utils.startOfDay(startDate)).
                findAllSorted("endOfShift", Sort.DESCENDING);

        Date endDateShift, startDateShift;
        if(shifts.size()>0){
            startDateShift = shifts.minDate("startOfShift");
            endDateShift = shifts.maxDate("endOfShift");

        }else{
            startDateShift = startDate;
            endDateShift = endDate;
        }

//        stops = realm.where(Stop.class)
//                .greaterThanOrEqualTo("date", Utils.UnixTime(startDateShift))
//                .lessThan("date", Utils.UnixTime(endDateShift))
//                .equalTo("workCenter",uidWorkCenter)
//                .findAllSorted("date");


        Calendar calendarCheckDate = Calendar.getInstance();
        calendarCheckDate.setTime(startDateShift);

        Calendar calendarStart = Calendar.getInstance();
        Calendar calendarEnd = Calendar.getInstance();

        RealmQuery<Stop> stopsQuery = realm.where(Stop.class);

        stopsQuery.beginGroup();
        stopsQuery.greaterThanOrEqualTo("date", Utils.UnixTime(startDateShift));
        stopsQuery.lessThan("date", Utils.UnixTime(endDateShift));
        stopsQuery.endGroup();

        if(shiftFilterChecked!=null){

            if(shiftFilterChecked.length==0){
                return;
            }

            stopsQuery.and().beginGroup();
            boolean addOr = false;
            while (!(calendarCheckDate.getTime().after(endDateShift) || calendarCheckDate.getTime().equals(endDateShift) )){
                for(int indexShift = 0;indexShift<shiftFilterChecked.length;indexShift++){

                    if(addOr){
                        stopsQuery.or();
                    }

                    calendarStart.setTime(Utils.dateFromUnixWithOut1000(shiftFilterChecked[indexShift][0]));
                    calendarEnd.setTime(Utils.dateFromUnixWithOut1000(shiftFilterChecked[indexShift][1]));

                    Duration shiftDuration = new Duration(calendarStart.getTime().getTime(), calendarEnd.getTime().getTime());
                    long shiftDurationMinutes = shiftDuration.getStandardMinutes();

                    calendarEnd.setTime(Utils.dateFromUnixWithOut1000(shiftFilterChecked[indexShift][0]));

                    calendarStart.set(Calendar.YEAR,calendarCheckDate.get(Calendar.YEAR));
                    calendarStart.set(Calendar.MONTH,calendarCheckDate.get(Calendar.MONTH));
                    calendarStart.set(Calendar.DAY_OF_MONTH,calendarCheckDate.get(Calendar.DAY_OF_MONTH));
                    calendarStart.set(Calendar.SECOND,0);
                    calendarStart.set(Calendar.MILLISECOND,0);

                    calendarEnd.set(Calendar.YEAR,calendarStart.get(Calendar.YEAR));
                    calendarEnd.set(Calendar.MONTH,calendarStart.get(Calendar.MONTH));
                    calendarEnd.set(Calendar.DAY_OF_MONTH,calendarStart.get(Calendar.DAY_OF_MONTH));
                    calendarEnd.set(Calendar.SECOND,0);
                    calendarEnd.set(Calendar.MILLISECOND,0);

                    calendarEnd.add(Calendar.MINUTE,(int) shiftDurationMinutes);

                    stopsQuery.beginGroup();
                    stopsQuery.greaterThanOrEqualTo("date", Utils.UnixTime(calendarStart.getTime()));
                    stopsQuery.lessThan("date", Utils.UnixTime(calendarEnd.getTime()));

                    stopsQuery.endGroup();

                    addOr = true;

                }
                calendarCheckDate.add(Calendar.DATE,1);
            }
            stopsQuery.endGroup();
        }
        stops = stopsQuery.equalTo("workCenter",uidWorkCenter).findAllSorted("date");



        for (Stop stop : stops) {

//            if(!Utils.dateBetweenStartEndShift(startDateShift,endDateShift,stop.getDate(),shiftFilterChecked)){
//                continue;
//            }

            if (stop.getReason()!=null){
                if (hmReasonsDescription.get(stop.getReason()) == null) {
                    hmReasonsDescription.put(stop.getReason(), stop.getReasonDescription());
                }
            }

            if(hmBarStop.get(stop.getReason())==null){
                hmBarStop.put(stop.getReason(), 1);
            } else{
                hmBarStop.put(stop.getReason(), hmBarStop.get(stop.getReason())+1);
            }

            if(hmBarDuration.get(stop.getReason())==null){
                hmBarDuration.put(stop.getReason(), stop.getDuration());
            } else{
                hmBarDuration.put(stop.getReason(), hmBarDuration.get(stop.getReason())+stop.getDuration());
            }
        }

        realm.close();

        hmBarStopSorted = Utils.sortByValues(hmBarStop);
        hmBarDurationSorted = Utils.sortByValues(hmBarDuration);

        Iterator it = hmBarStopSorted.entrySet().iterator();


        int index;

        index = 0;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            PieEntry pieEntry = new PieEntry((Integer) pair.getValue(), pair.getKey().toString());
            yValuesStops.add(pieEntry);

            hmReasonsQuantity.put(pieEntry,(Integer) pair.getKey());
            hmQuantityIndex.put((Integer) pair.getKey(),index);

            index++;
        }

        Iterator itDuration = hmBarDurationSorted.entrySet().iterator();

        index = 0;
        while (itDuration.hasNext()) {
            Map.Entry pair = (Map.Entry)itDuration.next();
            PieEntry pieEntry = new PieEntry((Integer) pair.getValue(), pair.getKey().toString());
            yValuesStopsDuration.add(pieEntry);

            hmReasonsDuration.put(pieEntry,(Integer) pair.getKey());
            hmDurationIndex.put((Integer) pair.getKey(),index);

            index++;
        }

        initDataSet(pieChartStopsQuantity,dataSetStopsQuantity,dataStopsQuantity,yValuesStops);
        initDataSet(pieChartStopsDuration,dataSetStopsDuration,dataStopsDuration,yValuesStopsDuration);
    }

    private class GraphHelper extends AsyncTask<Date, String, String> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(String r) {
            invalidateCharts(pieChartStopsQuantity, yValuesStops);
            invalidateCharts(pieChartStopsDuration, yValuesStopsDuration);

            isUpdaditing = false;

            if(yValuesStops.size()==0 || yValuesStopsDuration.size()==0){
                llEmptyData.setVisibility(View.VISIBLE);
                pieChartStopsQuantity.setVisibility(View.GONE);
                pieChartStopsDuration.setVisibility(View.GONE);
//                llAnalyticsStops.setVisibility(View.GONE);
            }else{
                llEmptyData.setVisibility(View.GONE);
//                llAnalyticsStops.setVisibility(View.VISIBLE);
                pieChartStopsQuantity.setVisibility(View.VISIBLE);
                pieChartStopsDuration.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected String doInBackground(Date... params) {

            try {
                updateGraphs();
            } catch (Exception e) {

            } finally {

            }
            return "";
        }
    }
}