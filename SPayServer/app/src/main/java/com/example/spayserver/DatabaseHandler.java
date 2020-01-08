package com.example.spayserver;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;

public class DatabaseHandler extends SQLiteOpenHelper {

    private String TABLE_NAME = "USERS";
    private String COL1 = "User_Mobile_Number";
    private String COL2 = "User_Name";
    private String COL3 = "User_Password";
    private String COL4 = "User_Key";
    private String COL5 = "User_Balance";
    private String TABLE_NAME1 = "TRANSACTIONS_TABLE";
    private String col1 =" Transaction_id";
    private String col2 = "From_sender";
    private String col3 = "To_recipient";
    private String col4 = "Amount";
    private String col5 = "Date";


    public DatabaseHandler(@Nullable Context context) {
        super(context,"MyDB",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "( " + COL1 +
                " BIGINT PRIMARY KEY," + COL2 + " TEXT, " + COL3 + " TEXT, " + COL4 + " TEXT, " + COL5 + " BIGINT " + ")";
        String CREATE_TABLE1 = "CREATE TABLE " + TABLE_NAME1 + "( " + col1 +
                " BIGINT PRIMARY KEY," + col2 + " BIGINT, " + col3 + " BIGINT, " + col4 + "  BIGINT , " + col5 + " STRING " + ")";
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_TABLE1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addUser(long number, String name, String password, String key, int amount) {
        try {
            ContentValues values = new ContentValues();
            values.put(COL1, number);
            Log.e("Sap","Reached AddUser");
            String Query = "Select * from " + TABLE_NAME + " where User_Mobile_Number = " + number;
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(Query, null);
            if(cursor.getCount() > 0){
                cursor.close();
                Log.e("Sap","Duplicate Request of Registration; User with mobile number "+number+" is already present.");
                return;
            }
            cursor.close();
            values.put(COL2, name);
            values.put(COL3, password);
            values.put(COL4, key);
            values.put(COL5, amount);



            db.insert(TABLE_NAME, null, values);
            Log.e("Sap",values.toString());
            db.close();
//            Log.e("Hap",number+" "+name+" "+)
        }
        catch(Exception e){
            Log.e("Sap",e.toString());
        }
    }

    public void addTransaction(String sender, String recipient, String amount, String id){
        Date date = new Date();
        String str = String.format("Current Date/Time : %tc", date );
//        System.out.println(v1+" "+v2+" "+v3+" "+v4+" "+str);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(col1, Long.parseLong(id));
        values.put(col2, Long.parseLong(sender));
        values.put(col3, Long.parseLong(recipient));
        values.put(col4, Long.parseLong(amount));
        values.put(col5, str);
        db.insert(TABLE_NAME1, null, values);
        db.close();

    }

    public ArrayList<User> showUsers(){
        ArrayList<User> users = new ArrayList();
//        String SHOW_TABLE_USER = " SELECT " + " * " + "FROM " +  TABLE_NAME ;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
//        db.execSQL(SHOW_TABLE_USER);
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + ";", null);
            if (cursor.moveToFirst()) {
                do {
//                    tasks.add(cursor.getString(0) + " -> " + cursor.getString(1));
                    users.add(new User(Long.parseLong(cursor.getString(0)),cursor.getString(1),cursor.getString(2),cursor.getString(3),Long.parseLong(cursor.getString(4))));
                } while (cursor.moveToNext());
            }
            Log.e("doggy",""+users.size());
        }
        catch (Exception e){
            Log.e("dog",e.getMessage());
        }
        return users;
    }

    public ArrayList<String> showTrans(){
//        String SHOW_TABLE_TRANS = " SELECT " + " * " + "FROM " +  TABLE_NAME1 ;
        SQLiteDatabase db = this.getReadableDatabase();
//        db.execSQL(SHOW_TABLE_TRANS);
//        String SHOW_TABLE_USER = " SELECT " + " * " + "FROM " +  TABLE_NAME ;
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL(SHOW_TABLE_USER);
        Cursor cursor =db.rawQuery("SELECT * FROM "+TABLE_NAME1+";",null);
        ArrayList<String> tasks = new ArrayList();
        if(cursor.moveToFirst()){
            do{
                tasks.add(cursor.getString(0)+" -> "+ cursor.getString(1));
            }while(cursor.moveToNext());
        }
        return tasks;
    }



}
