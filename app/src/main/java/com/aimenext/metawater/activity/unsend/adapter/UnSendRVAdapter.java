package com.aimenext.metawater.activity.unsend.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aimenext.metawater.R;
import com.aimenext.metawater.data.Job;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.recyclerview.widget.RecyclerView;

public class UnSendRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Job> taskList;

    public UnSendRVAdapter() {
        this.taskList = new ArrayList<>();
    }

    class JobViewHoler extends RecyclerView.ViewHolder {
        public TextView txt_type,
                txt_date,
                txt_num_image,
                txt_code;
        public Context context;

        public JobViewHoler(View itemView) {
            super(itemView);
            txt_type = (TextView) itemView.findViewById(R.id.txtType);
            txt_num_image = (TextView) itemView.findViewById(R.id.txtNumImage);
            txt_code = (TextView) itemView.findViewById(R.id.txtCode);
            txt_date = (TextView) itemView.findViewById(R.id.txtDate);
            context = itemView.getContext();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View ferryView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_unsend, parent, false);
        return new JobViewHoler(ferryView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        JobViewHoler viewHolder = (JobViewHoler) holder;
        Job data = taskList.get(position);
        viewHolder.txt_code.setText(data.getCanCode());
        viewHolder.txt_type.setText(data.getType());
        viewHolder.txt_date.setText(convertTimeLongToString(data.getDate()));
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void setJobs(ArrayList<Job> mJobs) {
        this.taskList.addAll(mJobs);
        notifyDataSetChanged();
    }

    public List<Job> getAll() {
        return taskList;
    }

    private String convertTimeLongToString(Long time) {
        try {
            return new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date(time));
        } catch (Exception e) {
            return "N/A";
        }
    }
}
