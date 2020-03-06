package com.imaginology.texas.Team.TeamDetails;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.imaginology.texas.R;
import com.imaginology.texas.Team.TeamDetails.ManupulateDtos.MemberDto;

import java.util.ArrayList;
import java.util.List;

public class TeamDetailsAdapter extends RecyclerView.Adapter<TeamDetailsAdapter.TeamDetHolder>{

    private List<MemberDto> teamMemberDtos=new ArrayList<>();
    @Override
    public TeamDetHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).
                inflate(R.layout.custom_team_detail_item,parent,false);
        return new TeamDetailsAdapter.TeamDetHolder(view);
    }

    @Override
    public void onBindViewHolder(TeamDetHolder holder, int position) {
        MemberDto currentMember=teamMemberDtos.get(position);
            holder.fullName.setText(currentMember.getFullName());
            holder.contact.setText(currentMember.getContact());
            holder.type.setText(currentMember.getType());
    }


    @Override
    public int getItemCount() {
        return teamMemberDtos.size();
    }
    void addMemberInTeam(List<MemberDto> newTeamMemberDtos){
        teamMemberDtos.addAll(newTeamMemberDtos);
        notifyDataSetChanged();
    }


    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
    public class TeamDetHolder extends RecyclerView.ViewHolder{
        TextView fullName,type,contact;
        public TeamDetHolder(View itemView) {
            super(itemView);
            fullName=itemView.findViewById(R.id.name);
            type=itemView.findViewById(R.id.type);
            contact=itemView.findViewById(R.id.contact);
        }
    }

}
