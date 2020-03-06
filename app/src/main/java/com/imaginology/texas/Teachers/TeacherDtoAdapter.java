package com.imaginology.texas.Teachers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.imaginology.texas.util.ImageViewLoader;
import com.squareup.picasso.Picasso;
import com.imaginology.texas.util.CheckError;
import com.imaginology.texas.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by deepbhai on 9/15/17.
 */

public class TeacherDtoAdapter extends RecyclerView.Adapter<TeacherDtoAdapter.TeacherListHolder> {

    private LayoutInflater inflator;
    private Context context;
    private List<TeacherListDto.Datum> teacherList = new ArrayList<>();
    private int rowLayout;
    private ClickListener clickListener;


    public TeacherDtoAdapter(int rowLayout, Context context) {
        this.context = context;
        this.rowLayout = rowLayout;
    }

    @Override
    public TeacherListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new TeacherListHolder(view);
    }

    @Override
    public void onBindViewHolder(TeacherListHolder holder, int position) {
        holder.fullName.setText(CheckError.checkNullString(teacherList.get(position).getFirstName())+" "+teacherList.get(position).getMiddleName() + " " + CheckError.checkNullString(teacherList.get(position).getLastName()));
        holder.email.setText(CheckError.checkNullString(teacherList.get(position).getEmail()));
        holder.phone.setText(CheckError.checkNullString(teacherList.get(position).getPhoneNumber()));
        String picUrl=teacherList.get(position).getProfilePicture();

        ImageViewLoader.loadImage(context,picUrl,holder.show);
        //Decode Base64 to Bitmap and display in circle view
        /*String stringPicture=teacherList.get(position).getProfilePicture();
        if(stringPicture!=null) {
            byte[] picCode = Base64.decode(stringPicture, Base64.DEFAULT);
            Bitmap pic = BitmapFactory.decodeByteArray(picCode, 0, picCode.length);
            holder.show.setImageBitmap(pic);
        }*/
    }

    public void setTeacherClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setFilter(List<TeacherListDto.Datum> newList){
        teacherList = new ArrayList<>();
        teacherList.addAll(newList);
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return teacherList.size();
    }
    void addTeacherToList(List<TeacherListDto.Datum> newTeacherList){
        teacherList.addAll(newTeacherList);
        notifyDataSetChanged();

    }


    public class TeacherListHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView fullName, email, phone;
        ImageView show;
        private TeacherListDto.Datum teacher;

        public TeacherListHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            fullName = v.findViewById(R.id.fullName);
            email = v.findViewById(R.id.email);
            phone = v.findViewById(R.id.phone);
            show=v.findViewById(R.id.list_image_view);
        }

        @Override
        public void onClick(View v) {
            if(clickListener!=null){
                clickListener.itemClicked(v, teacherList.get(getPosition()), getPosition());
            }
        }


    }


    public interface ClickListener {

        void itemClicked(View v, TeacherListDto.Datum teacherDTO, int position);
    }

}
