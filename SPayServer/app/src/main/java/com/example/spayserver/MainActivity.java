package com.example.spayserver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{

    Button showUsers, showTrans;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    String phoneNo,message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showUsers = findViewById(R.id.showUsers);
        showTrans = findViewById((R.id.showTrans));

//        DatabaseHandler db = new DatabaseHandler(this);
//        db.getWritableDatabase().execSQL("DELETE FROM USERS WHERE User_Mobile_Number = 9140703147");
//        db.close();


//        addTrans = findViewById(R.id.addTrans);

//        final DatabaseHandler db = new DatabaseHandler(this);

//        addTrans.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                db.addTransaction();
//            }
//        });

        showUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShowUsers.class);
                startActivity(intent);
            }
        });

//        showTrans.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                db.showTrans();
//            }
//        });

        showTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShowTransactions.class);
                startActivity(intent);
            }
        });

    }
}
