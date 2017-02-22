package fr.jlm2017.pap;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

/**
 * Created by thoma on 20/02/2017.
 */

class ButtonAnimationJLM {

    CircularProgressButton button;

    ButtonAnimationJLM(CircularProgressButton button) {
        this.button = button;
    }

    void WrongButtonAnimation() {
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

    void OKButtonAnimation( ) {
        final Handler handler = new Handler();
        int timing = button.getResources().getInteger(R.integer.loading_time_animation);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                button.doneLoagingAnimation(ContextCompat.getColor( button.getContext(), R.color.JLMgreen),BitmapFactory.decodeResource(button.getResources(),R.drawable.ic_done_white_48dp));
            }
        },timing);
    }

    void OKButtonAndRevertAnimation() {
        OKButtonAnimation( );
        revert(R.integer.loading_end_time_animation);
    }

    void revert(int id)
    {
        final Handler handler = new Handler();
        int timing =  button.getResources().getInteger(id);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                button.revertAnimation();
            }
        }, timing);
    }
}
