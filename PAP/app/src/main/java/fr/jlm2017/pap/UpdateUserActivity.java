package fr.jlm2017.pap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import fr.jlm2017.pap.MongoDB.GetAsyncTask;
import fr.jlm2017.pap.MongoDB.UpdateAsyncTask;

public class UpdateUserActivity extends AppCompatActivity {

    private EditText mPseudo, mEmail, mPassword;
    private CheckedTextView mAdmin;
    private Button mSave, mCancel;
    private Militant user;
    private String passwordREGEX = "";
    private int passwdLong = 6; // taille minimum d'un password
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
        Intent origin = getIntent();
        user = origin.getParcelableExtra("USER_EXTRA");

        mPseudo.setText(user.pseudo);
        mEmail.setText(user.email);
        if(!user.admin) mAdmin.setCheckMarkDrawable(R.drawable.btn_check_buttonless_off);

        mSave.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(!verifyPseudo()) { mPseudo.setError(getString(R.string.error_field_required));
                    mPseudo.requestFocus(); return; }
                if(!verifyEmail()) { mEmail.setError("Email invalide ou déjà pris");
                    mEmail.requestFocus(); return; }
                if (!verifyPassword()){ mPassword.setError("Minimum "+passwdLong+" caractères");
                    mPassword.requestFocus(); return; }
                UpdateAsyncTask tsk = new UpdateAsyncTask();
                try {
                    boolean finished = tsk.execute(user).get();
                    if(finished ) {
                        Intent result = new Intent();
                        result.putExtra("USER_EXTRA", user);
                        setResult(RESULT_OK, result);
                        finish();
                    }
                    else {
                        Toast.makeText(getBaseContext(),"Erreur de mise à jour, problème de connexion ?",Toast.LENGTH_SHORT).show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        mCancel.setEnabled(!origin.getBooleanExtra("USER_FIRST",false));

    }

    private boolean verifyPseudo() {
        String pseudo = mPseudo.getText().toString();
        boolean res= false;
        if(!pseudo.equals("")){
            user.pseudo=pseudo;
            res=true;
        }
        return res;
    }

    private boolean verifyEmail() {
        String email = mEmail.getText().toString();
        boolean res= false;
        if(LoginActivity.isEmailValid(email)){
            if(!user.email.equals(email)) {
                GetAsyncTask tsk = new GetAsyncTask();
                ArrayList<Pair<String,String>> ids = new ArrayList<>();
                ids.add(Pair.create("email",email));
                try {
                    if(!(tsk.execute(Pair.create("militants",ids)).get()).first.isEmpty()) return false; //on interdit de prendre le meme mail qu'un autre militant
                    user.email=email;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            res=true;
        }
        return res;
    }
    private boolean verifyPassword() { // TODO vérification de password
        String password = mPassword.getText().toString();
        if(!password.equals("")) {
            if(password.length() >= passwdLong) {
                user.password = LoginActivity.encode(password);
                return true;
            }
            else {
                return false;
            }
        }
        return true;
    }


}
