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
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import ir.radical_app.radical.R;
import ir.radical_app.radical.classes.Const;


public class Onboarding1 extends Fragment {

    private ImageView rings;
    private MaterialCardView start;
    private TextView title;
    private ImageView backbtn;
    private FloatingActionButton nextbtn;

    private View v;

    private boolean fromBack;

    void setFromBack() {
        this.fromBack = true;
    }

    public Onboarding1() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v= inflater.inflate(R.layout.fragment_onboarding1, container, false);
        init(v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(fromBack){
            reverseAnimation();
        }
    }

    private void init(View v){
        backbtn = getActivity().findViewById(R.id.intro_backbtn);
        backbtn.setVisibility(View.GONE);
        nextbtn = getActivity().findViewById(R.id.intro_nextbtn);
        nextbtn.hide();
        rings =v.findViewById(R.id.onboarding1_rings);
        start =v.findViewById(R.id.onboarding1_start);
        title =v.findViewById(R.id.onboarding1_title);


        start.setOnClickListener(View-> {
            animations();
        });

    }

    private void reverseAnimation(){
        ObjectAnimator ringsTranslation = ObjectAnimator.ofFloat(
                rings,
                "TranslationY",
                -2000f,0f
        );
        ringsTranslation.setDuration(700);

        ObjectAnimator titleTranslation = ObjectAnimator.ofFloat(
                title,
                "TranslationX",
                -2000f,0f
        );
        titleTranslation.setDuration(700);

        ObjectAnimator startTranslation = ObjectAnimator.ofFloat(
                start,
                "TranslationX",
                -2000f,0f
        );
        startTranslation.setDuration(700);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ringsTranslation,titleTranslation,startTranslation);
        animatorSet.start();

    }

    private void animations(){
        ObjectAnimator ringsTranslation = ObjectAnimator.ofFloat(
                rings,
                "TranslationY",
                0f,-2000f
        );
        ringsTranslation.setDuration(1000);

        ObjectAnimator titleTranslation = ObjectAnimator.ofFloat(
                title,
                "TranslationX",
                0f,-2000f
        );
        titleTranslation.setDuration(1000);

        ObjectAnimator startTranslation = ObjectAnimator.ofFloat(
                start,
                "TranslationX",
                0f,-2000f
        );
        startTranslation.setDuration(1000);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ringsTranslation,titleTranslation,startTranslation);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                new Handler().postDelayed(()-> {

                    Navigation.findNavController(v).navigate(R.id.action_onboarding1_to_onboarding2);


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
