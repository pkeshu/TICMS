package com.imaginology.texas;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ParentFragment extends Fragment {
    private TextView parentName,parentEmail,parentPhone,parentRelation;


    public ParentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_parent, container, false);
        parentName=view.findViewById(R.id.parent_name);
        parentEmail=view.findViewById(R.id.parent_email);
        parentRelation=view.findViewById(R.id.parent_relation);
        parentPhone=view.findViewById(R.id.parent_phone);

        Bundle bundle=getArguments();
        if(bundle!=null){
            String pName=bundle.getString("pName");
            String pEmail=bundle.getString("pEmail");
            String pRelation=bundle.getString("pRelation");
            String pPhone=bundle.getString("pPhone");
            changeParentDetails(pName,pEmail,pRelation,pPhone);
        }


        return view;
    }

    private void changeParentDetails(String pName, String pEmail, String pRelation, String pPhone) {

        parentName.setText(pName);
        parentEmail.setText(pEmail);
        parentRelation.setText(pRelation);
        parentPhone.setText(pPhone);
    }

}
