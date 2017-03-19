package fr.jlm2017.pap.utils;

/**
 * Created by thoma on 16/03/2017.
 * Project : Porte à Porte pour JLM2017
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import fr.jlm2017.pap.MongoDB.Porte;
import fr.jlm2017.pap.R;

public class MyMapMarker implements ClusterItem {
    private Porte p;
    private BitmapDescriptor mIcon;
    private Bitmap mClusteredIcon;


    public MyMapMarker(Context ctx, Porte port) {
        p=port;
        if(p.ouverte){
            mIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mClusteredIcon = MyMarkerRenderer.writeTextOnDisk(ctx, ctx.getResources().getColor(R.color.JLMgreen,null),"",1);
            }
            else {
                mClusteredIcon = MyMarkerRenderer.writeTextOnDisk(ctx, ctx.getResources().getColor(R.color.JLMgreen),"",1);
            }
        }
        else {
            mIcon= BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mClusteredIcon = MyMarkerRenderer.writeTextOnDisk(ctx, ctx.getResources().getColor(R.color.JLMred,null),"",1);
            }
            else {
                mClusteredIcon = MyMarkerRenderer.writeTextOnDisk(ctx, ctx.getResources().getColor(R.color.JLMred),"",1);
            }
        }
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(p.latitude,p.longitude);
    }

    public String getTitle() { return p.adresseResume; }

    public Porte getPorte() {return p;}

    public BitmapDescriptor getIcon() { return mIcon; }

    public Bitmap getClusteredIcon() { return mClusteredIcon; }

    public void setIcon(BitmapDescriptor btm) {
        mIcon = btm;
    }

}
