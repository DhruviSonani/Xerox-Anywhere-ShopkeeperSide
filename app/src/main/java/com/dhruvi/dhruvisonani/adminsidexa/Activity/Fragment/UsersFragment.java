package com.dhruvi.dhruvisonani.adminsidexa.Activity.Fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.dhruvi.dhruvisonani.adminsidexa.R;

public class UsersFragment extends Fragment {


    public UsersFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_users, container, false);
    }

}
