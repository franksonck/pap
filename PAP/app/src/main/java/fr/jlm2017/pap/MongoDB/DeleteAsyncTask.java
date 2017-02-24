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


public abstract class DeleteAsyncTask extends AsyncTask<DataObject, Void, Boolean> implements InterfaceReceivedData<Boolean> {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public OkHttpClient client;

    @Override
    protected Boolean doInBackground(DataObject... arg0) { //on supprime l'objet d'id "obj.id" du execute(obj)

        DataObject contact = arg0[0];

        QueryBuilder qb = new QueryBuilder();

        client = new OkHttpClient();

        Pair<String, Boolean> result =null;
        String response = "";
        try {
            String URL = qb.buildObjectsUpdateURL(contact);
            result = delete(URL);
            response = result.first;
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(response);
        assert result != null;
        return result.second;


    }

    private Pair<String, Boolean> delete(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();
        try (Response response = client.newCall(request).execute()) {
            return Pair.create(response.body().string(),response.isSuccessful());
        }
        catch (Exception e) {
            return Pair.create("",false);
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        onResponseReceived(aBoolean);
    }

}
