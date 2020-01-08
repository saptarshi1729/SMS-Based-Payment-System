package com.example.s_pay;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class TransferMoney extends Activity {

    TextView logout;
    EditText recipient, amt;
    Button submit, balancereq;
    String phoneNo, message;
    RelativeLayout back;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_transfer);

        logout = findViewById(R.id.logout);
        recipient = findViewById(R.id.phone);
        amt = findViewById(R.id.amount);
        submit = findViewById(R.id.btn);
        balancereq = findViewById(R.id.balancereq);
        back = findViewById(R.id.back);

        final Context context = this;

        balancereq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNo = "+918777026772";
                message = "Request for Balance";
                Log.e("saptarshi", message);
                Encryption encrypter = null;
                try {
                    encrypter = new Encryption("fqJfdzGDvfwbedsKSUGty3VZ9taXxMVw".getBytes("UTF-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                byte[] encrypted = {1};
                try {
                    encrypted = encrypter.encrypt(message.getBytes("UTF-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    message = "^^" + Base64.encodeToString(encrypted, Base64.NO_WRAP);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.e("sap", "Reached1");
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(TransferMoney.this,
                            Manifest.permission.SEND_SMS)) {
                    } else {
                        ActivityCompat.requestPermissions(TransferMoney.this,
                                new String[]{Manifest.permission.SEND_SMS},
                                MY_PERMISSIONS_REQUEST_SEND_SMS);
                    }
                }
                else {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, message, null, null);
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNo = "+918777026772";
                String num=recipient.getText().toString().trim();
                String amount=amt.getText().toString().trim();
                if(num.equals("")){
                    Toast.makeText(context,"Phone Number Field must be non-empty",Toast.LENGTH_LONG).show();
                    return;
                }
                if(amount.equals("")){
                    Toast.makeText(context,"Amount Field must be non-empty",Toast.LENGTH_LONG).show();
                    return;
                }
                message = "T1##"+num+"##"+amount+"##"+(long)(Math.random()*(10000000000000000L));
                Log.e("saptarshi",message);
                Encryption encrypter = null;
                try {
                    encrypter = new Encryption("fqJfdzGDvfwbedsKSUGty3VZ9taXxMVw".getBytes("UTF-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                byte[] encrypted={1};
                try {
                    encrypted = encrypter.encrypt(message.getBytes("UTF-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    message = "??"+ Base64.encodeToString(encrypted, Base64.NO_WRAP);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.e("sap","Reached1");
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(TransferMoney.this,
                            Manifest.permission.SEND_SMS)) {
                    }
                    else {
                        ActivityCompat.requestPermissions(TransferMoney.this,
                                new String[]{Manifest.permission.SEND_SMS},
                                MY_PERMISSIONS_REQUEST_SEND_SMS);
                    }
                    Log.e("sap","Reached");
                }
                else {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "SMS Sent!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filePath = context.getFilesDir().getPath() + "/state.txt";
                File file = new File(filePath);
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    Log.e("Sap",e.toString());
                    e.printStackTrace();
                }
                FileWriter fr = null;
                try {
                    fr = new FileWriter(file, false);
                } catch (IOException e) {
                    Log.e("Sap",e.toString());
                    e.printStackTrace();
                }
                try {
                    fr.write("0");
                    fr.close();
                    Log.e("Sap","File Re-written");
                } catch (IOException e) {
                    Log.e("Sap",e.toString());
                    e.printStackTrace();
                }
                Intent intent = new Intent(TransferMoney.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }

//    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
//        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
//        Cipher cipher = Cipher.getInstance("AES");
//        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
//        byte[] encrypted = cipher.doFinal(clear);
//        return encrypted;
//    }



    public void displayToast(View v)
    {
        Toast.makeText(this, "Payment Submission Successful", Toast.LENGTH_SHORT).show();
        thread.start();
    }
    Thread thread = new Thread(){
        @Override
        public void run() {
            try
            {
                Thread.sleep(Toast.LENGTH_LONG);
                TransferMoney.this.finish();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };



    @Override
    public void onBackPressed() {
        finishAffinity();
    }


    public class Encryption {

        private SecretKeySpec skeySpec;
        private Cipher cipher;

        public Encryption(byte [] keyraw) throws Exception{
            if(keyraw == null){
                byte[] bytesOfMessage = "".getBytes("UTF-8");
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] bytes = md.digest(bytesOfMessage);

                skeySpec = new SecretKeySpec(bytes, "AES");
                cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            }
            else{

                skeySpec = new SecretKeySpec(keyraw, "AES");
                cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

            }
        }

        public byte[] encrypt (byte[] plaintext) throws Exception{
            //returns byte array encrypted with key

            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

            byte[] ciphertext =  cipher.doFinal(plaintext);

            return ciphertext;
        }

        public byte[] decrypt (byte[] ciphertext) throws Exception{
            //returns byte array decrypted with key
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);

            byte[] plaintext = cipher.doFinal(ciphertext);

            return plaintext;
        }

//        public void main(String[] args) throws Exception {
//
//            String message="This is just an example";
//            Encryption encrypter = new Encryption(new byte[16]);
//
//            byte[] encrypted = encrypter.encrypt(message.getBytes("UTF-8"));
//            byte[] decrypted = encrypter.decrypt(encrypted);
//            System.out.println(new String (decrypted, "UTF-8"));
//            System.out.println(new String (encrypted, "UTF-8"));
//        }
    }


}
