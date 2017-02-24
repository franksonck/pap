package fr.jlm2017.pap.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import fr.jlm2017.pap.utils.ButtonAnimationJLM;
import fr.jlm2017.pap.utils.Encoder;
import fr.jlm2017.pap.MongoDB.Militant;
import fr.jlm2017.pap.MongoDB.DataObject;
import fr.jlm2017.pap.MongoDB.GetAsyncTask;
import fr.jlm2017.pap.MongoDB.SaveAsyncTask;
import fr.jlm2017.pap.R;

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
                mAddMilitAnimation.startAnimation();
                //animation tools 1/////////////////////////
                final Handler handler = new Handler();
                final int timing = getResources().getInteger(R.integer.decontracting_time_animation);
                //animation tools 1- end/////////////////////////

                final String email = mEmail.getText().toString();
                if(LoginActivity.isEmailValid(email)){
                    GetAsyncTask tsk = new GetAsyncTask() {
                        @Override
                        public void onResponseReceived(Pair<ArrayList<DataObject>, Boolean> result) {
                            if(!result.first.isEmpty()){ // on interdit de prendre le meme mail qu'un autre militant
                                //animation tools 2/////////////////////////
                                mAddMilitAnimation.WrongButtonAnimation();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mEmail.setError("Email déjà pris");
                                        mEmail.requestFocus();
                                    }
                                }, timing);
                                //animation tools 2-end/////////////////////////
                            }
                            else { // Email libre et valide
                                String basePWD = Encoder.encode(getResources().getString(R.string.basePWD));
                                final Militant mil = new Militant("", email, basePWD, mIsAdmin.isChecked());
                                SaveAsyncTask saveMil = new SaveAsyncTask() {
                                    @Override
                                    public void onResponseReceived(Pair<Boolean, String> resultatSave) {
                                        if(!resultatSave.first) {// connexion ratée
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
                                        mil.id_=resultatSave.second;
                                    }
                                };
                                saveMil.execute(mil);

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
                        }
                    };
                    ArrayList<Pair<String,String>> ids = new ArrayList<>();
                    ids.add(Pair.create("email",email));
                    tsk.execute(Pair.create("militants",ids));
                }
                else { // Email Invalide
                    //animation tools 2/////////////////////////
                    mAddMilitAnimation.WrongButtonAnimation();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mEmail.setError("Email invalide");
                            mEmail.requestFocus();
                        }
                    }, timing);
                    //animation tools 2-end/////////////////////////
                }
            }
        });

        return rootView;
    }

    private void resetUI () {

        mEmail.setText("");
        mIsAdmin.setChecked(false);

    }

    public void showLongToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }
}
