package com.restproject.mobile.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.restproject.mobile.R;
import com.restproject.mobile.models.PreviewScheduleResponse;

import java.util.List;

public class AvaiSchedSessionsListAdapter extends BaseAdapter {
    Context context;
    int layout;
    List<PreviewScheduleResponse.PreviewSessionResponse> sessions;

    public AvaiSchedSessionsListAdapter(Context context, int layout, List<PreviewScheduleResponse.PreviewSessionResponse> sessions) {
        this.context = context;
        this.layout = layout;
        this.sessions = sessions;
    }

    @Override
    public int getCount() {
        return sessions.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        var inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(this.layout, null);

        TextView name = view.findViewById(R.id.page_availableSched_dialog_sessionList_layoutItem_name);
        TextView desc = view.findViewById(R.id.page_availableSched_dialog_sessionList_layoutItem_desc);
        TextView level = view.findViewById(R.id.page_availableSched_dialog_sessionList_layoutItem_level);
        TextView muscles = view.findViewById(R.id.page_availableSched_dialog_sessionList_layoutItem_muscles);
        ListView exNames = view.findViewById(R.id.page_availableSched_dialog_sessionList_layoutItem_exerciseNamesList);

        PreviewScheduleResponse.PreviewSessionResponse session = sessions.get(position);
        name.setText(session.getSession().getName());
        desc.setText(session.getSession().getDescription());
        level.setText(session.getSession().getLevelEnum());
        muscles.setText(session.getSession().getMusclesStr());
        exNames.setAdapter(new ExNamesListAdapter(context, R.layout.layout_avaisched_exname_item,
            session.getExerciseNames()));
        ExNamesListAdapter.setListViewHeightBasedOnChildren(exNames);

        return view;
    }

}
