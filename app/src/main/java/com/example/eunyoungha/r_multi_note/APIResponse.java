package com.example.eunyoungha.r_multi_note;

import com.example.eunyoungha.r_multi_note.models.MemoList;
import com.example.eunyoungha.r_multi_note.models.UserInformation;

import java.io.Serializable;
import java.util.List;

/**
 * Created by eunyoung.ha on 2017/11/14.
 */

public class APIResponse implements Serializable{

    private String operation;
    private List<MemoList> memoList;
    private UserInformation user;
    private String photoUri;
    private String videoUri;
    private double latitude;
    private double longitude;

    public List<MemoList> getMemoList() {
        return memoList;
    }

    public void setMemoList(List<MemoList> memoList) {
        this.memoList = memoList;
    }

    public UserInformation getUser() {
        return user;
    }

    public void setUser(UserInformation user) {
        this.user = user;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(String videoUri) {
        this.videoUri = videoUri;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
