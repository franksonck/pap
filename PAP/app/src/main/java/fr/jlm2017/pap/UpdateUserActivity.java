package fr.jlm2017.pap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;

import fr.jlm2017.pap.MongoDB.UpdateAsyncTask;

public class UpdateUserActivity extends AppCompatActivity {

    private EditText mPseudo, mEmail, mPassword;
    private CheckedTextView mAdmin;
    private Button mSave, mCancel;
    private Militant user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);

        mPseudo = (EditText) findViewById(R.id.PseudoUpdate);
        mEmail = (EditText) findViewById(R.id.emailUpdate);
        mPassword = (EditText) findViewById(R.id.passwordUpdate);
        mAdmin = (CheckedTextView) findViewById(R.id.isAdminUpdate);
        mSave = (Button) findViewById(R.id.SaveUpdate);
        mCancel = (Button) findViewById(R.id.cancelUpdate);

        user = getIntent().getParcelableExtra("USER_EXTRA");

        mPseudo.setText(user.pseudo);
        mEmail.setText(user.email);
        if(!user.admin) mAdmin.setCheckMarkDrawable(null);

        mSave.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(!verifyPseudo()) { mPseudo.setError(getString(R.string.error_field_required));
                    mPseudo.requestFocus(); return; }
                if(!verifyEmail()) { mEmail.setError(getString(R.string.error_field_required));
                    mEmail.requestFocus(); return; }
                verifyPassword();
                UpdateAsyncTask tsk = new UpdateAsyncTask();
                tsk.execute(user);
                Intent result = new Intent();
                result.putExtra("USER_EXTRA", user);
                setResult(RESULT_OK, result);
                finish();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

    }

    private boolean verifyPseudo() { //TODO : vérifiaction de pseudo
        String pseudo = mPseudo.getText().toString();
        boolean res= false;
        if(!pseudo.equals("")){
            user.pseudo=pseudo;
            res=true;
        }
        return res;
    }

    private boolean verifyEmail() { //TODO : vérifiaction d'email
        String email = mEmail.getText().toString();
        boolean res= false;
        if(!email.equals("")){
            user.email=email;
            res=true;
        }
        return res;
    }
    private boolean verifyPassword() { // TODO vérification de password
        String password = mPassword.getText().toString();
        boolean res= false;
        if(!password.equals("")){
            user.password=LoginActivity.encode(password);
            res=true;
        }
        return res;
    }


}
