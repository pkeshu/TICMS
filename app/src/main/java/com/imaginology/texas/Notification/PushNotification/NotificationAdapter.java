package com.imaginology.texas.Notification.PushNotification;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.imaginology.texas.R;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseEntity;
import com.imaginology.texas.util.GetLoginInstanceFromDatabase;
import com.imaginology.texas.util.ImageViewLoader;
import com.imaginology.texas.util.RecyclerviewItemAnimator;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationListHolder> {
    public static final int CONTEXT_MENU_REPLY=101;
    public static final int CONTEXT_MENU_MARKASREAD=102;
    public static final int CONTEXT_MENU_DELETE=103;
    public static final int CONTEXT_MENU_EDIT=104;
    boolean checkClick = true;
    private Context mContext;
    private List<NotificationDto.Notification> notificationDtoList = new ArrayList<>();
    private String title, message, semester;
    private ClickListener clickListener;
    private int lastPosition = -1;
    private GetLoginInstanceFromDatabase getLoginInstanceFromDatabase;
    private UserLoginResponseEntity loginInstance;

    NotificationAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public NotificationAdapter.NotificationListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_notification, parent, false);
        return new NotificationAdapter.NotificationListHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationListHolder holder, int position) {
        NotificationDto.Notification currentNotification = notificationDtoList.get(position);
        if (!currentNotification.getSeen()) {
            holder.notiConstLayout.setBackground(mContext.getResources().getDrawable(R.drawable.custom_unseen_notification_item_bg));
            holder.notificationTitle.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
            holder.notificationMessage.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
            holder.notificationSender.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
            holder.notificationTime.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));

        }

        if (currentNotification.getSeen()) {
            holder.notiConstLayout.setBackground(mContext.getResources().getDrawable(R.drawable.custom_seen_notification_item_bg));
            holder.notificationTitle.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
            holder.notificationMessage.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
            holder.notificationSender.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
            holder.notificationTime.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
        }
        if(currentNotification.getTotalResponse()>0){
            holder.notiReply.setVisibility(View.VISIBLE);
            holder.notiReply.setText(notificationDtoList.get(position).getTotalResponse()+ " Response");
        }


        holder.notificationTitle.setText(notificationDtoList.get(position).getTitle());
        message = notificationDtoList.get(position).getMessage();
        if (message.length()>45) {
            message = message.substring(0,40);
            holder.notificationMessage.setText(message+" ...");
        }else {
            holder.notificationMessage.setText(notificationDtoList.get(position).getMessage());
        }
//        holder.notificationMessage.setText(notificationDtoList.get(position).getMessage());
        holder.notificationSender.setText(notificationDtoList.get(position).getSender().getSenderName());

        String picUrl = notificationDtoList.get(position).getSender().getProfilePicture();
        ImageViewLoader.loadImage(mContext, picUrl, holder.senderPic);
        Long time = System.currentTimeMillis();
        Long sentTime = Long.parseLong(String.valueOf(notificationDtoList.get(position).getSentDate()));
        Long diffTime = time - sentTime;
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
        title = notificationDtoList.get(position).getTitle();
        message = notificationDtoList.get(position).getMessage();
        /*if(message.length()>100) {
            message = message.substring(0, 100);
        }*/
        lastPosition = RecyclerviewItemAnimator.setAnimation(mContext, holder.itemView, position, lastPosition);


        /*holder.notiConstLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int margin = holder.notiConstLayout.getMaxHeight();

                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) view.getLayoutParams();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        params.setMarginStart(margin);
                    }else{
                        params.setMargins(margin,0,0,0);
                    }
                    view.setLayoutParams(params);

            }
        });*/

        /*holder.itemView.setOnClickListener(v -> {
            boolean expanded = movie.isExpanded();
            movie.setExpanded(!expanded);
            notifyItemChanged(position);
        });*/


//        holder.notiConstLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                AllNotification().markNotificationAsRead(notificationDtoList.get(position).getId());
//
//                if (checkClick) {
//
//                    System.out.println("Message is: " + message);
//                    holder.fullNotificationMessage.setVisibility(View.VISIBLE);
//                    holder.fullNotificationMessage.setText(notificationDtoList.get(position).getMessage());
//                    holder.fullNotificationMessage.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
//                    holder.notificationMessage.setVisibility(View.GONE);
////                    Toast.makeText(mContext, notificationDtoList.get(position).getTotalResponse(), Toast.LENGTH_SHORT).show();
//                    new AllNotification().markNotificationAsRead(notificationDtoList.get(position).getId());
//                    checkClick = false;
//                } else {
//                    System.out.println("Message is: " + message);
//                    holder.notificationMessage.setVisibility(View.VISIBLE);
//                    holder.notificationMessage.setText(notificationDtoList.get(position).getMessage());
//                    holder.notificationMessage.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
//                    holder.fullNotificationMessage.setVisibility(View.GONE);
////                    new AllNotification().markNotificationAsRead(notificationDtoList.get(position).getId());
//                    checkClick = true;
//                }
//
//
//            }
//        });
        /*holder.notiReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLoginInstanceFromDatabase=new GetLoginInstanceFromDatabase(mContext);
                loginInstance=getLoginInstanceFromDatabase.getLoginInstance();
                Intent intent = new Intent(mContext, NotificationReply.class);
                intent.putExtra("notificationId", notificationDtoList.get(position).getNotificationId());
                intent.putExtra("loginId", loginInstance.getLoginId());
                intent.putExtra("token", loginInstance.getToken());
                intent.putExtra("customerId", loginInstance.getCustomerId());
                mContext.startActivity(intent);
            }
        });*/


    }




    void addNotificationsToList(List<NotificationDto.Notification> newNotificationList) {
        notificationDtoList.addAll(newNotificationList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return notificationDtoList.size();
    }
    public Bundle getNotificationValue(int position){
        Bundle bundle=new Bundle();
        bundle.putInt("senderId",notificationDtoList.get(position).getSender().getSenderId());
        bundle.putInt("notiId",notificationDtoList.get(position).getId());
        bundle.putLong("messageId",notificationDtoList.get(position).getNotificationId());
        bundle.putString("title",notificationDtoList.get(position).getTitle());
        bundle.putString("message",notificationDtoList.get(position).getMessage());
        return bundle;

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }






    public class NotificationListHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener {
        TextView notificationTitle;
        TextView notificationMessage, fullNotificationMessage;
        TextView notificationTime;
        TextView notificationSender;
        ImageView senderPic;
        TextView notiReply;
        ConstraintLayout notiConstLayout;

        NotificationListHolder(View v) {
            super(v);
//            v.setOnClickListener(this);
            notificationTitle = v.findViewById(R.id.notification_title);
            notificationMessage = v.findViewById(R.id.counsellor_remarks);
            notificationTime = v.findViewById(R.id.date_of_counsil);
            notificationSender = v.findViewById(R.id.counsellor_name);
            senderPic = v.findViewById(R.id.counsellor_image);
            notiConstLayout = v.findViewById(R.id.notification_constraint_layout);
            fullNotificationMessage = v.findViewById(R.id.full_notification);
            notiReply = v.findViewById(R.id.notification_reply);
            notiConstLayout.setOnCreateContextMenuListener(this);
            notiConstLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.itemClicked(view, notificationDtoList.get(getPosition()), getPosition());
            }
        }



        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
//            super.onCreateContextMenu(menu, v, menuInfo);
//            MenuInflater inflater = getMenuInflater();
//            inflater.inflate(R.menu.toggleview_menu, menu);
            menu.setHeaderTitle("Select an option");
//            menu.add(this.getAdapterPosition(),CONTEXT_MENU_REPLY,getAdapterPosition(),"Reply");
            menu.add(this.getAdapterPosition(),CONTEXT_MENU_MARKASREAD,getAdapterPosition(),"Mark As Read");
            menu.add(this.getAdapterPosition(),CONTEXT_MENU_DELETE,getAdapterPosition(),"Delete");
            menu.add(this.getAdapterPosition(),CONTEXT_MENU_EDIT,getAdapterPosition(),"Edit");



        }

    }
    public interface ClickListener {

        void itemClicked(View v, NotificationDto.Notification notificationDto, int position);
    }
    void setNotificationClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

}
