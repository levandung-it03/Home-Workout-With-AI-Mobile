package com.restproject.mobile.fragments;

import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONObject;

public class PaginatedListFragment extends Fragment {
    protected ImageButton paginationPrevBtn;
    protected TextView paginationCurPage;
    protected ImageButton paginationNextBtn;
    protected int totalPages = 0;

    public JSONObject getDataToRequestList() {
        JSONObject requestData;
        try {
            requestData = new JSONObject().put("page", paginationCurPage.getText().toString());
        } catch (Exception e) {
            e.fillInStackTrace();
            Toast.makeText(this.getContext(), "An Error occurred. Please restart app.",
                Toast.LENGTH_SHORT).show();
            requestData = null;
        }
        return requestData;
    }


    public void setUpPagination() {
        this.paginationPrevBtn.setOnClickListener(view -> {
            int currentPage = Integer.parseInt(this.paginationCurPage.getText().toString());
            if (currentPage == 1) return;

            this.paginationCurPage.setText(currentPage - 1);
            this.requestMainUIListData(this.getDataToRequestList());
        });
        this.paginationNextBtn.setOnClickListener(view -> {
            int currentPage = Integer.parseInt(this.paginationCurPage.getText().toString());
            if (currentPage == totalPages) return;

            this.paginationCurPage.setText(currentPage + 1);
            this.requestMainUIListData(this.getDataToRequestList());
        });
    }

    public void requestMainUIListData(JSONObject requestObj) {
    }

}
