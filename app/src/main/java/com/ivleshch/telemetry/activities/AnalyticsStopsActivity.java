package com.ivleshch.telemetry.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ivleshch.telemetry.R;
import com.ivleshch.telemetry.Utils;
import com.ivleshch.telemetry.data.Constants;
import com.ivleshch.telemetry.data.DbContract;
import com.ivleshch.telemetry.data.Shift;
import com.ivleshch.telemetry.data.WorkCenter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Ivleshch on 13.02.2018.
 */

public class AnalyticsStopsActivity extends AppCompatActivity implements
        View.OnClickListener{

    private Toolbar toolbar, toolBarDrawer;
    private TextView tvDateRange;
    private Date startDate, endDate;
    private Long startDateUnix, endDateUnix;
    private ImageButton ibFilter;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private int idSubMenu, idItem, idSubMenuShift, idItemShift;
    private Menu navMenu;
    private SubMenu subMenu;
    private MenuItem menuItem;


    private int[] rbLinesArray;
    private int lineFilter;
    HashMap<Integer,RadioGroup> hmRadioGroup;
    HashMap<RadioGroup,String> hmWorkCenterFilter;
    private boolean clearCheck;
    private String uidWorkCenter;
    private ArrayList<RadioGroup> radioGroupsArray;


    private int[] rbShiftsArray;
    private int shiftFilter;
    HashMap<Integer,RadioGroup> hmShiftRadioGroup;
    HashMap<RadioGroup,String> hmShiftFilter;
    private boolean clearCheckShift;
    private Long startDateShiftFilter;
    private Long endDateShiftFilter;
    private ArrayList<RadioGroup> radioGroupsArrayShift;
    private HashMap<CheckBox,Long> hmShiftStartOfShift;
    private HashMap<CheckBox,Long> hmShiftEndOfShift;
    private ArrayList<String> arrayListShifts;
    private RelativeLayout llCheckBox;
    private ArrayList<CheckBox> shiftCheckBoxes;
    private Long[][]  shiftFilterChecked;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics_stops_main);

        clearCheck = false;

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle(getString(R.string.title_analytics));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolBarDrawer = (Toolbar) navigationView.getHeaderView(0).findViewById(R.id.toolBar);
        toolBarDrawer.setTitle(getString(R.string.title_filters));

        ibFilter = (ImageButton) findViewById(R.id.ib_filter);
        ibFilter.setOnClickListener(this);
        ibFilter.setVisibility(View.VISIBLE);

        tvDateRange = findViewById(R.id.tv_date_range);
        tvDateRange.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        if(getIntent().getExtras()!=null){
            startDate = Utils.dateFromUnixWithOut1000(getIntent().getLongExtra(DbContract.DATABASE_TABLE_LINE_INFORMATION_START_OF_SHIFT,currentDate.getTime()));
            endDate = Utils.dateFromUnixWithOut1000(getIntent().getLongExtra(DbContract.DATABASE_TABLE_LINE_INFORMATION_START_OF_SHIFT,currentDate.getTime()));
        }

        startDate = Utils.startOfDay(startDate);
        endDate = Utils.endOfDay(endDate);

        startDateUnix = Utils.UnixTime(startDate);
        endDateUnix = Utils.UnixTime(startDate);

        setDateRange();

        createRadioButton();

//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.fragment_analytics, new FragmentAnalyticsStops())
//                    .commit();
//        }

        Bundle bundle = new Bundle();

        bundle.putString(DbContract.DATABASE_TABLE_LINE_INFORMATION_WORK_CENTER_UID  , uidWorkCenter);
        bundle.putLong(DbContract.DATABASE_TABLE_LINE_INFORMATION_START_OF_SHIFT     , startDate.getTime());
        bundle.putLong(DbContract.DATABASE_TABLE_LINE_INFORMATION_END_OF_SHIFT       , endDate.getTime());

        FragmentAnalyticsStops fragmentAnalyticsStops = new FragmentAnalyticsStops();
        fragmentAnalyticsStops.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.replace(R.id.fragment_analytics, fragmentAnalyticsStops);
//        ft.commitNow();
        ft.commit();

//        updatePieChart();
    }

    private void updatePieChart(){

        if(getSupportFragmentManager().findFragmentById(R.id.fragment_analytics)!=null){
            FragmentAnalyticsStops fragmentAnalyticsStops = (FragmentAnalyticsStops) getSupportFragmentManager().findFragmentById(R.id.fragment_analytics);
            if (fragmentAnalyticsStops!=null){
                fragmentAnalyticsStops.updatePieChart(Utils.startOfDay(startDate),
                        Utils.endOfDay(endDate),
                        uidWorkCenter,
                        shiftFilterChecked);
            }

        }
    }

    private void createRadioButton() {

        navMenu = navigationView.getMenu();
        subMenu = navMenu.addSubMenu("Линии");

        Realm realm;
        realm = Realm.getDefaultInstance();

        RealmResults<WorkCenter> workCenters = realm.where(WorkCenter.class).findAll();

        idSubMenu=1111;
        idItem = 2222;
        int index = 0;
        lineFilter = 0;

        radioGroupsArray = new ArrayList<>();
        radioGroupsArray.clear();

        rbLinesArray = new int[workCenters.size()];
        hmRadioGroup = new HashMap<>();
        hmRadioGroup.clear();

        hmWorkCenterFilter = new HashMap<>();
        hmWorkCenterFilter.clear();
        uidWorkCenter = "";

        for(WorkCenter workCenter:workCenters){

            if(lineFilter==0){
                lineFilter = idItem;
            }
            rbLinesArray[index] = idItem;
            menuItem =   subMenu.add(idSubMenu, idSubMenu, idSubMenu, "");
            menuItem.setActionView(R.layout.radio_button_layout);
            RadioGroup rg = menuItem.getActionView().findViewById(R.id.rg_filter_lines);
            radioGroupsArray.add(rg);

            hmRadioGroup.put(idItem,rg);
            hmWorkCenterFilter.put(rg,workCenter.getUid());

            RadioButton newRadioButton = new RadioButton(this);
            newRadioButton.setText(workCenter.getDescription());
            newRadioButton.setId(idItem);
            LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.MATCH_PARENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT);


            rg.addView(newRadioButton, 0, layoutParams);
            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if(clearCheck){
                        return;
                    }{
                        lineFilter = group.getChildAt(0).getId();
                        if(!uidWorkCenter.equals(hmWorkCenterFilter.get(group))){
                            uidWorkCenter = hmWorkCenterFilter.get(group);
                            updatePieChart();
                        }
                    }

                    Iterator itDuration = hmRadioGroup.entrySet().iterator();

                    while (itDuration.hasNext()) {
                        Map.Entry pair = (Map.Entry)itDuration.next();

                        if((int)pair.getKey()!=lineFilter){
                            clearCheck = true;
                            ((RadioGroup) pair.getValue()).clearCheck();

                        }
                    }
                    clearCheck = false;
                }
            });

            index++;
            idItem++;
            idSubMenu++;
        }



        if(radioGroupsArray.size()>0){
            radioGroupsArray.get(0).getChildAt(0).performClick();
        }


        navMenu = navigationView.getMenu();
        subMenu = navMenu.addSubMenu("Смены");


        final RealmResults<Shift> shifts = realm.where(Shift.class).findAll();

        idSubMenuShift =3333;
        idItemShift = 4444;
        int indexShift = 0;
        shiftFilter = 0;

        radioGroupsArrayShift = new ArrayList<>();
        radioGroupsArrayShift.clear();

        rbShiftsArray = new int[shifts.size()];
        hmShiftRadioGroup = new HashMap<>();
        hmShiftRadioGroup.clear();

        hmShiftFilter = new HashMap<>();
        hmShiftFilter.clear();

        hmShiftStartOfShift = new HashMap<>();
        hmShiftStartOfShift.clear();

        hmShiftEndOfShift = new HashMap<>();
        hmShiftEndOfShift.clear();

        arrayListShifts = new ArrayList<>();
        arrayListShifts.clear();

        shiftCheckBoxes = new ArrayList<>();
        shiftCheckBoxes.clear();


        for(Shift shift:shifts){

            String uidShift = Utils.formatShiftStartEnd(shift.getStartOfShift(),shift.getEndOfShift());
            if(arrayListShifts.contains(uidShift)){
                continue;
            }

            arrayListShifts.add(uidShift);

            if(shiftFilter==0){
                shiftFilter = idItemShift;
            }
            rbShiftsArray[indexShift] = idItemShift;
            menuItem =   subMenu.add(idSubMenuShift, idSubMenuShift, idSubMenuShift, "");
            menuItem.setActionView(R.layout.check_box_layout);
            RelativeLayout rg = menuItem.getActionView().findViewById(R.id.layout_check_box);
//            radioGroupsArrayShift.add(rg);

//            hmShiftRadioGroup.put(idItemShift,rg);
//

            CheckBox newCheckBox = new CheckBox(this);
            String radioButtonText = Utils.formatShiftStartEnd(shift.getStartOfShift(),shift.getEndOfShift());
            newCheckBox.setText(radioButtonText);
            newCheckBox.setId(idItemShift);
            newCheckBox.setChecked(true);
            newCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int countCheckedShift = 0;
                    for(CheckBox checkBox:shiftCheckBoxes){
                        if (checkBox.isChecked()){
                            countCheckedShift++;
                        }
                    }

                    int indexShiftFilterRow = 0;

                    shiftFilterChecked  = new Long[countCheckedShift][2];

                    for(CheckBox checkBox:shiftCheckBoxes){
                        if (checkBox.isChecked()){
                            shiftFilterChecked[indexShiftFilterRow][0] = hmShiftStartOfShift.get(checkBox);
                            shiftFilterChecked[indexShiftFilterRow][1] = hmShiftEndOfShift.get(checkBox);
                            indexShiftFilterRow++;
                        }
                    }

                    updatePieChart();
                }
            });

            hmShiftStartOfShift.put(newCheckBox,shift.getStartOfShift().getTime());
            hmShiftEndOfShift.put(newCheckBox,shift.getEndOfShift().getTime());



            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);

            rg.addView(newCheckBox, 0, layoutParams);
            shiftCheckBoxes.add(newCheckBox);

            indexShift++;
            idItemShift++;
            idSubMenuShift++;
        }



//        if(radioGroupsArrayShift.size()>0){
//            radioGroupsArrayShift.get(0).getChildAt(0).performClick();
//        }

        realm.close();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_date_range:

                Intent intent = new Intent(this, ActivityDatePicker.class);
                intent.putExtra(DbContract.START_DATE, startDateUnix);
                intent.putExtra(DbContract.END_DATE, endDateUnix);

                startActivityForResult(intent, Constants.REQUEST_CODE_DATE_PICKER_OK);
                break;
            case R.id.ib_filter:
                if(drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(Gravity.RIGHT,true);
                } else{
                    drawer.openDrawer(Gravity.RIGHT,true);
                }
                break;
            default:
                break;
        }
    }

    public void setDateRange(){
        String date;
        if(startDateUnix.equals(endDateUnix)){
            date = Utils.formatDateRange(startDate);
        }else{
            date = Utils.formatDateRange(startDate)+" - "+Utils.formatDateRange(endDate);
        }
        tvDateRange.setText(date);

        updatePieChart();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((resultCode == requestCode)
                && (data != null)
                && (data.hasExtra(DbContract.START_DATE))
                && (data.hasExtra(DbContract.END_DATE))) {

            startDate = Utils.dateFromUnix(data.getLongExtra(DbContract.START_DATE,0));
            startDateUnix = Utils.UnixTime(startDate);

            endDate   = Utils.dateFromUnix(data.getLongExtra(DbContract.END_DATE,0));
            endDateUnix = Utils.UnixTime(endDate);

            setDateRange();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
