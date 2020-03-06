package com.imaginology.texas.Notification.PushNotification.NotificationReply;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.imaginology.texas.R;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseEntity;
import com.imaginology.texas.Students.StudentDetails;
import com.imaginology.texas.Teachers.TeacherDetails;
import com.imaginology.texas.Users.UserDetails;
import com.imaginology.texas.util.GetLoginInstanceFromDatabase;
import com.imaginology.texas.util.ImageViewLoader;

import java.util.ArrayList;
import java.util.List;

public class NotificationReplyAdapter extends RecyclerView.Adapter<NotificationReplyAdapter.NotificationReplyHolder>{
    private List<NotificationReplyDto.Notifications> notificationList=new ArrayList<>();
    private Context mContext;

    public NotificationReplyAdapter(Context mContext) {
        this.mContext=mContext;
    }

    @Override
    public NotificationReplyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==0){
            View view=LayoutInflater.from(mContext).inflate(R.layout.custome_reply_noti_item2,parent,false);
            return new NotificationReplyAdapter.NotificationReplyHolder(view);
        }
        View view=LayoutInflater.from(mContext).inflate(R.layout.custom_reply_noti_item,parent,false);
        return new NotificationReplyAdapter.NotificationReplyHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationReplyHolder holder, int position) {
        holder.notificationMessage.setText(notificationList.get(position).getMessage());
        if(notificationList.get(position).getSender().getSenderName()!=null)
            holder.notificationSender.setText(notificationList.get(position).getSender().getSenderName());

        String picUrl = notificationList.get(position).getSender().getProfilePicture();
        ImageViewLoader.loadImage(mContext, picUrl, holder.senderPic);
        Long time = System.currentTimeMillis();
        Long sentTime = notificationList.get(position).getRepliedDate();//Long.parseLong();
        Long diffTime=(long)0;
        if(sentTime!=null)
            diffTime= time - sentTime;
        Long diffTimeInSec = diffTime / 1000;
        if (diffTimeInSec < 30) {
            holder.notificationTime.setText("Just Now");
        } else if (diffTimeInSec > 30 && diffTimeInSec < 60) {
            holder.notificationTime.setText("Few seconds ago");
        } else if (diffTimeInSec >= 60 && diffTimeInSec < 3600) {
            holder.notificationTime.setText(diffTimeInSec / 60 + " " + "Minutes ago");
        } else if (diffTimeInSec >= 3600 && diffTimeInSec < 86400) {
            holder.notificationTime.setText(diffTimeInSec / 3600 + " " + "Hours ago");
        } else if (diffTimeInSec >= 86400 && diffTimeInSec < 2628002.88) {
            holder.notificationTime.setText(diffTimeInSec / 86400 + " " + "Day ago");
        } else if (diffTimeInSec >= 2628002.88 && diffTimeInSec < 31536000) {
            holder.notificationTime.setText(Math.round(diffTimeInSec / 2628002.88) + " " + "Months ago");
        } else if (diffTimeInSec >= 3153600) {
            holder.notificationTime.setText("One or Many years Ago");

        } else {
            holder.notificationTime.setText("Just now");

        }
//        String id=notificationList.get(position).getSender().getSenderId().toString();
        String id="329";
        holder.senderPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProfilePicture(picUrl);
//                if(notificationList.get(position).getSender().getUserRole().equalsIgnoreCase("ADMIN"))
//                    visitAdminProfile(id);
//                if(notificationList.get(position).getSender().getUserRole().equalsIgnoreCase("USER"))
//                    visitUserProfile(id);
//                if(notificationList.get(position).getSender().getUserRole().equalsIgnoreCase("STUDENT"))
//                    visitStudentProfile(id);
//                if(notificationList.get(position).getSender().getUserRole().equalsIgnoreCase("TEACHER"))
//                    visitTeacherProfile(id);
            }
        });

    }

    private void visitTeacherProfile(String teacherId) {
        Intent detailsActivity = new Intent(mContext, TeacherDetails.class);
        detailsActivity.putExtra("selectedTeacherId",teacherId);
        mContext.startActivity(detailsActivity);

    }

    private void visitStudentProfile(String studentId) {
        Intent studentDetailsActivity = new Intent(mContext, StudentDetails.class);
        studentDetailsActivity.putExtra("selectedUserId", studentId);
        mContext.startActivity(studentDetailsActivity);
    }

    private void visitUserProfile(String userId) {
        Intent detailsActivity = new Intent(mContext, UserDetails.class);
        detailsActivity.putExtra("userId",Integer.valueOf(userId));
        mContext.startActivity(detailsActivity);
    }

    private void visitAdminProfile(String id) {
        Intent detailsActivity = new Intent(mContext, UserDetails.class);
        detailsActivity.putExtra("userId",Integer.valueOf(id));
        mContext.startActivity(detailsActivity);

    }

    private void showProfilePicture(String picUrl) {
        Dialog showUserProfilePicture = new Dialog(mContext);
        showUserProfilePicture.setContentView(R.layout.show_user_image_form);
        ImageView userImage = showUserProfilePicture.findViewById(R.id.userImage);
        ImageViewLoader.loadImage(mContext, picUrl, userImage);
        if (showUserProfilePicture.getWindow() != null) {
            showUserProfilePicture.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            showUserProfilePicture.getWindow().setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        }
        showUserProfilePicture.show();
    }

    public void addNotificationReplyToList(List<NotificationReplyDto.Notifications> newnotificationList){


        notificationList.addAll(newnotificationList);
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    @Override
    public int getItemViewType(int position) {
        UserLoginResponseEntity loginResponseEntity=new GetLoginInstanceFromDatabase(mContext).getLoginInstance();
        int id=Integer.valueOf(String.valueOf(loginResponseEntity.getLoginId()));
        if(notificationList.get(position).getSender().getSenderId()==id){
            return 0;
        }else {
            return 1;
        }
//        return super.getItemViewType(position);
    }


    public class NotificationReplyHolder extends RecyclerView.ViewHolder{
        TextView notificationMessage;
        TextView notificationTime;
        TextView notificationSender;
        ImageView senderPic;
        ConstraintLayout notiConstLayout;

        public NotificationReplyHolder(View v) {
            super(v);
            notificationMessage = v.findViewById(R.id.counsellor_remarks);
            notificationTime = v.findViewById(R.id.date_of_counsil);
            notificationSender = v.findViewById(R.id.counsellor_name);
            senderPic = v.findViewById(R.id.counsellor_image);
            notiConstLayout = v.findViewById(R.id.notification_constraint_layout);
        }
    }


}
