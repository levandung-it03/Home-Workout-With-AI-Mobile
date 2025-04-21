package com.restproject.mobile.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.restproject.mobile.R;

public class Oauth2Fragment extends Fragment {
    private final String oauth2Url;
    public Oauth2Fragment(String oauth2Url) {
        this.oauth2Url = oauth2Url;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_oauth2, container, false);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(this.oauth2Url));
        startActivity(intent);
        if (getActivity() != null) {
            getParentFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .remove(this)
                .commit();
        }
        return view;
    }
}