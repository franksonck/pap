package fr.jlm2017.pap;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.audiofx.BassBoost;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Check extends AppCompatActivity {

    // other variables
    private int streetNum, appartNum;
    private String streetName, bisTer;
    private double latitude, longitude;
    private boolean isAppart, isOpen, comeBack, numFilled, streetFilled, positionOK;

    // DB variables
    private DatabaseReference mDoorbase;
    private DatabaseReference doors;

    //views variables
    private Button mAddDoor, mGPS;
    private EditText mAdress, mNumS, mNumA;
    private CheckBox bisButton, terButton;
    private CheckBox checkDoorOpen, checkComeBack, checkAppart;
    private TextView appartFix;

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
        streetFilled = false;
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
        mNumS = (EditText) findViewById(R.id.StreetNum);
        bisButton = (CheckBox) findViewById(R.id.ButtonBis);
        terButton = (CheckBox) findViewById(R.id.ButtonTer);
        checkDoorOpen = (CheckBox) findViewById(R.id.CheckOpenDoor);
        checkComeBack = (CheckBox) findViewById(R.id.CheckComeBack);
        checkAppart = (CheckBox) findViewById(R.id.CheckAppart);
        appartFix = (TextView) findViewById(R.id.numDoorFix);

        //values
        mNumA.setAlpha(0);
        mNumA.setEnabled(false);
        appartFix.setAlpha(0);
        checkComeBack.setEnabled(false);
        checkComeBack.setAlpha(0);
        isAppart = false;
        initValues();

        //Database
        mDoorbase = FirebaseDatabase.getInstance().getReference();
        doors = mDoorbase.child("Doors");


        //GPS
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                positionOK = true;
                String message = "Position récupérée : "+ latitude + " ; " + longitude;
                showToast(message);
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


        configureGPSBUtton();


        //events


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
                streetFilled = (!streetName.equals(""));

                if (numFilled) {
                    if (streetFilled) {
                        if (!isAppart || appartNum != 0) {
                            String street = streetNum + bisTer + ", " + streetName;
                            if (isAppart) street = "Apt n° " + appartNum + ", " + street;
                            DatabaseReference door = doors.push();
                            door.child("Latitude").setValue(latitude);
                            door.child("Longitude").setValue(longitude);
                            door.child("Adresse").setValue(street);
                            door.child("Open").setValue(isOpen);
                            door.child("Worth").setValue(comeBack);
                            String message = "Vous avez bien toqué au : " + street;

                            showLongToast(message);
                            resetUI();
                            initValues();

                        } else {
                            String message = "Veuillez rentrer un numéro d'appartement ou décocher \"Appartement\"";
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


        checkAppart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isAppart = b;
                mNumA.setEnabled(b);
                if (b) {
                    mNumA.setAlpha(1);
                    appartFix.setAlpha(1);
                } else {
                    mNumA.setAlpha(0);
                    appartFix.setAlpha(0);
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

