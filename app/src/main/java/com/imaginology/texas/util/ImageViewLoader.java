package com.imaginology.texas.util;

import android.content.Context;
import android.widget.ImageView;

import com.imaginology.texas.R;
import com.squareup.picasso.Picasso;

public class ImageViewLoader {
    public static void loadImage(Context mContext,String imageUrl, ImageView imageView){
        if(imageUrl!=null && !imageUrl.isEmpty() && !imageUrl.equalsIgnoreCase("Not available")){
            Picasso.with(mContext)
                    .load(imageUrl)
                    .into(imageView);
        }else {
            Picasso.with(mContext)
                    .load(R.drawable.user)
                    .into(imageView);
        }
    }
}
