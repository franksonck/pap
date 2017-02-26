package fr.jlm2017.pap.MongoDB;

/**
 * Created by thoma on 14/02/2017.
 */

import android.os.AsyncTask;
import android.util.Pair;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public abstract class UpdateAsyncTask extends AsyncTask<DataObject, Void, Boolean> implements InterfaceReceivedData<Boolean> {

    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public OkHttpClient client;

    @Override
    protected Boolean doInBackground(DataObject... arg0) {

        DataObject contact = arg0[0];

        QueryBuilder qb = new QueryBuilder();

        client = new OkHttpClient();

        String json = qb.setObject(contact);
        Pair<String, Boolean> result =null;
        String response = "";
        try {
            String URL = qb.buildObjectsUpdateURL(contact);
            result = put(URL,json);
            response = result.first;
        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.println(response);
        assert result != null;
        return result.second;

    }

    private Pair<String, Boolean> put(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .put(body)
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
