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



public class SaveAsyncTask extends AsyncTask<DBObject, Void, Boolean> {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public OkHttpClient client;

    @Override
    protected Boolean doInBackground(DBObject... arg0) {

        DBObject contact = arg0[0];

        QueryBuilder qb = new QueryBuilder();

        client = new OkHttpClient();

        String json = qb.createObject(contact);
        String response = "";
        try {
            String URL = qb.buildObjectsSaveURL(contact);
            response = post(URL,json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // TODO gérer les erreurs serveur + gérer les duplicata
        System.out.println(response);
        return true;

    }

    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
        catch (Exception e) {
            return "";
        }
    }

}
