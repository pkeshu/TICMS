package com.imaginology.texas.SplashScreen;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.imaginology.texas.MainActivity;
import com.imaginology.texas.R;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ImageView screenIV;
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        screenIV=findViewById(R.id.imageView5);
        LogoLauncher logoLauncher=new LogoLauncher();
        logoLauncher.start();
//        ObjectAnimator animatorY=ObjectAnimator.ofFloat(screenIV,"y",360f);
//        animatorY.setDuration(1500);
        Animation animation=AnimationUtils.loadAnimation(this,R.anim.transition);
        screenIV.startAnimation(animation);
//        ObjectAnimator rotateAnimator=ObjectAnimator.ofFloat(screenIV,"rotation",0f,360f);
//        rotateAnimator.setDuration(2000);
//        AnimatorSet animatorSet=new AnimatorSet();
//        animatorSet.playTogether(rotateAnimator);
//        animatorSet.start();

    }
    private class LogoLauncher extends Thread{
        public void run(){
            try{
                sleep(2000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            startActivity(new Intent(SplashScreenActivity.this,MainActivity.class));
            SplashScreenActivity.this.finish();
        }
    }

}
