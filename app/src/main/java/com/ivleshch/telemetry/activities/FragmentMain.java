package com.ivleshch.telemetry.activities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

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
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.ivleshch.telemetry.DateAxisValueFormatter;
import com.ivleshch.telemetry.R;
import com.ivleshch.telemetry.Utils;
import com.ivleshch.telemetry.data.Constants;
import com.ivleshch.telemetry.data.DbContract;
import com.ivleshch.telemetry.data.Event;
import com.ivleshch.telemetry.data.LineInformation;
import com.ivleshch.telemetry.data.Stop;

import java.text.DecimalFormat;
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

public class FragmentMain extends BaseFragment implements
        View.OnClickListener,
        View.OnTouchListener,
        OnChartGestureListener {

    private LineChart chart;
    private List<Entry> entries, currentEntries;
    private List<Entry> entriesStops;
    private LineDataSet dataSet, datasetStops;
    private LineData lineData;
    private List<ILineDataSet> dataSets;

    private BarChart barChart;
    private List<BarEntry> barEntries, barEntriesDuration;
    private BarDataSet barDataSet, barDataSetDuration;
    private BarData barData;
    private int reasonCount;

    private boolean isUpdaditing, invalidateChart, invalidateBarChart;

    private HashMap<Entry,Integer> hmReasonsCode;
    private HashMap<Entry,String> hmReasonsDescription;
    private HashMap<Integer,Integer> hmBarStop, hmBarStopSorted;
    private HashMap<Integer,Integer> hmReasonAndX,hmReasonAndXDuration;
    private HashMap<Integer,String> hmBarReasonsDescription;
    private HashMap<Integer,Integer> hmBarDuration;
    private HashMap<Integer,Integer> hmBarDurationSorted;

    private Date startOfShift, endOfShift;
    private Integer getDataResult;
    private boolean isTimer;
    private HashMap<Integer,Integer> hmDuration;
    private HashMap<Integer,Integer> hmDurationSorted;
    private HashMap<Integer,String> hmStops;
    private LineInformation lineInformation;
    private String uidWorkCenter, uidNomenclature;
    private ArrayList<LineInformation> lines;
    private CardView cardViewLine;
    private RelativeLayout rlLineChart, rlBarChart;
    private LinearLayout llBarChart, llEmptyData;
    private Switch switchButton;
    private boolean isTimerNeedToUpdate;
    private ProgressBar pbLineChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, parent, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pbLineChart = (ProgressBar) view.findViewById(R.id.pb_getDataLineChart);

        cardViewLine = (CardView) view.findViewById(R.id.card_view);
        rlLineChart = (RelativeLayout) view.findViewById(R.id.rl_line_chart);
        rlBarChart = (RelativeLayout) view.findViewById(R.id.rl_bar_chart);
        llBarChart = (LinearLayout) view.findViewById(R.id.ll_bar_chart);
        llEmptyData = (LinearLayout) view.findViewById(R.id.ll_empty);
        switchButton = (Switch) view.findViewById(R.id.switchButton);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                updateBarChart();
            }
        });

        if(getArguments()!=null){
            uidWorkCenter = getArguments().getString(DbContract.DATABASE_TABLE_LINE_INFORMATION_WORK_CENTER_UID);
            uidNomenclature = getArguments().getString(DbContract.DATABASE_TABLE_LINE_INFORMATION_NOMENCLATURE_UID);
            startOfShift = Utils.dateFromUnixWithOut1000(getArguments().getLong(DbContract.DATABASE_TABLE_LINE_INFORMATION_START_OF_SHIFT));
            endOfShift = Utils.dateFromUnixWithOut1000(getArguments().getLong(DbContract.DATABASE_TABLE_LINE_INFORMATION_END_OF_SHIFT));
        }

        initCharts(view);
        if(getArguments()!=null){
            updateOEE();
            updateChart(startOfShift,endOfShift,uidWorkCenter, Constants.ASYNC_TASK_RESULT_SUCCESSFUL);
        }

        cardViewLine.setOnTouchListener(this);
        view.setOnTouchListener(this);
//    }

    }

    public void updateBarChart(){


        boolean updateBarChart = false;
        if (switchButton.isChecked()){
            if(barEntriesDuration.size()>0){
                barData = new BarData(barDataSetDuration);
                updateBarChart = true;
            }
        }else{
            if(barEntries.size()>0){
                barData = new BarData(barDataSet);
                updateBarChart = true;
            }
        }

        if(updateBarChart){
            barChart.setData(barData);
            barChart.notifyDataSetChanged();
            barChart.invalidate();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gesture.onTouchEvent(event);
    }

    final GestureDetector gesture = new GestureDetector(getActivity(),
            new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onDown(MotionEvent e) {
                    return true;
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                       float velocityY) {

                    final int SWIPE_MIN_DISTANCE = 220;
                    final int SWIPE_MAX_OFF_PATH = 350;
                    final int SWIPE_THRESHOLD_VELOCITY = 300;
                    try {
                        if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                            return false;
                        if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                                && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                            ((MainActivity) getActivity()).gestureFinish(MotionEvent.EDGE_RIGHT);
                        } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                                && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                            ((MainActivity) getActivity()).gestureFinish(MotionEvent.EDGE_LEFT);
                        }
                    } catch (Exception e) {
                        // nothing
                    }
                    return super.onFling(e1, e2, velocityX, velocityY);
                }
            });

    public void initCharts(View view){

        hmDuration = new HashMap<>();
        hmDuration.clear();

        hmDurationSorted = new HashMap<>();
        hmDurationSorted.clear();

        hmStops = new HashMap<>();
        hmStops.clear();

        invalidateChart = false;
        invalidateBarChart = false;

        reasonCount = 0;

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

        hmReasonAndXDuration = new HashMap<>();
        hmReasonAndXDuration.clear();

        hmBarReasonsDescription = new HashMap<>();
        hmBarReasonsDescription.clear();

        hmBarDuration = new HashMap<>();
        hmBarDuration.clear();

        hmBarDurationSorted = new HashMap<>();
        hmBarDurationSorted.clear();

        entries = new ArrayList<Entry>();
        currentEntries = new ArrayList<Entry>();

        entriesStops = new ArrayList<Entry>();

        barEntries = new ArrayList<>();
        barEntriesDuration = new ArrayList<>();

        isUpdaditing = false;

        chart = (LineChart) view.findViewById(R.id.line_chart);
        chart.setTouchEnabled(true);
        chart.setScaleYEnabled(false);
        chart.setScaleXEnabled(true);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setHighlightPerTapEnabled(true);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setMaxHighlightDistance(10);
        chart.setOnChartGestureListener(this);
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onNothingSelected() {
                barChart.highlightValues(null);
                chart.highlightValues(null);
            }

            @Override
            public void onValueSelected(Entry e, Highlight h) {
                barChart.highlightValues(null);

            }
        });

        IMarker marker = new CustomMarkerView(getContext(),R.layout.marker_content_line_chart);
        chart.setMarker(marker);

        IAxisValueFormatter xAxisChartFormatter = new DateAxisValueFormatter(chart);
        XAxis xAxisChart = chart.getXAxis();
        xAxisChart.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisChart.setLabelCount(3);
        xAxisChart.setValueFormatter(xAxisChartFormatter);
        xAxisChart.setDrawGridLines(false);

        YAxis leftYAxisChart = chart.getAxisLeft();
        leftYAxisChart.setLabelCount(4, false);
        leftYAxisChart.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftYAxisChart.setDrawGridLines(false);

        YAxis rightYAxisChart = chart.getAxisRight();
        rightYAxisChart.setEnabled(false);


        barChart = (BarChart) view.findViewById(R.id.bar_chart);
        barChart.setTouchEnabled(true);
        barChart.setScaleYEnabled(false);
        barChart.setScaleXEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setHighlightPerTapEnabled(true);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setMaxHighlightDistance(10);
        barChart.setHighlightFullBarEnabled(true);
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onNothingSelected() {
                chart.highlightValues(null);
                barChart.highlightValues(null);
            }

            @Override
            public void onValueSelected(Entry e, Highlight h) {
                chart.highlightValues(null);

            }
        });

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
        leftYAxisBarChart.setEnabled(false);

        YAxis rightYAxisBarChart = barChart.getAxisRight();
        rightYAxisBarChart.setEnabled(false);

//        chart.setNoDataText("");
//        barChart.setNoDataText("");

//        chart.setNoDataText("");
//        barChart.setNoDataText("");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            default:
                break;
        }
    }

    public void updateOEE(){
        lines = new ArrayList<>();
        lines.clear();
        lines = Utils.getLines(startOfShift,endOfShift,uidWorkCenter,uidNomenclature);

        if(lines.size()>0){

            cardViewLine.setVisibility(View.VISIBLE);
            rlLineChart.setVisibility(View.VISIBLE);
            rlBarChart.setVisibility(View.VISIBLE);
            llBarChart.setVisibility(View.VISIBLE);
            llEmptyData.setVisibility(View.GONE);

            lineInformation = lines.get(0);
            Utils.fillLineInformation(cardViewLine,lineInformation);
        } else{
            cardViewLine.setVisibility(View.GONE);
            rlLineChart.setVisibility(View.GONE);
            rlBarChart.setVisibility(View.GONE);
            llBarChart.setVisibility(View.GONE);
            llEmptyData.setVisibility(View.VISIBLE);

        }
    }

    public void updateChart(Date start, Date end, String uid, Integer getData){

        isTimer = Utils.isTimer(getData);
        startOfShift = start;
        endOfShift = end;
        uidWorkCenter = uid;
        getDataResult = getData;

        if(!isTimer){
            updateGraph();
        } else{
            if(Utils.isTimerSuccesful(getData)){
                isTimerNeedToUpdate = Utils.istimerNeedToUpdate(startOfShift,endOfShift);
                if(isTimerNeedToUpdate){
                    updateGraph();
                }
            }
        }
    }

    private void updateGraph(){
        if(!isUpdaditing){

            rlLineChart.setEnabled(false);
            rlBarChart.setEnabled(false);
            rlLineChart.setClickable(false);
            rlBarChart.setClickable(false);
//            pbLineChart.setVisibility(View.VISIBLE);
            chart.setTouchEnabled(false);
            chart.setScaleXEnabled(false);

            barChart.setTouchEnabled(false);

            if(!isTimer){
                chart.setNoDataText(getString(R.string.messageUdate));
                barChart.setNoDataText(getString(R.string.messageUdate));
                chart.clear();
                barChart.clear();
                isUpdaditing = true;
                GraphHelper graphHelper = new GraphHelper();
                graphHelper.execute();
            } else{
                ((MainActivity) getActivity()).showTransparentLayout(true);
                updateGraphs();
            }

        }
    }

    private void updateGraphs(){

        invalidateChart = false;
        invalidateBarChart = false;

        Realm uiRealm;
        uiRealm = Realm.getDefaultInstance();
        uiRealm.refresh();

        entries.clear();
        currentEntries.clear();
        entriesStops.clear();

        barEntries.clear();
        barEntriesDuration.clear();

        hmReasonsCode.clear();
        hmReasonsDescription.clear();
        hmBarStop.clear();
        hmBarStopSorted.clear();
        hmReasonAndX.clear();
        hmReasonAndXDuration.clear();
        hmBarReasonsDescription.clear();
        hmBarDuration.clear();
        hmBarDurationSorted.clear();

        RealmResults<Event> graphEvents = uiRealm.where(Event.class)
                .greaterThanOrEqualTo("date", Utils.UnixTime(startOfShift))
                .lessThan("date", Utils.UnixTime(endOfShift))
                .equalTo("workCenter", uidWorkCenter)
                .findAllSorted("date");

        RealmResults<Stop> graphStops = uiRealm.where(Stop.class)
                .greaterThanOrEqualTo("date", Utils.UnixTime(startOfShift))
                .lessThan("date", Utils.UnixTime(endOfShift))
                .equalTo("workCenter", uidWorkCenter)
                .findAllSorted("date");

        int eventCount;
        Date eventDate;

        for (Event event : graphEvents) {
            eventCount = event.getCount();
            eventDate = Utils.dateFromUnix(event.getDate());
            Entry entry = new Entry(eventDate.getTime(), eventCount);
            entries.add(entry);
        }

        for (Stop stop : graphStops) {
            Entry entry = new Entry(Utils.dateFromUnix(stop.getDate()).getTime(), 0);
            entriesStops.add(entry);
            if (hmReasonsCode.get(entry) == null) {
                hmReasonsCode.put(entry, stop.getReason());
            }
            if (stop.getReason()!=null){
                if (hmReasonsDescription.get(entry) == null) {
                    hmReasonsDescription.put(entry, stop.getReasonDescription());
                }
                if (hmBarReasonsDescription.get(entry) == null) {
                    hmBarReasonsDescription.put(stop.getReason(), stop.getReasonDescription());
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

        hmBarStopSorted = Utils.sortByValues(hmBarStop);
        hmBarDurationSorted = Utils.sortByValues(hmBarDuration);

        Iterator it = hmBarStopSorted.entrySet().iterator();
        reasonCount = 1;

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();

            BarEntry barEntry = new BarEntry(reasonCount,(Integer) pair.getValue());
            barEntries.add(barEntry);

            if(hmReasonAndX.get(reasonCount)==null){
                hmReasonAndX.put(reasonCount, (Integer) pair.getKey());
            }

            reasonCount++;
        }

        Iterator itDuration = hmBarDurationSorted.entrySet().iterator();
        reasonCount = 1;

        while (itDuration.hasNext()) {
            Map.Entry pair = (Map.Entry)itDuration.next();

            BarEntry barEntry = new BarEntry(reasonCount,(Integer) pair.getValue());
            barEntriesDuration.add(barEntry);

            if(hmReasonAndXDuration.get(reasonCount)==null){
                hmReasonAndXDuration.put(reasonCount, (Integer) pair.getKey());
            }
            reasonCount++;
        }


        dataSets = new ArrayList<ILineDataSet>();
        if(entries.size()>0){
            dataSet = new LineDataSet(entries, "Speed");
            dataSet.setDrawValues(false);
            dataSet.setHighlightEnabled(false);
            dataSet.setColor(ContextCompat.getColor(getContext(),R.color.colorPrimary));
            dataSet.setDrawCircles(false);

            invalidateChart  = true;
            dataSets.add(dataSet);
        }

        if(entriesStops.size()>0){

            datasetStops = new LineDataSet(entriesStops, "Stops");
            datasetStops.setHighlightEnabled(true);
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
            invalidateBarChart = true;
        }

        if(barEntriesDuration.size()>0){

            barDataSetDuration = new BarDataSet(barEntriesDuration, "Stops duration");
            barDataSetDuration.setColors(ColorTemplate.COLORFUL_COLORS);
            barDataSetDuration.setValueTextColor(Color.BLUE);
            barDataSetDuration.setValueTextSize(Constants.TEXT_VALUE_SIZE);
            barDataSetDuration.setValueFormatter(new MyYValueFormatter());
            invalidateBarChart = true;

        }

        if (invalidateChart) {
            lineData = new LineData(dataSets);
            chart.setData(lineData);
        }

        if (invalidateBarChart){
            if(!switchButton.isChecked()){
                barData = new BarData(barDataSet);
            } else{
                barData = new BarData(barDataSetDuration);
            }
            barChart.setData(barData);
        }

        uiRealm.close();

        if(isTimer){
            invalidateCharts();
        }

    }

    public void invalidateCharts(){
        boolean updateZoom = !Utils.istimerNeedToUpdate(startOfShift,endOfShift);

        if (invalidateChart) {

            chart.highlightValues(null);
            if (updateZoom || !isTimer){
                chart.fitScreen();
            }
            if(isTimer){
                if(dataSet!=null){
                    dataSet.notifyDataSetChanged();
                }
                if(datasetStops!=null){
                    datasetStops.notifyDataSetChanged();
                }
                chart.notifyDataSetChanged();
            }
            chart.invalidate();
        } else{
            chart.clear();
        }
        if (invalidateBarChart){
            barChart.highlightValue(null);
            if (updateZoom || !isTimer){
                barChart.fitScreen();
            }
            if(isTimer){
                if(barDataSet!=null){
                    barDataSet.notifyDataSetChanged();
                }
                if(barDataSetDuration!=null){
                    barDataSetDuration.notifyDataSetChanged();
                }
                barChart.notifyDataSetChanged();
            }
            barChart.invalidate();
        }
        else{
            barChart.clear();
        }

        isUpdaditing = false;
        updateOEE();

        chart.setNoDataText(getString(R.string.messageEmptyData));
        barChart.setNoDataText(getString(R.string.messageEmptyData));

        chart.setTouchEnabled(true);
        chart.setScaleXEnabled(true);
        barChart.setTouchEnabled(true);

//            pbLineChart.setVisibility(View.GONE);
        rlLineChart.setEnabled(true);
        rlBarChart.setEnabled(true);
        rlLineChart.setClickable(true);
        rlBarChart.setClickable(true);
        if(isTimer){
            ((MainActivity) getActivity()).showTransparentLayout(false);
        }
    }

    private class GraphHelper extends AsyncTask<Date, String, String> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(String r) {
            invalidateCharts();
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
            if(!switchButton.isChecked()){
                if(hmBarDuration.get(hmReasonAndX.get((int) entry.getX()))!=null) {
                    return String.valueOf((int) entry.getY()) +"/"+ Utils.timeConversion(hmBarDuration.get(hmReasonAndX.get((int) entry.getX())));
                }
                else{
                    return  String.valueOf((int)entry.getY());
                }
            } else{
                if(hmBarStop.get(hmReasonAndXDuration.get((int) entry.getX()))!=null) {
                    return String.valueOf( hmBarStop.get(hmReasonAndXDuration.get((int) entry.getX()))+"/"+ Utils.timeConversion((int) entry.getY()));
                }
                else{
                    return  String.valueOf((int)entry.getY());
                }
            }

        }
    }

    public class CustomMarkerView extends MarkerView {

        private TextView tvContent;

        public CustomMarkerView(Context context, int layoutResource) {
            super(context, layoutResource);

            tvContent = (TextView) findViewById(R.id.tvContent);
        }

        @Override
        public void refreshContent(Entry entry, Highlight highlight) {
            if(hmReasonsDescription.get(entry)!=null){
                tvContent.setText(hmReasonsDescription.get(entry));
                if(hmReasonsDescription.get(entry).length()==0){
                    tvContent.setText(getString(R.string.emptyDescription));
                }
            } else
            {
                tvContent.setText(getString(R.string.emptyDescription));
            }

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

            if(chart != null) {
                lineChartWidth = chart.getWidth();
                lineChartHeight = chart.getHeight();
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

    public class BarCustomMarkerView extends MarkerView {

        private TextView tvContent;

        public BarCustomMarkerView(Context context, int layoutResource) {
            super(context, layoutResource);

            tvContent = (TextView) findViewById(R.id.tvContent);
        }

        @Override
        public void refreshContent(Entry entry, Highlight highlight) {
            if(!switchButton.isChecked()){
                if(hmReasonAndX.get((int) entry.getX())!=null){
                    if(hmBarReasonsDescription.get(hmReasonAndX.get((int) entry.getX()))!=null){
                        tvContent.setText(hmBarReasonsDescription.get(hmReasonAndX.get((int) entry.getX())));
                        if(hmBarReasonsDescription.get(hmReasonAndX.get((int) entry.getX())).length()==0){
                            tvContent.setText(getString(R.string.emptyDescription));
                        }
                    } else{
                        tvContent.setText(getString(R.string.emptyDescription));
                    }
                } else{
                    tvContent.setText(getString(R.string.emptyDescription));
                }
            } else{
                if(hmReasonAndXDuration.get((int) entry.getX())!=null){
                    if(hmBarReasonsDescription.get(hmReasonAndXDuration.get((int) entry.getX()))!=null){
                        tvContent.setText(hmBarReasonsDescription.get(hmReasonAndXDuration.get((int) entry.getX())));
                        if(hmBarReasonsDescription.get(hmReasonAndXDuration.get((int) entry.getX())).length()==0){
                            tvContent.setText(getString(R.string.emptyDescription));
                        }
                    } else{
                        tvContent.setText(getString(R.string.emptyDescription));
                    }
                } else{
                    tvContent.setText(getString(R.string.emptyDescription));
                }
            }

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

            if(chart != null) {
                lineChartWidth = barChart.getWidth();
                lineChartHeight = barChart.getHeight();
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

    public class BarValueAxisFormatter implements IAxisValueFormatter {

        private BarChart chart;


        public BarValueAxisFormatter(BarChart chart) {
            this.chart = chart;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            if (value ==(int) value && barChart.getVisibility()==View.VISIBLE){
                if(!switchButton.isChecked()){
                    if(hmReasonAndX.get((int) value)!=null){
                        return ""+hmReasonAndX.get((int )value);
                    } else
                    {
                        return "";
                    }
                } else{
                    if(hmReasonAndXDuration.get((int) value)!=null){
                        return ""+hmReasonAndXDuration.get((int )value);
                    } else
                    {
                        return "";
                    }
                }
            } else{
                return "";
            }
        }

    }

    @Override
    public boolean onBackPressed() {

        Bundle bundle = new Bundle();

        bundle.putLong(DbContract.DATABASE_TABLE_LINE_INFORMATION_START_OF_SHIFT     , startOfShift.getTime());
        bundle.putLong(DbContract.DATABASE_TABLE_LINE_INFORMATION_END_OF_SHIFT       , endOfShift.getTime());

        ((MainActivity) getActivity()).replaceFragment(Constants.LINE_FRAGMENT,bundle,"");

        return true;
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        me1 = null;
        me2 = null;

}
    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

        me = null;
}

    @Override
public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        me = null;

}

@Override
public void onChartLongPressed(MotionEvent me) {
    me = null;
}

    @Override
public void onChartDoubleTapped(MotionEvent me) {
        me = null;
}

@Override
public void onChartSingleTapped(MotionEvent me) {
    me = null;
}

@Override
public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
    me = null;
}

@Override
public void onChartTranslate(MotionEvent me, float dX, float dY) {
    me = null;
    }

}
