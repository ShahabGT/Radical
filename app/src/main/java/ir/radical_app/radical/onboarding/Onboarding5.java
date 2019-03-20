package ir.radical_app.radical.onboarding;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.Fragment;
import ir.radical_app.radical.R;
import ir.radical_app.radical.activities.LoginActivity;


public class Onboarding5 extends Fragment {
    private ImageView backbtn;
    private FloatingActionButton nextbtn;
    private MaterialCardView btn;

    public Onboarding5() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_onboarding5, container, false);

        init(v);

        return v;
    }

    private void init(View v){
        backbtn = getActivity().findViewById(R.id.intro_backbtn);
        backbtn.setVisibility(View.GONE);
        nextbtn = getActivity().findViewById(R.id.intro_nextbtn);
        nextbtn.hide();

        btn = v.findViewById(R.id.onboarding5_start);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),LoginActivity.class));
                getActivity().overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                getActivity().finish();
            }
        });


    }

}
