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

import java.math.BigInteger;

public class Register extends AppCompatActivity {

    Button button;
    EditText name, pass, number;
    TextView tv;
    String phoneNo, message, num;
    RelativeLayout back;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        button=findViewById(R.id.btn);
        name=findViewById(R.id.ansuname);
        pass=findViewById(R.id.anspass);
        back=findViewById(R.id.back);
        number=findViewById(R.id.ansnumber);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                View view = getCurrentFocus();
//                if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
//            }
        });
        tv=findViewById(R.id.tv);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, MainActivity.class);
                startActivity(intent);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMSMessage();
            }
        });

    }

    private void sendSMSMessage() {
        phoneNo = "+918777026772";
        String name1=name.getText().toString().trim();
        String pass1=pass.getText().toString().trim();
        num=number.getText().toString().trim();
        if(name1.equals("")){
            Toast.makeText(this,"Username Field must be non-empty",Toast.LENGTH_LONG).show();
            return;
        }
        if(pass1.equals("")){
            Toast.makeText(this,"Password Field must be non-empty",Toast.LENGTH_LONG).show();
            return;
        }
        if(num.equals("")){
            Toast.makeText(this,"Phone Number Field must be non-empty",Toast.LENGTH_LONG).show();
            return;
        }
        message = "R1##"+num+"##"+name1+"##"+pass1;



//        EllipticCurve c = new EllipticCurve(4, 20, 29, new Point(1, 5));
//
//        ECC ecc = new ECC(c);
//
////        String msg = "i understood the importance in principle of public key cryptography, but it is all moved much faster than i expected i did not expect it to be a mainstay of advanced communications technology";
//        String msg = message;
//        // generate pair of keys
//
//        BigInteger privateKey = new BigInteger("18");
//        Point publicKey = new Point(new BigInteger("2"),new BigInteger("23"));
//
//
//        KeyPair keys = new KeyPair(new PublicKey(c,publicKey),new PrivateKey(c,privateKey));
//
//        Log.e("sap",msg+" "+keys.getPublicKey());
//
//        // encrypt the msg
//        int[] cipherText = ecc.encrypt(msg, keys.getPublicKey());
//        //Helpers.print(cipherText);
//        Log.e("sap",""+cipherText[0]);
//
//
//
//        // decrypt the result
////        String plainText = ecc.decrypt(cipherText, keys.getPrivateKey());
//
//        //System.out.println("Cipher : ");
//        Helpers.print(cipherText);
//        System.out.println(keys.getPrivateKey()+" "+keys.getPublicKey());
//        Helpers.print(cipherText);
//        Log.e("arun","Plain text : \n" + plainText);
        message="##"+encrypt(message).toString();
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
}
