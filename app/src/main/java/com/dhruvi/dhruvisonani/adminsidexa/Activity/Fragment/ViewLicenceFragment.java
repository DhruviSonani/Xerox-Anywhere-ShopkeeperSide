package com.dhruvi.dhruvisonani.adminsidexa.Activity.Fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dhruvi.dhruvisonani.adminsidexa.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewLicenceFragment extends Fragment {


    ImageView img_showLicence;
    public ViewLicenceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_licence, container, false);
        img_showLicence = view.findViewById(R.id.img_showLicence);
        img_showLicence.setImageResource(R.drawable.licence);
        return view;
    }

}
