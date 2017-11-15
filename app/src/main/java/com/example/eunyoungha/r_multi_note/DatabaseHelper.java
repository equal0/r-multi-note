package com.example.eunyoungha.r_multi_note;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by eunyoung.ha on 2017/10/19.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    final String TABLE_MEMO = "MEMO";
    final String TABLE_PHOTO = "PHOTO";
    final String TABLE_VIDEO = "VIDEO";
    final String TABLE_VOICE = "VOICE";
    final String TABLE_MAP = "MAP";

    final String ID = "_id";
    final String INPUT_DATE = "input_date";
    final String CONTEXT_TEXT = "context_text";
    final String ID_PHOTO = "id_photo";
    final String ID_VIDEO = "id_video";
    final String ID_VOICE = "id_voice";
    final String ID_MAP = "id_map";

    final String URI = "uri";
    final String MAKER_NAME = "marker_name";
    final String LATITUDE = "latitude";
    final String LONGITUDE = "longitude";
    final String REALTIME = "real_time";

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "
                    + TABLE_MEMO
                    + "("
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT"
                    + ","+INPUT_DATE+" TEXT"
                    + ","+CONTEXT_TEXT+" TEXT"
                    + ","+ID_PHOTO+" INTEGER"
                    + ","+ID_VIDEO+" INTEGER"
                    + ","+ID_VOICE+" INTEGER"
                    + ","+ID_MAP+" INTEGER"
                    + ")"
        );

        db.execSQL("CREATE TABLE "
                    + TABLE_PHOTO
                    + "("
                    +"_id INTEGER PRIMARY KEY AUTOINCREMENT"
                    +"," + INPUT_DATE + " TEXT"
                    +"," + URI + " TEXT"
                    +")"
        );

        db.execSQL("CREATE INDEX "
                + TABLE_PHOTO + "_IDX"
                + " ON "
                + TABLE_PHOTO + "(" + URI + ");"
        );

        db.execSQL("CREATE TABLE "
                + TABLE_VIDEO
                + "("
                +"_id INTEGER PRIMARY KEY AUTOINCREMENT"
                +"," + INPUT_DATE + " TEXT"
                +"," + URI + " TEXT"
                +")"
        );

        db.execSQL("CREATE INDEX "
                + TABLE_VIDEO + "_IDX"
                + " ON "
                + TABLE_VIDEO + "(" + URI + ");"
        );

        db.execSQL("CREATE TABLE "
                + TABLE_VOICE
                + "("
                +"_id INTEGER PRIMARY KEY AUTOINCREMENT"
                +"," + INPUT_DATE + " TEXT"
                +"," + URI + " TEXT"
                +")"
        );

        db.execSQL("CREATE INDEX "
                + TABLE_VOICE + "_IDX"
                + " ON "
                + TABLE_VOICE + "(" + URI + ");"
        );

        db.execSQL("CREATE TABLE "
                + TABLE_MAP
                + "("
                +"_id INTEGER PRIMARY KEY AUTOINCREMENT"
                +"," + INPUT_DATE + " TEXT"
                +"," + MAKER_NAME + " TEXT"
                +"," + LATITUDE + " REAL"
                +"," + LONGITUDE + " REAL"
                +"," + REALTIME + " TEXT"
                +")"
        );

        db.execSQL("CREATE INDEX "
                + TABLE_MAP + "_IDX"
                + " ON "
                + TABLE_MAP + "(" + MAKER_NAME + ");"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //우선 텍스트메모만 저장 가능한 상황이라고 가정, 날짜와 텍스트만 저장한다
    protected void insert(String date, String content, int photoId, int videoId, int voiceId, int mapId){
        SQLiteDatabase db = getWritableDatabase();

        String insertSql = "INSERT INTO " + TABLE_MEMO + " VALUES(null,'"+date+"','"+content+"',"+photoId+","+videoId+", "+voiceId+","+mapId+");";
        db.execSQL(insertSql);
    }

    protected void update(int id, String content){
        SQLiteDatabase db = getWritableDatabase();

        String updateSql = "UPDATE " + TABLE_MEMO + " SET " + CONTEXT_TEXT + "='" +content
                            + "' WHERE " + ID + "=" + id + ";";
        db.execSQL(updateSql);
        Log.d("TEST",updateSql);
    }

    protected void delete(int id){
        SQLiteDatabase db = getReadableDatabase();

        String deleteSql = "DELETE FROM " + TABLE_MEMO + " WHERE " + ID + "=" + id + ";";

        db.execSQL(deleteSql);
    }

    protected ArrayList<MemoList> getResult() {
        int id;
        String date;
        String context_text;
        int id_photo;
        int id_video;
        int id_voice;
        int id_map;

        ArrayList<MemoList> memoList = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        //String selectQuery = "SELECT " + ID + ", " + INPUT_DATE + ", " +CONTEXT_TEXT + ", " + ID_PHOTO +", " + ID_VIDEO +"," + ID_VOICE + " FROM " + TABLE_MEMO + " ORDER BY _id desc";
        String selectQuery = "SELECT *" + " FROM " + TABLE_MEMO + " ORDER BY _id desc";

        Cursor cursor = db.rawQuery(selectQuery,null);

        while(cursor.moveToNext()){
            id = cursor.getInt(0);
            date = cursor.getString(1);
            context_text = cursor.getString(2);
            id_photo = cursor.getInt(3);
            id_video = cursor.getInt(4);
            id_voice = cursor.getInt(5);
            id_map = cursor.getInt(6);

            MemoList memo = new MemoList(id, date, context_text,id_photo,id_video,id_voice,id_map);

            memoList.add(memo);
        }
        cursor.close();
        return memoList;
    }

    protected void photoInsert(String date, String uri){
        SQLiteDatabase db = getWritableDatabase();

        String sql = "INSERT INTO " + TABLE_PHOTO + " VALUES(null,'" + date + "','" +uri + "');";
        db.execSQL(sql);
    }

    protected int getPhotoId(String uri){
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT _id FROM " + TABLE_PHOTO +" where uri = '" + uri +"';";
        int photoId = -1;
        Cursor cursor = db.rawQuery(sql,null);
        while(cursor.moveToNext()){
            photoId = cursor.getInt(0);
            return photoId;
        }
        cursor.close();
        return photoId;
    }

    protected String getPhotoUri(int photoId){
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT uri FROM " + TABLE_PHOTO + " WHERE _id=" + photoId + ";";
        String uri = null;
        Cursor cursor = db.rawQuery(sql,null);
        while(cursor.moveToNext()){
            uri = cursor.getString(0);
            return uri;
        }
        cursor.close();
        return uri;
    }

    protected void videoInsert(String date, String uri){
        SQLiteDatabase db = getWritableDatabase();

        String sql = "INSERT INTO " + TABLE_VIDEO + " VALUES(null,'" + date + "','" +uri + "');";
        db.execSQL(sql);
    }

    protected int getVideoId(String uri){
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT _id FROM " + TABLE_VIDEO +" where uri = '" + uri +"';";
        int videoId = -1;
        Cursor cursor = db.rawQuery(sql,null);
        while(cursor.moveToNext()){
            videoId = cursor.getInt(0);
            return videoId;
        }
        cursor.close();
        return videoId;
    }

    protected String getVideoUri(int videoId){
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT uri FROM " + TABLE_VIDEO + " WHERE _id=" + videoId + ";";
        String uri = null;
        Cursor cursor = db.rawQuery(sql,null);
        while(cursor.moveToNext()){
            uri = cursor.getString(0);
            return uri;
        }
        cursor.close();
        return uri;
    }

    protected void voiceInsert(String date, String uri){
        SQLiteDatabase db = getWritableDatabase();

        String sql = "INSERT INTO " + TABLE_VOICE + " VALUES(null,'" + date + "','" +uri + "');";
        db.execSQL(sql);
    }

    protected int getVoiceId(String uri){
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT _id FROM " + TABLE_VOICE +" where uri = '" + uri +"';";
        int voiceId = -1;
        Cursor cursor = db.rawQuery(sql,null);
        while(cursor.moveToNext()){
            voiceId = cursor.getInt(0);
            return voiceId;
        }
        cursor.close();
        return voiceId;
    }

    protected String getVoiceUri(int voiceId){
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT uri FROM " + TABLE_VOICE + " WHERE _id=" + voiceId + ";";
        String uri = null;
        Cursor cursor = db.rawQuery(sql,null);
        while(cursor.moveToNext()){
            uri = cursor.getString(0);
            return uri;
        }
        cursor.close();
        return uri;
    }

    protected void mapInsert(String date, String markerName, double latitude, double longitude, String realTime){
        SQLiteDatabase db = getWritableDatabase();

        String sql = "INSERT INTO " + TABLE_MAP + " VALUES(null,'" + date + "','" +markerName + "'," + latitude +"," + longitude + ",'"+realTime+"');";
        db.execSQL(sql);
    }

    protected int geMapId(String realTime){
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT _id FROM " + TABLE_MAP +" where "+REALTIME+"='"+realTime+"';";
        int mapId = -1;
        Cursor cursor = db.rawQuery(sql,null);
        while(cursor.moveToNext()){
            mapId = cursor.getInt(0);
            return mapId;
        }
        cursor.close();
        return mapId;
    }

    protected double getMapLatitude(int mapId){
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT " +LATITUDE +" FROM " + TABLE_MAP + " WHERE _id=" + mapId + ";";
        double latitude = 0;
        Cursor cursor = db.rawQuery(sql,null);
        while(cursor.moveToNext()){
            latitude = cursor.getDouble(0);
            return latitude;
        }
        cursor.close();
        return latitude;
    }

    protected double getMapLongitude(int mapId){
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT " + LONGITUDE +" FROM " + TABLE_MAP + " WHERE _id=" + mapId + ";";
        double longitude = 0;
        Cursor cursor = db.rawQuery(sql,null);
        while(cursor.moveToNext()){
            longitude = cursor.getDouble(0);
            return longitude;
        }
        cursor.close();
        return longitude;
    }
}
