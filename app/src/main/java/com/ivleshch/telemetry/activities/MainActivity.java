package com.ivleshch.telemetry.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ivleshch.telemetry.AdapterShifts;
import com.ivleshch.telemetry.R;
import com.ivleshch.telemetry.Utils;
import com.ivleshch.telemetry.data.Constants;
import com.ivleshch.telemetry.data.DbContract;
import com.ivleshch.telemetry.data.ReportForShift;
import com.ivleshch.telemetry.data.Shift;
import com.ivleshch.telemetry.data.ShiftUpdate;
import com.ivleshch.telemetry.webService.AsyncResponse;
import com.ivleshch.telemetry.webService.DbHelper;
import com.ivleshch.telemetry.webService.GetDataParams;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.Pivot;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener,
        AsyncResponse {

    private Date startofShift, endOfShift;
    private Calendar calendarCurrentDate;
    private DatePickerDialog.OnDateSetListener currentDatePicker;
    private String currentShiftName;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Realm realm;
    private RealmResults<Shift> shifts;
    private Toolbar toolbar;
    private ProgressBar pbGetData;
    private Timer timer;
    private boolean timerStarted,scrollToDate;
    private Shift currentShift;
    private int currentShiftIndex,oldShiftIndex;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DiscreteScrollView scrollView;
    private ArrayList<Shift> shiftsArray;
    private AdapterShifts adapterShifts;
    private HashMap<Date,Shift> hmShifts;
    private AdapterShifts.ItemClickListener itemClickListenerLines;
    private boolean datePickerSelect;
    private boolean updateNearestDate;
    private String uidWorkCenter;
    private FrameLayout frameLayout;
    private boolean isUpdaditing;
    private Date lastUpdate;
    private LinearLayout transparentLayout;
    private boolean autoUpdate;
    private int repeatMin;
    private final String CURRENT_SHIFT_UID = "current_shift_uid";
    private String currentShiftUid;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        if (sharedPreferences.getString(DbContract.SETTINGS_SERVER_KEY,"").equals("")){
            editor.putString(DbContract.SETTINGS_SERVER_KEY, DbContract.SETTINGS_SERVER_VALUE).apply();
        }
        if (sharedPreferences.getString(DbContract.SETTINGS_WEBSERVICE_KEY,"").equals("")){
            editor.putString(DbContract.SETTINGS_WEBSERVICE_KEY, DbContract.SETTINGS_WEBSERVICE_VALUE).apply();
        }

        if (!sharedPreferences.getBoolean(DbContract.SETTINGS_AUTO_UPDATE,false)){
            editor.putBoolean(DbContract.SETTINGS_AUTO_UPDATE, true).apply();
            editor.putBoolean(DbContract.SETTINGS_AUTO_UPDATE_KEY, DbContract.SETTINGS_AUTO_UPDATE_VALUE).apply();
            editor.putString(DbContract.SETTINGS_AUTO_UPDATE_INTERVAL_KEY, DbContract.SETTINGS_AUTO_UPDATE_INTERVAL_VALUE).apply();
        }

        transparentLayout = (LinearLayout) findViewById(R.id.transparentLayout);
        showTransparentLayout(false);

        updateNearestDate = false;
        datePickerSelect = false;

        frameLayout = (FrameLayout) findViewById(R.id.fragment_main_menu);

        hmShifts = new HashMap<>();
        hmShifts.clear();

        itemClickListenerLines = new AdapterShifts.ItemClickListener() {
            @Override
            public void onItemClick(Shift shift, Context context) {
                if(startofShift.equals(shift.getStartOfShift())){
                    calendarCurrentDate.setTime(shift.getDate());
                    int year  = calendarCurrentDate.get(Calendar.YEAR);
                    int month = calendarCurrentDate.get(Calendar.MONTH);
                    int day   = calendarCurrentDate.get(Calendar.DAY_OF_MONTH);

                    new DatePickerDialog(context, currentDatePicker, year, month,day).show();
                } else{
                    currentShift = shift;
                    currentShiftIndex = Utils.findCurrentShiftIndex(currentShift,adapterShifts.getShifts());
                    oldShiftIndex = currentShiftIndex;

                    scrollView.scrollToPosition(currentShiftIndex);

                    getData(false,false);


                }


            }
        };

        scrollView = (DiscreteScrollView) findViewById(R.id.picker);

        shiftsArray = new ArrayList<>();
        adapterShifts = new AdapterShifts(shiftsArray, itemClickListenerLines);
        scrollView.setAdapter(adapterShifts);

        timerStarted = false;

        scrollView.setOffscreenItems(3);
        scrollView.setItemTransformer(new ScaleTransformer.Builder()
                .setMaxScale(1.05f)
                .setMinScale(0.8f)
                .setPivotX(Pivot.X.CENTER) // CENTER is a default one
                .setPivotY(Pivot.Y.BOTTOM)// CENTER is a default one
                .build()
        );


        scrollView.addOnItemChangedListener(new DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder>() {
            @Override
            public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
              if(currentShift!=null && currentShiftIndex!=adapterPosition && currentShiftIndex==oldShiftIndex){
                  List<Shift> shifts = adapterShifts.getShifts();
                  currentShift = adapterShifts.getShifts().get(adapterPosition);
                  currentShiftIndex = Utils.findCurrentShiftIndex(currentShift,adapterShifts.getShifts());
                  oldShiftIndex = currentShiftIndex;

                  getData(false,false);

              }
            }
        });
        scrollView.addScrollStateChangeListener(new DiscreteScrollView.ScrollStateChangeListener<RecyclerView.ViewHolder>() {
            @Override
            public void onScrollStart(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {
                int a=5;
                a=6;

            }

            @Override
            public void onScrollEnd(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {
                int a=5;
                a=6;

            }

            @Override
            public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable RecyclerView.ViewHolder currentHolder, @Nullable RecyclerView.ViewHolder newCurrent) {
                int a=5;
                a=6;
            }
        });


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
//        menu = navigationView.getMenu();
//        subMenu = menu.addSubMenu(getString(R.string.subMenuTitle));


        calendarCurrentDate = Calendar.getInstance();


        currentShiftName = Utils.determineShift(calendarCurrentDate.getTime());

        currentDatePicker = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {


                calendarCurrentDate.set(Calendar.YEAR, year);
                calendarCurrentDate.set(Calendar.MONTH, monthOfYear);
                calendarCurrentDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendarCurrentDate.set(Calendar.HOUR_OF_DAY, 0);
                calendarCurrentDate.set(Calendar.MINUTE,0);
                calendarCurrentDate.set(Calendar.SECOND, 0);
                calendarCurrentDate.set(Calendar.MILLISECOND, 0);

                datePickerSelect = true;

                getData(false,false);
            }

        };


        FragmentLines fragmentLines = new FragmentLines();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.replace(R.id.fragment_main_menu, fragmentLines);
        ft.commit();



        startTimer();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getData(false,true);
                //swipeRefreshLayout.setRefreshing(false);
            }
        });

        getData(false,false);


    }

    public void  showTransparentLayout(boolean visibility){
        if(visibility){
            transparentLayout.setVisibility(View.VISIBLE);
        } else{
            transparentLayout.setVisibility(View.GONE);
        }
        transparentLayout.setClickable(false);
    }

    public void replaceFragment(int idFragment, Bundle bundle,String uid){
        FragmentTransaction ft;
        switch (idFragment){
            case Constants.LINE_FRAGMENT:
                FragmentLines fragmentLines = new FragmentLines();
                fragmentLines.setArguments(bundle);

                ft = getSupportFragmentManager().beginTransaction();
//                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right);
                ft.replace(R.id.fragment_main_menu, fragmentLines);
//                ft.addToBackStack(null);
                ft.commit();

                break;
            case Constants.CHART_FRAGMENT:

                uidWorkCenter = uid;
                FragmentMain fragmentMain = new FragmentMain();
                fragmentMain.setArguments(bundle);

                ft = getSupportFragmentManager().beginTransaction();
//                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right);
                ft.replace(R.id.fragment_main_menu, fragmentMain);
                ft.addToBackStack(null);
                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                ft.commit();

                break;
        }
    }

    public void gestureFinish(int gesture) {
        List<Shift> shifts = adapterShifts.getShifts();
        switch (gesture){
            case MotionEvent.EDGE_RIGHT:
                if((currentShiftIndex>0) && (shifts.size()>0) && (currentShiftIndex!=shifts.size()-1)){

                    currentShiftIndex = currentShiftIndex+1;
                    currentShift = shifts.get(currentShiftIndex);
                    oldShiftIndex = currentShiftIndex;
                    scrollView.scrollToPosition(currentShiftIndex);
                    getData(false,false);
                }
                break;
            case MotionEvent.EDGE_LEFT:
                if(currentShift!=null){
                    if(currentShiftIndex>0 && shifts.size()>0){
                        currentShiftIndex = currentShiftIndex-1;
                        currentShift = shifts.get(currentShiftIndex);
                        oldShiftIndex = currentShiftIndex;
                        scrollView.scrollToPosition(currentShiftIndex);
                        getData(false,false);
                    }
                }

                break;
            default:
                break;
        }
//        swipeRefreshLayout.setEnabled(gesture);
    }

    @Override
    public void processFinish(Integer output){

        isUpdaditing = false;

        realm = Realm.getDefaultInstance();
        shifts = realm.where(Shift.class)
                .findAllSorted(new String[]{"startOfShift"}, new Sort[]{Sort.ASCENDING});

        scrollToDate = hmShifts.size() == 0;

        for(Shift shift:shifts){
            if(hmShifts.get(shift.getStartOfShift())==null){
                adapterShifts.addItem(realm.copyFromRealm(shift));
                hmShifts.put(shift.getStartOfShift(),realm.copyFromRealm(shift));
            }
        }

        if(hmShifts.size()>0){
            if(currentShift==null || datePickerSelect){
                Shift currentFindShift = Utils.findCurrentShift(adapterShifts.getShifts(),datePickerSelect,calendarCurrentDate.getTime());
                if (!(datePickerSelect && currentFindShift==null)){
                    currentShift = currentFindShift;
                }
                if(datePickerSelect && currentFindShift==null){
                    Toast.makeText(this, getString(R.string.messageEmptyData), Toast.LENGTH_LONG).show();
                }
            }
            currentShiftIndex = Utils.findCurrentShiftIndex(currentShift,adapterShifts.getShifts());

            if(currentShift!=null){
                if(scrollToDate || currentShiftIndex!=oldShiftIndex){
                    scrollView.scrollToPosition(currentShiftIndex);
                    oldShiftIndex = currentShiftIndex;
                    if(!Utils.isTimer(output)){
                        if(scrollToDate && !Utils.dateBetween(calendarCurrentDate.getTime(),currentShift.getDate())){
                            getData(false, false);
                            updateNearestDate = true;
                        }
                    }
                }
            }

        }

        adapterShifts.notifyDataSetChanged();
        if(currentShift!=null){
            if(!Utils.isTimer(output)){
                startofShift = currentShift.getStartOfShift();
                endOfShift = currentShift.getEndOfShift();
                updateView(output);
            } else{
                if (Utils.istimerNeedToUpdate(currentShift.getStartOfShift(), currentShift.getEndOfShift())){
                    startofShift = currentShift.getStartOfShift();
                    endOfShift = currentShift.getEndOfShift();
                    updateView(output);
                }
            }
        }

        realm.close();

        if(!updateNearestDate){
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            swipeRefreshLayout.setRefreshing(false);
        }
//        }
        updateNearestDate = false;
        datePickerSelect = false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        item.setChecked(true);
        Intent intent;

        switch (item.getItemId()){
            case R.id.nav_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_analytics:
                intent = new Intent(this, AnalyticsStopsActivity.class);
                intent.putExtra(DbContract.DATABASE_TABLE_LINE_INFORMATION_START_OF_SHIFT,startofShift.getTime());
                intent.putExtra(DbContract.DATABASE_TABLE_LINE_INFORMATION_END_OF_SHIFT,endOfShift.getTime());
                startActivity(intent);
                break;
            default:
                break;
        }

//        if (subMenu.findItem(selectedDevice)!=null){
//            subMenu.findItem(selectedDevice).setChecked(false);
//        }

//        idDevice=item.getItemId();

//        selectedDevice = idDevice;
//        getData(false,false);


        drawer.closeDrawers();

//        toolbar.setTitle(item.getTitle());

        return false;
    }


    public void startTimer(){

        if(timerStarted){
            return;
        }

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(!sharedPreferences.getBoolean(DbContract.SETTINGS_AUTO_UPDATE_KEY,false)){
            return;
        }

        int repeatMin;
        try{
            repeatMin = Integer.parseInt(sharedPreferences.getString(DbContract.SETTINGS_AUTO_UPDATE_INTERVAL_KEY,""));
        }catch (Exception e){
            repeatMin = 0;
        }

        if(repeatMin==0){
            return;
        }

        timerStarted = true;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(currentShift!=null){
                    if (Utils.istimerNeedToUpdate(currentShift.getStartOfShift(), currentShift.getEndOfShift())){
                        getData(true,false);
                    }
                }
            }
        },repeatMin*Constants.TIME_INTERVAL_NOTIFICATION_START,repeatMin*Constants.TIME_INTERVAL_NOTIFICATION_REPEAT);
    }

    public void stopTimer(){
        if (timerStarted && timer != null) {
            timer.cancel();
            timer.purge();
            timerStarted = false;
        }
    }

    public void getData(boolean isTimer, boolean updateReport){

//        if(isUpdaditing){
//            return;
//        }

        isUpdaditing = true;

        boolean uploadData = true;
        Calendar calendar = Calendar.getInstance();
        Date currentTime = calendar.getTime();

        lastUpdate = null;


        if(!isTimer){
            swipeRefreshLayout.setRefreshing(true);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            if(datePickerSelect){
                startofShift = calendarCurrentDate.getTime();
                endOfShift   = calendarCurrentDate.getTime();
            } else{
                if(currentShift==null){
                    startofShift = currentTime;
                    endOfShift   = currentTime;
                }else{

                    startofShift = currentShift.getStartOfShift();
                    endOfShift = currentShift.getEndOfShift();
//                    if (lastUpdate!=null){
//
//                    }
                }
            }
        }

        uploadData = getLastUpdate(updateReport, endOfShift);

        if(uploadData){
            updateReport = false;
        }

        if(uploadData || updateReport) {

            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String server  = sharedPreferences.getString(DbContract.SETTINGS_SERVER_KEY,"");
            String webService = sharedPreferences.getString(DbContract.SETTINGS_WEBSERVICE_KEY,"");

            GetDataParams params;
            if(!isTimer){
                params = new GetDataParams(startofShift, endOfShift, isTimer,updateReport,server,webService,lastUpdate);
            } else{
                params = new GetDataParams(currentTime, currentTime, isTimer,updateReport,server,webService,lastUpdate);
            }
            DbHelper dbHelper = new DbHelper();
            dbHelper.delegate = this;
            dbHelper.execute(params);
        } else{
            isUpdaditing = false;
        }


    }

    private boolean getLastUpdate(boolean updateReport, Date end){
        Realm realmCheck;
        realmCheck = Realm.getDefaultInstance();
        realmCheck.refresh();
        boolean uploadData;

        uploadData = true;

        RealmResults<ShiftUpdate> shiftUpdates = realmCheck.where(ShiftUpdate.class)
                .equalTo("date",Utils.dateStartOfDate(startofShift))
                .findAll();
        if(shiftUpdates.size() > 0 && !updateReport){
            ShiftUpdate shiftUpdate = realmCheck.copyFromRealm(shiftUpdates.get(0));
            if(shiftUpdate.getLastUpdate().after(end)
                    || shiftUpdate.getLastUpdate().equals(end)){
                uploadData = false;
                lastUpdate = null;


                if(datePickerSelect){
                    if(hmShifts.size()>0){
                        Shift currentFindShift = Utils.findCurrentShift(adapterShifts.getShifts(),datePickerSelect,calendarCurrentDate.getTime());
                        if (currentFindShift!=null){
                            currentShift = currentFindShift;
                        } else{
                            Toast.makeText(this, getString(R.string.messageEmptyData), Toast.LENGTH_LONG).show();
                        }

                        currentShiftIndex = Utils.findCurrentShiftIndex(currentShift,adapterShifts.getShifts());

                        if(currentShift!=null){
                            if(currentShiftIndex!=oldShiftIndex){
                                scrollView.scrollToPosition(currentShiftIndex);
                                oldShiftIndex = currentShiftIndex;
//                                if(!Utils.isTimer(output)){
//                                    if(scrollToDate && !Utils.dateBetween(calendarCurrentDate.getTime(),currentShift.getDate())){
//                                        getData(false, false);
//                                        updateNearestDate = true;
//                                    }
//                                }
                            }
                            startofShift = currentShift.getStartOfShift();
                            endOfShift = currentShift.getEndOfShift();
                        }

                    }
                    datePickerSelect = false;
                }

                RealmResults<ReportForShift> reportForShifts = realmCheck.where(ReportForShift.class)
                        .equalTo("startOfShift", startofShift)
                        .equalTo("finished",false)
                        .findAll();

                if(reportForShifts.size()>0){
                    uploadData = true;
                    lastUpdate = shiftUpdate.getLastUpdate();
                }else{
                    updateView(Constants.ASYNC_TASK_RESULT_SUCCESSFUL);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    swipeRefreshLayout.setRefreshing(false);
                }


            } else{
                uploadData = true;
                lastUpdate = shiftUpdate.getLastUpdate();
            }
        } else{
            uploadData = true;
            lastUpdate = null;
        }

        realmCheck.close();

        return uploadData;
    }

    private void updateView(Integer getDataResult){
//        FragmentMain fragmentMain = (FragmentMain) getSupportFragmentManager().findFragmentById(R.id.fragment_main_menu);
//        fragmentMain.updateChart(startofShift,endOfShift,idDevice,currentShiftName,getDataResult);
        if(getSupportFragmentManager().findFragmentById(R.id.fragment_main_menu)!=null){
            switch (getSupportFragmentManager().findFragmentById(R.id.fragment_main_menu).getClass().getName()){
                case Constants.FRAGMENT_MAIN_CLASS:
                    FragmentMain fragmentMain = (FragmentMain) getSupportFragmentManager().findFragmentById(R.id.fragment_main_menu);
                    if (fragmentMain!=null){
                        fragmentMain.updateChart(startofShift,endOfShift,uidWorkCenter,getDataResult);
                    }
                    break;
                case Constants.FRAGMENT_LINE_CLASS:
                    FragmentLines fragmentLines = (FragmentLines) getSupportFragmentManager().findFragmentById(R.id.fragment_main_menu);
                    if (fragmentLines!=null){
                        fragmentLines.updateLineInfomation(currentShift.getStartOfShift(),currentShift.getEndOfShift(),getDataResult);
                    }
                    break;
                default:
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            default:break;
        }
    }



    @Override
    public void onBackPressed() {

        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();

        boolean handled = false;
        for(Fragment f : fragmentList) {
            if(f instanceof BaseFragment) {
                handled = ((BaseFragment)f).onBackPressed();

                if(handled) {
                    break;
                }
            }
        }

        if(!handled) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            new AlertDialog.Builder(this)
                    .setTitle("Ю - телеметрия")
                    .setMessage("Вы уверены, что хотите выйти?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            stopTimer();
                            finish();
                        }
                    }).create().show();

//            super.onBackPressed();

        }
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
