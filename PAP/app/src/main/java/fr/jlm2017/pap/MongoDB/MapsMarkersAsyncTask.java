package fr.jlm2017.pap.MongoDB;

import android.os.AsyncTask;
import android.util.Pair;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by thoma on 15/03/2017.
 * Project : Porte à Porte pour JLM2017
 */

public abstract class MapsMarkersAsyncTask extends AsyncTask<Pair<Pair<Double,Double>,String>, Void, Pair<ArrayList<Porte>, Boolean>> implements InterfaceReceivedData<Pair<ArrayList<Porte>, Boolean>> {

    public OkHttpClient client;

    @Override
    protected final Pair<ArrayList<Porte>, Boolean> doInBackground(Pair<Pair<Double,Double>,String>... arg0) {

        Pair<Pair<Double,Double>,String> args = arg0[0];
        String token = args.second;
        QueryBuilder qb = new QueryBuilder();
        // request with Token
        client = new OkHttpClient();
        Pair<String, Boolean> response = Pair.create("",false);
        try {
            String URL = qb.buildProcheURL(args.first);
            response = getFromDB(URL,token);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert response != null;
        Pair<ArrayList<Porte>, Boolean> result = Pair.create(new ArrayList<Porte>(),response.second);
        if(response.second) {
            String updatedJson = "{\"data\" : " + response.first + "}";

            DataWrapperPortes dataWrapper = DataWrapperPortes.fromJson(updatedJson);
            for (DataWrapperPortes.BigDataPorte big : dataWrapper.data) {
                result.first.add(new Porte(big));
            }

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
    protected void onPostExecute(Pair<ArrayList<Porte>, Boolean> pair) {
        onResponseReceived(pair);
    }
}
