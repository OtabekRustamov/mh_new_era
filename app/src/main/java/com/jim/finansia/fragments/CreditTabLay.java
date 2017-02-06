package com.jim.finansia.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

import com.jim.finansia.PocketAccounter;
import com.jim.finansia.PocketAccounterApplication;
import com.jim.finansia.R;
import com.jim.finansia.managers.DrawerInitializer;
import com.jim.finansia.managers.PAFragmentManager;
import com.jim.finansia.managers.ToolbarManager;

import java.util.ArrayList;

import javax.inject.Inject;


public class CreditTabLay extends Fragment implements View.OnClickListener, ViewPager.OnPageChangeListener{
    @Inject PAFragmentManager paFragmentManager;
    @Inject ToolbarManager toolbarManager;
    @Inject DrawerInitializer drawerInitializer;
    @Inject SharedPreferences sharedPreferences;

    public static final String CREDIT_ID = "credit_id";
    public static final String POSITION = "credit_position";
    public static final String MODE = "credit_mode";
    public static final String DEFAULT_POSITION = "default_position";
    public static final String LOCAL_APPEREANCE = "local_appereance";
    public static final int LOCAL_MAIN = 0, LOCAL_INFO = 1, LOCAL_EDIT = 2;
    private FloatingActionButton fb;
    private ArrayList<Fragment> list;
    private ViewPager viewPager;
    private PagerAdapter adapter;
    private TabLayout tabLayout;

    public void updateArchive(){
        if(adapter!=null){
            for(int i = 0;i<adapter.getCount();i++){
                if(adapter.getItem(i).getClass().getName().equals(CreditArchiveFragment.class.getName())){
                    ((CreditArchiveFragment) adapter.getItem(i)).updateList();
                    break;
                }
            }
        }
    }
    public void backupToolbar() {
        if (toolbarManager != null){
            toolbarManager.setToolbarIconsVisibility(View.GONE, View.GONE, View.GONE);
            toolbarManager.setOnTitleClickListener(null);
            toolbarManager.setTitle(getString(R.string.cred_managment));
            toolbarManager.setSubtitle("");
            toolbarManager.setSubtitleIconVisibility(View.GONE);
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((PocketAccounter) getContext()).component((PocketAccounterApplication) getContext().getApplicationContext()).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View V=inflater.inflate(R.layout.fragment_credit_tab_lay, container, false);


        V.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(PocketAccounter.keyboardVisible){
                    InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(V.getWindowToken(), 0);}
            }
        },100);

        tabLayout = (TabLayout) V.findViewById(R.id.sliding_tabs);
        fb = (FloatingActionButton) V.findViewById(R.id.fbDebtBorrowFragment);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paFragmentManager.displayFragment(new AddCreditFragment());
            }
        });


        viewPager = (ViewPager) V.findViewById(R.id.viewpager);
        list = new ArrayList<>();
        CreditFragment creditFragment = new CreditFragment();
        CreditArchiveFragment creditArchiveFragment = new CreditArchiveFragment();
        list.add(creditFragment);
        list.add(creditArchiveFragment);
        adapter = new PagerAdapter(paFragmentManager.getFragmentManager(), list);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(sharedPreferences.getInt(DEFAULT_POSITION,0));
        //TODO hide or show fab according to viewpagers position
        viewPager.addOnPageChangeListener(this);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.setupWithViewPager(viewPager);

        return V;}
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                fb.setAlpha(1-positionOffset);
            }
            else {
                if(positionOffset>=0.1f&&fb.getVisibility()==View.VISIBLE)
                    fb.setVisibility(View.GONE);
                else if(positionOffset<=0.1f&&fb.getVisibility()==View.GONE)
                    fb.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onPageSelected(int position) {
        if(position==1) {
            fb.setVisibility(View.GONE);
        }
        else{
            fb.setVisibility(View.VISIBLE);
        }
        sharedPreferences.edit().putInt(DEFAULT_POSITION,position).apply();

    }
    @Override
    public void onPageScrollStateChanged(int state) {}
    @Override
    public void onClick(View v) {}
    private boolean show = false;
    public void onScrolledList(boolean k) {
        if (k) {
            if (!show)
                fb.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fab_down));
            show = true;
        } else {
            if (show)
                fb.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fab_up));
            show = false;
        }
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        private ArrayList<Fragment> list;
        public PagerAdapter(FragmentManager fm, ArrayList<Fragment> list) {
            super(fm);
            this.list = list;
        }
        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }
        @Override
        public int getCount() {
            return 2;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return getResources().getString(R.string.active);
            }
            return getResources().getString(R.string.archiveee);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (toolbarManager != null){
            toolbarManager.setToolbarIconsVisibility(View.GONE, View.GONE, View.GONE);
            toolbarManager.setOnTitleClickListener(null);
            toolbarManager.setTitle(getString(R.string.cred_managment));
            toolbarManager.setSubtitle("");
            toolbarManager.setSubtitleIconVisibility(View.GONE);
        }
    }

}
