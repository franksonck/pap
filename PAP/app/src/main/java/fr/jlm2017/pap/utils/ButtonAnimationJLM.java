package fr.jlm2017.pap.utils;

import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.content.ContextCompat;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import fr.jlm2017.pap.R;

/**
 * Created by thoma on 20/02/2017.
 */

public class ButtonAnimationJLM {

    public CircularProgressButton button;
    private String Originaltext;

    public ButtonAnimationJLM(CircularProgressButton button) {
        this.button = button;
    }

    public void startAnimation(){
        Originaltext=button.getText().toString();
        button.startAnimation();
    }
    public void WrongButtonAnimation() {
        final Handler handler = new Handler();
        int timing = button.getResources().getInteger(R.integer.loading_time_animation);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                button.doneLoagingAnimation(ContextCompat.getColor(button.getContext(), R.color.JLMred), BitmapFactory.decodeResource(button.getResources(),R.drawable.ic_cross_white_48dp));
            }
        }, timing);
        revert(R.integer.loading_end_time_animation);
    }

    public void OKButtonAnimation() {
        final Handler handler = new Handler();
        int timing = button.getResources().getInteger(R.integer.loading_time_animation);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                button.doneLoagingAnimation(ContextCompat.getColor( button.getContext(), R.color.JLMgreen),BitmapFactory.decodeResource(button.getResources(),R.drawable.ic_done_white_48dp));
            }
        },timing);
    }

    public void OKButtonAndRevertAnimation() {
        OKButtonAnimation( );
        revert(R.integer.loading_end_time_animation);
    }

    public void revert(int id)
    {
        final Handler handler = new Handler();
        int timing =  button.getResources().getInteger(id);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                button.revertAnimation();
            }
        }, timing);
        timing =button.getResources().getInteger(R.integer.decontracting_time_animation);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                button.setText(Originaltext);
            }
        }, timing);

    }
}
