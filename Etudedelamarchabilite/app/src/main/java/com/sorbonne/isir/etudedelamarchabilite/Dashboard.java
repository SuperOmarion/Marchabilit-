package com.sorbonne.isir.etudedelamarchabilite;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Dashboard extends AppCompatActivity implements View.OnClickListener {


    private LocationManager lm;

    private double latitude;
    private double longitude;
    private double altitude;
    private float accuracy;


    public static String Latitude;
    public static String Longitude;
    public static LocationManager locationManager;
    public static LocationListener listener;
    public static Calendar calendar;
    public static SimpleDateFormat simpleDateFormat;
    public static Boolean gps_actived;
    public static String date;
    public String id_user = "";
    public TextView nom;
    public Switch switcher;
    public Button disconnetion;
    public Button sendMail;
    public ImageButton photos;
    public ImageButton videos;
    public ImageButton voices;
    public ImageButton textes;
    public ProgressDialog progressDialog;
    public static String varBinevenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        nom = (TextView) findViewById(R.id.bienvenue);
        disconnetion = (Button) findViewById(R.id.disconnect);
        sendMail = (Button) findViewById(R.id.fab);
        photos = (ImageButton) findViewById(R.id.photo);
        videos = (ImageButton) findViewById(R.id.video);
        voices = (ImageButton) findViewById(R.id.voix);
        textes = (ImageButton) findViewById(R.id.texte);
        switcher = (Switch) findViewById(R.id.switch_gps);
        disconnetion.setOnClickListener(this);
        sendMail.setOnClickListener(this);
        switcher.setOnClickListener(this);
        switcher.setChecked(true);
        photos.setOnClickListener(this);
        videos.setOnClickListener(this);
        voices.setOnClickListener(this);
        textes.setOnClickListener(this);
        if (switcher.isChecked()) {
            gps_actived = true;
        } else {
            gps_actived = false;
        }
        varBinevenu = getIntent().getExtras().getString("user");
        nom.setText(varBinevenu);
        Toast.makeText(Dashboard.this, "Bienvenu " + varBinevenu, Toast.LENGTH_LONG).show();
        progressDialog = new ProgressDialog(Dashboard.this,
                R.style.MyTheme2);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(3);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.setMessage("Initialisation...");
        progressDialog.show();
        Coordonnees();
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Quiter l'application?")
                .setMessage("Êtes vous sûr de vouloir de quitter l'application?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        moveTaskToBack(true);
                        finish();
                        System.exit(0);
                    }
                }).create().show();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.disconnect:
                new AlertDialog.Builder(this)
                        .setTitle("Déconnexion")
                        .setMessage("Êtes vous sûr de vouloir vous déconnecter?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent I = new Intent(Dashboard.this, Connection.class);
                                startActivityForResult(I, 0);
                                finish();
                                System.exit(0);
                            }
                        }).create().show();

                break;

            case R.id.switch_gps:

                if (switcher.isChecked()) {
                    Toast.makeText(Dashboard.this, "Votre GPS est activé", Toast.LENGTH_LONG).show();
                    gps_actived = true;
                } else {
                    gps_actived = false;
                    Toast.makeText(Dashboard.this, "Votre GPS est désactivé", Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.photo:
                Intent picture = new Intent(Dashboard.this, UploadPicture.class);
                picture.putExtra("user", id_user);
                startActivity(picture);
                break;

            case R.id.video:
                Intent film = new Intent(Dashboard.this, UploadVideo.class);
                film.putExtra("user", id_user);
                startActivity(film);
                break;

            case R.id.voix:
                Intent sound = new Intent(Dashboard.this, UploadVoice.class);
                sound.putExtra("user", id_user);
                startActivity(sound);
                break;

            case R.id.texte:
                Intent write = new Intent(Dashboard.this, AddComment.class);
                write.putExtra("user", id_user);
                startActivity(write);
                break;

            case R.id.fab:
                Intent mail = new Intent(Dashboard.this, Mail.class);
                startActivity(mail);
                break;

        }
    }

    public void Coordonnees() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                progressDialog.dismiss();
                calendar = Calendar.getInstance();
                simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                date = simpleDateFormat.format(calendar.getTime());
                Latitude = location.getLongitude() + "";
                Longitude = location.getLatitude() + "";


                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (!success) {
                                Toast.makeText(Dashboard.this, "Erreur dans la base de données", Toast.LENGTH_LONG).show();
                            }else{
                                id_user = jsonResponse.getString("id");
                                //Toast.makeText(Dashboard.this, "Retour ", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                if (gps_actived) {
                    Ittineraire iteneraireRequest = new Ittineraire(date ,Longitude,Latitude,varBinevenu, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(Dashboard.this);
                    queue.add(iteneraireRequest);
                   // Toast.makeText(Dashboard.this, "Envoie" , Toast.LENGTH_LONG).show();
                }



            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates("gps", 5000, 0, listener);

    }


}