package com.sorbonne.isir.etudedelamarchabilite;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.sorbonne.isir.etudedelamarchabilite.Dashboard.simpleDateFormat;


public class UploadPicture extends AppCompatActivity implements View.OnClickListener {

    public static String Latitude;
    public static String Longitude;
    public static LocationManager locationManager;
    public boolean send = false;
    public static LocationListener listener;
    public EditText comment;
    public String comm = "";
    public String id_user = "";
    public Button btnpic,btnSend;
    public ImageView imgTakenPic;
    private static final int CAM_REQUEST=123;
    public ProgressDialog progressDialog;
    public ProgressDialog progressDialog2;

    private TextView textView;
    private TextView textViewResponse;
    private String selectedPath;
    private String UPLOAD_URL = "http://aitslimane554.000webhostapp.com/ImageFileUpload.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_picture);
        btnpic = (Button) findViewById(R.id.capture);
        btnSend = (Button) findViewById(R.id.envoiPic);
        imgTakenPic = (ImageView) findViewById(R.id.imageV);
        comment = (EditText) findViewById(R.id.Piccomment);
        id_user = getIntent().getExtras().getString("user");

        btnSend.setOnClickListener(this);
        btnpic.setOnClickListener(this);
        progressDialog2 = new ProgressDialog(UploadPicture.this,
                R.style.MyTheme2);
        progressDialog2.setIndeterminate(true);
        progressDialog2.setProgressStyle(3);
        progressDialog2.setCancelable(false);
        progressDialog2.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog2.setMessage("Initialisation...");
        progressDialog2.show();
        Coordonnees();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.capture:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,CAM_REQUEST);
                break;

            case R.id.envoiPic:
                if (comment.getText().toString().equals("")) {
                    comment.setError("Veuillez écrire votre commentaire");
                }
                if (comment.length() <= 10){
                    comment.setError("Soyez un peu clair s'il vous plaît");
                }
                if (!comment.getText().toString().equals("") && comment.length() > 10){
                    //uploadPicture();
                    envComment();
                    Toast.makeText(UploadPicture.this, "Envoie", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == CAM_REQUEST && resultCode == Activity.RESULT_OK){

            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imgTakenPic.setImageBitmap(photo);

            Uri imageUri = data.getData();
            selectedPath = getPath(imageUri);


        }

    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    public void envComment(){
        comm = comment.getText().toString().trim();
        Calendar calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String d = simpleDateFormat.format(calendar.getTime());
        Toast.makeText(getBaseContext(), "Envoie "  , Toast.LENGTH_LONG).show();

        progressDialog = new ProgressDialog(UploadPicture.this,
                R.style.MyTheme3);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(3);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.show();

        progressDialog.setMessage("Envoi du commentaire de l'image...\nMerci pour votre contribution!! \nVotre aide est précieuse");
        progressDialog.show();

        final Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        uploadImage();
                        progressDialog.dismiss();
                        send = true;

                        Intent I = new Intent(UploadPicture.this, Dashboard.class);
                        startActivityForResult(I, 0);

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(UploadPicture.this);
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

        String[] filename = selectedPath.split("/");
        String path = filename[filename.length - 1];

        PictureRequest pictureRequest = new PictureRequest(comm ,d, Latitude,Longitude,id_user,path,responseListener);
        final RequestQueue queue = Volley.newRequestQueue(UploadPicture.this);
        queue.add(pictureRequest);



        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {

                        progressDialog.dismiss();
                        queue.stop();
                        if (send == false){
                            Toast.makeText(UploadPicture.this, "Verifiez votre connexion et réessayez", Toast.LENGTH_LONG).show();
                        }
                        send = false;


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

    private void uploadImage() {
        class Upload_Image extends AsyncTask<Void, Void, String> {

            private ProgressDialog uploading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                uploading = ProgressDialog.show(UploadPicture.this, "Uploading File", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                uploading.dismiss();
                textViewResponse.setText(Html.fromHtml("<b>Uploaded at <a href='" + s + "'>" + s + "</a></b>"));
                textViewResponse.setMovementMethod(LinkMovementMethod.getInstance());
            }

            @Override
            protected String doInBackground(Void... params) {
                Upload u = new Upload();
                String msg = u.uploadFile(selectedPath,UPLOAD_URL);
                return msg;
            }
        }

        Upload_Image uv = new Upload_Image();
        uv.execute();
    }
}

