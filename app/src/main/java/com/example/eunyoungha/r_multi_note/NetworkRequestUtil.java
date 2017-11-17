package com.example.eunyoungha.r_multi_note;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by eunyoung.ha on 2017/11/14.
 */

public class NetworkRequestUtil {

    public static JSONObject doHttpPost(String url) {

        JSONObject jsonObject = null;

        try {
            OkHttpClient httpClient = new OkHttpClient();
            Request.Builder httpRequestBuilder = new Request.Builder().url(url);

            FormBody.Builder formBody = new FormBody.Builder();
            RequestBody requestBody = formBody.build();

            Request httpRequest = httpRequestBuilder.post(requestBody).build();

            Response response = httpClient.newCall(httpRequest).execute();

            String unEscapedString = stringHelperUtil(response.body().string());

            try{
                jsonObject = new JSONObject(unEscapedString);
            } catch (JSONException e){

            }
        } catch (IOException e) {
        }

        return jsonObject;

    }

    public static String stringHelperUtil(String escapedValue){
        return escapedValue == null ? null : escapedValue
                .replace("&amp;", "&")
                .replace("&quot;", "\\\"")
                .replace("&#39;", "'")
                .replace("&gt;", ">")
                .replace("&lt;", "<");
    }

}
