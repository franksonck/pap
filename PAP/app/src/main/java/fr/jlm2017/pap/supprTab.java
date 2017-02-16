package fr.jlm2017.pap;

/**
 * Created by thoma on 15/02/2017.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import fr.jlm2017.pap.MongoDB.DataObject;
import fr.jlm2017.pap.MongoDB.DeleteAsyncTask;
import fr.jlm2017.pap.MongoDB.GetAsyncTask;

public class supprTab extends Fragment{

    ArrayList<Militant> listItems = new ArrayList<>();
    ArrayList<String> listItemsString = new ArrayList<>();
    ArrayAdapter<String> adapter;
    public int nbusers;
    ListView layout;
    TextView nbView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        nbusers=0;
        View rootView = inflater.inflate(R.layout.tab_admin_2, container, false);
        layout = (ListView) rootView.findViewById(R.id.militantsList);
        nbView = (TextView) rootView.findViewById(R.id.nbUsers);
        adapter = new ArrayAdapter<String>(this.getContext(),android.R.layout.simple_selectable_list_item,listItemsString);
        layout.setAdapter(adapter);
        refreshList();

        layout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> list, View v, int pos, long id) {
                // Your code for item clicks
                String message =("Vous avez supprimé :"+listItemsString.get(pos));
                DeleteAsyncTask tsk = new DeleteAsyncTask();
                try {
                    if(tsk.execute(listItems.get(pos)).get()){
                        Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
                        deleteMilitant(pos);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        Button refreshButton = (Button) rootView.findViewById(R.id.refreshButton);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                refreshList();
            }
        });

        return rootView;
    }


    public void refreshList() {
        listItems.clear();
        listItemsString.clear();
        nbusers = 0;
        adapter.notifyDataSetInvalidated();
        GetAsyncTask tsk = new GetAsyncTask();
        Pair<ArrayList<Pair<DataObject,String>>, Boolean> result = null;
        try {
            result = tsk.execute("militants").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println(result.toString());

        for(Pair<DataObject, String> x: result.first){

            Militant m = (Militant) x.first;
            m.id_ = x.second;
            addMilitant(m);
        }


    }

    private void addMilitant(Militant m) {
        listItems.add(m);
        listItemsString.add(m.email);
        nbusers++;
        nbView.setText(String.valueOf(nbusers));
        adapter.notifyDataSetChanged();
    }

    private void deleteMilitant(int pos) {
        listItems.remove(pos);
        listItemsString.remove(pos);
        nbusers--;
        nbView.setText(String.valueOf(nbusers));
        adapter.notifyDataSetChanged();
    }

    public void showLongToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if ("DATA_ACTION".equals(intent.getAction()) == true)
            {
                Militant mili  = intent.getParcelableExtra("DATA_EXTRA");
                System.out.println("ID caché : "+mili.id_);
                addMilitant(mili);
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter("DATA_ACTION"));
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
    }
}
