package fr.jlm2017.pap.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import kotlin.Triple;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import fr.jlm2017.pap.GeoLocalisation.GeoData;
import fr.jlm2017.pap.GeoLocalisation.GetForwardLoc;
import fr.jlm2017.pap.GeoLocalisation.GetReverseLoc;
import fr.jlm2017.pap.MongoDB.Militant;
import fr.jlm2017.pap.MongoDB.SaveAsyncTask;
import fr.jlm2017.pap.MongoDB.Porte;
import fr.jlm2017.pap.R;
import fr.jlm2017.pap.utils.ButtonAnimationJLM;


public class Check extends AppCompatActivity {

    public static int USER_CHANGED_CODE =1259;
    // other variables
    //animation tools 1/////////////////////////
    private Handler handler;
    private int timing ;
    //animation tools 1- end/////////////////////////
    private String appartNum, formatedFullAdress;
    private String streetNum, streetName, complementAdress, cityName;
    private double latitude, longitude, adressLatitude, adressLongitude;
    private boolean isAppart, isOpen, comeBack, numFilled, positionPrecise;
    private Militant user;

    //views variables
    private ImageButton menuImage;
    private ButtonAnimationJLM mAddDoorAnimation, mGPSAnimation;
    private CircularProgressButton mAddDoor, mGPS;
    private EditText mAdress, mNumS, mNumA, mCity, mComplement;
    private TextInputLayout appartLayout;
    private CheckBox checkDoorOpen, checkComeBack, checkAppart, checkGPS;

    //GPS variables

    private LocationManager locationManager;
    private LocationListener locationListener;

    // toasts
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void showLongToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void initValues () {
        numFilled = false;
        isOpen = false;
        comeBack = false;
        positionPrecise = false;
        complementAdress = "";
        latitude = 0;
        longitude = 0;
        streetNum = "";
        appartNum = "";
        formatedFullAdress = "";
    }

    public void resetUI () {
        checkDoorOpen.setChecked(false);
        changeBackgroundButton(mGPS,R.drawable.button_shape_default_rounded);
        if(isAppart) {
            mNumA.setText("");
        }
        else {
            adressLatitude=0;
            adressLongitude=0;
            mNumS.setText("");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //animation venant de l'activité précédente
        setupWindowAnimations();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_check);
        handler = new Handler();
        timing = getResources().getInteger(R.integer.decontracting_time_animation);
        //widgets
        mAddDoor = (CircularProgressButton) findViewById(R.id.AddDoor);
        mAddDoorAnimation = new ButtonAnimationJLM(mAddDoor);
        mGPS = (CircularProgressButton) findViewById(R.id.GPSButton);
        mGPSAnimation = new ButtonAnimationJLM(mGPS);
        mAdress = (EditText) findViewById(R.id.Adress);
        mNumA = (EditText) findViewById(R.id.DoorNum);
        appartLayout = (TextInputLayout) findViewById(R.id.DoorNumLayout) ;
        mNumS = (EditText) findViewById(R.id.StreetNum);
        mCity = (EditText) findViewById(R.id.Ville);
        mComplement = (EditText) findViewById(R.id.complement);
        checkDoorOpen = (CheckBox) findViewById(R.id.CheckOpenDoor);
        checkComeBack = (CheckBox) findViewById(R.id.CheckComeBack);
        checkAppart = (CheckBox) findViewById(R.id.CheckAppart);
        checkGPS =  (CheckBox) findViewById(R.id.GPSActive);
        menuImage = (ImageButton)  findViewById(R.id.menuButtonImage);
        //values
        appartLayout.setAlpha(0);
        mNumA.setEnabled(false);
        checkComeBack.setEnabled(false);
        checkComeBack.setAlpha(0);
        isAppart = false;

        user = getIntent().getParcelableExtra("USER_EXTRA");

        //GPS
        GPSInit();
        configureGPSBUttons();

        //events
        addAttemptListener();
        UIListeners();
    }

    private void setupWindowAnimations() { // TODO animations de transitions
/*        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        Fade fade = new Fade();
        fade.setDuration(getResources().getInteger(R.integer.transition_time_between_activites_fade));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(fade);
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(user.pseudo.equals("")) dialogFirstConnection();
        initValues();
        resetUI();
        checkGPS.setChecked(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!user.pseudo.equals(""))checkGPS.setChecked(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkGPS.setChecked(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // On vérifie tout d'abord à quel intent on fait référence ici à l'aide de notre identifiant
        if (requestCode == USER_CHANGED_CODE) {
            // On vérifie aussi que l'opération s'est bien déroulée
            switch(resultCode) {
                case RESULT_OK :  user = data.getParcelableExtra("USER_EXTRA"); return;
                default : return;
            }

        }
    }

    private void dialogFirstConnection() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_first_connection)
                .setTitle("Première connexion");
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Intent goUpdate = new Intent(Check.this, UpdateUserActivity.class);
                goUpdate.putExtra("USER_EXTRA", user);
                goUpdate.putExtra("USER_FIRST",true);
                Check.this.startActivityForResult(goUpdate,USER_CHANGED_CODE);
            }
        });
        AlertDialog box = builder.create();
        box.show();
    }

    // MAIN USE

    private void addAttemptListener() {
        mAddDoor.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                mAddDoorAnimation.startAnimation();
                disableUI();

                streetNum = mNumS.getText().toString().trim();
                appartNum =  mNumA.getText().toString().trim();
                streetName = mAdress.getText().toString().trim();
                cityName = mCity.getText().toString().trim();
                complementAdress = mComplement.getText().toString().trim();
                if (!streetNum.equals("")) {
                    if (!streetName.equals("")) {
                        if(!cityName.equals("")) {
                            if (!isAppart || !appartNum.equals("")) {

                                //création de la porte et envoi dans la base
                                if (!checkGPS.isChecked()) {
                                    GetForwardLoc geolocForward = new GetForwardLoc() {
                                        @Override
                                        public void onResponseReceived(Pair<ArrayList<GeoData>, Boolean> result) {
                                            validationPorteGPS(this,result);
                                        }
                                    };
                                    geolocForward.execute(new Triple<>(streetNum, streetName, cityName));
                                }
                                else {
                                    if(isAppart || !complementAdress.equals("")){
                                        formatedFullAdress= formatedFullAdress+ " ( " +(isAppart? "Apt n° "+appartNum + " " : "") + complementAdress + " )";
                                    }

                                    Porte porte = new Porte(formatedFullAdress,streetNum,appartNum,complementAdress,streetName, cityName, isOpen, comeBack, adressLatitude, adressLongitude);
                                    savePorte(porte);
                                }

                            } else {
                                //animation tools 2/////////////////////////
                                mAddDoorAnimation.WrongButtonAnimation();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mNumA.setError("Veuillez rentrer un numéro d'appartement");
                                        mNumA.requestFocus();
                                        enableUI();
                                    }
                                }, timing);
                                //animation tools 2-end/////////////////////////
                            }
                        }
                        else {
                            //animation tools 2/////////////////////////
                            mAddDoorAnimation.WrongButtonAnimation();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mCity.setError("Veuillez rentrer une ville");
                                    mCity.requestFocus();
                                    enableUI();
                                }
                            }, timing);
                            //animation tools 2-end/////////////////////////
                        }
                    } else {
                        //animation tools 2/////////////////////////
                        mAddDoorAnimation.WrongButtonAnimation();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mAdress.setError("Veuillez rentrer une adresse");
                                mAdress.requestFocus();
                                enableUI();
                            }
                        }, timing);
                        //animation tools 2-end/////////////////////////
                    }
                } else {
                    //animation tools 2/////////////////////////
                    mAddDoorAnimation.WrongButtonAnimation();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mNumS.setError("Veuillez rentrer le numéro");
                            mNumS.requestFocus();
                            enableUI();
                        }
                    }, timing);
                    //animation tools 2-end/////////////////////////
                }
            }
        });
    }

    private void validationPorteGPS(GetForwardLoc getForwardLoc, Pair<ArrayList<GeoData>, Boolean> geoloc) {
        if(geoloc.second) {
            final ArrayList<GeoData> localisations = geoloc.first;
            if(localisations.isEmpty()) {
                if(getForwardLoc.geo.fr) {
                    mAddDoorAnimation.WrongButtonAnimation();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showLongToast("Position non-répertoriée, êtes vous bien en France ?");
                            enableUI();
                        }
                    }, timing);
                }
                else {
                    mAddDoorAnimation.WrongButtonAnimation();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showLongToast("Position non-répertoriée");
                            enableUI();
                        }
                    }, timing);
                }
            }
            else if (localisations.size()==1) { // un seul résultat, on le met dans le formulaire
                final GeoData data = localisations.get(0);
                adressLatitude=data.geometry.lat;
                adressLongitude=data.geometry.lng;
                formatedFullAdress=data.formatted;
                if(!data.components.house_number.equals("")) {
                    streetNum=data.components.house_number;
                }
                else{//si aucun numéro dans le geocoding, on garde celui du formulaire qui est plus exact et on regarde si on a pas des coordonnées GPS
                    if(latitude!=0&&longitude!=0){
                        if(GeoData.distanceGPS(latitude,longitude,adressLatitude,adressLongitude)<500) { //si on est a + de 500m de la rue on ne prend pas nos coordonnées mais celles de la rue (cas ou l'on veut permettre de signaler à distance qu'on a toqué quelquepart et que cet endroit n'est pas répertorié)
                            adressLatitude = latitude;
                            adressLongitude = longitude;
                        }
                    }
                    formatedFullAdress=streetNum+", "+formatedFullAdress;
                }
                streetName=data.components.road;
                cityName=data.components.city;
                if(isAppart || !complementAdress.equals("")){
                    formatedFullAdress= formatedFullAdress+ " ( " +(isAppart? "Apt n° "+appartNum + " " : "") + complementAdress + " )";
                }

                Porte porte = new Porte(formatedFullAdress,streetNum,appartNum,complementAdress,streetName, cityName, isOpen, comeBack, adressLatitude, adressLongitude);
                savePorte(porte);

            }
            else {                              // plusieurs résultats, on choisit dans une dialog
                ArrayList<String> liste_locations = new ArrayList<>();
                for (GeoData data : localisations)
                {
                    liste_locations.add(data.formatted);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(mGPS.getContext(),android.R.layout.simple_selectable_list_item,liste_locations);
                AlertDialog.Builder builder = new AlertDialog.Builder(mGPS.getContext());
                builder.setTitle(R.string.dialog_title);
                builder.setCancelable(false);
                builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        GeoData selected = localisations.get(i);
                        adressLatitude=selected.geometry.lat;
                        adressLongitude=selected.geometry.lng;
                        formatedFullAdress=selected.formatted;
                        if(!selected.components.house_number.equals("")) {
                            streetNum=selected.components.house_number;
                        }
                        else{//si aucun numéro dans le geocoding, on garde celui du formulaire qui est plus exact et on regarde si on a pas des coordonnées GPS
                            if(latitude!=0&&longitude!=0){
                                adressLatitude=latitude;
                                adressLongitude=longitude;
                            }
                            formatedFullAdress=streetNum+", "+formatedFullAdress;
                        }
                        streetName=selected.components.road;
                        cityName=selected.components.city;
                        if(isAppart || !complementAdress.equals("")){
                            formatedFullAdress= formatedFullAdress+ " ( " +(isAppart? "Apt n° "+appartNum + " " : "") + complementAdress + " )";
                        }

                        Porte porte = new Porte(formatedFullAdress,streetNum,appartNum,complementAdress,streetName, cityName, isOpen, comeBack, adressLatitude, adressLongitude);
                        savePorte(porte);
                        dialogInterface.cancel();
                    }
                });
                final AlertDialog box = builder.create();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        box.show();
                    }
                }, timing);
            }
        }
        else {
            mAddDoorAnimation.WrongButtonAnimation();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showLongToast("Erreur, êtes-vous connecté à internet ?");
                    enableUI();
                }
            }, timing);
        }
    }

    private void savePorte(final Porte porte) { // finalisation du addDoor Listener

        SaveAsyncTask saveDoor = new SaveAsyncTask() {
            @Override
            public void onResponseReceived(Pair<Boolean, String> result) {
                if(!result.first) {// connexion ratée
                    mAddDoorAnimation.WrongButtonAnimation();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String message = "Connexion à la base impossible";
                            showLongToast(message);
                            enableUI();
                        }
                    }, timing);
                    return;
                }
                //animation tools 2/////////////////////////
                mAddDoorAnimation.OKButtonAndRevertAnimation();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String message = "Vous avez bien toqué au : " + porte.adresseResume;
                        showLongToast(message);
                        resetUI();
                        initValues();
                        enableUI();
                    }
                }, timing);
                //animation tools 2-end/////////////////////////
            }
        };
        saveDoor.execute(porte);
    }

    //MENU

    private Menu m = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) // creation du menu
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_admin, menu);
        if(!user.admin) menu.getItem(0).setEnabled(false);
        m = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) // options du menu
    {
        switch(item.getItemId())
        {
            case R.id.logout:
                finish();
                return true;
            case R.id.adminLog:
                Intent goAdmin = new Intent(Check.this, AdminActivity.class);
                goAdmin.putExtra("USER_EXTRA", user);
                Check.this.startActivity(goAdmin);
                return true;
            case R.id.updateUser:
                Intent goUpdate = new Intent(Check.this, UpdateUserActivity.class);
                goUpdate.putExtra("USER_EXTRA", user);
                goUpdate.putExtra("USER_FIRST",false);
                Check.this.startActivityForResult(goUpdate,USER_CHANGED_CODE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //MENUFIN//

    //GPS//
    private void GPSInit() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) { // appelé juste une fois au début
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    String message = "Position récupérée (imprécise) : "+ latitude + " ; " + longitude;
                    showToast(message);

                    // on résactive l'envoi
                    setEnabledButton(mAddDoor,R.string.envoyer ,R.drawable.button_shape_default_rounded ,true);
                    setEnabledButton(mGPS,R.string.gps_use ,R.drawable.button_shape_default_rounded ,true);
                }
                else {
                    if(mGPS.isClickable()) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        String message = "Position récupérée : " + latitude + " ; " + longitude;
                        showToast(message);
                        if (!positionPrecise) {
                            positionPrecise = true;
                            setEnabledButton(mGPS, R.string.gps_use_precise, R.drawable.button_green_shape_default_rounded, true);
                        }
                    }
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
    }

    private void configureGPSBUttons() {
        checkGPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { //checkbox d'utilisation du GPS Intégré
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (ActivityCompat.checkSelfPermission(checkGPS.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(checkGPS.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                                    , 10);
                        }
                        return;
                    }
                    // on désactive l'envoi tant que le GPS n'est pas ok, on affiche le bouton mais il n'est pas encore actif
                    setEnabledButton(mAddDoor,R.string.attente ,R.drawable.button_cancel_shape_default_rounded ,false);
                    mGPS.setVisibility(View.VISIBLE);
                    setEnabledButton(mGPS,R.string.attente ,R.drawable.button_cancel_shape_default_rounded ,false);
                    locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,30,locationListener);
                }
                else {      // si on décoche GPS, on arrete les updates et on cache le bouton. L'envoi est activé
                    locationManager.removeUpdates(locationListener);
                    mGPS.setVisibility(View.INVISIBLE);
                    setEnabledButton(mGPS,R.string.gps_use ,R.drawable.button_shape_default_rounded ,false);
                    setEnabledButton(mAddDoor,R.string.envoyer ,R.drawable.button_shape_default_rounded ,true);
                    positionPrecise = false;
                }
            }
        });


        mGPS.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) { // bouton de remplissage du formulaire par géocoding

            disableUI();
            setEnabledButton(mAddDoor,R.string.attente ,R.drawable.button_cancel_shape_default_rounded ,false);
            mGPSAnimation.startAnimation();

            final GetReverseLoc rev = new GetReverseLoc() {
                @Override
                public void onResponseReceived(Pair<ArrayList<GeoData>, Boolean> geoloc) {
                    if(geoloc.second) {
                        final ArrayList<GeoData> localisations = geoloc.first;
                        if(localisations.isEmpty()) {
                            if(this.geo.fr) {
                                mGPSAnimation.WrongButtonAnimation();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        showLongToast("Position non-répertoriée, êtes vous bien en France ?");
                                    }
                                }, timing);
                            }
                            else {
                                mGPSAnimation.WrongButtonAnimation();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        showLongToast("Position non-répertoriée");
                                    }
                                }, timing);
                            }
                        }
                        else if (localisations.size()==1) { // un seul résultat, on le met dans le formulaire
                            final GeoData data = localisations.get(0);
                            mGPSAnimation.OKButtonAndRevertAnimation();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mNumS.setText(data.components.house_number);
                                    mAdress.setText(data.components.road);
                                    mCity.setText(data.components.city);
                                    adressLatitude=data.geometry.lat;
                                    adressLongitude=data.geometry.lng;
                                    formatedFullAdress=data.formatted;
                                }
                            }, timing);
                        }
                        else {                              // plusieurs résultats, on choisit dans une dialog
                            CharSequence[] liste_locations = new CharSequence[localisations.size()];
                            int i =0;
                            for (GeoData data : localisations)
                            {
                                liste_locations[i] =data.formatted;
                                i++;
                            }

                            AlertDialog.Builder builder = new AlertDialog.Builder(mGPS.getContext());
                            builder.setTitle(R.string.dialog_title);
                            builder.setCancelable(false);
                            builder.setSingleChoiceItems(liste_locations, 0, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    GeoData selected = localisations.get(i);
                                    mNumS.setText(selected.components.house_number);
                                    mAdress.setText(selected.components.road);
                                    mCity.setText(selected.components.city);
                                    adressLatitude=selected.geometry.lat;
                                    adressLongitude=selected.geometry.lng;
                                    formatedFullAdress=selected.formatted;
                                    dialogInterface.cancel();
                                }
                            });
                            final AlertDialog box = builder.create();
                            mGPSAnimation.OKButtonAndRevertAnimation();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    box.show();
                                }
                            }, timing);
                        }
                    }
                    else {
                        mGPSAnimation.WrongButtonAnimation();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showLongToast("Erreur, êtes-vous connecté à internet ?");
                            }
                        }, timing);
                    }

                    enableUI();
                    setEnabledButton(mAddDoor,R.string.envoyer ,R.drawable.button_shape_default_rounded ,true);

                }
            };
            rev.execute(Pair.create(latitude, longitude));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10 :
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkGPS.setChecked(false);
                    checkGPS.callOnClick();
                }
                else {
                    checkGPS.setChecked(false);
                }
        }
    }

    //GPSFIN//

    //UI//

    private void UIListeners() {
        ///////////// Si on modifie un des 3 champs, on devra vérifier les coordonnées GPS
        mNumS.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                int imeActionId = EditorInfo.IME_ACTION_DONE;
                if (id == imeActionId) {
                    checkGPS.setChecked(false);
                }
                return false;
            }
        });
        mAdress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                int imeActionId = EditorInfo.IME_ACTION_DONE;
                if (id == imeActionId) {
                    checkGPS.setChecked(false);
                }
                return false;
            }
        });
        mCity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                int imeActionId = EditorInfo.IME_ACTION_DONE;
                if (id == imeActionId) {
                    checkGPS.setChecked(false);
                }
                return false;
            }
        });

        /////////////
        checkAppart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isAppart = b;
                mNumA.setEnabled(b);
                if (b) {
                    appartLayout.setAlpha(1);
                } else {
                    appartLayout.setAlpha(0);
                }
            }
        });

        checkDoorOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isOpen = b;
                checkComeBack.setEnabled(b);
                if (b) {
                    checkComeBack.setAlpha(1);
                } else {
                    checkComeBack.setAlpha(0);
                    checkComeBack.setChecked(false);
                }
            }
        });

        checkComeBack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                comeBack = b;
            }
        });

        menuImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(menuImage.getContext(),menuImage);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId())
                        {
                            case R.id.logout:
                                finish();
                                return true;
                            case R.id.adminLog:
                                Intent goAdmin = new Intent(Check.this, AdminActivity.class);
                                goAdmin.putExtra("USER_EXTRA", user);
                                Check.this.startActivity(goAdmin);
                                return true;
                            case R.id.updateUser:
                                Intent goUpdate = new Intent(Check.this, UpdateUserActivity.class);
                                goUpdate.putExtra("USER_EXTRA", user);
                                goUpdate.putExtra("USER_FIRST",false);
                                Check.this.startActivityForResult(goUpdate,USER_CHANGED_CODE);
                                return true;
                        }
                        return true;
                    }
                });
                MenuInflater inflate = popup.getMenuInflater();
                inflate.inflate(R.menu.menu_admin, popup.getMenu());
                if(!user.admin) popup.getMenu().getItem(0).setEnabled(false);
                popup.show();
            }
        });

    }

    private void setEnabledButton(CircularProgressButton button, int id_text, int id_drawable, boolean enabled) {
        if(button.isClickable()){
            button.setText(getResources().getString(id_text));
            changeBackgroundButton(button,id_drawable);
            button.setEnabled(enabled);
        }
    }

    private void enableUI() {
        mAdress.setEnabled(true);
        mCity.setEnabled(true);
        mNumS.setEnabled(true);
        checkGPS.setEnabled(true);
        checkAppart.setEnabled(true);
        if(checkAppart.isChecked())mNumA.setEnabled(true);
        if(checkDoorOpen.isChecked())checkComeBack.setEnabled(true);
        checkDoorOpen.setEnabled(true);
    }

    private void disableUI() {
        mAdress.setEnabled(false);
        mCity.setEnabled(false);
        mNumS.setEnabled(false);
        checkGPS.setEnabled(false);
        checkAppart.setEnabled(false);
        if(checkAppart.isChecked())mNumA.setEnabled(false);
        if(checkDoorOpen.isChecked())checkComeBack.setEnabled(false);
        checkDoorOpen.setEnabled(false);
    }

    public static void changeBackgroundButton (CircularProgressButton button, int drawable_ID)
    {
        Drawable roundDrawable;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            roundDrawable = button.getContext().getResources().getDrawable(drawable_ID);
            button.setBackgroundDrawable(roundDrawable);
        } else {
            roundDrawable = button.getContext().getResources().getDrawable(drawable_ID, null);
            button.setBackground(roundDrawable);
        }
    }





}

