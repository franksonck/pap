package fr.jlm2017.pap;

import android.content.Intent;
import android.os.Bundle;
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

import fr.jlm2017.pap.MongoDB.GetAsyncTask;
import fr.jlm2017.pap.MongoDB.SaveAsyncTask;

/**
 * Created by thoma on 15/02/2017.
 */

public class ajoutMilitantTab extends Fragment {

    Button mAddMilit;
    EditText mEmail;
    CheckBox mIsAdmin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_admin_1, container, false);

        mAddMilit = (Button) rootView.findViewById(R.id.addUser);
        mEmail = (EditText) rootView.findViewById(R.id.emailNewMilitant);
        mIsAdmin = (CheckBox) rootView.findViewById(R.id.isAdmin);


        mAddMilit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String email = mEmail.getText().toString();
                boolean cancel = false;
                View focusView = null;

                if(!verifyEmail()) { mEmail.setError("Email invalide ou déjà pris");
                    mEmail.requestFocus(); return; 
                }
                
                String basePWD = getResources().getString(R.string.basePWD);
                Militant mil = new Militant("", email, basePWD, mIsAdmin.isChecked());
                SaveAsyncTask saveMil = new SaveAsyncTask();
                Pair<Boolean, String> result;
                try {
                    result = saveMil.execute(mil).get();
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

                String message = "Vous avez bien ajouté un militant";
                showLongToast(message);
                resetUI();
                
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
                if(!(tsk.execute(Pair.create("militants",ids)).get()).first.isEmpty()) return false; //on interdit de prendre le meme mail qu'un autre militant
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
