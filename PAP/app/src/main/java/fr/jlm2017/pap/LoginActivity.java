package fr.jlm2017.pap;

import android.content.Context;
import android.content.Intent;

import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import android.util.Pair;
import android.widget.Toast;

import fr.jlm2017.pap.MongoDB.DataObject;
import fr.jlm2017.pap.MongoDB.GetAllAsyncTask;
import fr.jlm2017.pap.MongoDB.GetAsyncTask;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    public static boolean installed = false;
    public static int BACK_FROM_CHECK =11001;
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    public static boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    public boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 0;
    }

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world", "t@t:t"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private Militant regMilitant;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        addShortcut(getBaseContext()); // TODO vide, a corriger
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                int imeActionId = EditorInfo.IME_ACTION_DONE;
                if (id == imeActionId && mEmailView.getText().toString()!="") {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


    }

    // ** add shortcut to desktop

    public static void addShortcut(Context context)
    {
/*        Intent shortcutIntent = new Intent();
        shortcutIntent.setClassName("com.telespree.android.client", "com.telespree.android.client.ShortcutTest");
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "ShortcutTest");
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, R.drawable.com_facebook_button_icon));
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        intent.putExtra("duplicate", false);
        context.sendBroadcast(intent);*/
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

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            mEmailView.requestFocus(); return;
        } else if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            mPasswordView.requestFocus(); return;
        } else  {
            int correct  = isEmailAndPasswordOK(email,password);
            switch(correct) {
                case -1 : mEmailView.setError(getString(R.string.error_unknown_email));
                    mEmailView.requestFocus(); return;
                case -2 : mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus(); return;
                case 0 : Intent logged = new Intent(LoginActivity.this,Check.class);
                    logged.putExtra("USER_EXTRA", regMilitant);
                    LoginActivity.this.startActivity(logged); return;
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
                if(mili.password.equals(encode(password))){
                    regMilitant=mili;
                    return 0; // tout ok
                }
                else {
                    return -2; // pwd faux
                }
            }
            else {
                Toast.makeText(this,"connexion à la BDD impossible, êtes vous bien connecté à internet ?",Toast.LENGTH_LONG);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return -3;
    }

    // TODO : cryptage des passwords
    public static String encode(String password) {
        return password;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }



}

