package com.imaginology.texas;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.imaginology.texas.RoomDatabase.UserTeamResponse.TeamEntity;
import com.imaginology.texas.Team.TeamDto;
import com.imaginology.texas.util.CheckError;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class BasicInformationFragment extends Fragment {
    private TextView userFullName,userEmail,userPhone,userAddress;
    private RecyclerView recyclerView;
    private UserTeamAdapter userTeamAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public BasicInformationFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_user_details, container, false);
        userFullName=view.findViewById(R.id.user_full_name);
        userEmail=view.findViewById(R.id.user_email);
        userPhone=view.findViewById(R.id.user_phone);
        userAddress=view.findViewById(R.id.user_address);
        userTeamAdapter=new UserTeamAdapter(getContext());
        layoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView=view.findViewById(R.id.team_recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(userTeamAdapter);

        Bundle bundle= getArguments();
        if (bundle!=null){
            List<TeamDto.Content> contentList=bundle.getParcelableArrayList("userTeamList");
            List<TeamEntity> teamEntities= bundle.getParcelableArrayList("teamList");
            String fullname=CheckError.checkNullString(bundle.getString("fullname",null));
            String email=CheckError.checkNullString(bundle.getString("email",null));
            String phone=CheckError.checkNullString(bundle.getString("phone",null));
            String gender=CheckError.checkNullString(bundle.getString("gender",null));
//            Toast.makeText(getContext(),fullname,Toast.LENGTH_SHORT).show();
            if(contentList.isEmpty() && !teamEntities.isEmpty())
                changeItemforCurrentUser(fullname,email,phone,gender,teamEntities);
            if(!contentList.isEmpty() && teamEntities.isEmpty())
                changeItemforUser(fullname,email,phone,gender,contentList);
            if(contentList.isEmpty() && teamEntities.isEmpty())
                changeItemforStudentTeacher(fullname,email,phone,gender);


        }

        return view;
    }

    private void changeItemforStudentTeacher(String fullname, String email, String phone, String gender) {
        userFullName.setText(fullname);
        userEmail.setText(email);
        userPhone.setText(phone);
        userAddress.setText(gender);

    }

    public void changeItemforUser(String fullname, String email, String phone, String gender, List<TeamDto.Content> contentList) {
        userTeamAdapter.addUserTeamToList(contentList);
        userFullName.setText(fullname);
        userEmail.setText(email);
        userPhone.setText(phone);
        userAddress.setText(gender);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                boolean isScrolling=false;
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    isScrolling=true;
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });
    }

    public void changeItemforCurrentUser(String fullname,String email,String phone,String gender,List<TeamEntity> teamEntityList){
        userTeamAdapter.addTeamToList(teamEntityList);
        userFullName.setText(fullname);
        userEmail.setText(email);
        userPhone.setText(phone);
        userAddress.setText(gender);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                boolean isScrolling=false;
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    isScrolling=true;
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });

    }

}
