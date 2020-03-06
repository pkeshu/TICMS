package com.imaginology.texas.Team.AddingMemberInTeam;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.imaginology.texas.R;
import com.imaginology.texas.Users.UserListDto;
import com.imaginology.texas.util.ImageViewLoader;

import java.util.ArrayList;
import java.util.List;

public class AddUserInTeamAdapter extends RecyclerView.Adapter<AddUserInTeamAdapter.MemberHolder>{
    private List<UserListDto.Datum> userList=new ArrayList<>();
    private Context mContext;
    public List<UserListDto.Datum> checkedLIst=new ArrayList<>();

    public AddUserInTeamAdapter(Context mContext) {
        this.mContext = mContext;
    }


    @Override
    public MemberHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_adapter,parent,false);
        return new AddUserInTeamAdapter.MemberHolder(view);
    }

    @Override
    public void onBindViewHolder(MemberHolder holder, int position) {

        UserListDto.Datum currentUser=userList.get(position);

        holder.fullName.setText(currentUser.getFirstName()+" "+currentUser.getLastName());
        holder.email.setText(currentUser.getEmail());
        holder.phone.setText(currentUser.getMobileNumber());
        String pPUrl=currentUser.getProfilePicture();
        ImageViewLoader.loadImage(mContext,pPUrl,holder.profilePicture);

        holder.setItemClickLIsterner(new ItemClickLIsterner() {
            @Override
            public void onItemClick(View v, int position) {
                CheckBox checkBox= (CheckBox) v;

                if(checkBox.isChecked()){
                    checkedLIst.add(userList.get(position));

                }else if(!checkBox.isChecked()){
                    checkedLIst.remove(userList.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    void addUserToLIst(List<UserListDto.Datum> newUserList){
        userList.addAll(newUserList);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }
    public class MemberHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView profilePicture;
        TextView fullName,phone,email;
        CheckBox itemCheck;
        ItemClickLIsterner itemClickLIsterner;

        MemberHolder(View itemView) {
            super(itemView);
            profilePicture=itemView.findViewById(R.id.list_image_view);
            fullName=itemView.findViewById(R.id.fullName);
            email=itemView.findViewById(R.id.email);
            phone=itemView.findViewById(R.id.phone);
            itemCheck=itemView.findViewById(R.id.checkBox);
            itemCheck.setVisibility(View.VISIBLE);
            itemCheck.setOnClickListener(this);
        }

        public void setItemClickLIsterner(ItemClickLIsterner itemClickLIsterner){
            this.itemClickLIsterner=itemClickLIsterner;
        }

        @Override
        public void onClick(View view) {
            this.itemClickLIsterner.onItemClick(view,getLayoutPosition());
        }
    }

    public interface ItemClickLIsterner{
        void onItemClick(View v,int position);
    }
}
