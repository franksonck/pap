package fr.jlm2017.pap;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import fr.jlm2017.pap.MongoDB.SaveAsyncTask;


public class Check extends AppCompatActivity {

    public static int USER_CHANGED_CODE =1259;
    // other variables
    private int streetNum, appartNum;
    private String streetName, bisTer, cityName;
    private double latitude, longitude;
    private boolean isAppart, isOpen, comeBack, numFilled, positionOK;
    private Militant user;

    //views variables
    private ButtonAnimationJLM mAddDoorAnimation, mGPSmAddDoorAnimation;
    private CircularProgressButton mAddDoor, mGPS;
    private EditText mAdress, mNumS, mNumA, mCity;
    private TextInputLayout appartLayout;
    private CheckBox bisButton, terButton;
    private CheckBox checkDoorOpen, checkComeBack, checkAppart;

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
        positionOK = false;
        bisTer = "";
        latitude = 0;
        longitude = 0;
        streetNum = 0;
        appartNum = 0;
    }

    public void resetUI () {
        checkDoorOpen.setChecked(false);
        if(isAppart) {
            mNumA.setText("");
        }
        else {
            mNumS.setText("");
            bisButton.setChecked(false);
            terButton.setChecked(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //animation venant de l'activité précédente
        setupWindowAnimations();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_check);

        //widgets
        mAddDoor = (CircularProgressButton) findViewById(R.id.AddDoor);
        mAddDoorAnimation = new ButtonAnimationJLM(mAddDoor);
        mGPS = (CircularProgressButton) findViewById(R.id.GPSButton);
        mGPSmAddDoorAnimation = new ButtonAnimationJLM(mGPS);
        mAdress = (EditText) findViewById(R.id.Adress);
        mNumA = (EditText) findViewById(R.id.DoorNum);
        appartLayout = (TextInputLayout) findViewById(R.id.DoorNumLayout) ;
        mNumS = (EditText) findViewById(R.id.StreetNum);
        mCity = (EditText) findViewById(R.id.Ville);
        bisButton = (CheckBox) findViewById(R.id.ButtonBis);
        terButton = (CheckBox) findViewById(R.id.ButtonTer);
        checkDoorOpen = (CheckBox) findViewById(R.id.CheckOpenDoor);
        checkComeBack = (CheckBox) findViewById(R.id.CheckComeBack);
        checkAppart = (CheckBox) findViewById(R.id.CheckAppart);

        //values
        appartLayout.setAlpha(0);
        mNumA.setEnabled(false);
        checkComeBack.setEnabled(false);
        checkComeBack.setAlpha(0);
        isAppart = false;
        initValues();

        user = getIntent().getParcelableExtra("USER_EXTRA");

        //GPS
        GPSInit();
        configureGPSBUtton();

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

    private void UIListeners() {
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

        bisButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    terButton.setChecked(false);
                    bisTer = " bis";
                }
                else {
                    bisTer = "";
                }
            }
        });

        terButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    bisButton.setChecked(false);
                    bisTer = " ter";
                }
                else {
                    bisTer = "";
                }
            }
        });
    }

    private void addAttemptListener() {
        mAddDoor.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                mAddDoor.startAnimation();

                String num = mNumS.getText().toString().trim();
                if (!num.equals("")) {
                    streetNum = Integer.parseInt(num);
                    numFilled = true;
                } else {
                    numFilled = false;
                }

                if (isAppart) {
                    num = mNumA.getText().toString().trim();
                    if (!num.equals("")) {
                        appartNum = Integer.parseInt(num);
                    }
                }

                streetName = mAdress.getText().toString();
                cityName = mCity.getText().toString();

                //animation tools 1/////////////////////////
                final Handler handler = new Handler();
                int timing = getResources().getInteger(R.integer.decontracting_time_animation);
                //animation tools 1- end/////////////////////////
                if (numFilled) {
                    if (!streetName.equals("")) {
                        if(!cityName.equals("")) {
                            if (!isAppart || appartNum != 0) {
                                String street = streetNum + bisTer + ", " + streetName;
                                if (isAppart) street = "Apt n° " + appartNum + ", " + street;

                                //création de la porte et envoi dans la base
                                Porte porte = new Porte(street, cityName, isOpen, comeBack, latitude, longitude);

                                SaveAsyncTask saveDoor = new SaveAsyncTask();
                                try {
                                    Pair<Boolean , String> result = saveDoor.execute(porte).get();
                                    if(!result.first) {// connexion ratée
                                        mAddDoorAnimation.WrongButtonAnimation();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                String message = "Connexion à la base impossible";
                                                showLongToast(message);
                                            }
                                        }, timing);
                                        return;
                                    }
                                    //animation tools 2/////////////////////////
                                    mAddDoorAnimation.OKButtonAndRevertAnimation();
                                    final String finalStreet = street;
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            String message = "Vous avez bien toqué au : " + finalStreet;
                                            showLongToast(message);
                                            resetUI();
                                            initValues();
                                        }
                                    }, timing);
                                    //animation tools 2-end/////////////////////////
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                //animation tools 2/////////////////////////
                                mAddDoorAnimation.WrongButtonAnimation();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mNumA.setError("Veuillez rentrer un numéro d'appartement");
                                        mNumA.requestFocus();
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
                        }
                    }, timing);
                    //animation tools 2-end/////////////////////////
                }
            }
        });
    }

    //MENU

    private Menu m = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_admin, menu);
        if(!user.admin) menu.getItem(0).setEnabled(false);
        m = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
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

    private void GPSInit() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //animation tools 1/////////////////////////
                final Handler handler = new Handler();
                int timing = getResources().getInteger(R.integer.decontracting_time_animation);
                //animation tools 1- end/////////////////////////
                //animation tools 2/////////////////////////
                mGPSmAddDoorAnimation.OKButtonAndRevertAnimation();
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        positionOK = true;
                        String message = "Position récupérée : "+ latitude + " ; " + longitude;
                        showToast(message);

                        // on résactive l'envoi
                        mAddDoor.setText("ENVOYER");
                        mAddDoor.setEnabled(true);
                    }
                }, timing);
                //animation tools 2-end/////////////////////////

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

    private void configureGPSBUtton() {
        mGPS.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                                , 10);
                    }
                    return;
                }
                // on désactive l'envoi tant que le GPS n'est pas ok
                mAddDoor.setText("En attente du GPS");
                mAddDoor.setEnabled(false);
                mGPS.startAnimation();
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null); // on essaie avec la précision max
                if(!positionOK) locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10 : if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                configureGPSBUtton();
            }
                return;
        }
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


}

