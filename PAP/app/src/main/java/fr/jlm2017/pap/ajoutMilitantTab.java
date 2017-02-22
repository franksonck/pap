package fr.jlm2017.pap;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import fr.jlm2017.pap.MongoDB.DataObject;
import fr.jlm2017.pap.MongoDB.GetAsyncTask;
import fr.jlm2017.pap.MongoDB.SaveAsyncTask;

/**
 * Created by thoma on 15/02/2017.
 */

public class ajoutMilitantTab extends Fragment {

    CircularProgressButton mAddMilit;
    ButtonAnimationJLM mAddMilitAnimation;
    EditText mEmail;
    CheckBox mIsAdmin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_admin_1, container, false);

        mAddMilit = (CircularProgressButton) rootView.findViewById(R.id.addUser);
        mAddMilitAnimation = new ButtonAnimationJLM(mAddMilit);
        mEmail = (EditText) rootView.findViewById(R.id.emailNewMilitant);
        mIsAdmin = (CheckBox) rootView.findViewById(R.id.isAdmin);


        mAddMilit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                mAddMilit.startAnimation();
                //animation tools 1/////////////////////////
                final Handler handler = new Handler();
                int timing = getResources().getInteger(R.integer.decontracting_time_animation);
                //animation tools 1- end/////////////////////////

                String email = mEmail.getText().toString();

                if(!verifyEmail()) {
                    //animation tools 2/////////////////////////
                    mAddMilitAnimation.WrongButtonAnimation();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mEmail.setError("Email invalide ou déjà pris");
                            mEmail.requestFocus();
                        }
                    }, timing);
                    return;
                    //animation tools 2-end/////////////////////////
                }
                
                String basePWD = Encoder.encode(getResources().getString(R.string.basePWD));
                Militant mil = new Militant("", email, basePWD, mIsAdmin.isChecked());
                SaveAsyncTask saveMil = new SaveAsyncTask();
                Pair<Boolean, String> result;
                try {
                    result = saveMil.execute(mil).get();
                    if(!result.first) {// connexion ratée
                        mAddMilitAnimation.WrongButtonAnimation();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                String message = "Connexion à la base impossible";
                                showLongToast(message);
                            }
                        }, timing);
                        return;
                    }
                    mil.id_=result.second;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                //on actualise l'affichage dans le Fragement de suppression
                final Intent intent = new Intent("DATA_ACTION");
                intent.putExtra("DATA_EXTRA", mil);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

                //animation tools 2/////////////////////////
                mAddMilitAnimation.OKButtonAndRevertAnimation();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String message = "Vous avez bien ajouté un militant";
                        showLongToast(message);
                        resetUI();
                    }
                }, timing);
                //animation tools 2-end/////////////////////////
                
            }
        });

        return rootView;
    }

    private boolean verifyEmail() {
        String email = mEmail.getText().toString();
        boolean res= false;
        if(LoginActivity.isEmailValid(email)){
            GetAsyncTask tsk = new GetAsyncTask();
            ArrayList<Pair<String,String>> ids = new ArrayList<>();
            ids.add(Pair.create("email",email));
            try {
                Pair<ArrayList<DataObject>,Boolean> result = (tsk.execute(Pair.create("militants",ids))).get();
                if(!result.first.isEmpty()) return false; //on interdit de prendre le meme mail qu'un autre militant
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            res=true;
        }
        return res;
    }

    private void resetUI () {

        mEmail.setText("");
        mIsAdmin.setChecked(false);

    }

    public void showLongToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }
}
