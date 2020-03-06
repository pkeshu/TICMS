package com.imaginology.texas.Students;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.imaginology.texas.R;
import com.imaginology.texas.util.CheckError;
import com.imaginology.texas.util.ImageViewLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by deepbhai on 9/15/17.
 */

public class StudentDtoAdapter extends RecyclerView.Adapter<StudentDtoAdapter.StudentListHolder> {

    private LayoutInflater inflator;
    private Context context;
    private List<StudentListDto.Datum> studentList = new ArrayList<>();
//    private List<StudentList> studentList = new ArrayList<>();

    private int rowLayout;
    private ClickListener clickListener;

    public StudentDtoAdapter(int rowLayout, Context context) {
        this.context = context;
        this.rowLayout = rowLayout;
    }

    @Override
    public StudentListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new StudentListHolder(view);
    }

    @Override
    public void onBindViewHolder(StudentListHolder holder, int position) {
        holder.fullName.setText(CheckError.checkNullString(studentList.get(position).getFirstName()) + " " + studentList.get(position).getLastName());
        holder.email.setText(CheckError.checkNullString(studentList.get(position).getEmail()));
        holder.phone.setText(CheckError.checkNullString(studentList.get(position).getPhoneNumber()));
//        holder.fullName.setText(CheckError.checkNullString(studentList.get(position).getFirstName()));
        String picUrl=studentList.get(position).getProfilePicture();
//        String picUrl = "";


        ImageViewLoader.loadImage(context, picUrl, holder.show);
        //Decode Base64 to Bitmap and display in circle view
        /*String stringPicture=studentList.get(position).getProfilePicture();
        if(stringPicture!=null) {
            byte[] picCode = Base64.decode(stringPicture, Base64.DEFAULT);
            Bitmap pic = BitmapFactory.decodeByteArray(picCode, 0, picCode.length);
            holder.show.setImageBitmap(pic);
        }*/
    }

    public void setStudentClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setFilter(List<StudentListDto.Datum> newList) {
        studentList = new ArrayList<>();
        studentList.addAll(newList);
        notifyDataSetChanged();
    }

    //    public void setFilter(List<StudentList> newList){
//        studentList = new ArrayList<>();
//        studentList.addAll(newList);
//        notifyDataSetChanged();
//    }
    @Override
    public int getItemCount() {
        return studentList.size();
    }

    void addStudentToList(List<StudentListDto.Datum> newStudentList) {
        studentList.addAll(newStudentList);
        notifyDataSetChanged();

    }
//    void addStudentToList(List<StudentList> newStudentList){
//        studentList.addAll(newStudentList);
//        notifyDataSetChanged();
//
//    }


    public class StudentListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView fullName, email, phone;
        ImageView show;

        private StudentListDto.Datum student;

        public StudentListHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            fullName = v.findViewById(R.id.fullName);
            email = v.findViewById(R.id.email);
            phone = v.findViewById(R.id.phone);
            show = v.findViewById(R.id.list_image_view);
        }

        //        @Override
//        public void onClick(View v) {
//            if(clickListener!=null){
//                clickListener.itemClicked(v, studentList.get(getPosition()), getPosition());
//            }
//        }
        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.itemClicked(v, studentList.get(getPosition()), getPosition());
            }
        }

    }


    public interface ClickListener {

        void itemClicked(View v, StudentListDto.Datum userDTO, int position);
    }

}
