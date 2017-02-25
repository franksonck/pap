package fr.jlm2017.pap.MongoDB;

/**
 * Created by thoma on 14/02/2017.
 */

import android.os.AsyncTask;
import android.util.Pair;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;



public abstract class GetAsyncTask extends AsyncTask<Pair<String,ArrayList<Pair<String,String>>>, Void, Pair<ArrayList<DataObject>, Boolean>> implements InterfaceReceivedData<Pair<ArrayList<DataObject>, Boolean>> {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public OkHttpClient client;

    @SafeVarargs
    @Override
    protected final Pair<ArrayList<DataObject>, Boolean> doInBackground(Pair<String, ArrayList<Pair<String, String>>>... arg0) {

        ArrayList<DataObject> objectsGet = new ArrayList<>();
        Pair<String,ArrayList<Pair<String,String>>> contact = arg0[0];

        QueryBuilder qb = new QueryBuilder();

        client = new OkHttpClient();

        boolean success =false;
        Pair<String, Boolean> result =null;
        String response = "";
        try {
            String URL = qb.builObjectGetFilteredURL(contact.first,contact.second);
            result = getFromDB(URL);
            response = result.first;
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert result != null;
        if(result.second){
            String updatedJson = "{\"data\" : " + response + "}";
            if(contact.first.equals("militants")) {
                DataWrapperMilitant dataWrapper = DataWrapperMilitant.fromJson(updatedJson);
                for (DataWrapperMilitant.BigDataMilitant big : dataWrapper.data) {
                    objectsGet.add(big.militant);
                }
            }
            else {
                DataWrapperPortes dataWrapper = DataWrapperPortes.fromJson(updatedJson);
                for (DataWrapperPortes.BigDataPorte big : dataWrapper.data) {
                    objectsGet.add(big.porte);
                }
            }

            success =true;
        }
        return Pair.create(objectsGet,success);

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
    protected void onPostExecute(Pair<ArrayList<DataObject>, Boolean> pair) {
        onResponseReceived(pair);
    }

}
