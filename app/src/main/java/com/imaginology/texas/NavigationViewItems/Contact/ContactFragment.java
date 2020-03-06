package com.imaginology.texas.NavigationViewItems.Contact;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.imaginology.texas.R;


public class ContactFragment extends Fragment {
    TextView versionShow;
    private WebView webView;
    String html;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        webView=view.findViewById(R.id.wv_contact_us_embd_map);
        html="<iframe src=\"https://www.google.com/maps/embed?pb=!1m14!1m8!1m3!1d3532.158377085739!2d85.3424053!3d27.7123959!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x39eb1970a9ff7041%3A0xfcaa45db29104458!2sTexas+International+College!5e0!3m2!1sen!2snp!4v1546161939963\" width=\"340\" height=\"240\" frameborder=\"0\" style=\"border:0\" allowfullscreen></iframe>";
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadData(html,"text/html", null);
        versionShow=view.findViewById(R.id.version_show);


        //version show
        String versionName="";
        try {
            versionName=getActivity().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        versionShow.setText("Version: "+versionName);

        return view;
    }

}
