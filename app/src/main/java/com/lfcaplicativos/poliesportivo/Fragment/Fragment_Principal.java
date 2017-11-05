package com.lfcaplicativos.poliesportivo.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lfcaplicativos.poliesportivo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Principal extends Fragment {


    public Fragment_Principal() {
        // Required empty public constructor
    }

    public static Fragment_Principal newInstance() {
        Fragment_Principal fragment = new Fragment_Principal();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_principal, container, false);
    }

}
