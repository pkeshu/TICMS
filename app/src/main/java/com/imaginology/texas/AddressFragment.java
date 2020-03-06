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
public class AddressFragment extends Fragment {
    private TextView addZone,addDistrict,addVdc,addType;
    private TextView labelZone,labelDistrict,labelSdc,labelType;



    public AddressFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_address, container, false);
        addDistrict=view.findViewById(R.id.add_district);
        addType=view.findViewById(R.id.add_type);
        addVdc=view.findViewById(R.id.add_vdc);
        addZone=view.findViewById(R.id.add_zone);
        Bundle bundle=getArguments();
        if(bundle!=null){
            String district=bundle.getString("district");
            String zone=bundle.getString("zone");
            String vdc=bundle.getString("vdc");
            String type=bundle.getString("type");
            ChangeAddress(district,zone,vdc,type);
        }



        return view;
    }

    private void ChangeAddress(String district, String zone, String vdc, String type) {

        addDistrict.setText(district);
        addZone.setText(zone);
        addVdc.setText(vdc);
        addType.setText(type);
    }

}
