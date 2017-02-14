package fr.jlm2017.pap.MongoDB;

/**
 * Created by thoma on 14/02/2017.
 */
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import android.os.AsyncTask;



public class SaveAsyncTask extends AsyncTask<DBObject, Void, Boolean> {

    @Override
    protected Boolean doInBackground(DBObject... arg0) {
        try
        {
            DBObject contact = arg0[0];

            QueryBuilder qb = new QueryBuilder();

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost request = new HttpPost(qb.buildObjectsSaveURL());

            StringEntity params =new StringEntity(qb.createObject(contact));
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);

            if(response.getStatusLine().getStatusCode()<205)
            {
                return true;
            }
            else
            {
                return false;
            }
        } catch (Exception e) {
            //e.getCause();
            String val = e.getMessage();
            String val2 = val;
            return false;
        }
    }

}
