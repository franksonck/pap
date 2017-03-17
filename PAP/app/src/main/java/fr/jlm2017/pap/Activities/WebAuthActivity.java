package fr.jlm2017.pap.Activities;

import android.content.Intent;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.HashMap;
import java.util.Map;

import fr.jlm2017.pap.MongoDB.QueryBuilder;
import fr.jlm2017.pap.R;

public class WebAuthActivity extends AppCompatActivity {

    private WebView web_page;
    private final HashMap<String, String> authorizationReturnParameters = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_auth);
        String app_token = getIntent().getStringExtra("APP_TOKEN");
        web_page = (WebView) findViewById(R.id.web_page);

        web_page.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                /*
             * Is this the "redirect URI" that we are about to load? If so, parse it and don't load it. Parsing is based
             * on the # and the & characters, so make sure they are present before accepting this as a valid redirect
             * URI.
             */
                if (url.indexOf("papjlm://OKajout") == 0 && url.indexOf("&") != -1) {

                    parseRedirectURI(url);
                    view.loadUrl("about:blank");
                    Intent result = new Intent();
                    result.putExtra("USER_ID", authorizationReturnParameters.get("user_id"));
                    setResult(RESULT_OK, result);
                    finish();
                    return true;

                } else {

                /*
                 * The url we are about to load is not the "redirect URI", so load it. Note that if anything goes wrong
                 * with the authentication, the last message in the webview, and
                 * listener.displayResults(authorizationReturnParameters) will never be called.
                 */
                    view.loadUrl(url);
                    return true;
                }
            }
        });


        Map<String, String> headers = new HashMap<>();
        headers.put("device",app_token);
        web_page.loadUrl(new QueryBuilder().buildConnexionTokenHeaderURL(),headers);
    }

    /**
     * Parse a redirect url into its parameters. The string has the form
     * [redirectURI]#[param1]=[val1]&[param2]=[val2]...
     *
     * @param redirectUrl the redirect url to be parsed
     */
    private void parseRedirectURI(final String redirectUrl) {

        String[] params = redirectUrl.split("ajout")[1].split("&");

        for (String parameter : params) {
            if (parameter.contains("=")) {
                authorizationReturnParameters.put(parameter.split("=")[0], parameter.split("=")[1]);
            } else {
                authorizationReturnParameters.put(parameter, "");
            }
        }
    }
}
