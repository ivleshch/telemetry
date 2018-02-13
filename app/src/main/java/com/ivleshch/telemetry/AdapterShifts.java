package com.ivleshch.telemetry;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ivleshch.telemetry.data.Shift;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivleshch on 30.01.2018.
 */

public class AdapterShifts extends RecyclerView.Adapter<AdapterShifts.ShiftsViewHolder> {

    private List<Shift> shifts = new ArrayList<>();
    private LayoutInflater inflater;
    private final ItemClickListener clickListener;

    public AdapterShifts(ArrayList<Shift> shiftsArray, ItemClickListener clickListener) {
        updateAdapter(shiftsArray);
        this.clickListener = clickListener;
//        LinesViewHolder.currentSelectedItemPosition = -1;
    }

    public void updateAdapter(ArrayList<Shift> shiftsArray) {
        shifts.clear();
        shifts.addAll(shiftsArray);
        notifyDataSetChanged();
    }

    public void addItem(Shift shift){

        if(shifts.size()>0){
            if(shifts.get(0).getStartOfShift().after(shift.getStartOfShift())){
                shifts.add(0,shift);
            } else if(shifts.get(shifts.size()-1).getStartOfShift().before(shift.getStartOfShift())){
                shifts.add(shift);

            } else{
                int index = 0;
                for(Shift shiftItem:shifts){

                    if(shiftItem.getStartOfShift().after(shift.getStartOfShift())){
                        break;
                    }
                    index = index+1;
                }
                shifts.add(index,shift);
            }
        } else{
            shifts.add(shift);
        }
    }

    public List<Shift> getShifts(){
        return shifts;
    }

    @Override
    public ShiftsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        return ShiftsViewHolder.create(inflater, parent,clickListener);
    }

    @Override
    public void onBindViewHolder(ShiftsViewHolder holder, int position) {
        Shift shift = shifts.get(position);
//        holder.setIsRecyclable(false);
        holder.bind(shift);
    }

    @Override
    public int getItemCount() {
        return shifts.size();
    }


    static class ShiftsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvShift;
        private final ItemClickListener clickListener;
        private Shift shift;
//        private static int currentSelectedItemPosition = -1;


        static ShiftsViewHolder create(LayoutInflater inflater, ViewGroup parent, ItemClickListener clickListener) {
            return new ShiftsViewHolder(inflater.inflate(R.layout.item_shift, parent, false),clickListener);
        }


        ShiftsViewHolder(View itemView,ItemClickListener clickListener) {
            super(itemView);
            this.clickListener = clickListener;

            tvShift = (TextView) itemView.findViewById(R.id.item_shift);

            itemView.setOnClickListener(this);

        }

        void bind(Shift shift) {
            this.shift = shift;

            String shiftTitle = shift.getDate().toString() + shift.getStartOfShift();

            tvShift.setText(Utils.formatShift(shift.getDate(),shift.getStartOfShift(),shift.getEndOfShift()));

//            if (currentSelectedItemPosition == getAdapterPosition() && !onlyRead){
//                itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.colorDeepOrange300));
////                notifyDataSetChanged();
//            }

//            imageView.setImageDrawable(new ColorDrawable(item.getColor()));
        }



        @Override
        public void onClick(View v) {
//            if (clickListener != null && !onlyRead) {

//                if (currentSelectedItem!=null){
//                    currentSelectedItem.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.ripple_main_menu));
//                }

//                v.setBackgroundColor(ContextCompat.getColor(v.getContext(),R.color.colorDeepOrange300));
//                currentSelectedItem = v;
//                currentSelectedItemPosition = getAdapterPosition();
                clickListener.onItemClick(shift, v.getContext());
//            }
        }
    }

    public interface ItemClickListener {
        void onItemClick(Shift shift, Context context);
    }
}