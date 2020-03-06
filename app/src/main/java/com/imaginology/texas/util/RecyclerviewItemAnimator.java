package com.imaginology.texas.util;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class RecyclerviewItemAnimator {

    public static int setAnimation(Context mContext, View viewToAnimate, int position, int lastPosition)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            animation.setDuration(1000);

            //Set random duration for each item of recycler view
//            animation.setDuration(new Random().nextInt(1001));

            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
        return lastPosition;
    }
}
