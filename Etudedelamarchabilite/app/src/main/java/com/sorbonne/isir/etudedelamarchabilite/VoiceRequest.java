package com.sorbonne.isir.etudedelamarchabilite;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Super.Omarion on 21/01/2018.
 */

public class VoiceRequest extends StringRequest {
    private static final String REGISTER_REQUEST_URL = "https://aitslimane554.000webhostapp.com/UploadVoice.php";
    private Map<String, String> params;

    public VoiceRequest(String commentaire, String date,String longitude ,String latitude,String id,String file, Response.Listener<String> listener) {
        super(Request.Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("commentaire", commentaire+"");
        params.put("date", date+"");
        params.put("longitude", longitude+"");
        params.put("latitude", latitude+"");
        params.put("id_us", id + "");
        params.put("url", file + "");

    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
