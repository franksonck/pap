package fr.jlm2017.pap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

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

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError(getString(R.string.error_field_required));
                    focusView = mEmail;
                    cancel = true;
                } else if (!LoginActivity.isEmailValid(email)) {
                    mEmail.setError(getString(R.string.error_invalid_email));
                    focusView = mEmail;
                    cancel = true;
                }

                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                } else {
                    // Show a progress spinner, and kick off a background task to
                    // perform the user login attempt.
                    String basePWD = getResources().getString(R.string.basePWD);
                    Militant mil = new Militant("toto", email, basePWD, mIsAdmin.isChecked());
                    SaveAsyncTask saveMil = new SaveAsyncTask();
                    saveMil.execute(mil);

                    String message = "Vous avez bien ajouté : " + mil.toString();
                    Check.showLongToast(message);
                    resetUI();
                }
            }
        });

        return rootView;
    }

    private void resetUI () {

        mEmail.setText("");
        mIsAdmin.setChecked(false);

    }
}
