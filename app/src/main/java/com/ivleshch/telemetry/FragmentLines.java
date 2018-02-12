package com.ivleshch.telemetry;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ivleshch.telemetry.data.DbContract;
import com.ivleshch.telemetry.data.LineInformation;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Ivleshch on 30.01.2018.
 */

public class FragmentLines extends Fragment implements View.OnTouchListener{

    private AdapterLines adapterLines;
    private AdapterLines.ItemClickListener itemClickListenerLines;
    private ArrayList<LineInformation> lines;
    private RecyclerView rvLines;
    private boolean isTimer, linesIsUpdaditing;
    private Date startOfShift, endOfShift, endOfShiftRealm;
    private Integer getDataResult;
    private ItemTouchHelper itemTouchHelper;
    private Paint p = new Paint();
    private LinearLayout llEmptyData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lines, parent, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(getArguments()!=null){
            startOfShift = Utils.dateFromUnixWithOut1000(getArguments().getLong(DbContract.DATABASE_TABLE_LINE_INFORMATION_START_OF_SHIFT));
            endOfShift = Utils.dateFromUnixWithOut1000(getArguments().getLong(DbContract.DATABASE_TABLE_LINE_INFORMATION_END_OF_SHIFT));
        }

        llEmptyData = (LinearLayout) view.findViewById(R.id.ll_empty);
        rvLines = (RecyclerView) view.findViewById(R.id.rv_lines);

        itemClickListenerLines = new AdapterLines.ItemClickListener(){
            @Override
            public void onItemClick(LineInformation line) {
                Bundle bundle = new Bundle();

                bundle.putString(DbContract.DATABASE_TABLE_LINE_INFORMATION_WORK_CENTER_UID  , line.getWorkCenter().getUid());
                bundle.putString(DbContract.DATABASE_TABLE_LINE_INFORMATION_NOMENCLATURE_UID  , line.getNomenclature().getUid());
                bundle.putLong(DbContract.DATABASE_TABLE_LINE_INFORMATION_START_OF_SHIFT     , startOfShift.getTime());
                bundle.putLong(DbContract.DATABASE_TABLE_LINE_INFORMATION_END_OF_SHIFT       , endOfShift.getTime());

                ((MainActivity) getActivity()).replaceFragment(Constants.CHART_FRAGMENT,bundle,line.getWorkCenter().getUid());
            }
        };

        lines = new ArrayList<>();
        adapterLines = new AdapterLines(lines,itemClickListenerLines);
        if(getArguments()!=null){
            updateLineInfomation(startOfShift,endOfShift,Constants.ASYNC_TASK_RESULT_SUCCESSFUL);
        }



        view.setOnTouchListener(this);

//        view.setOnTouchListener(new OnSwipeTouchListener(getContext()));
//        view.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {

//            }
//        });

        itemTouchHelper = new ItemTouchHelper(mIth);
        itemTouchHelper.attachToRecyclerView(rvLines);


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


    ItemTouchHelper.SimpleCallback mIth = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();

            if (direction == ItemTouchHelper.LEFT){
                ((MainActivity) getActivity()).gestureFinish(MotionEvent.EDGE_RIGHT);
            } else if (direction == ItemTouchHelper.RIGHT){
                ((MainActivity) getActivity()).gestureFinish(MotionEvent.EDGE_LEFT);
            }
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        }

    };

    public void updateLineInfomation(Date start, Date end, Integer getData){

        if(getData.equals(Constants.TIMER_ASYNC_TASK_RESULT_SUCCESSFUL)
                || getData.equals(Constants.TIMER_ASYNC_TASK_RESULT_FAILED)){
            isTimer = true;
        }else{
            isTimer = false;
        }

        startOfShift = start;
        endOfShift = end;
        getDataResult = getData;
//        if((getDataResult.equals(Constants.TIMER_ASYNC_TASK_RESULT_SUCCESSFUL))
//                && startOfShift.equals(Utils.startOfShift(Utils.currentDate(),currentShiftName))){
//            updateLines();
//        }else if(!isTimer){
            updateLines();
//        }
    }

    private void updateLines(){
        if(!linesIsUpdaditing){
            if(isTimer){
//                chart.setTouchEnabled(false);
//                barChart.setTouchEnabled(false);
//                chart.setScaleXEnabled(false);
            }
            linesIsUpdaditing = true;
            LineHelper lineHelper = new LineHelper();
            lineHelper.execute();
        }
    }

    private class LineHelper extends AsyncTask<Date, String, String> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(String r) {
            linesIsUpdaditing = false;

            if(adapterLines.getItemCount()>0){
                rvLines.setVisibility(View.VISIBLE);
                llEmptyData.setVisibility(View.GONE);
            }else{
                rvLines.setVisibility(View.GONE);
                llEmptyData.setVisibility(View.VISIBLE);
            }
            rvLines.setAdapter(adapterLines);
            adapterLines.notifyDataSetChanged();

        }

        @Override
        protected String doInBackground(Date... params) {

            try {

                lines.clear();
                lines = Utils.getLines(startOfShift,endOfShift,null,null);
                adapterLines = new AdapterLines(lines,itemClickListenerLines);

            } catch (Exception e) {

            } finally {

            }
            return "";
        }
    }
}
