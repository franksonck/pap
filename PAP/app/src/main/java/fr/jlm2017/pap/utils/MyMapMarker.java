package fr.jlm2017.pap.utils;

/**
 * Created by thoma on 16/03/2017.
 * Project : Porte à Porte pour JLM2017
 */
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import fr.jlm2017.pap.MongoDB.Porte;

public class MyMapMarker implements ClusterItem {
    private Porte p;
    private BitmapDescriptor mIcon;


    public MyMapMarker(Porte port) {
        p=port;
        if(p.ouverte) mIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        else mIcon= BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(p.latitude,p.longitude);
    }

    public String getTitle() { return p.adresseResume; }

    public Porte getPorte() {return p;}

    public BitmapDescriptor getIcon() { return mIcon; }

    public void setIcon(BitmapDescriptor btm) {
        mIcon = btm;
    }

}
