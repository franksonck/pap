package fr.jlm2017.pap;

import android.content.Intent;
import android.os.Handler;
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

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import fr.jlm2017.pap.MongoDB.GetAsyncTask;
import fr.jlm2017.pap.MongoDB.UpdateAsyncTask;

public class UpdateUserActivity extends AppCompatActivity {

    private EditText mPseudo, mEmail, mPassword;
    private CheckedTextView mAdmin;
    private Button mCancel;
    private CircularProgressButton mSave;
    private ButtonAnimationJLM mSaveAnimation;
    private Militant user;
    private String passwordREGEX = ""; //TODO password regex
    private int passwdLong = 6; // taille minimum d'un password
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);

        mPseudo = (EditText) findViewById(R.id.PseudoUpdate);
        mEmail = (EditText) findViewById(R.id.emailUpdate);
        mPassword = (EditText) findViewById(R.id.passwordUpdate);
        mAdmin = (CheckedTextView) findViewById(R.id.isAdminUpdate);
        mSave = (CircularProgressButton) findViewById(R.id.SaveUpdate);
        mSaveAnimation = new ButtonAnimationJLM(mSave);
        mCancel = (Button) findViewById(R.id.cancelUpdate);
        Intent origin = getIntent();
        user = origin.getParcelableExtra("USER_EXTRA");

        mPseudo.setText(user.pseudo);
        mEmail.setText(user.email);
        if(!user.admin) mAdmin.setCheckMarkDrawable(R.drawable.btn_check_buttonless_off);

        mSave.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                mSave.startAnimation();
                //animation tools 1/////////////////////////
                final Handler handler = new Handler();
                int timing = getResources().getInteger(R.integer.decontracting_time_animation);
                //animation tools 1- end/////////////////////////

                if(!verifyPseudo()) {
                    //animation tools 2/////////////////////////
                    mSaveAnimation.WrongButtonAnimation();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPseudo.setError(getString(R.string.error_field_required));
                            mPseudo.requestFocus();
                        }
                    }, timing);
                    return;
                    //animation tools 2-end/////////////////////////
                    }
                if(!verifyEmail()) {
                    mSaveAnimation.WrongButtonAnimation();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mEmail.setError("Email invalide ou déjà pris");
                            mEmail.requestFocus();
                        }
                    }, timing);
                    return;
                    }
                if (!verifyPassword()){
                    mSaveAnimation.WrongButtonAnimation();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPassword.setError("Minimum "+passwdLong+" caractères");
                            mPassword.requestFocus();
                        }
                    }, timing);
                    return;
                }
                UpdateAsyncTask tsk = new UpdateAsyncTask();
                try {
                    boolean finished = tsk.execute(user).get();
                    if(finished ) {
                        mSaveAnimation.OKButtonAndRevertAnimation();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent result = new Intent();
                                result.putExtra("USER_EXTRA", user);
                                setResult(RESULT_OK, result);
                                finish();
                            }
                        }, timing);
                    }
                    else {
                        mSaveAnimation.WrongButtonAnimation();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(),"Erreur de mise à jour, problème de connexion ?",Toast.LENGTH_SHORT).show();
                            }
                        }, timing);

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
        if (origin.getBooleanExtra("USER_FIRST",false)) { //en cas de premiere connection on interdit le cancel
            mCancel.setEnabled(false);
            mCancel.setVisibility(View.INVISIBLE);
        }


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
                user.password =  Encoder.encode(password);
                return true;
            }
            else {
                return false;
            }
        }
        return true;
    }


}
