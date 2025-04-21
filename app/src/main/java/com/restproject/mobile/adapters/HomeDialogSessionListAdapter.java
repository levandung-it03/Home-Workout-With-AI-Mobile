package com.restproject.mobile.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.internal.LinkedTreeMap;
import com.restproject.mobile.R;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HomeDialogSessionListAdapter extends BaseAdapter {
    Context context;
    int layout;
    List<SessionInItemLayout> sessions;

    public HomeDialogSessionListAdapter(Context context, int layout, List<SessionInItemLayout> sessions) {
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

    @SuppressLint({"ViewHolder", "SetTextI18n"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        var inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(this.layout, null);

        TextView ordinal = convertView.findViewById(R.id.page_home_dialog_sessionList_layoutItem_ordinal);
        TextView name = convertView.findViewById(R.id.page_home_dialog_sessionList_layoutItem_name);
        TextView level = convertView.findViewById(R.id.page_home_dialog_sessionList_layoutItem_level);
        TextView desc = convertView.findViewById(R.id.page_home_dialog_sessionList_layoutItem_desc);
        TextView muscles = convertView.findViewById(R.id.page_home_dialog_sessionList_layoutItem_muscles);

        SessionInItemLayout session = this.sessions.get(position);
        ordinal.setText(session.getOrdinal().toString());
        name.setText(session.getSessionName());
        desc.setText(session.getDescription());
        muscles.setText(session.getMusclesStr());
        AdapterHelper.checkAndChangeLevelTag(level, session.getLevelEnum());

        return convertView;
    }

    public static class SessionInItemLayout {
        private Integer ordinal;
        private String sessionName;
        private String description;
        private String levelEnum;
        private List<LinkedTreeMap> muscles;
        private String musclesStr;

        public SessionInItemLayout() {
        }

        public SessionInItemLayout(Integer ordinal, String sessionName, String description,
                                   String levelEnum, List<LinkedTreeMap> muscles) {
            this.ordinal = ordinal;
            this.sessionName = sessionName;
            this.description = description;
            this.levelEnum = levelEnum;
            this.muscles = muscles;
        }

        public Integer getOrdinal() {
            return ordinal;
        }

        public void setOrdinal(Integer ordinal) {
            this.ordinal = ordinal;
        }

        public String getSessionName() {
            return sessionName;
        }

        public void setSessionName(String sessionName) {
            this.sessionName = sessionName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLevelEnum() {
            return levelEnum;
        }

        public void setLevelEnum(String levelEnum) {
            this.levelEnum = levelEnum;
        }

        public List<LinkedTreeMap> getMuscles() {
            return muscles;
        }

        public void setMuscles(List<LinkedTreeMap> muscles) {
            this.muscles = muscles;
        }

        public String getMusclesStr() {
            return musclesStr;
        }

        public void setMusclesStr(String musclesStr) {
            this.musclesStr = musclesStr;
        }

        public static SessionInItemLayout mapping(LinkedTreeMap data) {
            SessionInItemLayout result = new SessionInItemLayout();
            if (data.containsKey("session") && Objects.nonNull(data.get("session"))) {
                LinkedTreeMap session = (LinkedTreeMap) data.get("session");
                if (data.containsKey("ordinal") && Objects.nonNull(data.get("ordinal")))
                    result.setOrdinal((int) Double.parseDouble(data.get("ordinal").toString()));
                if (session.containsKey("name") && Objects.nonNull(session.get("name")))
                    result.setSessionName(session.get("name").toString());
                if (session.containsKey("description") && Objects.nonNull(session.get("description")))
                    result.setDescription(session.get("description").toString());
                if (session.containsKey("levelEnum") && Objects.nonNull(session.get("levelEnum")))
                    result.setLevelEnum(session.get("levelEnum").toString());
                if (session.containsKey("muscles") && Objects.nonNull(session.get("muscles"))) {
                    result.setMuscles((List<LinkedTreeMap>) session.get("muscles"));
                    result.setMusclesStr(result.getMuscles().stream()
                        .map(map -> map.get("muscleName").toString())
                        .collect(Collectors.joining(" - ")));
                }
            }
            return result;
        }
    }
}
