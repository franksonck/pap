package fr.jlm2017.pap.utils;

import android.os.AsyncTask;
import android.util.Pair;

import java.io.IOException;

import fr.jlm2017.pap.MongoDB.InterfaceReceivedData;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by thoma on 12/03/2017.
 * Project : Porte à Porte pour JLM2017
 */
public abstract class OAuthAsyncTask extends AsyncTask<Pair<String,String>, Void, Pair<String, Boolean>> implements InterfaceReceivedData<Pair<String, Boolean>> {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public OkHttpClient client;

    @SafeVarargs
    @Override
    protected final Pair<String, Boolean> doInBackground(Pair<String,String>... arg0) {

        Pair<String,String> contact = arg0[0];


        client = new OkHttpClient();

        Pair<String, Boolean> result = Pair.create("",false);
        try {
            String URL = "OAuthURL"; // TODO OAuth
            result = getFromDB(URL);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;

    }

    private Pair<String, Boolean> getFromDB(String url) throws IOException {
        Request request = new Request.Builder()
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
