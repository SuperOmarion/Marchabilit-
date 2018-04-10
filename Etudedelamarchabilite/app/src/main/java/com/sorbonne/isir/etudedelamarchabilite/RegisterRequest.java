package com.sorbonne.isir.etudedelamarchabilite;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {
    private static final String REGISTER_REQUEST_URL = "https://aitslimane554.000webhostapp.com/Register.php";
    private Map<String, String> params;

    public RegisterRequest(String name, String age, String tel, String  password , String canne , String lunettes , String genre   , String deamabulateur , String aide , Response.Listener<String> listener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("name", name);
        params.put("age", age);
        params.put("telephone", tel);
        params.put("mot_de_passe", password);
        params.put("canne", canne);
        params.put("lunettes", lunettes);
        params.put("sex", genre);
        params.put("deambulateur", deamabulateur);
        params.put("aide", aide);

    }
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}