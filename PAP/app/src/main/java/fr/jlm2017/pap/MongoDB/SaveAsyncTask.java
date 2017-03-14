package fr.jlm2017.pap.MongoDB;

/**
 * Created by thoma on 14/02/2017.
 */
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.os.AsyncTask;
import android.util.Pair;


public abstract class SaveAsyncTask extends AsyncTask<Pair<Porte, String>, Void, Boolean> implements InterfaceReceivedData<Boolean> {

    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public OkHttpClient client;

    @Override
    protected Boolean doInBackground(Pair<Porte, String>... arg0) {

        Pair<Porte, String> contact = arg0[0];

        QueryBuilder qb = new QueryBuilder();

        client = new OkHttpClient();

        String json = qb.createPorte(contact.first);
        Pair<String, Boolean> result =null;
        String response = "";
        try {
            String URL = qb.buildPorteSaveURL();
            result = post(URL,json, contact.second);
            response =result.first;
        } catch (IOException e) {
            e.printStackTrace();
        }

//      System.out.println(response);
        assert result != null;
        return result.second;

    }

    private Pair<String, Boolean> post(String url, String json, String token) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .header("Authorization",  "Bearer " + token)
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return Pair.create(response.body().string(),response.isSuccessful());
        }
        catch (Exception e) {
            return Pair.create("",false);
        }
    }

    @Override
    protected void onPostExecute(Boolean arrayListBooleanPair) {
        onResponseReceived(arrayListBooleanPair);
    }

}
