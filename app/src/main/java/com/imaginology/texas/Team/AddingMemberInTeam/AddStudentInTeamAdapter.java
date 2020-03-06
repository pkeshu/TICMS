package com.imaginology.texas.Team.AddingMemberInTeam;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.imaginology.texas.R;
import com.imaginology.texas.Students.StudentListDto;
import com.imaginology.texas.util.ImageViewLoader;

import java.util.ArrayList;
import java.util.List;

public class AddStudentInTeamAdapter extends RecyclerView.Adapter<AddStudentInTeamAdapter.MemberHolder> {
    private List<StudentListDto.Datum> userList = new ArrayList<>();
    private Context mContext;
    public List<StudentListDto.Datum> checkedLIst = new ArrayList<>();
    private List<Integer> selectedItem = new ArrayList<>();
    public AddStudentInTeamAdapter(Context mContext) {
        this.mContext = mContext;
    }
    @Override
    public AddStudentInTeamAdapter.MemberHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_adapter, parent, false);
        return new AddStudentInTeamAdapter.MemberHolder(view);
    }

    @Override
    public void onBindViewHolder(AddStudentInTeamAdapter.MemberHolder holder, int position) {

        StudentListDto.Datum currentUser = userList.get(position);

        holder.fullName.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
        holder.email.setText(currentUser.getEmail());
        holder.phone.setText(currentUser.getPhoneNumber());
        String pPUrl = currentUser.getProfilePicture();
        ImageViewLoader.loadImage(mContext, pPUrl, holder.profilePicture);
//        if (currentUser.getLoginId() == selectedItem.get(position)) {
//            holder.itemCheck.setChecked(true);
//            checkedLIst.add(currentUser);
//        }
        holder.itemCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    checkedLIst.add(userList.get(position));

                } else if (b) {
                    checkedLIst.remove(userList.get(position));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    void addStudentToLIst(List<StudentListDto.Datum> newUserList) {
        userList.addAll(newUserList);
        notifyDataSetChanged();
    }

    void addCheckedItemToList(List<Integer> newCheckedList) {
        selectedItem.addAll(newCheckedList);
    }

    public class MemberHolder extends RecyclerView.ViewHolder {
        ImageView profilePicture;
        TextView fullName, phone, email;
        CheckBox itemCheck;

        MemberHolder(View itemView) {
            super(itemView);
            profilePicture = itemView.findViewById(R.id.list_image_view);
            fullName = itemView.findViewById(R.id.fullName);
            email = itemView.findViewById(R.id.email);
            phone = itemView.findViewById(R.id.phone);
            itemCheck = itemView.findViewById(R.id.checkBox);
            itemCheck.setVisibility(View.VISIBLE);
        }
    }
}
