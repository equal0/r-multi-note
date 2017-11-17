package com.example.eunyoungha.r_multi_note;

import com.example.eunyoungha.r_multi_note.models.MemoList;
import com.example.eunyoungha.r_multi_note.models.UserInformation;

import java.io.Serializable;
import java.util.List;

/**
 * Created by eunyoung.ha on 2017/11/14.
 */

public class APIResponse implements Serializable{

    private List<MemoList> memoList;
    private UserInformation user;

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
}
