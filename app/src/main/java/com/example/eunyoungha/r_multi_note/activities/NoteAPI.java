package com.example.eunyoungha.r_multi_note.activities;

import android.content.Context;
import android.os.AsyncTask;

import com.example.eunyoungha.r_multi_note.APIResponse;
import com.example.eunyoungha.r_multi_note.NetworkRequestUtil;
import com.example.eunyoungha.r_multi_note.models.MemoList;
import com.example.eunyoungha.r_multi_note.interfaces.NoteAPICallback;
import com.example.eunyoungha.r_multi_note.models.UserInformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by eunyoung.ha on 2017/11/14.
 */

public class NoteAPI extends AsyncTask <Object, Object, APIResponse>{

    private static final String TAG = NoteAPI.class.getCanonicalName();

    //private final Context mContext;
    private final NoteAPICallback mCallback;

    public NoteAPI(NoteAPICallback mCallback) {
        this.mCallback = mCallback;
    }

    @Override
    protected APIResponse doInBackground(Object[] params) {
        String status = (String) params[0];
        String url = (String) params[1];

        switch(status){
            case "login":
                return doUserAuthenticate(url);
            case "getMemo":
                return doGetMemos(url);
            case "photoDetails":
                return doGetPhotos(url);
            case "videoDetails":
                return doGetVideos(url);
            case "mapDetails":
                return doGetMaps(url);
            case "getSpecificMemo":
                return doGetSpecificMemo(url);
        }
        return null;
    }

    @Override
    protected void onPostExecute(APIResponse response) {
        mCallback.processFinish(response);
        super.onPostExecute(response);
    }

    private APIResponse doUserAuthenticate(String url){
        APIResponse response = new APIResponse();

        try {
            JSONObject result = NetworkRequestUtil.doHttpPost(url);
            JSONObject userInfo = result.getJSONObject("user");
            UserInformation user = new UserInformation(userInfo);

            response.setUser(user);
        }catch (JSONException e){}



        return response;
    }

    private APIResponse doGetMemos(String url){
        APIResponse response = new APIResponse();
        response.setOperation("getAllMemo");

        try {
            JSONObject result = NetworkRequestUtil.doHttpPost(url);
            if (result != null) {
                JSONArray memo = result.getJSONArray("memo");
                ArrayList<MemoList> memoLists = new ArrayList<>();
                for(int i = 0 ; i < memo.length() ; i++){
                    JSONObject list = (JSONObject) memo.get(i);
                    MemoList memoList = new MemoList(list);
                    memoLists.add(memoList);
                }
                response.setMemoList(memoLists);
            }
        } catch (JSONException e){

        }
        return response;
    }

    private APIResponse doGetSpecificMemo(String url){
        APIResponse response = new APIResponse();
        response.setOperation("getSpecificMemo");
        try {
            JSONObject result = NetworkRequestUtil.doHttpPost(url);
            if (result != null) {
                JSONArray memo = result.getJSONArray("memo");
                ArrayList<MemoList> memoLists = new ArrayList<>();
                for(int i = 0 ; i < memo.length() ; i++){
                    JSONObject list = (JSONObject) memo.get(i);
                    MemoList memoList = new MemoList(list);
                    memoLists.add(memoList);
                }
                response.setMemoList(memoLists);
            }
        } catch (JSONException e){

        }
        return response;
    }

    private APIResponse doGetPhotos(String url){
        APIResponse response = new APIResponse();
        response.setOperation("getPhoto");

        try{
            JSONObject result = NetworkRequestUtil.doHttpPost(url);
            if(result != null){
                response.setPhotoUri(result.getString("photouri"));
            }
        }catch (JSONException e){

        }
        return response;
    }

    private APIResponse doGetVideos(String url){
        APIResponse response = new APIResponse();
        response.setOperation("getVideo");

        try{
            JSONObject result = NetworkRequestUtil.doHttpPost(url);
            if(result != null){
                response.setVideoUri(result.getString("videouri"));
            }
        }catch (JSONException e){

        }
        return response;
    }

    private APIResponse doGetMaps(String url){
        APIResponse response = new APIResponse();
        response.setOperation("getMap");

        try{
            JSONObject result = NetworkRequestUtil.doHttpPost(url);
            if(result != null){
                response.setLatitude(result.getDouble("latitude"));
                response.setLongitude(result.getDouble("longitude"));
            }
        }catch (JSONException e){

        }
        return response;
    }
}
