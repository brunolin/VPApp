package com.vendasplus.vpapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.vendasplus.vpapp.model.MySingleton;
import com.vendasplus.vpapp.model.Util;
import com.vendasplus.vpapp.model.Vendas;

import org.json.JSONArray;

import java.util.ArrayList;

public class HistoryActivity extends Activity {

    private int userId;
    private TextView vendasContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        userId = getIntent().getIntExtra("userId", 0);

        vendasContainer = (TextView) findViewById(R.id.vendasContainer);

        getNotas();
    }

    public void getNotas() {

        String url = "http://vendasplus.com.br/r/vendedor/getNotasVendedor/" + userId;

        JsonArrayRequest request = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        setNotas(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        MySingleton.getInstance(this).addToRequestQueue(request);
    }

    public void setNotas(JSONArray array) {
        ArrayList<Vendas> vendas = Util.JSONToVendas(array.toString());
        String aux = "";

        for(Vendas venda : vendas) {
            aux += "Id venda: " + venda.getIdVenda();
            aux += "\nProduto: " + venda.getNomeProduto();
            aux += "\nStatus: " + getAprovadaStatus(venda.getAprovada());
            aux += "\n\n";
        }

        vendasContainer.setText(aux);
    }

    public String getAprovadaStatus(String status) {
        if(status.equals("T")) {
            return "Aprovada";
        } else if(status.equals("X")) {
            return "Reprovada";
        }

        return "Em avaliação";
    }

    public void voltar(View view) {
        finish();
        startActivity(new Intent(this, ProfileActivity.class));
    }
}
