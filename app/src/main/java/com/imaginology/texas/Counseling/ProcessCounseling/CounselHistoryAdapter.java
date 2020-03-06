package com.imaginology.texas.Counseling.ProcessCounseling;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.imaginology.texas.Counseling.CounselingDetail.History;
import com.imaginology.texas.R;
import com.imaginology.texas.util.ImageViewLoader;

import java.util.ArrayList;
import java.util.List;

public class CounselHistoryAdapter extends RecyclerView.Adapter<CounselHistoryAdapter.CounselHistoryViewHolder>{

    private List<History> historyList=new ArrayList<>();
    private Context context;

    public CounselHistoryAdapter(Context context) {
        this.context=context;
    }

    @Override
    public CounselHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_history_item,parent,false);
        return new CounselHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CounselHistoryViewHolder holder, int position) {
        History history=historyList.get(position);
        ImageViewLoader.loadImage(context,history.getProfilePicture(),holder.counsellorProfilePicture);
        holder.counsellorName.setText(history.getCounselledby());
        holder.counselTime.setText(history.getCounseledDate());
        holder.counselRemarks.setText(history.getRemarks());
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }
     public   void addHistoryInList(List<History> newHistoryList){
        historyList.addAll(newHistoryList);
        notifyDataSetChanged();
     }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public class CounselHistoryViewHolder extends RecyclerView.ViewHolder{
        ImageView counsellorProfilePicture;
        TextView counselTime,counsellorName,counselRemarks;

        public CounselHistoryViewHolder(View itemView) {
            super(itemView);
            counsellorProfilePicture=itemView.findViewById(R.id.counsellor_image);
            counselTime=itemView.findViewById(R.id.date_of_counsil);
            counsellorName=itemView.findViewById(R.id.counsellor_name);
            counselRemarks=itemView.findViewById(R.id.counsellor_remarks);
        }
    }
}
