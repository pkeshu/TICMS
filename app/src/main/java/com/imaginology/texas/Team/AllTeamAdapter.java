package com.imaginology.texas.Team;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.imaginology.texas.R;
import java.util.ArrayList;
import java.util.List;

public class AllTeamAdapter extends RecyclerView.Adapter<AllTeamAdapter.TeamHolder> {
    private Context mContext;
    private List<TeamDto.Content> teamList = new ArrayList<>();
    private ClickListener clickListener;

    public AllTeamAdapter(Context mContext) {
        this.mContext = mContext;
    }


    @Override
    public TeamHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_team_list, parent, false);
        return new AllTeamAdapter.TeamHolder(view);
    }

    @Override
    public void onBindViewHolder(TeamHolder holder, int position) {
        holder.teamName.setText(teamList.get(position).getName());
        holder.teamDescription.setText(teamList.get(position).getDescription());
        holder.teamType.setText(teamList.get(position).getType());

    }

    @Override
    public int getItemCount() {
        return teamList.size();
    }



    void addTeamToList(List<TeamDto.Content> newTeamList) {
        teamList.addAll(newTeamList);
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


    public class TeamHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView teamName, teamDescription, teamType;
        ConstraintLayout rootLayout;

        public TeamHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            teamName = itemView.findViewById(R.id.team_name);
            teamDescription = itemView.findViewById(R.id.team_description);
            teamType = itemView.findViewById(R.id.team_type);
            rootLayout = itemView.findViewById(R.id.rootLayout);
//            addMember=itemView.findViewById(R.id.add_member);

        }

        @Override
        public void onClick(View view) {
            if(clickListener!=null){
                clickListener.itemClicked(view, teamList.get(getPosition()), getPosition());
            }
        }
    }

    public interface ClickListener {

        void itemClicked(View v, TeamDto.Content teamDto, int position);
    }
    public void setTeamClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
