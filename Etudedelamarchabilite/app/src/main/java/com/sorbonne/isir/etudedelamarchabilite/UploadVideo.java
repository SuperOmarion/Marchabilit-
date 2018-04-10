package com.sorbonne.isir.etudedelamarchabilite;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class UploadVideo extends AppCompatActivity implements View.OnClickListener  {

    public static String Latitude;
    public static String Longitude;
    private LocationManager locationManager;
    public static LocationListener listener;
    public boolean send = false;
    static final int REQUEST_VIDEO_CAPTURE = 0;
    private Button mRecordView, mPlayView,envoi,mStopView;
    private VideoView mVideoView;
    private int ACTIVITY_START_CAMERA_APP=0;
    public EditText comment;
    public String id_user = "";
    public String comm = "";
    public ProgressDialog progressDialog2;

    private TextView textView;
    private TextView textViewResponse;
    private String selectedPath;
    private String UPLOAD_URL = "http://aitslimane554.000webhostapp.com/VideoFileUpload.php";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);
        mRecordView = (Button) findViewById(R.id.filmer);
        mPlayView = (Button) findViewById(R.id.lire);
        mPlayView.setEnabled(false);
        mStopView = (Button) findViewById(R.id.stop);
        mStopView.setEnabled(false);
        envoi = (Button) findViewById(R.id.envoie);
        mVideoView=(VideoView) findViewById(R.id.video);
        comment = (EditText) findViewById(R.id.VideoComment);
        mRecordView.setOnClickListener(this);
        mPlayView.setOnClickListener(this);
        mStopView.setOnClickListener(this);
        envoi.setOnClickListener(this);

        id_user = getIntent().getExtras().getString("user");
        progressDialog2 = new ProgressDialog(UploadVideo.this,
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
            case R.id.filmer:
                dispatchTakeVideoIntent(v);

                break;
            case R.id.lire:
                mVideoView.start();
                mPlayView.setEnabled(false);
                mStopView.setEnabled(true);

                break;

            case R.id.stop:
                if(null!=mVideoView){
                    mVideoView.pause();
                }
                mStopView.setEnabled(false);
                mPlayView.setEnabled(true);

                break;
            case R.id.envoie:
                if (comment.getText().toString().equals("")) {
                    comment.setError("Veuillez écrire votre commentaire");
                }
                if (comment.length() <= 10){
                    comment.setError("Soyez un peu clair s'il vous plaît");
                }
                if (!comment.getText().toString().equals("") && comment.length() > 10){
                    //
                    envComment();
                    //Toast.makeText(UploadVideo.this, "Envoie", Toast.LENGTH_LONG).show();
                }

                break;
        }
    }


    public void envComment(){
        comm = comment.getText().toString().trim();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String d = simpleDateFormat.format(calendar.getTime());
       // Toast.makeText(getBaseContext(), "Envoie "  , Toast.LENGTH_LONG).show();

        final ProgressDialog progressDialog = new ProgressDialog(UploadVideo.this,
                R.style.MyTheme3);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(3);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.show();

        progressDialog.setMessage("Envoi de la video ainsi que la description...\nMerci pour votre contribution!! \nVotre aide est précieuse");
        progressDialog.show();

        final Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        uploadVideo();
                        progressDialog.dismiss();
                        send = true;

                        Intent I = new Intent(UploadVideo.this, Dashboard.class);
                        startActivityForResult(I, 0);

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(UploadVideo.this);
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

        Toast.makeText(UploadVideo.this,filename.length + path, Toast.LENGTH_LONG).show();
        VideoRequest videoRequest = new VideoRequest(comm ,d, Latitude,Longitude,id_user,path,responseListener);
        final RequestQueue queue = Volley.newRequestQueue(UploadVideo.this);
        queue.add(videoRequest);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {

                        progressDialog.dismiss();
                        queue.stop();
                        if (send == false){
                            Toast.makeText(UploadVideo.this, "Verifiez votre connexion et réessayez", Toast.LENGTH_LONG).show();
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
    public void dispatchTakeVideoIntent(View v) {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra("android.intent.extra.videoQuality", 0);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            takeVideoIntent.putExtra("android.intent.extra.durationLimit", 5);
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    private void uploadVideo() {
        class Upload_Video extends AsyncTask<Void, Void, String> {

            private ProgressDialog uploading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                uploading = ProgressDialog.show(UploadVideo.this, "Uploading File", "Please wait...", false, false);
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
                String msg = u.uploadFile(selectedPath, UPLOAD_URL);
                //Toast.makeText(UploadVideo.this, msg, Toast.LENGTH_LONG).show();
                return msg;
            }
        }

        Upload_Video uv = new Upload_Video();
        uv.execute();
    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();

        return path;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_START_CAMERA_APP && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();

            selectedPath = getPath(videoUri);
            mVideoView.setVideoURI(videoUri);
            mPlayView.setEnabled(true);

        }
    }

}

