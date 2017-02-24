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


public abstract class SaveAsyncTask extends AsyncTask<DataObject, Void, Pair<Boolean,String>> implements InterfaceReceivedData<Pair<Boolean,String>> {

    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public OkHttpClient client;

    @Override
    protected Pair<Boolean,String> doInBackground(DataObject... arg0) {

        DataObject contact = arg0[0];

        QueryBuilder qb = new QueryBuilder();

        client = new OkHttpClient();

        String json = qb.createObject(contact);
        Pair<String, Boolean> result =null;
        String response = "";
        try {
            String URL = qb.buildObjectsSaveURL(contact);
            result = post(URL,json);
            response =result.first;
        } catch (IOException e) {
            e.printStackTrace();
        }

//        System.out.println(response);
        String id="";
        assert result != null;
        if(result.second && contact.getClass()== Militant.class) id= DataWrapperMilitant.IDfromJson(response); // récupère l'ID renvoyé par le server
        return Pair.create(result.second, id) ;

    }

    private Pair<String, Boolean> post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
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
    protected void onPostExecute(Pair<Boolean, String> arrayListBooleanPair) {
        onResponseReceived(arrayListBooleanPair);
    }

}
