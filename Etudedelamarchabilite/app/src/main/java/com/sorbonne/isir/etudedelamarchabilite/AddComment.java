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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddComment extends AppCompatActivity implements View.OnClickListener {

    public static String Latitude;
    public static String Longitude;
    public static LocationListener listener;
    public EditText autre;
    public TextView coord;
    public TextView comment;
    public Button env;
    public String objet = "";
    public String comm = "";
    public String id_user = "";
    public boolean sent = false;
    public static Calendar calendar;
    public static SimpleDateFormat simpleDateFormat;
    private LocationManager locationManager;
    public static String date;
    public ProgressDialog progressDialog2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);
        autre = (EditText) findViewById(R.id.autres);
        coord = (TextView) findViewById(R.id.titre);
        comment = (TextView) findViewById(R.id.comment);
        env = (Button) findViewById(R.id.envoie);
        env.setOnClickListener(this);
        id_user = getIntent().getExtras().getString("user");
        objet = "Route Dégradée";
        progressDialog2 = new ProgressDialog(AddComment.this,
                R.style.MyTheme2);
        progressDialog2.setIndeterminate(true);
        progressDialog2.setProgressStyle(3);
        progressDialog2.setCancelable(false);
        progressDialog2.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog2.setMessage("Initialisation...");
        progressDialog2.show();
        Coordonnees();
        Spinner dropdown = (Spinner) findViewById(R.id.objets);
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter.createFromResource(this, R.array.MesObjets, android.R.layout.simple_spinner_item);
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dropdown.setAdapter(staticAdapter);
        dropdown.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                autre.setVisibility(View.INVISIBLE);
                switch (position){
                    case 0:
                        objet = "Sécurité, Traffic";
                        break;
                    case 1:
                        objet = "Etretien, Propreté, Eclairage";
                        break;
                    case 2:
                        objet = "Aménagement du lieu";
                        break;
                    case 3:
                        objet = "Non respect";
                        break;
                    case 4:
                        objet = "autre";
                        autre.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

    }


    @Override
    public void onClick(View v) {
        if (objet.equals("autre")) {
            if (!autre.getText().toString().equals("")) {
                objet = autre.getText().toString();
            } else {
                autre.setError("Veuillez mettre l'objet du commentaire");
            }
        }

        if (comment.getText().toString().equals("")) {
            comment.setError("Veuillez écrire votre commentaire");
        }
        if (comment.length() <= 15){
            comment.setError("Soyez un peu clair s'il vous plaît");
        }

        if (!objet.equals("") && !comment.getText().toString().equals("") && comment.length() > 15){

            Envoie();

        }
    }

    public void Envoie(){
        comm = comment.getText().toString().trim();
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String d = simpleDateFormat.format(calendar.getTime());
        Toast.makeText(getBaseContext(), "Envoie "  , Toast.LENGTH_LONG).show();

        final ProgressDialog progressDialog = new ProgressDialog(AddComment.this,
                R.style.MyTheme3);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(3);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.show();

        progressDialog.setMessage("Envoi du commentaire...");
        progressDialog.show();

        final Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        progressDialog.dismiss();
                        sent = true;
                        new AlertDialog.Builder(AddComment.this)
                                .setTitle("Commentaire envoyé")
                                .setMessage("Merci pour votre contribution!! \nVotre aide est précieuse")
                                .setPositiveButton(R.string.next, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface arg0, int arg1) {
                                        Intent I = new Intent(AddComment.this, Dashboard.class);
                                        startActivityForResult(I, 0);

                                    }
                                }).create().show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddComment.this);
                        builder.setMessage("Le compte n'existe pas")
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        CommentRequest commentRequest = new CommentRequest(objet,comm ,d, Latitude,Longitude,id_user,responseListener);
        final RequestQueue queue = Volley.newRequestQueue(AddComment.this);
        queue.add(commentRequest);
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {

                        progressDialog.dismiss();
                        queue.stop();

                        if (sent == false){
                            Toast.makeText(AddComment.this, "Verifiez votre connexion et réessayez", Toast.LENGTH_LONG).show();
                        }
                        sent = false;


                    }
                }, 15000);
    }
    public void Coordonnees() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                progressDialog2.dismiss();

                Latitude = location.getLongitude() + "";
                Longitude = location.getLatitude() + "";
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
        locationManager.requestLocationUpdates("gps", 1000, 0, listener);

    }


}
