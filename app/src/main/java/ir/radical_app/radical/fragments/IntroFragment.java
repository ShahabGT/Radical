package ir.radical_app.radical.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import androidx.fragment.app.Fragment;
import ir.radical_app.radical.activities.LoginActivity;
import ir.radical_app.radical.R;


public class IntroFragment extends Fragment {


    private String title;
    private String message;
    private int logo;
    private boolean  showBtn = false;

    public void setShowBtn() {
        this.showBtn = true;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLogo(int logo) {
        this.logo = logo;
    }

    public IntroFragment() {
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private TextView mTitle,mMessage;
    private ImageView mLogo;
    private MaterialButton mBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_intro, container, false);

        mTitle =  v.findViewById(R.id.intro_title);
        mMessage =  v.findViewById(R.id.intro_message);
        mLogo = v.findViewById(R.id.intro_logo);
        mBtn = v.findViewById(R.id.intro_btn);

        mLogo.setImageResource(logo);

        mTitle.setText(title);
        mMessage.setText(message);
      //  mLogo.setActualImageResource(logo);
        if(showBtn)
            mBtn.setVisibility(View.VISIBLE);

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });

        return v;
    }

}
