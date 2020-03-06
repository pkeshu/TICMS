package com.imaginology.texas.Users;

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

public class UserDtoAdapter extends RecyclerView.Adapter<UserDtoAdapter.UserListHolder> {

    private LayoutInflater inflator;
    private Context context;
    private List<UserListDto.Datum> userList = new ArrayList<>();
    private int rowLayout;
    private ClickListener clickListener;

    public UserDtoAdapter(int rowLayout, Context context) {
        this.context = context;
        this.rowLayout = rowLayout;
    }

    @Override
    public UserListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new UserListHolder(view);
    }


    @Override
    public void onBindViewHolder(UserListHolder holder, int position) {
        holder.fullName.setText(CheckError.checkNullString(userList.get(position).getFirstName()) + " " + CheckError.checkNullString(userList.get(position).getLastName()));
        holder.email.setText(CheckError.checkNullString(userList.get(position).getEmail()));
        holder.phone.setText(CheckError.checkNullString(userList.get(position).getMobileNumber()));

        String picUrl=userList.get(position).getProfilePicture();

        ImageViewLoader.loadImage(context,picUrl,holder.show);

    }

    public void setUserClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }


    public void setFilter(List<UserListDto.Datum> newList){
        userList = new ArrayList<>();
        userList.addAll(newList);
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return userList.size();
    }
    void addUserToList(List<UserListDto.Datum> newUserList){
        userList.addAll(newUserList);
        notifyDataSetChanged();

    }


    public class UserListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView fullName, email, phone;
        ImageView show;
        private UserListDto.Datum student;

        public UserListHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            fullName = v.findViewById(R.id.fullName);
            email = v.findViewById(R.id.email);
            phone = v.findViewById(R.id.phone);
            show = v.findViewById(R.id.list_image_view);
        }

        @Override
        public void onClick(View v) {
            if(clickListener!=null){
                clickListener.itemClicked(v, userList.get(getPosition()), getPosition());
            }
        }

    }


    public interface ClickListener {

        void itemClicked(View v, UserListDto.Datum userDTO, int position);
    }

}
