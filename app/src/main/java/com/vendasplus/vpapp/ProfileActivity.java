package com.vendasplus.vpapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vendasplus.vpapp.model.MySingleton;
import com.vendasplus.vpapp.model.Util;
import com.vendasplus.vpapp.model.Vendedor;

import org.json.JSONObject;

public class ProfileActivity extends Activity implements View.OnClickListener {

    private int userId;
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
                    userId = vendedor.getIdVendedor();
                    editVendedorData(vendedor);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });

        MySingleton.getInstance(this).addToRequestQueue(request);
    }

    public void editVendedorData(Vendedor vendedor){
        progressDialog.dismiss();
        String info = "Olá " + vendedor.getNome() + "!\nvocê possui " + vendedor.getPontos() + " pontos!";
        userData.setText(info);

    }

    public void addNota(View view) {
        finish();
        startActivity(new Intent(this, AddNotaActivity.class).putExtra("userId", userId));
    }

    public void history(View view) {
        finish();
        startActivity(new Intent(this, HistoryActivity.class).putExtra("userId", userId));
    }
}