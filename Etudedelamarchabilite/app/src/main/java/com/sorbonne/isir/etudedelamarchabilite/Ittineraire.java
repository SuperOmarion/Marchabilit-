package com.sorbonne.isir.etudedelamarchabilite;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Super.Omarion on 18/01/2018.
 */

public class Ittineraire extends StringRequest {
    private static final String REGISTER_REQUEST_URL = "https://aitslimane554.000webhostapp.com/Itineraire.php";
    private Map<String, String> params;

    public Ittineraire(String Date_Itineraire, String longitude, String latitude,String name, Response.Listener<String> listener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("date", Date_Itineraire);
        params.put("longitude", longitude+"");
        params.put("latitude", latitude + "");
        params.put("name", name );

    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}