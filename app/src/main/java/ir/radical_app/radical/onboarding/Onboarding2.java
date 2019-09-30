package ir.radical_app.radical.onboarding;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ir.radical_app.radical.R;

public class Onboarding2 extends Fragment {
    private ImageView backbtn;
    private FloatingActionButton nextbtn;
    private ImageView logo, round1, round2;
    private TextView text, title;
    private boolean fromBack;

    private View v;

    void setFromBack() {
        this.fromBack = true;
    }

    public Onboarding2() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_onboarding2, container, false);

        init(v);


        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (fromBack)
            fromBackAnimation();
        else
            initAnimation();

    }

    private void init(View v) {
        backbtn = getActivity().findViewById(R.id.intro_backbtn);
        backbtn.setVisibility(View.VISIBLE);
        nextbtn = getActivity().findViewById(R.id.intro_nextbtn);
        nextbtn.show();

        text = v.findViewById(R.id.onboarding2_text);
        title = v.findViewById(R.id.onboarding2_title);
        logo = v.findViewById(R.id.onboarding2_logo);
        round1 = v.findViewById(R.id.onboarding2_round1);
        round2 = v.findViewById(R.id.onboarding2_round2);

        backbtn.setOnClickListener(View -> {
                    backAnimation();

                }
        );

        nextbtn.setOnClickListener(View -> {
                    nextAnimation();

                }
        );

    }

    private void initAnimation() {
        ObjectAnimator textTranslation = ObjectAnimator.ofFloat(
                text,
                "TranslationX",
                1500f, 0f
        );
        textTranslation.setDuration(500);

        ObjectAnimator titleTranslation = ObjectAnimator.ofFloat(
                title,
                "TranslationX",
                2000f, 0f
        );
        titleTranslation.setDuration(500);

        ObjectAnimator logoAlpha = ObjectAnimator.ofFloat(
                logo,
                "alpha",
                0f, 1f
        );
        logoAlpha.setDuration(500);

        ObjectAnimator round1Translation = ObjectAnimator.ofFloat(
                round1,
                "TranslationX",
                -500f, 0f
        );
        round1Translation.setDuration(500);

        ObjectAnimator round2Translation = ObjectAnimator.ofFloat(
                round2,
                "TranslationX",
                500f, 0f
        );
        round2Translation.setDuration(500);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(textTranslation, titleTranslation, logoAlpha, round1Translation, round2Translation);
        animatorSet.start();
    }

    private void fromBackAnimation() {
        ObjectAnimator textTranslation = ObjectAnimator.ofFloat(
                text,
                "TranslationX",
                -1500f, 0f
        );
        textTranslation.setDuration(500);

        ObjectAnimator titleTranslation = ObjectAnimator.ofFloat(
                title,
                "TranslationX",
                -2000f, 0f
        );
        titleTranslation.setDuration(500);

        ObjectAnimator logoAlpha = ObjectAnimator.ofFloat(
                logo,
                "alpha",
                0f, 1f
        );
        logoAlpha.setDuration(500);

        ObjectAnimator round1Translation = ObjectAnimator.ofFloat(
                round1,
                "TranslationX",
                -700f, 0f
        );
        round1Translation.setDuration(500);

        ObjectAnimator round2Translation = ObjectAnimator.ofFloat(
                round2,
                "TranslationX",
                700f, 0f
        );
        round2Translation.setDuration(500);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(textTranslation, titleTranslation, logoAlpha, round1Translation, round2Translation);
        animatorSet.start();
    }

    private void backAnimation() {
        ObjectAnimator textTranslation = ObjectAnimator.ofFloat(
                text,
                "TranslationX",
                0f, 1500f
        );
        textTranslation.setDuration(500);

        ObjectAnimator titleTranslation = ObjectAnimator.ofFloat(
                title,
                "TranslationX",
                0f, 2000f
        );
        titleTranslation.setDuration(500);

        ObjectAnimator logoAlpha = ObjectAnimator.ofFloat(
                logo,
                "alpha",
                1f, 0f
        );
        logoAlpha.setDuration(500);

        ObjectAnimator round1Translation = ObjectAnimator.ofFloat(
                round1,
                "TranslationX",
                0f, -700f
        );
        round1Translation.setDuration(500);

        ObjectAnimator round2Translation = ObjectAnimator.ofFloat(
                round2,
                "TranslationX",
                0f, 700f
        );
        round2Translation.setDuration(500);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(textTranslation, titleTranslation, logoAlpha, round1Translation, round2Translation);

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Navigation.findNavController(v).popBackStack();
                    }
                }, 400);
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

    private void nextAnimation() {
        ObjectAnimator textTranslation = ObjectAnimator.ofFloat(
                text,
                "TranslationX",
                0f, -1500f
        );
        textTranslation.setDuration(500);

        ObjectAnimator titleTranslation = ObjectAnimator.ofFloat(
                title,
                "TranslationX",
                0f, -2000f
        );
        titleTranslation.setDuration(500);
        ObjectAnimator round1Translation = ObjectAnimator.ofFloat(
                round1,
                "TranslationX",
                0f, -700f
        );
        round1Translation.setDuration(500);

        ObjectAnimator round2Translation = ObjectAnimator.ofFloat(
                round2,
                "TranslationX",
                0f, 700f
        );
        round2Translation.setDuration(500);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(round1Translation, round2Translation, textTranslation, titleTranslation);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                new Handler().postDelayed(() -> {

                            Navigation.findNavController(v).navigate(R.id.action_onboarding2_to_onboarding3);
                        }
                        , 200);
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
