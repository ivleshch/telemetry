package com.ivleshch.telemetry.data;

/**
 * Created by Ivleshch on 12.01.2018.
 */

public class Constants {

    public static final String START_OF_SHIFT = "start_of_shift";
    public static final String END_OF_SHIFT   = "end_of_shift";

    public static final String ID_DEVICE   = "id_device";

    public static final int SHIFT_DURATION = 43200;

    public static final int LINE_PERFORMANCE = 200;

    public static final int CHART_CIRCLE_RADIUS = 5;
    public static final int TEXT_VALUE_SIZE = 10;
    public static final int HIGH_LIGHT_LINE_WIDTS = 3;

    public static final int LINE_CHART_SPEED_ID = 1001;
    public static final int BAR_CHART_STOPS_ID = 1002;

    public static final String FIRST_SHIFT_NAME =  "08:00 - 20:00";
    public static final String SECOND_SHIFT_NAME = "20:00 - 08:00";

    public static final Integer TIME_INTERVAL_NOTIFICATION_START = 60000;  //60 sec
    public static final Integer TIME_INTERVAL_NOTIFICATION_REPEAT = 60000; //60 sec

    public static final int ASYNC_TASK_RESULT_SUCCESSFUL = 2001;
    public static final int ASYNC_TASK_RESULT_FAILED = 2002;

    public static final int TIMER_ASYNC_TASK_RESULT_SUCCESSFUL = 3001;
    public static final int TIMER_ASYNC_TASK_RESULT_FAILED = 3002;
    public static final int     TIMER_RUN_ASYNC = 3;

    public static final int PERCENT_COLOR_GRREN = 90;
    public static final int PERCENT_COLOR_YELLOW = 70;
    public static final int PERCENT_COLOR_RED = 60;

    public static final int LINE_FRAGMENT   = 10001;
    public static final int CHART_FRAGMENT  = 10002;

    public static final String FRAGMENT_LINE_CLASS = "com.ivleshch.telemetry.activities.FragmentLines";
    public static final String FRAGMENT_MAIN_CLASS = "com.ivleshch.telemetry.activities.FragmentMain";

    public static final String FRAGMENT_CLICK_ON_LINE = "fragment_click_on_line";

    public static Integer NO_DATA = -1;


}
