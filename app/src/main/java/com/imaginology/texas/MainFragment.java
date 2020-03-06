package com.imaginology.texas;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.imaginology.texas.RoomDatabase.UserLoginResponse.UserLoginResponseEntity;
import com.imaginology.texas.util.GetLoginInstanceFromDatabase;
import com.imaginology.texas.util.ImageViewLoader;
import com.imaginology.texas.util.LoginChecker;
import com.imaginology.texas.util.NavmenuHideShow;
import com.imaginology.texas.util.StringHtmlConversion;
import com.imaginology.texas.util.SupportActionBarInitializer;


public class MainFragment extends Fragment {

    private SliderLayout mSlider;
    private WebView webviiewer;
    private String string1;
    private TextSliderView textSliderView1,textSliderView2,textSliderView3;

    private TextView mUsername, mEmail, mLogin;
    private ImageView headernavimage;
    private UserLoginResponseEntity loginInstance;
    private NavigationView navigationView;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        SupportActionBarInitializer.setSupportActionBarTitle( ((AppCompatActivity) getActivity()).getSupportActionBar(),"Home");

        GetLoginInstanceFromDatabase loginInstanceFromDatabase=new GetLoginInstanceFromDatabase(getContext());
        loginInstance=loginInstanceFromDatabase.getLoginInstance();
        init(view);

        navigationView = getActivity().findViewById(R.id.nav_view);
        View mView = navigationView.getHeaderView(0);
        mUsername= mView.findViewById(R.id.username1);
        mEmail= mView.findViewById(R.id.email1);
        headernavimage= mView.findViewById(R.id.image);
        mLogin= mView.findViewById(R.id.loginfirst);
        return view;
    }

    @Override
    public void onStop() {
        mSlider.stopAutoCycle();
        super.onStop();
    }

    private void init(View view) {
        webviiewer=  view.findViewById(R.id.webview);
        mSlider = view.findViewById(R.id.daimajia_slider_image);
        textSliderView1 = new TextSliderView(getContext());
        textSliderView2 = new TextSliderView(getContext());
        textSliderView3 = new TextSliderView(getContext());
        textSliderView1.description("").image(R.drawable.texasbanner1);
        textSliderView2.description("").image(R.drawable.texasbanner2);
        textSliderView3.description("").image(R.drawable.texasbanner3);
        mSlider.addSlider(textSliderView1);
        mSlider.addSlider(textSliderView2);
        mSlider.addSlider(textSliderView3);
        string1 = getResources().getString(R.string.welcome_message);
        webviiewer.loadData(StringHtmlConversion.getHtml(string1), "text/html",null);



    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

    }

    @Override
    public void onStart() {
        super.onStart();
        String username, email, imageUrl;

        if (LoginChecker.IsLoggedIn(getContext())) {
            username = loginInstance.getFirstName()+" "+loginInstance.getLastName();
            email = loginInstance.getEmail();
            imageUrl = loginInstance.getProfilePicture();
            NavmenuHideShow.showHideNavMenuAccordingToLoginRole(navigationView, loginInstance.getLoginType(),getContext());
            if (mUsername.getVisibility() != View.VISIBLE || mEmail.getVisibility() != View.VISIBLE) {
                mUsername.setVisibility(View.VISIBLE);
                mEmail.setVisibility(View.VISIBLE);
                mLogin.setVisibility(View.INVISIBLE);
            }


            mUsername.setText(username);
            mEmail.setText(email);

        } else {
            NavmenuHideShow.showHideNavMenuAccordingToLoginRole(navigationView, loginInstance.getLoginType(),getContext());
            if (mUsername.getVisibility() != View.INVISIBLE || mEmail.getVisibility() != View.INVISIBLE) {
                mUsername.setVisibility(View.INVISIBLE);
                mEmail.setVisibility(View.INVISIBLE);
            }
            if (mLogin.getVisibility() == View.INVISIBLE) {
                mLogin.setVisibility(View.VISIBLE);
            }
        }

//        ImageViewLoader.loadImage(getContext(), imageUrl, headernavimage);


    }
}
