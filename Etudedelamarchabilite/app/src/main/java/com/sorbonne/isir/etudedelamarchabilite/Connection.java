package com.sorbonne.isir.etudedelamarchabilite;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Connection extends AppCompatActivity implements View.OnClickListener{
    DataBaseH data;
    public Button btnConnect;
    public Button btnNew;
    public EditText name;
    public EditText pass;
    public CheckBox show;
    public TextView errone;
    public Boolean logged = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        name = (EditText) findViewById(R.id.name);
        pass = (EditText) findViewById(R.id.password);
        show = (CheckBox) findViewById(R.id.show);
        btnConnect = (Button) findViewById(R.id.login);
        btnNew = (Button) findViewById(R.id.signup);
        errone = (TextView) findViewById(R.id.error);
        btnConnect.setOnClickListener(this);
        btnNew.setOnClickListener(this);
        show.setOnClickListener(this);
    }

    @Override
    protected void onStart()
    {
        data = new DataBaseH(Connection.this,"marche.db",null,1);
        Cursor res;
        res = data.getAllData();
        if (res != null && res.getCount() > 0){
            res.moveToLast();
            name.setText(res.getString(1));
            pass.setText(res.getString(2));

        }
        super.onStart();
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

        switch(v.getId()) {

            case R.id.login:
                    if(ConnexionInternet.isConnectedInternet(Connection.this)) {
                        login();
                    } else {
                        Toast.makeText(getBaseContext(), "Verifiez votre connexion internet", Toast.LENGTH_LONG).show();
                    }
                break;


            case R.id.signup:
                    Intent myIn = new Intent(this, Inscription.class);
                    startActivity(myIn);
                break;

            case R.id.show:
                    if(show.isChecked()){
                        pass.setInputType(InputType.TYPE_CLASS_TEXT);

                    }else{
                        pass.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                break;

        }

    }
    public void login() {

        if (!validate()) {
            onLoginFailed();
            return;
        }

        final String nom = name.getText().toString();
        final String passw = pass.getText().toString();


        final ProgressDialog progressDialog = new ProgressDialog(Connection.this,
                R.style.MyTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(3);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.setMessage("Authentication...");
        progressDialog.show();






        final Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        onLoginSuccess();
                        progressDialog.dismiss();
                        logged = true;
                        Intent intent = new Intent(Connection.this, Dashboard.class);
                        intent.putExtra("user", nom);
                        startActivity(intent);
                        System.exit(0);
                        finish();

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Connection.this);
                        builder.setMessage("Le compte n'existe pas")
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        ConnectionRequest connectionRequest = new ConnectionRequest(nom,passw,responseListener);
        final RequestQueue queue = Volley.newRequestQueue(Connection.this);
        queue.add(connectionRequest);
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {

                        progressDialog.dismiss();
                        queue.stop();
                        if (logged == false){
                            Toast.makeText(Connection.this, "Verifiez votre connexion et réessayez", Toast.LENGTH_LONG).show();
                        }
                        logged = false;


                    }
                }, 15000);


    }

    public void onLoginSuccess() {
        btnConnect.setEnabled(true);
    }

    public void onLoginFailed() {
        btnConnect.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String nom = name.getText().toString();
        String password = pass.getText().toString();

        if (nom.isEmpty()) {
            name.setError("entrez votre nom s'il vous plait");
            valid = false;
        } else {
            name.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 21) {
            pass.setError("Entrez un mot de passe correct entre 5 et 20 caractères");
            valid = false;
        } else {
            pass.setError(null);
        }

        return valid;
    }
}
