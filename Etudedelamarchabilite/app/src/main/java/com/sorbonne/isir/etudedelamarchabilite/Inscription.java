package com.sorbonne.isir.etudedelamarchabilite;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Inscription extends AppCompatActivity implements View.OnClickListener {

    public Button btnSign;
    public EditText nom;
    public EditText age;
    public EditText pass;
    public EditText repass;
    public EditText tel;
    public String choix = "";
    public String choixL = "";
    public String genre = "";
    public String deambulateur = "";
    public String aide = "";
    public TextView connect;
    public RadioButton oui;
    public RadioButton non;
    public RadioButton lunettesOui;
    public RadioButton lunettesNon;
    public RadioButton homme;
    public RadioButton femme;

    public RadioButton deamboui;
    public RadioButton deambnon;
    public RadioButton aideoui;
    public RadioButton aidenon;
    private static final String TAG = "Inscription";
    public DataBaseH data;
    public Boolean logged = false;

    public static String iden;
    public static String old;
    public static String phone;
    public static String passw;
    public static Boolean res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);
        btnSign = (Button) findViewById(R.id.signup);
        nom = (EditText) findViewById(R.id.name);
        age = (EditText) findViewById(R.id.age);
        pass = (EditText) findViewById(R.id.password);
        repass = (EditText) findViewById(R.id.passwordMatch);
        tel = (EditText) findViewById(R.id.tel);
        connect = (TextView) findViewById(R.id.link_login);

        non = (RadioButton) findViewById(R.id.sans);
        oui = (RadioButton) findViewById(R.id.avec);

        lunettesOui = (RadioButton) findViewById(R.id.avecL);
        lunettesNon = (RadioButton) findViewById(R.id.sansL);

        homme = (RadioButton) findViewById(R.id.homme);
        femme = (RadioButton) findViewById(R.id.femme);

        deamboui = (RadioButton) findViewById(R.id.Adeamu);
        deambnon = (RadioButton) findViewById(R.id.Sdeambu);

        aideoui = (RadioButton) findViewById(R.id.Aaide);
        aidenon = (RadioButton) findViewById(R.id.Saide);

        btnSign.setOnClickListener(this);
        oui.setOnClickListener(this);
        non.setOnClickListener(this);
        lunettesNon.setOnClickListener(this);
        lunettesOui.setOnClickListener(this);
        homme.setOnClickListener(this);
        femme.setOnClickListener(this);

        deamboui.setOnClickListener(this);
        deambnon.setOnClickListener(this);
        aidenon.setOnClickListener(this);
        aideoui.setOnClickListener(this);
        connect.setOnClickListener(this);

    }


    @Override
    public void onBackPressed() {
        Intent i = new Intent(Inscription.this, Connection.class);
        startActivity(i);
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.link_login:

                Intent myIntent = new Intent(Inscription.this, Connection.class);
                startActivityForResult(myIntent, 0);

                break;
            case R.id.signup :
                if(ConnexionInternet.isConnectedInternet(Inscription.this))
                {
                    signup();
                }
                else
                {
                    Toast.makeText(this, "Verifiez votre connexion internet", Toast.LENGTH_LONG).show();
                }

                break;
            case  R.id.sans :
                choix = "Sans";
                break;
            case  R.id.avec :
                choix = "Avec";
                break;

            case  R.id.sansL :
                choixL = "Sans";
                break;
            case  R.id.avecL :
                choixL = "Avec";
                break;

            case  R.id.homme :
                genre = "Homme";
                break;
            case  R.id.femme :
                genre = "Femme";
                break;

            case  R.id.Adeamu :
                deambulateur = "Avec";
                break;
            case  R.id.Sdeambu :
                deambulateur = "Sans";
                break;
            case  R.id.Aaide :
                aide = "Avec";
                break;
            case  R.id.Saide :
                aide = "Sans";
                break;
        }
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }


        iden = nom.getText().toString();
        old = age.getText().toString();
        phone = tel.getText().toString();
        passw = pass.getText().toString();




        final ProgressDialog progressDialog = new ProgressDialog(Inscription.this,R.style.MyTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Création du compte...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.show();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        data = new DataBaseH(Inscription.this,"marche.db",null,1);
                        res = data.insertData(iden,passw);
                        onSignupSuccess();
                        progressDialog.dismiss();
                        logged = true;
                        Intent intent = new Intent(Inscription.this, Dashboard.class);
                        intent.putExtra("user", iden);
                        startActivity(intent);
                        finish();

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Inscription.this);
                        builder.setMessage("Un compte sur ce nom existe déja")
                                .setNegativeButton("Oh non!!", null)
                                .create()
                                .show();
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        RegisterRequest registerRequest = new RegisterRequest(iden, old, phone, passw , choix , choixL , genre, deambulateur, aide , responseListener);
        final RequestQueue queue = Volley.newRequestQueue(Inscription.this);
        queue.add(registerRequest);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {

                        progressDialog.dismiss();
                        queue.stop();
                        if (logged == false){
                            Toast.makeText(Inscription.this, "Verifiez votre connexion et réessayez", Toast.LENGTH_LONG).show();
                        }
                        logged = false;

                    }
                }, 15000);
    }


    public void onSignupSuccess() {
        btnSign.setEnabled(true);
        setResult(RESULT_OK, null);


    }

    public void onSignupFailed() {


        btnSign.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = nom.getText().toString();
        String old = age.getText().toString();
        String phone = tel.getText().toString();
        String passw = pass.getText().toString();
        String repassw = repass.getText().toString();


        if (name.isEmpty() || name.length() < 3) {
            nom.setError("Entrez votre nom s'il vous plait");
            valid = false;
        } else {
            nom.setError(null);
        }

        if (old.isEmpty()) {
            age.setError("Entrez votre age");
            valid = false;
        } else {

            age.setError(null);
        }
        if (phone.isEmpty()) {
            tel.setError("Entrez votre numéro de télephone");
            valid = false;
        } else {

            tel.setError(null);
        }

        if (choix.isEmpty()) {
            Toast.makeText(Inscription.this, "Vous utilisez une canne? ou pas?", Toast.LENGTH_LONG).show();
            valid = false;
        }

        if (choixL.isEmpty()) {
            Toast.makeText(Inscription.this, "Vous portez des lunettes?", Toast.LENGTH_LONG).show();
            valid = false;
        }

        if (genre.isEmpty()) {
            Toast.makeText(Inscription.this, "Vous êtes un Homme ou une Femme ?", Toast.LENGTH_LONG).show();
            valid = false;
        }

        if (deambulateur.isEmpty()) {
            Toast.makeText(Inscription.this, "Vous utilisez un déambulateur ?", Toast.LENGTH_LONG).show();
            valid = false;
        }

        if (aide.isEmpty()) {
            Toast.makeText(Inscription.this, "Êtes accompagné par une personne aidante ?", Toast.LENGTH_LONG).show();
            valid = false;
        }

        if (passw.equals("") || passw.length() < 5 || passw.length() > 21) {
            pass.setError("Entrez un mot de passe correct entre 5 et 20 caractères");
            valid = false;
        } else {
            pass.setError(null);
        }

        if (repassw.equals("") || !repassw.toString().equals(passw.toString())) {
            repass.setError("Vous mots de passe ne sont pas identiques");
            valid = false;
        } else {
            repass.setError(null);
        }

        return valid;
    }
}


