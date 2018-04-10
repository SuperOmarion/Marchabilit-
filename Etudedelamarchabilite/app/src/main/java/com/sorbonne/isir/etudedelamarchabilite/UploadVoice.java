package com.sorbonne.isir.etudedelamarchabilite;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class UploadVoice extends AppCompatActivity implements View.OnClickListener {

    public static String Latitude;
    public static String Longitude;
    private LocationManager locationManager;
    public static LocationListener listener;
    private static MediaRecorder mediaRecorder;
    private static MediaPlayer mediaPlayer;
    public EditText comment;
    public String comm = "";
    public boolean send = false;
    public String id_user;
    public TextView coord;
    public Button play;
    public Button record;
    public Button stopRecord;
    public Button stop;
    public Button sendV;
    public String AudioSavePathInDevice = null;
    public Random random ;
    public String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    public ImageView micoI;
    public static final int RequestPermissionCode = 1;
    public ProgressDialog progressDialog2;
    public String UPLOAD_URL="http://aitslimane554.000webhostapp.com/VoiceFileUpload.php";
    private TextView textView;
    private TextView textViewResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_voice);
        play = (Button) findViewById(R.id.ecouter);
        record = (Button) findViewById(R.id.parler);
        stopRecord = (Button) findViewById(R.id.finir);
        stop = (Button) findViewById(R.id.arreter);
        sendV = (Button) findViewById(R.id.envoiVoix);
        comment = (EditText) findViewById(R.id.VoiceComment);

        play.setOnClickListener(this);
        stopRecord.setOnClickListener(this);
        record.setOnClickListener(this);
        stop.setOnClickListener(this);


        sendV.setOnClickListener(this);
        random = new Random();


        random = new Random();
        micoI = (ImageView) findViewById(R.id.micro);
        micoI.setImageResource(R.drawable.voicered);
        coord = (TextView) findViewById(R.id.titre);
        id_user = getIntent().getExtras().getString("user");
        progressDialog2 = new ProgressDialog(UploadVoice.this,
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
            case R.id.parler:
                Toast.makeText(getBaseContext(), "Enregistrement "  , Toast.LENGTH_LONG).show();
                micoI.setImageResource(R.drawable.voicegreen);
                recordAudio();

                break;
            case R.id.finir:

                try {
                    mediaRecorder.prepare();
                    mediaRecorder.stop();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                micoI.setImageResource(R.drawable.voicered);
                stopRecord.setEnabled(false);
                play.setEnabled(true);
                record.setEnabled(true);
                stop.setEnabled(false);
                Toast.makeText(getBaseContext(), "Arret d'enregistrement "  , Toast.LENGTH_LONG).show();
                break;

            case R.id.ecouter:
                Toast.makeText(getBaseContext(), "Ecoute "  , Toast.LENGTH_LONG).show();
                playAudio();
                break;

            case R.id.arreter:
                Toast.makeText(getBaseContext(), "Arret "  , Toast.LENGTH_LONG).show();
                stopAudio();
                break;

            case R.id.envoiVoix:
                if (comment.getText().toString().equals("")) {
                    comment.setError("Veuillez écrire votre commentaire");
                }
                if (comment.length() <= 10){
                    comment.setError("Soyez un peu clair s'il vous plaît");
                }
                if (!comment.getText().toString().equals("") && comment.length() > 10){

                    envComment();
                   // Toast.makeText(UploadVoice.this, "Envoie", Toast.LENGTH_LONG).show();
                }

                break;
        }
    }


    public void recordAudio() {
        stop.setEnabled(true);
        play.setEnabled(false);
        record.setEnabled(false);
        stopRecord.setEnabled(true);
       // mediaPlayer.release();

        if(checkPermission()) {

            AudioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + CreateRandomAudioFileName(5) + "AudioRecording.3gp";

            MediaRecorderReady();

            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }
        else {

            requestPermission();

        }


    }
    public void stopAudio() {

        stop.setEnabled(false);
        play.setEnabled(true);
        stopRecord.setEnabled(false);
        record.setEnabled(true);
        if(mediaPlayer != null){

            mediaPlayer.pause();

            MediaRecorderReady();

        }

    }

    public void playAudio() throws IllegalArgumentException, SecurityException, IllegalStateException{
        play.setEnabled(false);
        //record.setEnabled(false);
        stop.setEnabled(true);
        stopRecord.setEnabled(false);

        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(AudioSavePathInDevice);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.start();

    }
    public void MediaRecorderReady(){

        mediaRecorder=new MediaRecorder();

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

        mediaRecorder.setOutputFile(AudioSavePathInDevice);

    }

    public String CreateRandomAudioFileName(int string){

        StringBuilder stringBuilder = new StringBuilder( string );

        int i = 0 ;
        while(i < string ) {

            stringBuilder.append(RandomAudioFileName.charAt(random.nextInt(RandomAudioFileName.length())));

            i++ ;
        }
        return stringBuilder.toString();

    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(UploadVoice.this, new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {

                    boolean StoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {

                        Toast.makeText(UploadVoice.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(UploadVoice.this,"Permission Denied",Toast.LENGTH_LONG).show();

                    }
                }

                break;
        }
    }

    public boolean checkPermission() {

        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    public void envComment(){
        comm = comment.getText().toString().trim();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String d = simpleDateFormat.format(calendar.getTime());
       // Toast.makeText(getBaseContext(), "Envoie "  , Toast.LENGTH_LONG).show();

        final ProgressDialog progressDialog = new ProgressDialog(UploadVoice.this,
                R.style.MyTheme3);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(3);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.show();

        progressDialog.setMessage("Envoi du commentaire et de la video...\nMerci pour votre contribution!! \nVotre aide est précieuse");
        progressDialog.show();

        final Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        uploadVoice();
                        progressDialog.dismiss();
                        send = true;

                        Intent I = new Intent(UploadVoice.this, Dashboard.class);
                        startActivityForResult(I, 0);

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(UploadVoice.this);
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


        String[] filename = AudioSavePathInDevice.split("/");

        String path = filename[filename.length - 1];

        Toast.makeText(UploadVoice.this,filename.length + path, Toast.LENGTH_LONG).show();
        VoiceRequest voiceRequest = new VoiceRequest(comm ,d, Latitude,Longitude,id_user,path,responseListener);
        final RequestQueue queue = Volley.newRequestQueue(UploadVoice.this);
        queue.add(voiceRequest);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {

                        progressDialog.dismiss();
                        queue.stop();
                        if (send == false){
                            Toast.makeText(UploadVoice.this, "Verifiez votre connexion et réessayez", Toast.LENGTH_LONG).show();
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

    private void uploadVoice() {
        class Upload_Voice extends AsyncTask<Void, Void, String> {

            private ProgressDialog uploading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                uploading = ProgressDialog.show(UploadVoice.this, "Uploading File", "Please wait...", false, false);
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
                String msg = u.uploadFile(AudioSavePathInDevice,UPLOAD_URL);
                return msg;
            }
        }

        Upload_Voice uv = new Upload_Voice();
        uv.execute();
    }

}

