package com.vendasplus.vpapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Brunolin on 07/06/2017.
 */

public class Util {

    public static String streamToString(InputStream is){
        BufferedReader br;
        StringBuilder sb = new StringBuilder();

        br = new BufferedReader(new InputStreamReader(is));
        String line;
        try {
            while((line = br.readLine()) != null){
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static Vendedor JSONToVendedor(String jsonFile){

        JSONObject mainObj;
        Vendedor vendedor = new Vendedor();

        try {
            mainObj = new JSONObject(jsonFile);

            vendedor = new Vendedor();
            vendedor.setNome(mainObj.getString("nome"));
            vendedor.setEmail(mainObj.getString("email"));
            vendedor.setCidade(mainObj.getString("cidade"));
            vendedor.setEstado(mainObj.getString("estado"));
            vendedor.setPontos(mainObj.getInt("pontos"));
            vendedor.setTelefone(mainObj.getLong("telefone"));

        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            return vendedor;
        }
    }

}
