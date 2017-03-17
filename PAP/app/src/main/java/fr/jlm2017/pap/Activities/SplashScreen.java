package fr.jlm2017.pap.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;

import fr.jlm2017.pap.MongoDB.QueryBuilder;
import fr.jlm2017.pap.R;
import fr.jlm2017.pap.utils.Encoder;
import fr.jlm2017.pap.MongoDB.OAuthAsyncTask;

public class SplashScreen extends AppCompatActivity {
    public int SPLASH_SCREEN_TIME=2000;
    private static String SavedAppTokenPreference = "SavedAppTokenPreference";
    private static int BACK_FROM_WEB = 2000;
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Thread startApp = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(SPLASH_SCREEN_TIME);
                    // app token generation
                    SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    token = shared.getString(SavedAppTokenPreference, "");
                    if(token.equals("")) {
                        SharedPreferences.Editor editor = shared.edit();
                        token = new Encoder.RandomString(32).nextString();
                        editor.putString(SavedAppTokenPreference, token );
                        editor.apply();
                    }
                    DoOAuth(token);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        startApp.start();
    }

    private void DoOAuth(final String token) {
        OAuthAsyncTask tsk = new OAuthAsyncTask() {
            @Override
            public void onResponseReceived(Pair<String, Boolean> result) {
                if(result.second){
                    Intent goCheck = new Intent(getApplicationContext(),Main.class);
                    goCheck.putExtra("APP_TOKEN", token);
                    goCheck.putExtra("USER_ID", "toto");
                    startActivity(goCheck);
                    finish();
                }
                else {
                    QueryBuilder qb = new QueryBuilder();
                    Intent goWeb = new Intent(Intent.ACTION_VIEW, Uri.parse(qb.buildConnexionURL(token)));
                    startActivityForResult(goWeb,BACK_FROM_WEB);
                }
            }
        };
        tsk.execute(token);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==BACK_FROM_WEB) {
           if(resultCode == RESULT_OK) {
               Intent goCheck = new Intent(getApplicationContext(),Main.class);
               goCheck.putExtra("APP_TOKEN", token);
               goCheck.putExtra("USER_ID", data.getStringExtra("USER_ID"));
               startActivity(goCheck);
               finish();
           }
           else {
               DoOAuth(token);
           }
        }

    }
}
