package com.restproject.mobile.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.restproject.mobile.R;
import com.restproject.mobile.models.Schedule;

import java.util.List;

public class HomeScheduleListAdapter extends BaseAdapter {
    Context context;
    int layout;
    List<Schedule> scheduleList;

    public HomeScheduleListAdapter(Context context, int layout, List<Schedule> scheduleList) {
        this.context = context;
        this.layout = layout;
        this.scheduleList = scheduleList;
    }

    @SuppressLint({"ViewHolder", "SetTextI18n"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        var inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(this.layout, null);

        TextView id = convertView.findViewById(R.id.page_home_scheduleList_layoutItem_id);
        TextView name = convertView.findViewById(R.id.page_home_scheduleList_layoutItem_name);
        TextView level = convertView.findViewById(R.id.page_home_scheduleList_layoutItem_level);
        TextView desc = convertView.findViewById(R.id.page_home_scheduleList_layoutItem_desc);

        Schedule schedule = this.scheduleList.get(position);
        id.setText("#" + schedule.getScheduleId().toString());
        name.setText(schedule.getName());
        desc.setText(schedule.getDescription());
        AdapterHelper.checkAndChangeLevelTag(level, schedule.getLevelEnum());

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return scheduleList.size();
    }
}
