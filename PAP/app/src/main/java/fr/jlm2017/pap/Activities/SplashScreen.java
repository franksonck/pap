package fr.jlm2017.pap.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import fr.jlm2017.pap.R;

public class SplashScreen extends AppCompatActivity {
    public int SPLASH_SCREEN_TIME=3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Thread startApp = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(SPLASH_SCREEN_TIME);
                    Intent goLogin = new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(goLogin);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        startApp.start();
    }
}
