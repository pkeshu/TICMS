package com.imaginology.texas.Counseling;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.imaginology.texas.util.ImageViewLoader;
import com.squareup.picasso.Picasso;
import com.imaginology.texas.util.CheckError;
import com.imaginology.texas.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Created by yubar on 5/25/2018.
 */

public class CounselingDtoAdapter extends RecyclerView.Adapter<CounselingDtoAdapter.CounsellingListHolder> {
    private LayoutInflater inflator;
    private Context context;
    public List<CounselingDetailListDto> counselingList = new ArrayList<>();
    private int rowLayout;
    private ClickListener clickListener;


    public CounselingDtoAdapter(int rowLayout, Context context) {
        this.context = context;
        this.rowLayout = rowLayout;
    }

    @Override
    public CounsellingListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new CounsellingListHolder(view);
    }

    @Override
    public void onBindViewHolder(CounsellingListHolder holder, int position) {
        if(counselingList!=null){
            holder.fullName.setText(CheckError.checkNullString(counselingList.get(position).getFullName()));
            holder.email.setText(CheckError.checkNullString(counselingList.get(position).getEmail()));
            holder.course.setText(CheckError.checkNullString(counselingList.get(position).getCourseName()));
            String picUrl=counselingList.get(position).getProfilePicture();

            ImageViewLoader.loadImage(context,picUrl,holder.show);
        }else {
            Toast.makeText(context, "No more Counseling", Toast.LENGTH_SHORT).show();
        }

        //Decode Base64 to Bitmap and display in circle view
        /*String stringPicture=counselingList.get(position).getProfilePicture();
        if(stringPicture!=null) {
            byte[] picCode = Base64.decode(stringPicture, Base64.DEFAULT);
            Bitmap pic = BitmapFactory.decodeByteArray(picCode, 0, picCode.length);
            holder.show.setImageBitmap(pic);
        }*/
    }

    public void setCounselingClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setFilter(List<CounselingDetailListDto> newList){
        counselingList = new ArrayList<>();
        counselingList.addAll(newList);
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return counselingList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
    public void addListInCounseling(List<CounselingDetailListDto> newCounselingList){
        counselingList.addAll(newCounselingList);
        notifyDataSetChanged();

    }


    public class CounsellingListHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView fullName, email, course;
        ImageView show;
        private CounselingListDto Counselling;

        public CounsellingListHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            fullName = v.findViewById(R.id.fullName);
            email = v.findViewById(R.id.email);
            course = v.findViewById(R.id.phone);
            show=v.findViewById(R.id.list_image_view);
        }

        @Override
        public void onClick(View v) {
            if(clickListener!=null){
                clickListener.itemClicked(v, counselingList.get(getPosition()), getPosition());
            }
        }

    }


    public interface ClickListener {

        void itemClicked(View v, CounselingDetailListDto teacherDTO, int position);
    }

}
