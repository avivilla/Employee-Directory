package com.example.EmployeeDirectory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME="EmployeeDb01.db";
    public static final String TABLE_NAME="employee";
    public static final String COL_1="ID";
    public static final String COL_2="NAME";
    public static final String COL_3="AGE";
    public static final String COL_4="GENDER";
    public  static final String COL_5="IMAGE";


    public DbHelper(Context context) {
        super(context, DATABASE_NAME,null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME +" (id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,age INTEGER,gender GENDER,image BLOB)");
       // System.out.println("created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }


    ////////////////////////////////    INSERTION       ////////////////////////////////////


    public boolean insertData(String name,Integer age,String gender,String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,name);
        contentValues.put(COL_3,age);
        contentValues.put(COL_4,gender);
        contentValues.put(COL_5,image);
        long result = db.insert(TABLE_NAME,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }


    ////////////////////////////            RETRIEVE ALL DATA     ////////////////////////////////


    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select id,name,gender,age,image from "+TABLE_NAME,null);
        return res;
    }


    ///////////////////////////////////            RETRIEVE DATA BY ID       ///////////////////////////////



    public Cursor getByID(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select name,age,gender,image from "+TABLE_NAME + " where id = "+id,null);
        return res;
    }



    /////////////////////////////////          DELETE DATA        /////////////////////////
    public Integer deleteData (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?",new String[] {id});
    }


    /////////////////////////////       GET LAST ID  //////////////////////


    public  Integer getSize()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select id from "+TABLE_NAME + " order by id desc limit 1",null);
        if(res.getCount()==0)
            return 1;
       if(res.moveToFirst())
       {
           return res.getInt(0)+1;
       }
       return 1;
    }


    /////////////////////////////////         UPDATE DATA      /////////////////////////////////////


    public boolean updateData(String id,String name,Integer age,String gender,String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,id);
        contentValues.put(COL_2,name);
        contentValues.put(COL_3,age);
        contentValues.put(COL_4,gender);
        contentValues.put(COL_5,image);
        db.update(TABLE_NAME, contentValues, "ID = ?",new String[] { id });
        return true;
    }


}
