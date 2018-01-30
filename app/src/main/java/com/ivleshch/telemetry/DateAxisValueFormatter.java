package com.ivleshch.telemetry;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ivleshch on 18.01.2018.
 */

public class DateAxisValueFormatter implements IAxisValueFormatter
{

    private LineChart chart;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

    public DateAxisValueFormatter(LineChart chart) {
        this.chart = chart;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        return simpleDateFormat.format(new Date((long) value));
    }

}
