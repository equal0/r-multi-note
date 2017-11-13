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

    final String ID = "_id";
    final String INPUT_DATE = "input_date";
    final String CONTEXT_TEXT = "context_text";
    final String ID_PHOTO = "id_photo";
    final String ID_VIDEO = "id_video";
    final String ID_VOICE = "id_voice";

    final String URI = "uri";


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

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //우선 텍스트메모만 저장 가능한 상황이라고 가정, 날짜와 텍스트만 저장한다
    protected void insert(String date, String content, int photoId){
        SQLiteDatabase db = getWritableDatabase();

        String insertSql = "INSERT INTO " + TABLE_MEMO + " VALUES(null,'"+date+"','"+content+"',"+photoId+", null, null);";
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

        ArrayList<MemoList> memoList = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT " + ID + ", " + INPUT_DATE + ", " +CONTEXT_TEXT + ", " + ID_PHOTO +" FROM " + TABLE_MEMO + " ORDER BY _id desc";

        Cursor cursor = db.rawQuery(selectQuery,null);

        while(cursor.moveToNext()){
            id = cursor.getInt(0);
            date = cursor.getString(1);
            context_text = cursor.getString(2);
            id_photo = cursor.getInt(3);

            MemoList memo = new MemoList(id, date, context_text,id_photo);

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
}
