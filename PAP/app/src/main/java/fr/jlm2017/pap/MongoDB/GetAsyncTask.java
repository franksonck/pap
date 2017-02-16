package fr.jlm2017.pap.MongoDB;

/**
 * Created by thoma on 14/02/2017.
 */

import android.os.AsyncTask;
import android.util.Pair;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;



public class GetAsyncTask extends AsyncTask<String, Void, Pair<ArrayList<Pair<DataObject,String>>, Boolean>> {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public OkHttpClient client;

    @Override
    protected Pair<ArrayList<Pair<DataObject,String>>, Boolean> doInBackground(String... arg0) {

        ArrayList<Pair<DataObject,String>> objectsGet = new ArrayList<>();
        String contact = arg0[0];

        QueryBuilder qb = new QueryBuilder();

        client = new OkHttpClient();

        boolean success =false;
        Pair<String, Boolean> result =null;
        String response = "";
        try {
            String URL = qb.buildObjectsSaveOrGetURL(contact);
            result = getFromDB(URL);
            response = result.first;
        } catch (IOException e) {
            e.printStackTrace();
        }
        // TODO gérer les erreurs serveur + gérer les duplicata
        System.out.println(response);
        if(result.second){
            String updatedJson = "{\"data\" : " + response + "}";
            if(contact == "militants") {
                DataWrapperMilitant dataWrapper = DataWrapperMilitant.fromJson(updatedJson);
                for (DataWrapperMilitant.BigDataMilitant big : dataWrapper.data) {
                    objectsGet.add(Pair.<DataObject, String>create(big.militant,big._id.$oid));
                }
            }
            else {
                DataWrapperPortes dataWrapper = DataWrapperPortes.fromJson(updatedJson);
                for (DataWrapperPortes.BigDataPorte big : dataWrapper.data) {
                    objectsGet.add(Pair.<DataObject, String>create(big.porte,big._id.$oid));
                }
            }

            success =true;
        }
        return Pair.create(objectsGet,success);

    }

    Pair<String, Boolean> getFromDB(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return Pair.create(response.body().string(),response.isSuccessful());
        }
        catch (Exception e) {
            return null;
        }
    }

}
