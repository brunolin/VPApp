package com.vendasplus.vpapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.vendasplus.vpapp.model.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddNotaActivity extends Activity {

    private JSONArray campanhas;
    private DatePicker dataVendaPicker;
    private Spinner campanhasSpinner;
    private ArrayList<String> spinnerList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private EditText idVenda;
    private ProgressDialog progressDialog;

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_nota);
        progressDialog = new ProgressDialog(this);
        userId = getIntent().getIntExtra("userId", 0);

        dataVendaPicker = (DatePicker) findViewById(R.id.dataVenda);
        campanhasSpinner = (Spinner) findViewById(R.id.produtos);
        idVenda = (EditText) findViewById(R.id.idVenda);


        campanhasSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getSelectedData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        dataVendaPicker.setMaxDate(new Date().getTime());

        getCampanhas();
    }

    public void getCampanhas() {

        String url = "http://vendasplus.com.br/r/vendedor/getCampanhas";

        JsonArrayRequest request = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        campanhas = response;
                        setSpinner(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        MySingleton.getInstance(this).addToRequestQueue(request);
    }

    public void setSpinner(JSONArray array) {
        for(int i = 0; i < array.length(); i++) {
            try {
                JSONObject elem = array.getJSONObject(i);
                spinnerList.add(elem.getString("nomeProduto"));
            } catch (JSONException e ) {
                e.printStackTrace();
            }
        }

        adapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, R.id.txt, spinnerList);
        campanhasSpinner.setAdapter(adapter);
    }

    public int setIdProduto() {
        String produtoSelecionado = campanhasSpinner.getSelectedItem().toString();

        for(int i = 0; i < campanhas.length(); i++) {
            try {
                JSONObject elem = campanhas.getJSONObject(i);
                if(produtoSelecionado.equals(elem.getString("nomeProduto"))) {
                    return elem.getInt("idProduto");
                }
            } catch (JSONException e ) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    public int setIdEmpresa() {
        String produtoSelecionado = campanhasSpinner.getSelectedItem().toString();

        for(int i = 0; i < campanhas.length(); i++) {
            try {
                JSONObject elem = campanhas.getJSONObject(i);
                if(produtoSelecionado.equals(elem.getString("nomeProduto"))) {
                    return elem.getInt("idEmpresa");
                }
            } catch (JSONException e ) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    public String getOrderData() {
        String time = dataVendaPicker.getYear() + "-" + dataVendaPicker.getMonth() + "-" + dataVendaPicker.getDayOfMonth() + "T02:00:00.000Z";
        return time;
    }

    public void getSelectedData() {

            String produtoSelecionado = campanhasSpinner.getSelectedItem().toString();

            for(int i = 0; i < campanhas.length(); i++) {
                try {
                    JSONObject elem = campanhas.getJSONObject(i);
                    if(produtoSelecionado.equals(elem.getString("nomeProduto"))) {
                        setData(elem.getString("inicioCampanha"));
                    }
                } catch (JSONException e ) {
                    e.printStackTrace();
                }
            }
    }

    public long setData(String dataSelecionada) {
        try {
            if(!dataSelecionada.isEmpty()) {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = sdf.parse(dataSelecionada.split("T")[0]);

                dataVendaPicker.setMinDate(date.getTime());
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addNota(final View view) {

        progressDialog.setMessage("Salvando nota...");
        progressDialog.show();
        String url = "http://vendasplus.com.br/r/vendedor/cadastrarNota";
        try {

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("idVenda", idVenda.getText().toString());
            jsonBody.put("nomeProduto", campanhasSpinner.getSelectedItem().toString());
            jsonBody.put("idProduto", setIdProduto());
            jsonBody.put("idVendedor", userId);
            jsonBody.put("idEmpresa", setIdEmpresa());
            jsonBody.put("data", getOrderData());

            final String requestBody = jsonBody.toString();

            StringRequest request = new StringRequest(Request.Method.POST,
                    url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    progressDialog.setMessage("Nota salva!");
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

    public void notaAdicionada() {
        finish();
        startActivity(new Intent(this, ProfileActivity.class));
    }

    public void voltar(View view) {
        finish();
        startActivity(new Intent(this, ProfileActivity.class));
    }

}
