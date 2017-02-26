package fr.jlm2017.pap.GeoLocalisation;

/**
 * Created by thoma on 14/02/2017.
 */

import android.os.AsyncTask;
import android.util.Pair;

import java.io.IOException;
import java.util.ArrayList;

import fr.jlm2017.pap.MongoDB.InterfaceReceivedData;
import kotlin.Triple;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public abstract class GetForwardLoc extends AsyncTask<Triple<String,String,String>, Void, Pair< ArrayList<GeoData>, Boolean>> implements InterfaceReceivedData<Pair< ArrayList<GeoData>, Boolean>>{


    public OkHttpClient client;
    public GeoDecoder geo;

    @SafeVarargs
    @Override
    protected final Pair< ArrayList<GeoData>, Boolean> doInBackground(Triple<String,String,String>... arg0) {

        Triple<String,String,String> location = arg0[0];
        ArrayList<GeoData> objectsGet = new ArrayList<>();

        client = new OkHttpClient();

        boolean success =false;
        Pair<String, Boolean> result =Pair.create("",false);
        String response = "";
        try {
            geo = new GeoDecoder();
            String URL = geo.getForwardURL(location.getFirst(),location.getSecond(),location.getThird());
            result = getFromURL(URL);
            response = result.first;
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println("réponse geodecoder forward : "+response);
        //System.out.println("success : "+result.second);

        if(result.second){
            GeoDataWrapper dataWrapper = GeoDataWrapper.fromJson(response);
            for (GeoData loc : dataWrapper.results) {
                objectsGet.add(loc);
            }
        }
        return Pair.create(objectsGet,result.second);

    }

    private Pair<String,Boolean> getFromURL(String url) throws IOException {

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
    protected void onPostExecute(Pair<ArrayList<GeoData>, Boolean> arrayListBooleanPair) {
        onResponseReceived(arrayListBooleanPair);
    }

}
