package fr.jlm2017.pap.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.ArrayList;
import java.util.List;

import fr.jlm2017.pap.R;

/**
 * Created by thoma on 16/03/2017.
 * Project : Porte à Porte pour JLM2017
 */

public class MyMarkerRenderer extends DefaultClusterRenderer<MyMapMarker> {
    private static int GREEN = Color.GREEN ;
    private static int RED = Color.RED ;
    private static int YELLOW = Color.YELLOW;
    private Context myContext;

    public MyMarkerRenderer(Context context, GoogleMap map, ClusterManager<MyMapMarker> clusterManager) {
        super(context, map, clusterManager);
        myContext = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            GREEN = context.getResources().getColor(R.color.JLMgreen,null);
            RED = context.getResources().getColor(R.color.JLMred,null);
            YELLOW = context.getResources().getColor(R.color.JLMyellow,null);
        }
        else {
            GREEN = context.getResources().getColor(R.color.JLMgreen);
            RED = context.getResources().getColor(R.color.JLMred);
            YELLOW = context.getResources().getColor(R.color.JLMyellow);
        }
    }

    @Override
    protected void onBeforeClusterItemRendered(MyMapMarker mymarker, MarkerOptions markerOptions) {
        // Draw a single person.
        // Set the info window to show their name.
        markerOptions.icon(mymarker.getIcon()).title(mymarker.getTitle());
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<MyMapMarker> cluster, MarkerOptions markerOptions) {
        // pour un cluster on définit la couleur en fonction des couleurs des portes internes
        int color = YELLOW;
        int num = cluster.getItems().size();
        for (MyMapMarker mymarker : cluster.getItems()) {
            if (!mymarker.getPorte().ouverte) {
                if (color == GREEN) {
                    color = YELLOW;
                    break;
                } else {
                    color=RED;
                }
            }
            else {
                if (color == RED) {
                    color = YELLOW;
                    break;
                } else {
                    color=GREEN;
                }
            }
        }

        BitmapDescriptor mIcon = BitmapDescriptorFactory.fromBitmap(writeTextOnDisk(myContext,color, String.valueOf(num),4));
        markerOptions.icon(mIcon);
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        // Always render clusters.
        return cluster.getSize() > 1;
    }

    public static Bitmap writeTextOnDisk(Context mContext, int color, String text, int strokeWidth) {

        int x = convertToPixels(mContext,10);
        int y = convertToPixels(mContext,10);
        int width = convertToPixels(mContext,50);
        int height = convertToPixels(mContext,50);

        //draw background full disk
        ShapeDrawable mDrawable = new ShapeDrawable(new OvalShape());
        mDrawable.getPaint().setColor(color);
        mDrawable.setBounds(x, y, x + width, y + height);

        Bitmap bm = Bitmap.createBitmap(2*x + width, 2*x + height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        mDrawable.draw(canvas);
        // add stroke
        mDrawable.getPaint().setColor(Color.BLACK);
        mDrawable.getPaint().setStyle(Paint.Style.STROKE);
        mDrawable.getPaint().setStrokeWidth(convertToPixels(mContext,strokeWidth));
        mDrawable.getPaint().setAntiAlias(true);
        mDrawable.draw(canvas);

        Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(convertToPixels(mContext, 15));

        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        //If the text is bigger than the canvas , reduce the font size
        if(textRect.width() >= (canvas.getWidth() - 4))     //the padding on either sides is considered as 4, so as to appropriately fit in the text
            paint.setTextSize(convertToPixels(mContext, 7));        //Scaling needs to be used for different dpi's

        //Calculate the positions
        int xPos = (canvas.getWidth() / 2) - 2;     //-2 is for regulating the x position offset

        //"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
        int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)) ;

        canvas.drawText(text, xPos, yPos, paint);

        return  bm;
    }

    public static int convertToPixels(Context context, int nDP)
    {
        final float conversionScale = context.getResources().getDisplayMetrics().density;

        return (int) ((nDP * conversionScale) + 0.5f) ;

    }


}
