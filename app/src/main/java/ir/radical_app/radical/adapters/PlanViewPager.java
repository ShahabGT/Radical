package ir.radical_app.radical.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PlanViewPager extends FragmentPagerAdapter {

    private Context context;
    private Fragment[] fragments;

    public PlanViewPager(FragmentManager fragmentManager, Fragment[] fragments){
        super(fragmentManager);
        this.context=context;
        this.fragments=fragments;



    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}
