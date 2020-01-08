package com.example.spayserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class SmsListener extends BroadcastReceiver {

    DatabaseHandler db;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    String phoneNo,message;


    @Override
    public void onReceive(Context context, Intent intent) {
        db = new DatabaseHandler(context);
        Log.d("Sap","Reached");
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from;
            Log.d("Sap","Reached there");
            if (bundle != null){
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();
                        process(msgBody,msg_from,context);
//                        Toast.makeText(context, msg_from+"\n"+msgBody, Toast.LENGTH_LONG).show();
                    }
                }
                catch(Exception e){
                    Log.d("Exception caught",e.getMessage());
                }
            }
        }
    }

    private void process(String msgBody, String msg_from, Context context) throws NoSuchAlgorithmException {
        if(msgBody.startsWith("##")){
            msgBody=msgBody.substring(2);
            Log.e("sap",msgBody);
            msgBody=decrypt(msgBody);
            Log.e("sap",msgBody);
            String ar[]=msgBody.split("##");
            if(ar.length<4)
                return;
            if(ar[0].equals("R1")){
                Log.e("sap","Entered R1");
                if(("+91"+ar[1]).equals(msg_from)){
                    Log.e("sap","Confirmed number");
                    String key="";
                    for(int i=0;i<16;i++)
                        key+=(char)(Math.random()*256);
                    MessageDigest md = MessageDigest.getInstance("SHA-512");
                    byte[] digest = md.digest(ar[3].getBytes());
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < digest.length; i++) {
                        sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
                    }
                    ar[3]=sb.toString();
                    db.addUser(Long.parseLong(ar[1]), ar[2], ar[3], key, 200);
                    ArrayList<User> ar1=db.showUsers();
                    for(User i : ar1){
                        Log.e("cat",i.getMob()+" -> "+i.getUserName());
                    }
                }
                else {
                    Log.e("sap","Incorrect Number!");
                    return;
                }
            }
            else {

                return;
            }
        }
        else if(msgBody.startsWith("#?")) {
            msgBody=msgBody.substring(2);
            Log.e("encr : ",msgBody);
            msgBody=decrypt(msgBody);
            Log.e("decr : ",msgBody);
            String ar[] = msgBody.split("##");
            if (ar.length < 4)
                return;
            if(ar[0].equals("R2")){
                if(("+91"+ar[1]).equals(msg_from)){
                    readFromDB(ar[1],ar[2],context);
                }
                else{
                    Toast.makeText(context,"Incorrect Number of Sender",Toast.LENGTH_LONG).show();
                    return;
                }

            }
        }
        else if(msgBody.startsWith("??")){
            msgBody=msgBody.substring(2);
            Log.e("sap",msgBody);
            Encryption encrypter = null;
            try {
                encrypter = new Encryption("fqJfdzGDvfwbedsKSUGty3VZ9taXxMVw".getBytes("UTF-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            byte[] decrypted;
            try {
                decrypted = encrypter.decrypt(Base64.decode(msgBody.getBytes("UTF-8"), Base64.NO_WRAP));
                msgBody = new String(decrypted,"UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }

            String ar[] = msgBody.split("##");
            if (ar[0].equals("T1")) {
                String qu = "Select * from " + "TRANSACTIONS_TABLE" + " where Transaction_id = " + ar[3];
                Cursor cr = db.getReadableDatabase().rawQuery(qu,null);
                Log.e("sap",cr.getCount()+"");
                if(cr.getCount() > 0){
                    cr.close();
                    Log.e("rap","Transaction exists "+qu+" "+qu);
                    return;
                }
                cr.close();
                int y=updateBalance(context, msg_from, ar[1], ar[2]);
                Log.e("sap",y+"");
                if(y==1){
                    db.addTransaction(msg_from.substring(3), ar[1], ar[2], ar[3]);
                }
            }
            else
                return;
        }
        else if(msgBody.startsWith("^^")){
            msgBody=msgBody.substring(2);
            Log.e("sap",msgBody);
            Encryption encrypter = null;
            try {
                encrypter = new Encryption("fqJfdzGDvfwbedsKSUGty3VZ9taXxMVw".getBytes("UTF-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            byte[] decrypted;
            try {
                decrypted = encrypter.decrypt(Base64.decode(msgBody.getBytes("UTF-8"), Base64.NO_WRAP));
                msgBody = new String(decrypted,"UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("sap",msgBody);
            if(msgBody.equals("Request for Balance")){
                String query = "SELECT * FROM USERS WHERE User_Mobile_Number = "+msg_from.substring(3);
                SQLiteDatabase db = new DatabaseHandler(context).getReadableDatabase();
                String balance = "";
                try {

                    Cursor cursor = db.rawQuery(query, null);
                    try {

                        if (cursor.moveToFirst()) {
                            do {
                                balance = ""+cursor.getString(4);
                                break;
                            } while (cursor.moveToNext());
                        }

                    } finally {
                        try { cursor.close(); } catch (Exception ignore) {}
                    }

                }
                finally {
                    try {
                        db.close();
                    }
                    catch (Exception ignore) {}
                }
                message = "&&"+balance;
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(msg_from, null, message, null, null);
            }
        }
    }

    private String decrypt(String message) {
        BigInteger d=new BigInteger("13127113444053596712267908320957889390091086444980490405868911433855747859661");
        BigInteger n=new BigInteger("65635567220267983561339541604789446950968621224933658204152251027063985561009");
        return new String(new BigInteger(message).modPow(d, n).toByteArray());
    }

    public class Encryption {

        private SecretKeySpec skeySpec;
        private Cipher cipher;

        public Encryption(byte[] keyraw) throws Exception {
            if (keyraw == null) {
                byte[] bytesOfMessage = "".getBytes("UTF-8");
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] bytes = md.digest(bytesOfMessage);

                skeySpec = new SecretKeySpec(bytes, "AES");
                cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            } else {

                skeySpec = new SecretKeySpec(keyraw, "AES");
                cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

            }
        }

        public byte[] encrypt(byte[] plaintext) throws Exception {
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] ciphertext = cipher.doFinal(plaintext);
            return ciphertext;
        }

        public byte[] decrypt(byte[] ciphertext) throws Exception {
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] plaintext = cipher.doFinal(ciphertext);
            return plaintext;
        }
    }

    private int updateBalance(Context context, String msg_from, String s, String s1) {
        try {
            String TABLE_NAME1 = "USERS";
            String sender = msg_from.substring(3);
            String read_Amount = "SELECT User_Balance FROM " + TABLE_NAME1 + " WHERE User_Mobile_Number " + " = " + sender;
            db = new DatabaseHandler(context);
            Cursor c = db.getWritableDatabase().rawQuery(read_Amount, null);
            if (c == null)
                return 0;
            c.moveToFirst();
            String amount_sender = c.getString(0);
            long diff = Long.parseLong(s1);
            if (diff > Long.parseLong(amount_sender)) {
                Toast.makeText(context, "Balance insufficient", Toast.LENGTH_LONG).show();
                Log.e("sap", "Balance Insufficient");
                return 0;
            }
            String read_Amount1 = "SELECT User_Balance FROM " + TABLE_NAME1 + " WHERE User_Mobile_Number " + " = " + s;
            c = db.getWritableDatabase().rawQuery(read_Amount1, null);
            if (c == null)
                return 0;
            c.moveToFirst();
            String amount_recipient = c.getString(0);
            Log.e("difference",""+diff+" "+amount_recipient+" "+amount_sender+" "+s+" "+sender);
            String Update_Table = "UPDATE " + TABLE_NAME1 + " SET User_Balance = " + (Long.parseLong(amount_recipient) + diff) + " WHERE User_Mobile_Number = " + s+ ";";
            String Update_Table1 = "UPDATE " + TABLE_NAME1 + " SET User_Balance = " + (Long.parseLong(amount_sender) - diff) + " WHERE User_Mobile_Number = " + sender+ ";";
            Log.e("sap",Update_Table);
            Log.e("sap",Update_Table1);
            db.getWritableDatabase().execSQL(Update_Table);
            db.getWritableDatabase().execSQL(Update_Table1);
            db.close();
            db=new DatabaseHandler(context);
            c = db.getReadableDatabase().rawQuery(read_Amount, null);
            if (c == null)
                return 0;
            c.moveToFirst();
            amount_sender = c.getString(0);
            Log.e("sap",amount_sender);
            Log.e("sap", "Transaction Successful");
            phoneNo = "+91"+sender;
            message = "Transaction Successful";
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(context, "SMS Sent!", Toast.LENGTH_LONG).show();
            db.close();
            return 1;
        }
        catch(Exception e){
            Log.e("saptarshi",e.getMessage());
        }
        return 1;
    }

    private void readFromDB(String number, String password, Context context) {
        try {
            db = new DatabaseHandler(context);
            String Query = "Select * from " + "USERS" + " where User_Mobile_Number = " + number;
            Cursor cursor = db.getReadableDatabase().rawQuery(Query, null);
            if(cursor.getCount() == 0){
                cursor.close();
                Log.e("rap","Not Registered "+Query);
                return;
            }
            cursor.moveToFirst();
            if(cursor==null){
                Log.e("rap","Null ");
                cursor.close();
                return;
            }
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] digest = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < digest.length; i++) {
                sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
            }
            Log.e("sap",sb.toString());
            if(!cursor.getString(2).equals(sb.toString())){
                Log.e("Sap","Wrong Password");
                Toast.makeText(context,"Incorrect Password in Login Request",Toast.LENGTH_LONG).show();
                cursor.close();
                return;
            }
            String k=cursor.getString(3);
            cursor.close();
            sendKey(number,k,context,password);
        }
        catch(Exception e){
            Log.e("Exception", "File read failed: " + e.toString());
        }
    }

    void sendKey(String number, String key, Context context, String password){
        phoneNo = "+91"+number;
        message = "$_"+key;
        Log.e("sap","Reached1");
        String s="";
        while(s.length()<32){
            s=s+password;
        }
        Encryption encrypter = null;
        try {
            encrypter = new Encryption(s.substring(0,32).getBytes("UTF-8"));
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
            message = "!!"+ Base64.encodeToString(encrypted, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNo, null, message, null, null);
        Toast.makeText(context, "SMS Sent!",
                Toast.LENGTH_LONG).show();
        Toast.makeText(context, key+" sent to number "+number,Toast.LENGTH_LONG).show(); // send message to client with key
    }
}
