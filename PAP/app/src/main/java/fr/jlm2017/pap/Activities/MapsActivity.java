package fr.jlm2017.pap.Activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import fr.jlm2017.pap.MongoDB.MapsMarkersAsyncTask;
import fr.jlm2017.pap.MongoDB.Porte;
import fr.jlm2017.pap.MongoDB.myDoorsAsyncTask;
import fr.jlm2017.pap.R;

public class MapsActivity extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<Marker> markersClose,markersUser;
    private ArrayList<MarkerOptions> markersCloseOptn, markersUserOptn;
    private double latitudeCam, longitudeCam;
    private String app_token, user_id;
    private boolean rdy=false;
    private float zoomMax, zoomFR, zoom;
    private int nb_doors_user = 0;
    private TextView number;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_map, container, false);
        app_token=((Main) getActivity()).app_token;
        user_id=((Main) getActivity()).user_id;
        markersClose = new ArrayList<>();
        markersCloseOptn = new ArrayList<>();
        markersUser = new ArrayList<>();
        markersUserOptn = new ArrayList<>();
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
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MapFragment fragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);
    }

    // reception du Broadcaster si on vient d'ajouter un militant dans l'autre Fragment

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
                zoom=zoomMax;
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(port.latitude, port.longitude),zoom));
//                //System.out.println("ID caché : "+mili.id_);
            }
            if ("DATA_MAJ".equals(intent.getAction()))
            {
                double latitude  = intent.getDoubleExtra("latitude",0);
                double longitude  = intent.getDoubleExtra("longitude",0);
                UpdateMarkarList(latitude,longitude);
//                //System.out.println("ID caché : "+mili.id_);
            }
        }
    };

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
        rdy=true;
        // Add a marker in Sydney and move the camera
        for(MarkerOptions m : markersCloseOptn) {
           markersClose.add (mMap.addMarker(m));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitudeCam, longitudeCam),zoom));//on se place au centre de la France
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
    }

    public void UpdateMarkarList (double lat, double lng) {
        latitudeCam=lat;
        longitudeCam=lng;
        zoom=zoomMax;
        EmptyMarkers(markersClose,markersCloseOptn);
        MapsMarkersAsyncTask tsk = new MapsMarkersAsyncTask() {
            @Override
            public void onResponseReceived(Pair<ArrayList<Porte>, Boolean> result) {
                if(result.second) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitudeCam, longitudeCam),zoom));
                    for(Porte p : result.first) {
                        if(!p.user_id.equals(user_id)) addMarker(p);
                    }
                }
                else {
                    Toast.makeText(getActivity(), "échec de récupération des proches", Toast.LENGTH_LONG).show();
                    System.out.println("échec de récupération des proches");
                }
            }
        };
        tsk.execute(Pair.create(Pair.create(lat,lng),app_token));
    }

    private void EmptyMarkers(ArrayList<Marker> markersClos,ArrayList<MarkerOptions> markersCloseOpt) {
        for(Marker m : markersClos) {
            m.remove();
        }
        markersClos.clear();
        markersCloseOpt.clear();
    }

    public void UpdateMarkarListUser () {
        nb_doors_user=0;
        EmptyMarkers(markersUser,markersUserOptn);
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
        BitmapDescriptor btm ;
        if(p.ouverte) btm = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        else btm= BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        MarkerOptions m = new MarkerOptions().position(new LatLng(p.latitude, p.longitude)).title(p.adresseResume)
                .icon(btm);
        markersCloseOptn.add(m);
        if(rdy) {
            markersClose.add(mMap.addMarker(m));
        }
    }

    private void addUserMarker(Porte p) {
        BitmapDescriptor btm ;
        if(p.ouverte) btm = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        else btm= BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        MarkerOptions m = new MarkerOptions().position(new LatLng(p.latitude, p.longitude)).title(p.adresseResume)
                .icon(btm);
        markersUserOptn.add(m);
        if(rdy) {
            markersUser.add(mMap.addMarker(m));
        }
    }
}
