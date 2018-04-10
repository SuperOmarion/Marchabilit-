package com.sorbonne.isir.etudedelamarchabilite;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Mail extends AppCompatActivity implements View.OnClickListener{
    public EditText obj;
    public EditText email;
    public EditText Ademail;
    public Button send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail);
        obj = (EditText) findViewById(R.id.objet);
        email = (EditText) findViewById(R.id.mail);
        send = (Button) findViewById(R.id.envoiMail);
        Ademail = (EditText) findViewById(R.id.Adresse);
        send.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String textOb = obj.getText().toString();
        String textMail = email.getText().toString();
        String textAdresse = Ademail.getText().toString();
        if (textOb.length() <= 5){
            obj.setError("Mettez l'objet de votre Email");
        }
        if (textMail.length() <= 10){
            email.setError("Ecrivez votre mail");
        }
        if (textAdresse.length() <= 10){
            Ademail.setError("Ecrivez votre Adresse Email");
        }
        if (textMail.length()> 10 && textOb.length() > 5 && textAdresse.length() > 10){
            SendMail(textAdresse,textOb,textMail);
        }
    }

    public void SendMail(String ad,String ob, String ma){

        Toast.makeText(getBaseContext(), ob +"   " + ma, Toast.LENGTH_LONG).show();
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setData(Uri.parse("email"));
        i.putExtra(Intent.EXTRA_EMAIL,ad);
        i.putExtra(Intent.EXTRA_SUBJECT,ob);
        i.putExtra(Intent.EXTRA_TEXT,ma);
        i.setType("message/rfc822");
        Intent chooser = Intent.createChooser(i,"Demarrer le mailling");
        startActivity(chooser);
    }
}