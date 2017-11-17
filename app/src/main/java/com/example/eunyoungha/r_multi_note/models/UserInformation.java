package com.example.eunyoungha.r_multi_note.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by eunyoung.ha on 2017/11/17.
 */

public class UserInformation implements Parcelable{

    private static final String TAG = UserInformation.class.getCanonicalName();

    private String userId;
    private String userPassword;

    public UserInformation(JSONObject object){
        try{
            this.userId = object.getString("id");
            this.userPassword = object.getString("password");
        }catch (JSONException e){
            Log.d(TAG,"json exception!");
        }
    }

    public UserInformation(Parcel source){
        this.userId = source.readString();
        this.userPassword = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<UserInformation> CREATOR = new Parcelable.Creator<UserInformation>(){
        @Override
        public UserInformation createFromParcel(Parcel source) {
            return new UserInformation(source);
        }

        @Override
        public UserInformation[] newArray(int size) {
            return new UserInformation[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(userPassword);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
}
