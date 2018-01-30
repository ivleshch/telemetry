package com.ivleshch.telemetry;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.github.anastr.speedviewlib.SpeedView;
import com.github.anastr.speedviewlib.util.OnPrintTickLabel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.ivleshch.telemetry.data.DbContract;
import com.ivleshch.telemetry.data.Event;
import com.ivleshch.telemetry.data.Stop;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Ivleshch on 11.01.2018.
 */

public class FragmentMain extends Fragment implements
        View.OnClickListener,
        OnChartGestureListener{

    private SpeedView svSpeed, svAvaibility, svPerformance;
    private int stopDuration;
    private double avaibilityPercent,averageSpeed;
    private Realm realm;
    private Animation slideUp;

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
    private LineChart chart;
    private List<Entry> entries;
    private List<Entry> entriesStops;
    private LineDataSet dataSet, datasetStops;
    private LineData lineData;
    private List<ILineDataSet> dataSets;

    private BarChart barChart;
    private List<BarEntry> barEntries, barEntriesDuration;
    private BarDataSet barDataSet, barDataSetDuration;
    private BarData barData;
    private int reasonCount;

    private boolean chartIsUpdaditing, invalidateChart, invalidateBarChart;

    private HashMap<Entry,Integer> hmReasonsCode;
    private HashMap<Entry,String> hmReasonsDescription;
    private HashMap<Integer,Integer> hmBarStop, hmBarStopSorted;
    private HashMap<Integer,Integer> hmReasonAndX;
    private HashMap<Integer,String> hmBarReasonsDescription;
    private HashMap<Integer,Integer> hmBarDuration;
    private HashMap<Integer,Integer> hmBarDurationSorted;


//    private HashMap<Date,Integer> hmCurrentLineChart;
//    private HashMap<Date,Entry>   hmCurrentLineChartEntry;
//    private HashMap<Date,Integer> hmCurrentLineChartAdd;


    private Date startOfShift, endOfShift;
    private String currentShiftName;
    private int idDevice;
    private Integer getDataResult;
    private boolean isTimer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, parent, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        invalidateChart = false;
        invalidateBarChart = false;

        reasonCount = 0;

        slideUp = AnimationUtils.loadAnimation(getContext(),R.anim.slide_up);

        svSpeed = (SpeedView) view.findViewById(R.id.sv_speed);
        svSpeed.setOnPrintTickLabel(new OnPrintTickLabel() {
            @Override
            public String getTickLabel(int tickPosition, int tick) {
                return "";
            }
        });
        svSpeed.setOnClickListener(this);

        svAvaibility = (SpeedView) view.findViewById(R.id.sv_avaibility);
        svAvaibility.setOnPrintTickLabel(new OnPrintTickLabel() {
            @Override
            public String getTickLabel(int tickPosition, int tick) {
                return "";
            }
        });
        svAvaibility.setOnClickListener(this);

        svPerformance = (SpeedView) view.findViewById(R.id.sv_performance);
        svPerformance.setOnPrintTickLabel(new OnPrintTickLabel() {
            @Override
            public String getTickLabel(int tickPosition, int tick) {
                return "";
            }
        });
        svPerformance.setOnClickListener(this);

        hmReasonsCode = new HashMap<>();
        hmReasonsCode.clear();

        hmReasonsDescription = new HashMap<>();
        hmReasonsDescription.clear();

        hmBarStop = new HashMap<>();
        hmBarStop.clear();

        hmBarStopSorted = new HashMap<>();
        hmBarStopSorted.clear();

        hmReasonAndX = new HashMap<>();
        hmReasonAndX.clear();

        hmBarReasonsDescription = new HashMap<>();
        hmBarReasonsDescription.clear();

        hmBarDuration = new HashMap<>();
        hmBarDuration.clear();

        hmBarDurationSorted = new HashMap<>();
        hmBarDurationSorted.clear();

//        hmCurrentLineChart = new HashMap<>();
//        hmCurrentLineChart.clear();
//
//        hmCurrentLineChartEntry = new HashMap<>();
//        hmCurrentLineChartEntry.clear();
//
//        hmCurrentLineChartAdd = new HashMap<>();
//        hmCurrentLineChartAdd.clear();

        entries = new ArrayList<Entry>();

        entriesStops = new ArrayList<Entry>();

        barEntries = new ArrayList<>();
        barEntriesDuration = new ArrayList<>();

        chartIsUpdaditing = false;

        chart = (LineChart) view.findViewById(R.id.line_chart);
        chart.setTouchEnabled(true);
        chart.setScaleYEnabled(false);
        chart.setScaleXEnabled(true);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setHighlightPerTapEnabled(true);
        chart.setOnChartGestureListener(this);
        chart.setDoubleTapToZoomEnabled(false);
//        chart.setGridBackgroundColor(R.color.colorPrimaryLight);




        IMarker marker = new CustomMarkerView(getContext(),R.layout.marker_content_line_chart);
        chart.setMarker(marker);

        IAxisValueFormatter xAxisChartFormatter = new DateAxisValueFormatter(chart);
        XAxis xAxisChart = chart.getXAxis();
        xAxisChart.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisChart.setLabelCount(3);
        xAxisChart.setValueFormatter(xAxisChartFormatter);
        xAxisChart.setDrawGridLines(false);
//       xAxis.setAxisMinimum(startOfShift.getTime());

        YAxis leftYAxisChart = chart.getAxisLeft();
        leftYAxisChart.setLabelCount(4, false);
        leftYAxisChart.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftYAxisChart.setDrawGridLines(false);

        YAxis rightYAxisChart = chart.getAxisRight();
        rightYAxisChart.setEnabled(false);


        barChart = (BarChart) view.findViewById(R.id.bar_chart);
        barChart.setTouchEnabled(true);
        barChart.setScaleYEnabled(false);
        barChart.setScaleXEnabled(true);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setHighlightPerTapEnabled(true);
        barChart.setOnChartGestureListener(this);
        barChart.setDoubleTapToZoomEnabled(false);

        IMarker barMarker = new BarCustomMarkerView(getContext(),R.layout.marker_content_line_chart);
        barChart.setMarker(barMarker);

        IAxisValueFormatter xAxisBarChartFormatter = new BarValueAxisFormatter(barChart);
        XAxis xAxisBarChart = barChart.getXAxis();
        xAxisBarChart.setGranularity(1f);
        xAxisBarChart.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisBarChart.setValueFormatter(xAxisBarChartFormatter);
        xAxisBarChart.setDrawGridLines(false);
//        xAxisBarChart.setCenterAxisLabels(true);


//       xAxis.setAxisMinimum(startOfShift.getTime());


        YAxis leftYAxisBarChart = barChart.getAxisLeft();
        leftYAxisBarChart.setLabelCount(4, false);
        leftYAxisBarChart.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftYAxisBarChart.setDrawGridLines(false);

        YAxis rightYAxisBarChart = barChart.getAxisRight();
        rightYAxisBarChart.setEnabled(false);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sv_speed:
                changeChartVisibility(Constants.LINE_CHART_SPEED_ID);
                break;
            case R.id.sv_performance:
                changeChartVisibility(Constants.LINE_CHART_SPEED_ID);
                break;
            case R.id.sv_avaibility:
                changeChartVisibility(Constants.BAR_CHART_STOPS_ID);
                break;
            default:
                break;
        }
    }

    public void changeChartVisibility(int chartID){
        switch (chartID){
            case Constants.LINE_CHART_SPEED_ID:
                if(chart.getVisibility()==View.GONE){
                    chart.setVisibility(View.VISIBLE);
                    chart.startAnimation(slideUp);

                    barChart.setVisibility(View.GONE);

                }
                break;
            case Constants.BAR_CHART_STOPS_ID:
                if(barChart.getVisibility()==View.GONE){
                    barChart.setVisibility(View.VISIBLE);
                    barChart.startAnimation(slideUp);

                    chart.setVisibility(View.GONE);
                }
                break;
             default:
                 break;
        }
    }

    public void updateOEE(){
        svAvaibility.speedPercentTo((int) avaibilityPercent);
        svSpeed.speedTo((int) averageSpeed);
        svPerformance.speedPercentTo((int) ((double) averageSpeed / Constants.LINE_PERFORMANCE * 100));
    }

    public void updateChart(Date start, Date end, int id, String shift, Integer getData){

        if(getData.equals(Constants.TIMER_ASYNC_TASK_RESULT_SUCCESSFUL)
                || getData.equals(Constants.TIMER_ASYNC_TASK_RESULT_FAILED)){
            isTimer = true;
        }else{
            isTimer = false;
        }

        startOfShift = start;
        endOfShift = end;
        idDevice = id;
        currentShiftName = shift;
        getDataResult = getData;
        if((getDataResult.equals(Constants.TIMER_ASYNC_TASK_RESULT_SUCCESSFUL))
                && startOfShift.equals(Utils.startOfShift(Utils.currentDate(),currentShiftName))){
            updateGraph();
        }else if(!isTimer){
            updateGraph();
        }
    }

    private void updateGraph(){
        if(!chartIsUpdaditing){
            if(isTimer){
                chart.setTouchEnabled(false);
                barChart.setTouchEnabled(false);
//                chart.setScaleXEnabled(false);
            }
            chartIsUpdaditing = true;
            GraphHelper graphHelper = new GraphHelper();
            graphHelper.execute();
        }
    }


    private class GraphHelper extends AsyncTask<Date, String, String> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(String r) {

            boolean updateZoom = !(startOfShift.equals(Utils.startOfShift(Utils.currentDate(),currentShiftName)));


            if (invalidateChart) {

                chart.highlightValues(null);
                if (updateZoom || !isTimer){
                    chart.fitScreen();
                }
                if(isTimer){
                    chart.notifyDataSetChanged();
                }
                chart.invalidate();
            } else{
                chart.clear();
            }
            if (invalidateBarChart){

                if (updateZoom || !isTimer){
                    barChart.fitScreen();
                }
                if(isTimer){
                    barChart.notifyDataSetChanged();
                }
                barChart.invalidate();
            }
            else{
                barChart.clear();
            }
            if(isTimer){
                chart.setTouchEnabled(true);
                barChart.setTouchEnabled(true);
            }
            chartIsUpdaditing = false;
            updateOEE();
        }

        @Override
        protected String doInBackground(Date... params) {

            try {

                invalidateChart = false;
                invalidateBarChart = false;

                Realm graphRealm;
                graphRealm = Realm.getDefaultInstance();
                graphRealm.refresh();

                //updateOEE

                //update Chart
//                if(!isTimer){
                    entries.clear();
                    entriesStops.clear();
                    barEntries.clear();
                    barEntriesDuration.clear();

                    hmReasonsCode.clear();
                    hmReasonsDescription.clear();
                    hmBarStop.clear();
                    hmBarStopSorted.clear();
                    hmReasonAndX.clear();
                    hmBarReasonsDescription.clear();
                    hmBarDuration.clear();
                    hmBarDurationSorted.clear();
//                    hmCurrentLineChart.clear();
//                    hmCurrentLineChartEntry.clear();
//                }

//                hmCurrentLineChartAdd.clear();

                RealmResults<Event> graphEvents = graphRealm.where(Event.class)
                        .between(DbContract.DATABASE_TABLE_RAW_EVENTS_COLUMN_DATE,
                                startOfShift,
                                endOfShift)
                        .equalTo("deviceCode", idDevice)
                        .findAllSorted(DbContract.DATABASE_TABLE_RAW_EVENTS_COLUMN_DATE);

                RealmResults<Stop> graphStops = graphRealm.where(Stop.class)
                        .between(DbContract.DATABASE_TABLE_STOPS_COLUMN_DATE,
                                startOfShift,
                                endOfShift)
                        .equalTo("deviceCode", idDevice)
                        .findAllSorted(DbContract.DATABASE_TABLE_STOPS_COLUMN_DATE);

                averageSpeed = Math.round(graphEvents.average(DbContract.DATABASE_TABLE_RAW_EVENTS_COLUMN_COUNT));
                stopDuration = graphStops.sum(DbContract.DATABASE_TABLE_STOPS_COLUMN_DURATION).intValue();
                avaibilityPercent = 100 - Math.round((double) stopDuration / Constants.SHIFT_DURATION * 100);


                int eventCount;
                Date eventDate;

//                if(!isTimer){
//                    hmCurrentLineChart.clear();
//                    hmCurrentLineChartEntry.clear();
//                }

                for (Event event : graphEvents) {
                    eventCount = event.getCount();
                    eventDate = event.getDate();

                    Entry entry = new Entry(eventDate.getTime(), eventCount);
//                    if(isTimer){
//                        if(hmCurrentLineChart.get(eventDate)==null){
//                            hmCurrentLineChartAdd.put(eventDate,eventCount);
//                        }else{
//                            if(hmCurrentLineChartEntry.get(eventDate)!=null){
//                                if(hmCurrentLineChartEntry.get(eventDate).getY()!=eventCount){
//                                    int a=2;
//                                    a=6;
//                                }
//                            }
//                        }
//                    }
//                    else{
//                        hmCurrentLineChart.put(eventDate,eventCount);
//                        hmCurrentLineChartEntry.put(eventDate,entry);

                        entries.add(entry);
//                    }
                }

//                Iterator itAdd = hmCurrentLineChartAdd.entrySet().iterator();
//                while (itAdd.hasNext()) {
//                    Map.Entry pair = (Map.Entry)itAdd.next();
//
//                    eventCount = (int) pair.getValue();
//                    eventDate = (Date) pair.getKey();
//
//                    Entry entry = new Entry(eventDate.getTime(), eventCount);
//
//                    entries.add(entry);
//
//                }


                for (Stop stop : graphStops) {
                    Entry entry = new Entry(stop.getDate().getTime(), 0);
                    entriesStops.add(entry);
                    if (hmReasonsCode.get(entry) == null) {
                        hmReasonsCode.put(entry, stop.getReasonCode());
                    }
                    if (stop.getReason()!=null){
                        if (hmReasonsDescription.get(entry) == null) {
                            hmReasonsDescription.put(entry, stop.getReason().getReason());
                        }
                        if (hmBarReasonsDescription.get(entry) == null) {
                            hmBarReasonsDescription.put(stop.getReasonCode(), stop.getReason().getReason());
                        }
                    }

                    if(hmBarStop.get(stop.getReasonCode())==null){
                        hmBarStop.put(stop.getReasonCode(), 1);
                    } else{
                        hmBarStop.put(stop.getReasonCode(), hmBarStop.get(stop.getReasonCode())+1);
                    }

                    if(hmBarDuration.get(stop.getReasonCode())==null){
                        hmBarDuration.put(stop.getReasonCode(), stop.getDuration());
                    } else{
                        hmBarDuration.put(stop.getReasonCode(), hmBarDuration.get(stop.getReasonCode())+stop.getDuration());
                    }
                }

                hmBarStopSorted = Utils.sortByValues(hmBarStop);
//                hmBarDurationSorted = Utils.sortByValues(hmBarDuration);

//                int maxStoped = 0;
//                int maxStopedDuration = 0;
//
//                if (hmBarStopSorted.size()>0 && hmBarDurationSorted.size()>0){
//                    maxStoped = hmBarStopSorted.entrySet().iterator().next().getValue();
//                    maxStopedDuration = hmBarDurationSorted.entrySet().iterator().next().getValue();
//                }


                Iterator it = hmBarStopSorted.entrySet().iterator();
                reasonCount = 1;
                float scaleDuration;

                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();

                    BarEntry barEntry = new BarEntry(reasonCount,(Integer) pair.getValue());
                    barEntries.add(barEntry);


                    if(hmReasonAndX.get(reasonCount)==null){
                        hmReasonAndX.put(reasonCount, (Integer) pair.getKey());
                    }

//                    if(maxStopedDuration==0){
//                        scaleDuration = 0;
//                    }else{
//                        scaleDuration = ((float) hmBarDuration.get((Integer) pair.getKey())/maxStopedDuration)*maxStoped;
//                    }
//                    BarEntry barEntryDuration = new BarEntry(reasonCount,scaleDuration);
//                    barEntriesDuration.add(barEntryDuration);
                    reasonCount++;
                }


                dataSets = new ArrayList<ILineDataSet>();
                if(entries.size()>0){
                    dataSet = new LineDataSet(entries, "Speed");
                    dataSet.setDrawValues(false);
                    dataSet.setHighlightEnabled(false);
                    dataSet.setColor(Color.BLUE);
                    dataSet.setDrawCircles(false);

                    invalidateChart  = true;
                    dataSets.add(dataSet);
                }

                if(entriesStops.size()>0){

                    datasetStops = new LineDataSet(entriesStops, "Stops");
                    datasetStops .setHighlightEnabled(true);
                    datasetStops.setHighlightLineWidth(Constants.HIGH_LIGHT_LINE_WIDTS);
                    datasetStops.setCircleRadius(Constants.CHART_CIRCLE_RADIUS);
                    datasetStops.setCircleColor(Color.RED);
                    datasetStops.setColor(Color.TRANSPARENT);
                    datasetStops.setValueFormatter(new MyValueFormatter());
                    datasetStops.setValueTextSize(Constants.TEXT_VALUE_SIZE);
                    datasetStops.setCircleColorHole(Color.RED);
                    datasetStops.setValueTextColor(Color.RED);

                    invalidateChart  = true;
                    dataSets.add(datasetStops);



                }

                if(barEntries.size()>0){
                    barDataSet = new BarDataSet(barEntries, "Stops");
                    barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                    barDataSet.setValueTextColor(Color.BLUE);
                    barDataSet.setValueTextSize(Constants.TEXT_VALUE_SIZE);
                    barDataSet.setValueFormatter(new MyYValueFormatter());

//                    barDataSetDuration = new BarDataSet(barEntriesDuration, "Stops duration");
//                    barDataSetDuration.setColors(ColorTemplate.COLORFUL_COLORS);
//                    barDataSetDuration.setValueTextColor(Color.BLUE);
//                    barDataSetDuration.setValueTextSize(Constants.TEXT_VALUE_SIZE);


                    invalidateBarChart = true;
                }

                if (invalidateChart) {
                    lineData = new LineData(dataSets);
                    chart.setData(lineData);
                }

                if (invalidateBarChart){
//                    float groupSpace = 0.06f;
//                    float barSpace = 0.02f; // x2 dataset
//                    float barWidth = 0.45f;

//                    barData = new BarData(barDataSet,barDataSetDuration);
                    barData = new BarData(barDataSet);
//                    barData.setBarWidth(barWidth);
                    barChart.setData(barData);
//                    barChart.groupBars(1,groupSpace,barSpace);
//                    barChart.setFitBars(true);
                }

                graphRealm.close();

            } catch (Exception e) {

            } finally {

            }
            return "";
        }




    }

    public class MyValueFormatter implements IValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("###,###,##0.0"); // use one decimal
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            if(hmReasonsCode.get(entry)!=null){
                return ""+hmReasonsCode.get(entry);
            } else
            {
                return "";
            }

        }
    }

    public class MyYValueFormatter implements IValueFormatter {

        private DecimalFormat mFormat;

        public MyYValueFormatter() {
            mFormat = new DecimalFormat("###,###,##0.0"); // use one decimal
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            if(hmBarDuration.get(hmReasonAndX.get((int) entry.getX()))!=null) {
                return String.valueOf((int) entry.getY()) +"/"+ Utils.timeConversion(hmBarDuration.get(hmReasonAndX.get((int) entry.getX())));
            }
            else{
                return  String.valueOf((int)entry.getY());
            }

        }
    }



    public class CustomMarkerView extends MarkerView {

        private TextView tvContent;

        public CustomMarkerView(Context context, int layoutResource) {
            super(context, layoutResource);

            // find your layout components
            tvContent = (TextView) findViewById(R.id.tvContent);
        }

        // callbacks everytime the MarkerView is redrawn, can be used to update the
        // content (user-interface)
        @Override
        public void refreshContent(Entry entry, Highlight highlight) {
            if(hmReasonsDescription.get(entry)!=null){
                tvContent.setText(hmReasonsDescription.get(entry));
            } else
            {
                tvContent.setText("");
            }


            // this will perform necessary layouting
            super.refreshContent(entry, highlight);
        }

        private MPPointF mOffset;

        @Override
        public MPPointF getOffset() {

            if(mOffset == null) {
                // center the marker horizontally and vertically
                mOffset = new MPPointF(-(getWidth() / 2), -getHeight()*2);
            }

            return mOffset;
        }
    }

    public class BarCustomMarkerView extends MarkerView {

        private TextView tvContent;

        public BarCustomMarkerView(Context context, int layoutResource) {
            super(context, layoutResource);

            // find your layout components
            tvContent = (TextView) findViewById(R.id.tvContent);
        }

        // callbacks everytime the MarkerView is redrawn, can be used to update the
        // content (user-interface)
        @Override
        public void refreshContent(Entry entry, Highlight highlight) {
            if(hmReasonAndX.get((int) entry.getX())!=null){
                if(hmBarReasonsDescription.get(hmReasonAndX.get((int) entry.getX()))!=null){
                    tvContent.setText(hmBarReasonsDescription.get(hmReasonAndX.get((int) entry.getX())));
                } else{
                    tvContent.setText("");
                }
            } else{
                tvContent.setText("");
            }

            // this will perform necessary layouting
            super.refreshContent(entry, highlight);
        }

        private MPPointF mOffset;

        @Override
        public MPPointF getOffset() {

            if(mOffset == null) {
                // center the marker horizontally and vertically
                mOffset = new MPPointF(-(getWidth() / 2), -getHeight()*2);
            }

            return mOffset;
        }
    }

    public class BarValueAxisFormatter implements IAxisValueFormatter {

        private BarChart chart;


        public BarValueAxisFormatter(BarChart chart) {
            this.chart = chart;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            if (value ==(int) value && barChart.getVisibility()==View.VISIBLE){
                if(hmReasonAndX.get((int) value)!=null){
                    return ""+hmReasonAndX.get((int )value);
                } else
                {
                    return "";
                }
            } else{
                return "";
            }
        }

    }


    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

}
