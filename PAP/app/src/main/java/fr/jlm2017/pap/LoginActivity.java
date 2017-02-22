package fr.jlm2017.pap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import android.util.Pair;
import android.widget.Toast;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import fr.jlm2017.pap.MongoDB.DataObject;
import fr.jlm2017.pap.MongoDB.GetAsyncTask;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    public static boolean installed = false;
    private static String SavedEmailsPreference = "SavedEmails_";

    public static boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }
    public boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 0;
    }
    private Militant regMilitant;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private ButtonAnimationJLM mEmailSignInButtonAnimated;
    private static int USER_LOG_OUT = 1002;

    private void setupWindowAnimations() { //TODO animations de transition
/*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
            Slide slide = new Slide();
            slide.setDuration(getResources().getInteger(R.integer.transition_time_between_activites));
            getWindow().setExitTransition(slide);
        }*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setupWindowAnimations();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                int imeActionId = EditorInfo.IME_ACTION_DONE;
                if (id == imeActionId) {
                    mPasswordView.clearFocus();
                    hideKeyboard(LoginActivity.this);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mEmailSignInButtonAnimated.button.callOnClick();
                        }
                    }, 10);

                    return true;
                }
                return false;
            }
        });

        mEmailSignInButtonAnimated = new ButtonAnimationJLM((CircularProgressButton) findViewById(R.id.email_sign_in_button));
        mEmailSignInButtonAnimated.button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mEmailSignInButtonAnimated.button.startAnimation();
                attemptLogin();
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    private void populateAutoComplete() {
        List<String> mails =getSharedPreferenceStringList(this);
        addEmailsToAutoComplete(mails);
    }

    public static void addMailSharedPreference(Context pContext, String pData) {
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(pContext);
        SharedPreferences.Editor editor = shared.edit();
        int size = shared.getInt(SavedEmailsPreference + "size", 0);
        for (int i = 0; i < size; i++) {
            if(shared.getString(SavedEmailsPreference + i, "").equals(pData))return; // on vérifie que le mail n'est pas deja sauvé
        }
        editor.putInt(SavedEmailsPreference + "size",size+1);
        editor.putString(SavedEmailsPreference + size,pData);
        editor.apply();
    }

    public static List<String> getSharedPreferenceStringList(Context pContext) {
        SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(pContext);
        int size = shared.getInt(SavedEmailsPreference + "size", 0);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(shared.getString(SavedEmailsPreference + i, ""));
        }
        return list;
    }
    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        //animation tools 1/////////////////////////
        final Handler handler = new Handler();
        int timing = getResources().getInteger(R.integer.decontracting_time_animation);
        //animation tools 1- end/////////////////////////
        // Check for a valid email address.
        if (!isEmailValid(email)) {
            //animation tools 2/////////////////////////
            mEmailSignInButtonAnimated.WrongButtonAnimation();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mEmailView.setError(getString(R.string.error_invalid_email));
                    mEmailView.requestFocus();
            }
            }, timing);
            //animation tools 2-end/////////////////////////
            return;

        } else if (TextUtils.isEmpty(password)) {
            //animation tools 2/////////////////////////
            mEmailSignInButtonAnimated.WrongButtonAnimation();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPasswordView.setError(getString(R.string.error_field_required));
                    mPasswordView.requestFocus();
                }
            }, timing);
            //animation tools 2-end/////////////////////////
            return;

        } else  {
            int correct  = isEmailAndPasswordOK(email,password); // vérification avec la BDD
            switch(correct) {
                case -1 :
                    //animation tools 2/////////////////////////
                    mEmailSignInButtonAnimated.WrongButtonAnimation();
                    handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mEmailView.setError(getString(R.string.error_unknown_email));
                        mEmailView.requestFocus();
                    }
                    }, timing);
                    //animation tools 2-end/////////////////////////
                    return;
                case -2 :
                    mEmailSignInButtonAnimated.WrongButtonAnimation();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPasswordView.setError(getString(R.string.error_incorrect_password));
                            mPasswordView.requestFocus();
                        }
                    }, timing);
                    return;

                case 0 :
                    addMailSharedPreference(this,email); // l'email est ok, on essaie de l'ajouter
                    mEmailSignInButtonAnimated.OKButtonAnimation();
                    timing = getResources().getInteger(R.integer.loading_end_time_animation);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            final Intent logged = new Intent(LoginActivity.this,Check.class); // login OK on passe à CHeck
                            logged.putExtra("USER_EXTRA", regMilitant); // on envoie a Check les données utilisateur
                            LoginActivity.this.startActivityForResult(logged,USER_LOG_OUT);
                        }
                    }, timing);
                    return;

                default : return;
            }

        }
    }


    private int isEmailAndPasswordOK(String email, String password) {
        GetAsyncTask tsk = new GetAsyncTask();
        try {
            ArrayList<Pair<String,String>> indexValuesCouples = new ArrayList<>();
            indexValuesCouples.add(Pair.create("email",email));
            Pair<ArrayList<DataObject>, Boolean> result = tsk.execute(Pair.create("militants",indexValuesCouples)).get();
            if(result.second) {// on a bien récupéré les données
                if(result.first.isEmpty()) return -1;
                Militant mili = (Militant) result.first.get(0);
                String hidden = Encoder.encode(password);
                System.out.println(" pwd : "+ hidden + "\n stored : "+mili.password);

                if(Encoder.decode(password,mili.password)){
                    regMilitant=mili;
                    return 0; // tout ok
                }
                else {
                    return -2; // pwd faux
                }
            }
            else {
                mEmailSignInButtonAnimated.WrongButtonAnimation();
                Toast.makeText(this,"connexion à la BDD impossible, êtes vous bien connecté à internet ?",Toast.LENGTH_LONG);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return -3;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // On vérifie tout d'abord à quel intent on fait référence ici à l'aide de notre identifiant
        if (requestCode == USER_LOG_OUT) {
            // On vérifie aussi que l'opération s'est bien déroulée
            mEmailSignInButtonAnimated.revert(R.integer.decontracting_time_animation_onRestart);
        }
    }


}

