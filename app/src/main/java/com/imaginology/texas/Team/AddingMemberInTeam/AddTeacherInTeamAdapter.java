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
import com.imaginology.texas.Teachers.TeacherListDto;
import com.imaginology.texas.Users.UserListDto;
import com.imaginology.texas.util.ImageViewLoader;

import java.util.ArrayList;
import java.util.List;

public class AddTeacherInTeamAdapter extends RecyclerView.Adapter<AddTeacherInTeamAdapter.MemberHolder>{
    private List<TeacherListDto.Datum> teacherList=new ArrayList<>();
    private Context mContext;
    public List<TeacherListDto.Datum> checkedLIst=new ArrayList<>();

    public AddTeacherInTeamAdapter(Context mContext) {
        this.mContext = mContext;
    }


    @Override
    public AddTeacherInTeamAdapter.MemberHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_adapter,parent,false);
        return new AddTeacherInTeamAdapter.MemberHolder(view);
    }

    @Override
    public void onBindViewHolder(AddTeacherInTeamAdapter.MemberHolder holder, int position) {

        TeacherListDto.Datum currentUser=teacherList.get(position);

        holder.fullName.setText(currentUser.getFirstName()+" "+currentUser.getLastName());
        holder.email.setText(currentUser.getEmail());
        holder.phone.setText(currentUser.getPhoneNumber());
        String pPUrl=currentUser.getProfilePicture();
        ImageViewLoader.loadImage(mContext,pPUrl,holder.profilePicture);

        holder.setItemClickLIsterner(new AddTeacherInTeamAdapter.ItemClickLIsterner() {
            @Override
            public void onItemClick(View v, int position) {
                CheckBox checkBox= (CheckBox) v;

                if(checkBox.isChecked()){
                    checkedLIst.add(teacherList.get(position));

                }else if(!checkBox.isChecked()){
                    checkedLIst.remove(teacherList.get(position));
                }
            }
        });
    }




    void addTeacherToLIst(List<TeacherListDto.Datum> newTeacherList){
        teacherList.addAll(newTeacherList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return teacherList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public class MemberHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView profilePicture;
        TextView fullName,phone,email;
        CheckBox itemCheck;
        AddTeacherInTeamAdapter.ItemClickLIsterner itemClickLIsterner;

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

        public void setItemClickLIsterner(AddTeacherInTeamAdapter.ItemClickLIsterner itemClickLIsterner){
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
