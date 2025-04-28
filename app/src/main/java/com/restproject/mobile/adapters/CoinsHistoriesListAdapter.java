package com.restproject.mobile.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.restproject.mobile.R;
import com.restproject.mobile.models.ChangingCoinsHistories;

import java.util.List;

public class CoinsHistoriesListAdapter extends BaseAdapter {
    Context context;
    int layout;
    List<ChangingCoinsHistories> histories;

    public CoinsHistoriesListAdapter(Context context, int layout, List<ChangingCoinsHistories> histories) {
        this.context = context;
        this.layout = layout;
        this.histories = histories;
    }

    @Override
    public int getCount() {
        return histories.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint({"ViewHolder", "SetTextI18n", "ResourceAsColor"})
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        var inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(this.layout, null);

        ChangingCoinsHistories data = this.histories.get(position);
        TextView changingType = view.findViewById(R.id.page_coinsHistories_historiesListView_changingType);
        TextView coinsAmount = view.findViewById(R.id.page_coinsHistories_historiesListView_coinsAmount);
        TextView changingDate = view.findViewById(R.id.page_coinsHistories_historiesListView_changingDate);
        coinsAmount.setText(data.getChangingCoins().toString());
        changingDate.setText(data.getChangingTime());
        if (data.getChangingCoinsType().equals("DEPOSIT")) {
            changingType.setText("+ " + data.getChangingCoinsType());
            changingType.setBackgroundResource(R.drawable.bg_blue_histories);
        } else if (data.getChangingCoinsType().equals("USING")) {
            changingType.setText("- " + data.getChangingCoinsType());
            changingType.setBackgroundResource(R.drawable.bg_black_histories);
        }
        return view;
    }

}
