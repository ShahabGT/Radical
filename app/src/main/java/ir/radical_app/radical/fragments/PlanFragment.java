package ir.radical_app.radical.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ir.radical_app.radical.R;


public class PlanFragment extends Fragment {


    public PlanFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_plan, container, false);

        init(v);

        return v;
    }

    private void init(View v){

    }

}
