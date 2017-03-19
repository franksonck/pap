package fr.jlm2017.pap.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import fr.jlm2017.pap.MongoDB.MapsMarkersAsyncTask;
import fr.jlm2017.pap.MongoDB.Porte;
import fr.jlm2017.pap.MongoDB.myDoorsAsyncTask;
import fr.jlm2017.pap.R;
import fr.jlm2017.pap.utils.ButtonAnimationJLM;
import fr.jlm2017.pap.utils.MathTool;
import fr.jlm2017.pap.utils.MyMapMarker;
import fr.jlm2017.pap.utils.MyMarkerRenderer;

public class MapsActivity extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<MyMapMarker> markersClose,markersUser;
    private double latitudeCam, longitudeCam;
    private String app_token, user_id;
    private boolean rdy=false;
    private float zoomMax, zoomFR, zoom;
    private int nb_doors_user = 0,timing;
    private TextView number;
    private ClusterManager<MyMapMarker> mClusterManager;
    private CircularProgressButton mRefresh;
    private ButtonAnimationJLM mRefreshAnimation;
    private Handler handler;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_map, container, false);
        //Animations tools for refresh Button
        mRefresh = (CircularProgressButton) rootView.findViewById(R.id.refresh_button);
        mRefreshAnimation = new ButtonAnimationJLM(mRefresh);
        mRefresh.setVisibility(View.INVISIBLE);
        mRefresh.setEnabled(false);
        handler = new Handler();
        timing = getResources().getInteger(R.integer.decontracting_time_animation);
        //ids
        app_token=((Main) getActivity()).app_token;
        user_id=((Main) getActivity()).user_id;
        //markers
        markersClose = new ArrayList<>();
        markersUser = new ArrayList<>();
        latitudeCam=46.258451;
        longitudeCam=2.213055;
        zoomFR = 5.0f;
        zoomMax=19.0f ;
        nb_doors_user=0;
        zoom =zoomFR;
        rdy=false;
        number = (TextView) rootView.findViewById(R.id.user_doors);
        UpdateMarkarListUser();
        number.setText(String.valueOf(nb_doors_user));

        mRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               UpdateMarkarList(latitudeCam,longitudeCam,zoom);
            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MapFragment fragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter("DATA_ACTION"));
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter("DATA_MAJ"));
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        MapFragment mapFragment = (MapFragment) getActivity()
                .getFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null)
            getActivity().getFragmentManager().beginTransaction()
                    .remove(mapFragment).commit();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markersClose or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMaxZoomPreference(zoomMax);
        rdy=true;
        setUpClusterer();
        // si des markers ont deja été ajoutés, avant que la carte soit initialisée, on les traite
        for(MyMapMarker m : markersClose) {
            mClusterManager.addItem(m);
        }
        for(MyMapMarker m : markersUser) {
            mClusterManager.addItem(m);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitudeCam, longitudeCam),zoom));//on se place au centre de la France
    }

    private void setUpClusterer() {

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<>(getContext(), mMap);
        mClusterManager.setRenderer(new MyMarkerRenderer(getContext(),mMap,mClusterManager));
        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyMapMarker>() {
            @Override
            public boolean onClusterClick(Cluster<MyMapMarker> cluster) {
                if(mMap.getCameraPosition().zoom == zoomMax) {
                    //si zoom max, on ouvre un pop up avec les différents titres cliquables
                    ArrayList<MyMapMarker> arr= new ArrayList<MyMapMarker>();
                    for(MyMapMarker m : cluster.getItems()) {
                        arr.add(m);
                    }
                    MyMapsAdapter  adapter = new MyMapsAdapter(getContext(),arr);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(R.string.maps_dialog_title);
                    builder.setCancelable(true);
                    builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    final AlertDialog box = builder.create();
                    box.show();
                }
                else {
                    // sinon on zoom jusqu'à déclusteriser

                    LatLngBounds.Builder builder = LatLngBounds.builder();
                    for (MyMapMarker item : cluster.getItems()) {
                        builder.include(item.getPosition());
                    }
                    final LatLngBounds bounds = builder.build();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
                }
                return true;
            }
        });
        // Point the map's listeners at the listeners implemented by the cluster
        // manager.

        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

    }

    public void UpdateMarkarList (double lat, double lng, float zoomB) {
        mRefresh.setEnabled(true);
        mRefresh.setVisibility(View.VISIBLE);
        latitudeCam=lat;
        longitudeCam=lng;
        zoom=zoomB;
        EmptyMarkers(markersClose);
        mRefresh.startAnimation();

        MapsMarkersAsyncTask tsk = new MapsMarkersAsyncTask() {
            @Override
            public void onResponseReceived(final Pair<ArrayList<Porte>, Boolean> result) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitudeCam, longitudeCam),zoom));
                if(result.second) {
                    mRefreshAnimation.OKButtonAndRevertAnimation();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            for(Porte p : result.first) {
                                if(!p.user_id.equals(user_id)) addMarker(p);
                            }
                            mRefresh.setText(getResources().getString(R.string.maj_proches));
                        }
                    }, timing);

                }
                else {
                    mRefreshAnimation.WrongButtonAnimation();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "échec de récupération des proches", Toast.LENGTH_LONG).show();
                            System.out.println("échec de récupération des proches");
                            mRefresh.setText("MAJ");
                        }
                    }, timing);

                }
            }
        };
        tsk.execute(Pair.create(Pair.create(lat,lng),app_token));
    }

    private void EmptyMarkers(ArrayList<MyMapMarker> markersClos) {
        if(rdy) {
            for (MyMapMarker m : markersClos) {
                mClusterManager.removeItem(m);
            }
        }
        markersClos.clear();
    }

    public void UpdateMarkarListUser () {
        nb_doors_user=0;
        EmptyMarkers(markersUser);
        myDoorsAsyncTask tskuser = new myDoorsAsyncTask() {
            @Override
            public void onResponseReceived(Pair<ArrayList<Porte>, Boolean> result) {
                if(result.second) {
                    for(Porte p : result.first) {
                        addUserMarker(p);
                        nb_doors_user++;
                    }
                    number.setText(String.valueOf(nb_doors_user));
                }
                else {
                    Toast.makeText(getActivity(), "échec de récupération des portes toquées par " + user_id, Toast.LENGTH_LONG).show();
                    System.out.println("échec de récupération des portes toquées par " + user_id);
                }
            }
        };
        tskuser.execute(Pair.create(user_id,app_token));
    }

    private void addMarker(Porte p) {
        MyMapMarker mrk = new MyMapMarker(getContext(),p);
        markersClose.add(mrk);
        if(rdy) {
            mClusterManager.addItem(mrk);
        }
    }

    private void addUserMarker(Porte p) {
        MyMapMarker mrk = new MyMapMarker(getContext(),p);
        markersUser.add(mrk);
        if(rdy) {
            mClusterManager.addItem(mrk);
        }
    }

    // reception du Broadcaster si on vient d'ajouter une porte dans l'autre Fragment

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if ("DATA_ACTION".equals(intent.getAction()))
            {
                Porte port  = intent.getParcelableExtra("DATA_EXTRA");
                addUserMarker(port);
                nb_doors_user++;
                number.setText(String.valueOf(nb_doors_user));
                UpdateMarkarList(port.latitude,port.longitude,zoomMax);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(port.latitude, port.longitude),zoom));
//                //System.out.println("ID caché : "+mili.id_);
            }
            if ("DATA_MAJ".equals(intent.getAction()))
            {
                double latitude  = intent.getDoubleExtra("latitude",0);
                double longitude  = intent.getDoubleExtra("longitude",0);
                UpdateMarkarList(latitude,longitude,zoom);
                if(rdy && !mMap.isMyLocationEnabled()) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }
                }
//                //System.out.println("ID caché : "+mili.id_);
            }
        }
    };

    private class MyMapsAdapter extends BaseAdapter {

        LayoutInflater mInflater;
        ArrayList<MyMapMarker> coll;

        public MyMapsAdapter(Context context, ArrayList<MyMapMarker> coll) {
            this.mInflater = LayoutInflater.from(context);
            this.coll = coll;
        }

        private class ViewHolder {
            public TextView mNom;
            public ImageView mImg;
        }

        @Override
        public int getCount() {
            return coll.size();
        }

        @Override
        public Object getItem(int i) {
            return coll.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ViewHolder holder = null;
            // Si la vue n'est pas recyclée
            if(convertView == null) {
                // On récupère le layout
                convertView  = mInflater.inflate(R.layout.maps_list_item, null);

                holder = new ViewHolder();
                // On place les widgets de notre layout dans le holder
                holder.mNom = (TextView) convertView.findViewById(R.id.mapsTxt);
                holder.mImg = (ImageView) convertView.findViewById(R.id.mapsImg);

                // puis on insère le holder en tant que tag dans le layout
                convertView.setTag(holder);
            } else {
                // Si on recycle la vue, on récupère son holder en tag
                holder = (ViewHolder)convertView.getTag();
            }

            // Dans tous les cas, on récupère le contact téléphonique concerné
            MyMapMarker c = (MyMapMarker)getItem(i);
            // Si cet élément existe vraiment…
            if(c != null) {
                // On place dans le holder les informations sur le contact
                holder.mNom.setText(c.getTitle());
                holder.mImg.setImageBitmap(c.getClusteredIcon());
            }
            return convertView;
        }
    }
}
