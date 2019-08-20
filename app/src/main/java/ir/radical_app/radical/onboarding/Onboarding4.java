package ir.radical_app.radical.onboarding;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import ir.radical_app.radical.R;
import ir.radical_app.radical.activities.LoginActivity;
import ir.radical_app.radical.classes.Const;

public class Onboarding4 extends Fragment {

    private ImageView backbtn;
    private FloatingActionButton nextbtn;
    private ImageView logo,round1,round2;
    private TextView text,text2,title;
    private MaterialCardView btn;

    private View v;


    public Onboarding4() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v= inflater.inflate(R.layout.fragment_onboarding4, container, false);

        init(v);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initAnimation();
    }

    private void init(View v){
        backbtn = getActivity().findViewById(R.id.intro_backbtn);
        nextbtn = getActivity().findViewById(R.id.intro_nextbtn);

        text = v.findViewById(R.id.onboarding4_text);
        text2 = v.findViewById(R.id.onboarding5_text);
        title = v.findViewById(R.id.onboarding4_title);
        logo = v.findViewById(R.id.onboarding4_logo);
        round1=v.findViewById(R.id.onboarding4_round1);
        round2= v.findViewById(R.id.onboarding4_round2);

        backbtn.setOnClickListener(View->
                backAnimation()
            );
        nextbtn.hide();



        btn = v.findViewById(R.id.onboarding5_start);
        btn.setOnClickListener(View-> {

                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                getActivity().finish();

        });

    }
    private void initAnimation(){
        ObjectAnimator textTranslation = ObjectAnimator.ofFloat(
                text,
                "TranslationX",
                1500f,0f
        );
        ObjectAnimator text2Translation = ObjectAnimator.ofFloat(
                text2,
                "TranslationX",
                1500f,0f
        );
        textTranslation.setDuration(500);
        text2Translation.setDuration(500);

        ObjectAnimator titleTranslation = ObjectAnimator.ofFloat(
                title,
                "TranslationX",
                2000f,0f
        );
        titleTranslation.setDuration(500);

        ObjectAnimator logoAlpha = ObjectAnimator.ofFloat(
                logo,
                "alpha",
                0f,1f
        );
        logoAlpha.setDuration(500);

        ObjectAnimator logo2Alpha = ObjectAnimator.ofFloat(
                btn,
                "alpha",
                0f,1f
        );
        logo2Alpha.setDuration(500);

        ObjectAnimator round1Translation = ObjectAnimator.ofFloat(
                round1,
                "TranslationX",
                -500f,0f
        );
        round1Translation.setDuration(500);

        ObjectAnimator round2Translation = ObjectAnimator.ofFloat(
                round2,
                "TranslationX",
                500f,0f
        );
        round2Translation.setDuration(500);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(textTranslation,text2Translation,titleTranslation,logoAlpha,logo2Alpha,round1Translation,round2Translation);
        animatorSet.start();
    }
    private void backAnimation(){
        ObjectAnimator textTranslation = ObjectAnimator.ofFloat(
                text,
                "TranslationX",
                0f,1500f
        );
        textTranslation.setDuration(500);

        ObjectAnimator text2Translation = ObjectAnimator.ofFloat(
                text2,
                "TranslationX",
                0f,1500f
        );
        text2Translation.setDuration(500);

        ObjectAnimator titleTranslation = ObjectAnimator.ofFloat(
                title,
                "TranslationX",
                0f,2000f
        );
        titleTranslation.setDuration(500);

        ObjectAnimator logoAlpha = ObjectAnimator.ofFloat(
                logo,
                "alpha",
                1f,0f
        );
        logoAlpha.setDuration(500);

        ObjectAnimator round1Translation = ObjectAnimator.ofFloat(
                round1,
                "TranslationX",
                0f,-700f
        );
        round1Translation.setDuration(500);

        ObjectAnimator round2Translation = ObjectAnimator.ofFloat(
                round2,
                "TranslationX",
                0f,700f
        );
        round2Translation.setDuration(500);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(textTranslation,text2Translation,titleTranslation,logoAlpha,round1Translation,round2Translation);

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                new Handler().postDelayed(()-> {

                    Navigation.findNavController(v).popBackStack();
                },400);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }

}
