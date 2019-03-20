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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ir.radical_app.radical.R;

import static ir.radical_app.radical.classes.Const.onboarding;


public class Onboarding3 extends Fragment {
    private ImageView backbtn;
    private FloatingActionButton nextbtn;
    private boolean fromBack;
    private ImageView logo,shape;
    private TextView text,title;

    public void setFromBack(boolean fromBack) {
        this.fromBack = fromBack;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_onboarding3, container, false);

        init(v);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(fromBack)
            fromBackAnimation();
        else
            initAnimation();


    }

    private void init(View v){
        backbtn = getActivity().findViewById(R.id.intro_backbtn);
        nextbtn = getActivity().findViewById(R.id.intro_nextbtn);

        logo = v.findViewById(R.id.onboarding3_logo);
        shape = v.findViewById(R.id.onboarding3_shape);
        text = v.findViewById(R.id.onboarding3_text);
        title = v.findViewById(R.id.onboarding3_title);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backAnimation();
            }
        });

        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextAnimation();
            }
        });

    }

    private void initAnimation(){
        ObjectAnimator textTranslation = ObjectAnimator.ofFloat(
                text,
                "TranslationX",
                1500f,0f
        );
        textTranslation.setDuration(500);

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

        ObjectAnimator shapeTranslation = ObjectAnimator.ofFloat(
                shape,
                "TranslationY",
                -2000f,0f
        );
        shapeTranslation.setDuration(500);


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(textTranslation,titleTranslation,logoAlpha,shapeTranslation);
        animatorSet.start();
    }
    private void backAnimation(){
        ObjectAnimator textTranslation = ObjectAnimator.ofFloat(
                text,
                "TranslationX",
                0f,1500f
        );
        textTranslation.setDuration(500);

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

        ObjectAnimator shapeTranslation = ObjectAnimator.ofFloat(
                shape,
                "TranslationY",
                0f,-2000f
        );
        shapeTranslation.setDuration(500);


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(textTranslation,titleTranslation,logoAlpha,shapeTranslation);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Onboarding2 onboarding2 = new Onboarding2();
                        onboarding2.setFromBack(true);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.enter_left,R.anim.exit_right)
                                .replace(R.id.intro_container, onboarding2)
                                .commit();
                        onboarding=2;
                    }
                },200);

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
    private void fromBackAnimation(){
        ObjectAnimator textTranslation = ObjectAnimator.ofFloat(
                text,
                "TranslationX",
                -1500f,0f
        );
        textTranslation.setDuration(500);

        ObjectAnimator titleTranslation = ObjectAnimator.ofFloat(
                title,
                "TranslationX",
                -2000f,0f
        );
        titleTranslation.setDuration(500);

        ObjectAnimator logoAlpha = ObjectAnimator.ofFloat(
                logo,
                "alpha",
                0f,1f
        );
        logoAlpha.setDuration(500);

        ObjectAnimator shapeTranslation = ObjectAnimator.ofFloat(
                shape,
                "TranslationY",
                -2000f,0f
        );
        shapeTranslation.setDuration(500);


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(textTranslation,titleTranslation,logoAlpha,shapeTranslation);
        animatorSet.start();
    }

    private void nextAnimation(){
        ObjectAnimator textTranslation = ObjectAnimator.ofFloat(
                text,
                "TranslationX",
                0f,-1500f
        );
        textTranslation.setDuration(500);

        ObjectAnimator titleTranslation = ObjectAnimator.ofFloat(
                title,
                "TranslationX",
                0f,-2000f
        );
        titleTranslation.setDuration(500);

        ObjectAnimator logoAlpha = ObjectAnimator.ofFloat(
                logo,
                "alpha",
                1f,0f
        );
        logoAlpha.setDuration(500);

        ObjectAnimator shapeTranslation = ObjectAnimator.ofFloat(
                shape,
                "TranslationY",
                0f,-2000f
        );
        shapeTranslation.setDuration(500);


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(textTranslation,titleTranslation,logoAlpha,shapeTranslation);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Onboarding4 onboarding4 = new Onboarding4();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.enter_right,R.anim.exit_left)
                                .replace(R.id.intro_container, onboarding4)
                                .commit();
                        onboarding=4;
                    }
                },200);

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
