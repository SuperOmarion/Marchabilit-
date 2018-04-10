package com.sorbonne.isir.etudedelamarchabilite;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
/**
 * Created by Super.Omarion on 18/01/2018.
 */

public class DataBaseH extends SQLiteOpenHelper {
    private static final String TABLE = "users";
    private static final String COL_ID = "ID";
    private static final String COL_name = "nom";
    private static final String COL_mdp = "motdepasse";

    private static final String CREATE_BDD = "CREATE TABLE " + TABLE + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_name + " TEXT NOT NULL, "
            + COL_mdp + " TEXT NOT NULL);";

    public DataBaseH(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_BDD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABLE + ";");
        onCreate(db);
    }

    public boolean insertData(String nom,String mdp){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(COL_name,nom);
        content.put(COL_mdp,mdp);
        long result = db.insert(TABLE,null,content);
        db.close();

        if (result == 1) {
            return false;
        }else{
            return true;
        }
    }

    public Cursor getAllData(){

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE +";" ,null);
        return res;
    }
}