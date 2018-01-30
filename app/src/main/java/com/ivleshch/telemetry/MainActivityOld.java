package com.ivleshch.telemetry;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.ivleshch.telemetry.data.DbContract;
import com.ivleshch.telemetry.data.DbHelper;
import com.ivleshch.telemetry.data.Device;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivityOld extends AppCompatActivity
        implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener,
        AsyncResponse{

    private Date startofShift, endOfShift;
    private TextView tvCurrentDate;
    private ImageButton arrowLeftDate, arrowRightDate;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private Calendar calendarCurrentDate;
    private DatePickerDialog.OnDateSetListener currentDatePicker;
    private Spinner spShifts;
    private String currentShiftName;
    private DrawerLayout drawer;
    private SubMenu subMenu;
    private NavigationView navigationView;
    private Menu menu;
    private Realm realm;
    private RealmResults<Device> devices;
    private int idDevice, selectedDevice;
    private Toolbar toolbar;
    private ProgressBar pbGetData;
    private Timer timer;
    private boolean timerStarted;
    private SwipeRefreshLayout swipeRefreshLayout;


    public void gestureFinish(Boolean gesture) {
        swipeRefreshLayout.setEnabled(gesture);
    }

    @Override
    public void processFinish(Integer output){

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        swipeRefreshLayout.setRefreshing(false);
//        pbGetData.setVisibility(View.GONE);

        realm = Realm.getDefaultInstance();
        devices = realm.where(Device.class)
                .findAllSorted(DbContract.DATABASE_TABLE_DEVICES_COLUMN_ID);
        for (Device device: devices) {
            if (subMenu.findItem(device.getId())==null){
                subMenu.add(device.getId(), device.getId(), device.getId(), device.getDevice_name()).setIcon(R.drawable.ic_chart_line);

            }
        }

        if (devices.size()>0){
            if (idDevice ==0){
                idDevice = devices.get(0).getId();
                toolbar.setTitle(devices.get(0).getDevice_name());
            }
            subMenu.findItem(idDevice).setChecked(true);
            selectedDevice = idDevice;
            updateView(output);
        }
        realm.close();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        item.setChecked(true);

        if (subMenu.findItem(selectedDevice)!=null){
            subMenu.findItem(selectedDevice).setChecked(false);
        }

        idDevice=item.getItemId();

        selectedDevice = idDevice;
        getData(false);


        drawer.closeDrawers();

        toolbar.setTitle(item.getTitle());

        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerStarted = false;

        pbGetData = (ProgressBar) findViewById(R.id.pb_getData);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        menu = navigationView.getMenu();
        subMenu = menu.addSubMenu(getString(R.string.subMenuTitle));



        spShifts = (Spinner) findViewById(R.id.sp_shifts);
        List<String> spinnerArray =  new ArrayList<String>();

        spinnerArray.add(Constants.FIRST_SHIFT_NAME);
        spinnerArray.add(Constants.SECOND_SHIFT_NAME);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, R.layout.spinner_layout, spinnerArray);

        adapter.setDropDownViewResource(R.layout.spinner_layout_item);
        spShifts.setAdapter(adapter);
        spShifts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentShiftName = spShifts.getSelectedItem().toString();
                if (!startofShift.equals(Utils.startOfShift(calendarCurrentDate.getTime(),spShifts.getSelectedItem().toString()))){
                    getData(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        calendarCurrentDate = Calendar.getInstance();

        currentShiftName = Utils.determineShift(calendarCurrentDate.getTime());

        spShifts.setSelection(adapter.getPosition(currentShiftName));

        tvCurrentDate = findViewById(R.id.tv_current_date);
        tvCurrentDate.setOnClickListener(this);
        tvCurrentDate.setText(simpleDateFormat.format(calendarCurrentDate.getTime()));

        arrowLeftDate = (ImageButton) findViewById(R.id.ib_arrow_left_date);
        arrowLeftDate.setOnClickListener(this);

        arrowRightDate = (ImageButton) findViewById(R.id.ib_arrow_right_date);
        arrowRightDate.setOnClickListener(this);


        currentDatePicker = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendarCurrentDate.set(Calendar.YEAR, year);
                calendarCurrentDate.set(Calendar.MONTH, monthOfYear);
                calendarCurrentDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateCurrentDate();
            }

        };

        FragmentMain fragmentMain = new FragmentMain();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.replace(R.id.fragment_main_menu, fragmentMain);
        ft.commit();

        startTimer();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getData(false);
                //swipeRefreshLayout.setRefreshing(false);
            }
        });

        getData(false);


    }

    public void startTimer(){

        if(timerStarted){
            return;
        }

        timerStarted = true;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getData(true);
            }
        },Constants.TIME_INTERVAL_NOTIFICATION_START,Constants.TIME_INTERVAL_NOTIFICATION_REPEAT);
    }

    public void stopTimer(){
        if (timerStarted && timer != null) {
            timer.cancel();
            timer.purge();
            timerStarted = false;
        }
    }

    public void getData(boolean isTimer){
        if(!isTimer){
//            pbGetData.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(true);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        startofShift = Utils.startOfShift(calendarCurrentDate.getTime(),currentShiftName);
        endOfShift   = Utils.endOfShift(calendarCurrentDate.getTime(),currentShiftName);

        DbHelper dbHelper = new DbHelper();
        dbHelper.delegate = this;
        if(!isTimer){
            dbHelper.execute(startofShift,endOfShift);
        }else{
            dbHelper.execute(startofShift,endOfShift,new Date());
        }

    }

    private void updateView(Integer getDataResult){
        FragmentMain fragmentMain = (FragmentMain) getSupportFragmentManager().findFragmentById(R.id.fragment_main_menu);
        fragmentMain.updateChart(startofShift,endOfShift,idDevice,currentShiftName,getDataResult);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_current_date:
                int year  = calendarCurrentDate.get(Calendar.YEAR);
                int month = calendarCurrentDate.get(Calendar.MONTH);
                int day   = calendarCurrentDate.get(Calendar.DAY_OF_MONTH);

                new DatePickerDialog(this, currentDatePicker, year, month,day).show();

                break;
            case R.id.ib_arrow_left_date:
                calendarCurrentDate.add(Calendar.DATE,-1);
                updateCurrentDate();
                break;
            case R.id.ib_arrow_right_date:
                calendarCurrentDate.add(Calendar.DATE,1);
                updateCurrentDate();
                break;
            default:break;
        }
    }

    private void updateCurrentDate(){
        tvCurrentDate.setText(simpleDateFormat.format(calendarCurrentDate.getTime()));
        getData(false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        pbGetData.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    protected void onPause() {
        stopTimer();
        super.onPause();
    }

    @Override
    protected void onResume() {
        startTimer();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        stopTimer();
        super.onDestroy();
    }
}
