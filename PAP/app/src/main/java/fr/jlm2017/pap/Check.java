package fr.jlm2017.pap;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import fr.jlm2017.pap.MongoDB.SaveAsyncTask;


public class Check extends AppCompatActivity {

    // other variables
    private int streetNum, appartNum;
    private String streetName, bisTer, cityName;
    private double latitude, longitude;
    private boolean isAppart, isOpen, comeBack, numFilled, positionOK;

    //views variables
    private Button mAddDoor, mGPS;
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
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_check);

        //widgets
        mAddDoor = (Button) findViewById(R.id.AddDoor);
        mGPS = (Button) findViewById(R.id.GPSButton);
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

        //GPS
        GPSInit();
        configureGPSBUtton();

        //events
        addAttemptListener();
        UIListeners();

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

                if (numFilled) {
                    if (!streetName.equals("")) {
                        if(!cityName.equals("")) {
                            if (!isAppart || appartNum != 0) {
                                String street = streetNum + bisTer + ", " + streetName;
                                if (isAppart) street = "Apt n° " + appartNum + ", " + street;

                                //création de la porte et envoi dans la base
                                Porte porte = new Porte(street, cityName, isOpen, comeBack, latitude, longitude);

                                SaveAsyncTask saveDoor = new SaveAsyncTask();
                                saveDoor.execute(porte);

                                String message = "Vous avez bien toqué au : " + street;
                                showLongToast(message);
                                resetUI();
                                initValues();

                            } else {
                                String message = "Veuillez rentrer un numéro d'appartement ou décocher \"Appartement\"";
                                showToast(message);
                            }
                        }
                        else {
                            String message = "Veuillez rentrer une ville";
                            showToast(message);
                        }
                    } else {
                        String message = "Veuillez rentrer une adresse";
                        showToast(message);
                    }
                } else {
                    String message = "Veuillez rentrer le numéro correspondant à l'adresse";
                    showToast(message);
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
        m = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.logout:
                Intent logout = new Intent(Check.this, LoginActivity.class);
                Check.this.startActivity(logout);
                return true;
            case R.id.adminLog:
                Intent goAdmin = new Intent(Check.this, AdminActivity.class);
                Check.this.startActivity(goAdmin);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void GPSInit() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                positionOK = true;
                String message = "Position récupérée : "+ latitude + " ; " + longitude;
                showToast(message);

                // on résactive l'envoi
                mAddDoor.setText("ENVOYER");
                mAddDoor.setEnabled(true);
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




}

