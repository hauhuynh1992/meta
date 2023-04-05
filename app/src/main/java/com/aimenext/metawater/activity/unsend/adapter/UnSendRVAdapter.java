package com.aimenext.metawater.activity.unsend.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.aimenext.metawater.R;
import com.aimenext.metawater.data.Job;
import com.aimenext.metawater.utils.CountUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UnSendRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Job> taskList;
    private ArrayList<UnSendItem> listViews = new ArrayList<>();

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
        UnSendItem data = listViews.get(position);
        viewHolder.txt_code.setText(data.getCanCode());
        viewHolder.txt_type.setText(data.getType());
        viewHolder.txt_num_image.setText(data.getImageNum() + " 枚の画像");
        viewHolder.txt_date.setText(convertTimeLongToString(data.getDate()));
    }

    @Override
    public int getItemCount() {
        return listViews.size();
    }

    public void setJobs(ArrayList<Job> mJobs) {
        this.taskList.addAll(mJobs);
        ArrayList<Job> filter = CountUtils.removeDuplicate(this.taskList);
        for (int i = 0; i < filter.size(); i++) {
            int num = CountUtils.getDuplicateCode(filter.get(i).getCanCode(), taskList);
            listViews.add(new UnSendItem(
                    filter.get(i).getId(),
                    num,
                    filter.get(i).getCanCode(),
                    filter.get(i).getType(),
                    filter.get(i).getUnique(),
                    filter.get(i).getDate()

            ));
        }
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
