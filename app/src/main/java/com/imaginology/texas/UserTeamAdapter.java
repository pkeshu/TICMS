package com.imaginology.texas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.imaginology.texas.RoomDatabase.UserTeamResponse.TeamEntity;
import com.imaginology.texas.Team.TeamDto;

import java.util.ArrayList;
import java.util.List;

public class UserTeamAdapter extends RecyclerView.Adapter<UserTeamAdapter.TeamListHolder> {

    private Context mContext;
    private List<TeamEntity> teamList=new ArrayList<>();
    private List<TeamDto.Content> contentList=new ArrayList<>();
    public UserTeamAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public TeamListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.custum_team_layout,parent,false);
        return new TeamListHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(TeamListHolder holder, int position) {
        if(!teamList.isEmpty() && contentList.isEmpty()){
            if(position%2==0){
                holder.teamNametxt.setBackgroundResource(R.drawable.custom_team_border);
                holder.teamNametxt.setText(teamList.get(position).getName());
                holder.teamNametxt.setTextColor(Color.WHITE);


            }

            else {
                holder.teamNametxt.setBackgroundResource(R.drawable.custom_team_layout_border);
                holder.teamNametxt.setText(teamList.get(position).getName());
                holder.teamNametxt.setTextColor(Color.WHITE);


            }
        }
        if(teamList.isEmpty() && !contentList.isEmpty()){
            if(position%2==0){
                holder.teamNametxt.setBackgroundResource(R.drawable.custom_team_border);
                holder.teamNametxt.setText(contentList.get(position).getName());
                holder.teamNametxt.setTextColor(Color.WHITE);


            }

            else {
                holder.teamNametxt.setBackgroundResource(R.drawable.custom_team_layout_border);
                holder.teamNametxt.setText(contentList.get(position).getName());
                holder.teamNametxt.setTextColor(Color.WHITE);


            }
        }
    }

    @Override
    public int getItemCount() {

        if(!teamList.isEmpty() && contentList.isEmpty())
            return teamList.size();
        if(teamList.isEmpty() && !contentList.isEmpty())
            return contentList.size();
        return 0;
    }

    void addTeamToList(List<TeamEntity> newTeamList){
        teamList.addAll(newTeamList);
        notifyDataSetChanged();
    }

    void addUserTeamToList(List<TeamDto.Content> newContentList){
        contentList.addAll(newContentList);
        notifyDataSetChanged();
    }

    public class TeamListHolder extends RecyclerView.ViewHolder{

        TextView teamNametxt;

        public TeamListHolder(View itemView) {
            super(itemView);
            teamNametxt=itemView.findViewById(R.id.team_name);

        }
    }

}
