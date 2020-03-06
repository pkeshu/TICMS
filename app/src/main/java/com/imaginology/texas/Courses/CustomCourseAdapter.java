package com.imaginology.texas.Courses;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.imaginology.texas.R;
import com.imaginology.texas.util.CheckError;

import java.util.ArrayList;
import java.util.List;

public class CustomCourseAdapter extends RecyclerView.Adapter<CustomCourseAdapter.CourseListHolder>{
    private List<CoursesDto> coursesDtos = new ArrayList<>();
    private int rowLayout;
    private ClickListener clickListener;
    private Context context;

    public CustomCourseAdapter(Context context, int rowLayout) {
        this.context=context;
        this.rowLayout = rowLayout;
    }

    @Override
    public CourseListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(rowLayout,parent,false);
        return new CourseListHolder(view);
    }

    @Override
    public void onBindViewHolder(CourseListHolder holder, int position) {
        if(position%2==0){
            holder.relativeView.setBackground(context.getResources().getDrawable(R.drawable.custom_unseen_notification_item_bg));
        }

        if(position%2 !=0 ){ holder.relativeView.setBackground(context.getResources().getDrawable(R.drawable.custom_seen_notification_item_bg));}
        holder.courseName.setText(CheckError.checkNullString(coursesDtos.get(position).getName()));
        holder.courseDescription.setText(CheckError.checkNullString(coursesDtos.get(position).getDescription()));
        holder.courseAffilation.setText(CheckError.checkNullString(coursesDtos.get(position).getAffilation()));


    }
    @Override
    public int getItemCount() {
        return coursesDtos.size();
    }
    public void setFilter(List<CoursesDto> newList){
        coursesDtos.addAll(newList);
        notifyDataSetChanged();
    }
    public void addCourseInList(List<CoursesDto> newList){
        coursesDtos.addAll(newList);
        notifyDataSetChanged();

    }
    void setNotificationClickListener(ClickListener clickListener){
        this.clickListener=clickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public class CourseListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView courseName, courseDescription, courseAffilation;
        View relativeView;

        public CourseListHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            courseName = itemView.findViewById(R.id.course_name);
            courseDescription = itemView.findViewById(R.id.course_description);
            courseAffilation = itemView.findViewById(R.id.course_affilation);
            relativeView=itemView.findViewById(R.id.course_row_relativelayout);
        }

        @Override
        public void onClick(View view) {
            if(clickListener!=null){
                clickListener.itemClicked(view, coursesDtos.get(getLayoutPosition()), getLayoutPosition());
            }
        }
    }
    public interface ClickListener {

        void itemClicked(View v, CoursesDto notificationDto, int position);
    }
}
