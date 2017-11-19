package com.vendasplus.vpapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends Activity implements View.OnClickListener {

    private Button logout;
    private TextView userData;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        logout = (Button) findViewById(R.id.logout);
        userData = (TextView) findViewById(R.id.dadosUsuario);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        if(firebaseAuth.getCurrentUser() == null) {
            finish();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
        user = firebaseAuth.getCurrentUser();
        logout.setOnClickListener(this);

        progressDialog.setMessage("Buscando informações...");
        progressDialog.show();

        getDadosVendedor();
    }

    @Override
    public void onClick(View v) {
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    public void getDadosVendedor() {

        Uri.Builder postBody = new Uri.Builder();
        postBody.appendQueryParameter("email", user.getEmail());
        String query = postBody.build().toString();

        URL url = null;
        try {
            url = new URL("http://vendasplus.com.br/r/vendedor/getInfoVendedorByEmail" + query);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url.toString(), null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Vendedor vendedor  = Util.JSONToVendedor(response.toString());
                    editVendedorData(vendedor);
                    addNota();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });

        MySingleton.getInstance(this).addToRequestQueue(request);
    }

    public void addNota() {

        String url = "http://vendasplus.com.br/r/vendedor/cadastrarNota";
        try {

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("idVenda", "TesteAndroid");
            jsonBody.put("nomeProduto", "Teste 2");
            jsonBody.put("idProduto", 76);
            jsonBody.put("idVendedor", 1);
            jsonBody.put("idEmpresa", 3);
            jsonBody.put("data", "2017-11-18T02:00:00.000Z");

            final String requestBody = jsonBody.toString();

            StringRequest request = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        notaAdicionada();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
            }) {

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

            };

            MySingleton.getInstance(this).addToRequestQueue(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void editVendedorData(Vendedor vendedor){
        progressDialog.dismiss();
        editUserData(vendedor);
    }

    public void editUserData(Vendedor vendedor) {
        String info = "Nome: " + vendedor.getNome();
        info += "\nEmail: " + vendedor.getEmail();
        info += "\nCidade: " + vendedor.getCidade();
        info += "\nEstado: " + vendedor.getEstado();
        info += "\nTelefone: " + vendedor.getTelefone();
        info += "\nPontos: " + vendedor.getPontos();

        userData.setText(info);
    }

    public void notaAdicionada() {
        String userText = userData.getText().toString();
        userText += "\nNota Adicionada!";
        userData.setText(userText);
    }
}