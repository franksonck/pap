package fr.jlm2017.pap.Activities;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import fr.jlm2017.pap.R;

public class WebCatcher extends AppCompatActivity {
    private TextView txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_catcher);
        txt = (TextView) findViewById(R.id.Answer);
        Uri data = getIntent().getData();
        String host = data.getHost();
        String text;
        if(host.equals("success")) text="Autorisation reçue !";
        else text="Autorisation refusée !";
        txt.setText(text);
        Thread startApp = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(2000);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        startApp.start();

    }
}
