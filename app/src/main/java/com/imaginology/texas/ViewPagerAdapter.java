package com.imaginology.texas;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0)
            return new BasicInformationFragment();
        if(position==1)
            return new AddressFragment();
        if(position==2)
            return new ParentFragment();
        return null;

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position==0)
            return "General";
        else if(position==1)
            return "Address";
        else if(position==2)
            return "Parent";
        else
            return null;


    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        int mCurrentPosition=getItemPosition(object);


    }
}
