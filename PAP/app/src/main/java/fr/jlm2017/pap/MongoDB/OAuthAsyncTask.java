package fr.jlm2017.pap.MongoDB;


import android.os.AsyncTask;
import android.util.Pair;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by thoma on 12/03/2017.
 * Project : Porte à Porte pour JLM2017
 */
public abstract class OAuthAsyncTask extends AsyncTask<String, Void, Pair<String, Boolean>> implements InterfaceReceivedData<Pair<String, Boolean>> {

    public OkHttpClient client;

    @Override
    protected final Pair<String, Boolean> doInBackground(String... arg0) {

        String token = arg0[0];
        QueryBuilder qb = new QueryBuilder();
        // request with Token
        client = new OkHttpClient();
        Pair<String, Boolean> result = Pair.create("",false);
        try {
            String URL = qb.buildOAuthURL();
            result = getFromDB(URL,token);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;

    }

    private Pair<String, Boolean> getFromDB(String url, String token) throws IOException {
        Request request = new Request.Builder()
                .header("Authorization",  "Bearer " + token)
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return Pair.create(response.body().string(),response.isSuccessful());
        }
        catch (Exception e) {
            return Pair.create("",false);
        }
    }

    @Override
    protected void onPostExecute(Pair<String, Boolean> pair) {
        onResponseReceived(pair);
    }
}
