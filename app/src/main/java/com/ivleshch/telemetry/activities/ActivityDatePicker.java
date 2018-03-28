package com.ivleshch.telemetry.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.appeaser.sublimepickerlibrary.SublimePicker;
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.helpers.SublimeListenerAdapter;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;
import com.ivleshch.telemetry.R;
import com.ivleshch.telemetry.Utils;
import com.ivleshch.telemetry.data.Constants;
import com.ivleshch.telemetry.data.DbContract;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ivleshch on 02.03.2018.
 */

public class ActivityDatePicker extends AppCompatActivity {

    SublimePicker mSublimePicker;
    private Date startDate, endDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);

        startDate = Utils.dateFromUnix(getIntent().getExtras().getLong(DbContract.START_DATE));
        endDate = Utils.dateFromUnix(getIntent().getExtras().getLong(DbContract.END_DATE));


        SublimeListenerAdapter mListener = new SublimeListenerAdapter() {
            @Override
            public void onCancelled() {
                setResult(Constants.REQUEST_CODE_DATE_PICKER_CANCEL);
                finish();
            }

            @Override
            public void onDateTimeRecurrenceSet(SublimePicker sublimeMaterialPicker,
                                                SelectedDate selectedDate,
                                                int hourOfDay,
                                                int minute,
                                                SublimeRecurrencePicker.RecurrenceOption recurrenceOption,
                                                String recurrenceRule) {


                Intent intent = new Intent();
                intent.putExtra(DbContract.START_DATE,Utils.UnixTime(selectedDate.getStartDate().getTime()));
                intent.putExtra(DbContract.END_DATE,Utils.UnixTime(selectedDate.getEndDate().getTime()));
                setResult(Constants.REQUEST_CODE_DATE_PICKER_OK,intent);
                finish();
            }

        };

        mSublimePicker = (SublimePicker) findViewById(R.id.sublime_picker);

        Calendar calendarStartDate = Calendar.getInstance();
        calendarStartDate.setTime(startDate);
        Calendar calendarEndDate   = Calendar.getInstance();
        calendarEndDate.setTime(endDate);

        SelectedDate selectedDate = new SelectedDate(calendarStartDate,calendarEndDate);

        SublimeOptions sublimeOptions = new SublimeOptions();
        sublimeOptions.setCanPickDateRange(true);
        sublimeOptions.setDateParams(selectedDate);

        mSublimePicker.initializePicker(sublimeOptions, mListener);

    }
}
