package com.example.eunyoungha.r_multi_note;

import java.io.Serializable;

/**
 * Created by eunyoung.ha on 2017/11/14.
 */

public class APIRequest implements Serializable {

    private String loginUrl;


    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }
}
