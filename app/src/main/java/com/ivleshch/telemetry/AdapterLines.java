package com.ivleshch.telemetry;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ivleshch.telemetry.data.LineInformation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivleshch on 30.01.2018.
 */

public class AdapterLines extends RecyclerView.Adapter<AdapterLines.LinesViewHolder> {

    private List<LineInformation> lines = new ArrayList<>();
    private LayoutInflater inflater;
    private final ItemClickListener clickListener;

    public AdapterLines(ArrayList<LineInformation> linesArray, ItemClickListener clickListener) {
        updateAdapter(linesArray);
        this.clickListener = clickListener;
//        LinesViewHolder.currentSelectedItemPosition = -1;
    }

    public void updateAdapter(ArrayList<LineInformation> linesArray) {
        lines.clear();
        lines.addAll(linesArray);
        notifyDataSetChanged();
    }

    @Override
    public LinesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        return LinesViewHolder.create(inflater, parent, clickListener);
    }

    @Override
    public void onBindViewHolder(LinesViewHolder holder, int position) {
        LineInformation line = lines.get(position);
//        holder.setIsRecyclable(false);
        holder.bind(line);
    }

    @Override
    public int getItemCount() {
        return lines.size();
    }


    static class LinesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

//        private TextView tvMaster,tvNomenclature,
//                tvQuantityPlan,tvQuantityFact,
//                tvQuantityDefect,tvQuantityWaste,
//                tvWorkCenter, tvAvailability,
//                tvPerformancePercent,tvQualityPercent,
//                tvOeePercent, tvQuantityStop,
//                tvDurationStop, tvReasonStop;
//        private LinearLayout llOee, llavailability, llPerformance, llQuality;
        private final ItemClickListener clickListener;
        private LineInformation line;
//        private static int currentSelectedItemPosition = -1;


        static LinesViewHolder create(LayoutInflater inflater, ViewGroup parent, ItemClickListener clickListener) {
            return new LinesViewHolder(inflater.inflate(R.layout.item_line, parent, false), clickListener);
        }

        LinesViewHolder(View itemView, ItemClickListener clickListener) {
            super(itemView);
            this.clickListener = clickListener;
//            tvMaster = (TextView) itemView.findViewById(R.id.tv_master);
//            tvNomenclature = (TextView) itemView.findViewById(R.id.tv_nomenclature);
//            tvQuantityPlan = (TextView) itemView.findViewById(R.id.tv_quantity_plan);
//            tvQuantityFact = (TextView) itemView.findViewById(R.id.tv_quantity_fact);
//            tvQuantityDefect = (TextView) itemView.findViewById(R.id.tv_quantity_defect);
//            tvQuantityWaste = (TextView) itemView.findViewById(R.id.tv_quantity_waste);
//            tvWorkCenter = (TextView) itemView.findViewById(R.id.tv_work_center);
//            tvAvailability = (TextView) itemView.findViewById(R.id.tv_availability_percent);
//            tvPerformancePercent = (TextView) itemView.findViewById(R.id.tv_performance_percent);
//            tvQualityPercent = (TextView) itemView.findViewById(R.id.tv_quality_percent);
//            tvOeePercent = (TextView) itemView.findViewById(R.id.tv_oee_percent);
//            tvQuantityStop = (TextView) itemView.findViewById(R.id.tv_quantity_stop);
//            tvDurationStop = (TextView) itemView.findViewById(R.id.tv_duration_stop);
//            tvReasonStop = (TextView) itemView.findViewById(R.id.tv_reason_stop);
//
//            llOee = (LinearLayout) itemView.findViewById(R.id.ll_line_oee);
//            llavailability = (LinearLayout) itemView.findViewById(R.id.ll_line_availability);
//            llPerformance = (LinearLayout) itemView.findViewById(R.id.ll_line_performance);
//            llQuality = (LinearLayout) itemView.findViewById(R.id.ll_line_quality);


            itemView.setOnClickListener(this);

        }

        void bind(LineInformation line) {
            this.line = line;

            Utils.fillLineInformation(itemView,line);
//            tvMaster.setText(line.getMaster().getDescription());
//            tvNomenclature.setText(line.getNomenclature().getDescription());
//            tvQuantityPlan.setText(line.getQuantityPlan().toString());
//            tvQuantityFact.setText(line.getQuantityFact().toString());
//            tvQuantityDefect.setText(line.getQuantityDefect().toString());
//            tvQuantityWaste.setText(line.getQuantityWaste().toString());
//            tvWorkCenter.setText(line.getWorkCenter().getDescription());
//            tvAvailability.setText(line.getAvailabilityPercent().toString()+"%");
//            tvPerformancePercent.setText(line.getPerformancePercent().toString()+"%");
//            tvQualityPercent.setText(line.getQualityPercent().toString()+"%");
//            tvOeePercent.setText(line.getOeePercent().toString()+"%");
//
//            tvQuantityStop.setText(line.getQuantityStops().toString());
//            tvDurationStop.setText(line.getDurationStops().toString());
//            tvReasonStop.setText(line.getReasonDescription());
//
//            llOee.setBackgroundColor(ContextCompat.getColor(llOee.getContext(), Utils.percentColor(line.getOeePercent())));
//            llavailability.setBackgroundColor(ContextCompat.getColor(llOee.getContext(), Utils.percentColor(line.getAvailabilityPercent())));
//            llPerformance.setBackgroundColor(ContextCompat.getColor(llOee.getContext(), Utils.percentColor(line.getPerformancePercent())));
//            llQuality.setBackgroundColor(ContextCompat.getColor(llOee.getContext(), Utils.percentColor(line.getQualityPercent())));

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
                clickListener.onItemClick(line);
//            }
        }
    }

    public interface ItemClickListener {
        void onItemClick(LineInformation line);
    }
}