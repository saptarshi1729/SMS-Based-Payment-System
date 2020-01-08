package com.example.s_pay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;

public class MainActivity extends AppCompatActivity implements Navigator {

    Button button;
    EditText name, pass;
    TextView tv;
    RelativeLayout back;
    String phoneNo, message;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0 ;
    public static String passf="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button=findViewById(R.id.btn);
        name=findViewById(R.id.ansuname);
        pass=findViewById(R.id.anspass);
        tv=findViewById(R.id.tv);
        back=findViewById(R.id.back);
        final Context context = this;
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECEIVE_SMS)) {
            }
            else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECEIVE_SMS},
                        1);
            }
            Log.e("sap","Reached TT");
        }
        else{
            Log.e("sap","Has Permission");
        }

        try {
            String filePath = context.getFilesDir().getPath() + "/state.txt";
            File file = new File(filePath);
            if(file.exists())
                Log.e("sap","File Exists");
            else
                Log.e("sap","File does not exist");
            BufferedReader br = new BufferedReader(new FileReader(file));

            String st=br.readLine();
            if(st!=null&&st.startsWith("1")){
                Intent intent = new Intent(MainActivity.this, TransferMoney.class);
                startActivity(intent);
            }

//            while ((st = br.readLine()) != null){
//                Log.e("sap",st);
//                if(!st.startsWith(number))
//                    continue;
//                String a[]=st.split("%%");
//                if(!a[2].equals(password))
//                    return;
////                Toast.makeText(context,a[3],Toast.LENGTH_LONG).show();
//                Log.e("sap", a[3]);
//                sendKey(number,a[3],context);
//            }
        }
        catch(Exception e){
            Log.e("Exception", "File read failed: " + e.toString());
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
        });

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Register.class);
//                Intent intent = new Intent(MainActivity.this, TransferMoney.class);
                startActivity(intent);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMSMessage();
//                String filePath = context.getFilesDir().getPath() + "/state.txt";
//                File file = new File(filePath);
//                try {
//                    file.createNewFile();
//                } catch (IOException e) {
//                    Log.e("Sap",e.toString());
//                    e.printStackTrace();
//                }
//                FileWriter fr = null;
//                try {
//                    fr = new FileWriter(file, false);
//                } catch (IOException e) {
//                    Log.e("Sap",e.toString());
//                    e.printStackTrace();
//                }
//                try {
//                    fr.write("1");
//                    fr.close();
//                    Log.e("Sap","File Re-written");
////                    Log.e("fat",(new BufferedReader(new FileReader(file))).readLine());
//                } catch (IOException e) {
//                    Log.e("Sap",e.toString());
//                    e.printStackTrace();
//                }
//                Intent intent = new Intent(MainActivity.this, TransferMoney.class);
//                startActivity(intent);
            }
        });

//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendSMSMessage();
//            }
//        });

    }

    private void sendSMSMessage() {
        phoneNo = "+918777026772";
        String name1=name.getText().toString().trim();
        String pass1=pass.getText().toString().trim();
        if(name1.equals("")){
            Toast.makeText(this,"Username Field must be non-empty",Toast.LENGTH_LONG).show();
            return;
        }
        if(pass1.equals("")){
            Toast.makeText(this,"Password Field must be non-empty",Toast.LENGTH_LONG).show();
            return;
        }
        message = "R2##"+name1+"##"+pass1+"##1";
        passf = pass1;
        message="#?"+encrypt(message).toString();
        Log.e("sap","Reached1");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            }
            else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
            Log.e("sap","Reached");
        }
        else{
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS Sent!",
                    Toast.LENGTH_LONG).show();
        }
    }

    private BigInteger encrypt(String message) {
        BigInteger mess=new BigInteger(message.getBytes());
        BigInteger e=new BigInteger("5");
        BigInteger n=new BigInteger("65635567220267983561339541604789446950968621224933658204152251027063985561009");
        return mess.modPow(e, n);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    public void navigate() {
        Log.e("Sap","Navigate Reached Further");
        Intent intent = new Intent(MainActivity.this, TransferMoney.class);
        startActivity(intent);
    }
}
