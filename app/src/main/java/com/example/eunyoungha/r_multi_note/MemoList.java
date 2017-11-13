package com.example.eunyoungha.r_multi_note;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by eunyoung.ha on 2017/10/20.
 */

 class MemoList implements Parcelable{

    private int id;
    private String date;
    private String content_text;
    private int id_photo;
    private int id_video;
    private int id_voice;

    public MemoList(int id, String date, String content_text,int id_photo) {
        this.id = id;
        this.date = date;
        this.content_text = content_text;
        this.id_photo = id_photo;
    }

    public MemoList() {
    }

    public MemoList(Parcel source) {
        this.id = source.readInt();
        this.date = source.readString();
        this.content_text = source.readString();
        this.id_photo = source.readInt();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent_text() {
        return content_text;
    }

    public void setContent_text(String content_text) {
        this.content_text = content_text;
    }

    public int getId_photo() {
        return id_photo;
    }

    public void setId_photo(int id_photo) {
        this.id_photo = id_photo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(date);
        dest.writeString(content_text);
        dest.writeInt(id_photo);
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        @Override
        public MemoList createFromParcel(Parcel source) {
            return new MemoList(source);
        }

        @Override
        public MemoList[] newArray(int size) {
            return new MemoList[size];
        }
    };

}
