package com.example.s_pay;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import static com.example.s_pay.MainActivity.passf;

public class SMSListener extends BroadcastReceiver {

    private Navigator nav;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Sap","Reached");
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();
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
                    }
                }
                catch(Exception e){
                    Log.d("Exception caught",e.getMessage());
                }
            }
        }
    }

    private void process(String msgBody, String msg_from, Context context) {
        Log.e("Cat",msg_from+" "+msgBody);
        if(!msg_from.equals("+918777026772"))
            return;
        if(msgBody.startsWith("&&")){
            Toast.makeText(context,"Outstanding Balance : Rs. "+msgBody.substring(2),Toast.LENGTH_LONG).show();
            return;
        }
        if(msgBody.startsWith("!!")){
            msgBody=msgBody.substring(2);
            Log.e("sap",msgBody);
            Encryption encrypter = null;
            String s="";
            while(s.length()<32){
                s=s+passf;
            }
            try {
                encrypter = new Encryption(s.substring(0,32).getBytes("UTF-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            byte[] decrypted;
            try {
                decrypted = encrypter.decrypt(Base64.decode(msgBody.getBytes("UTF-8"), Base64.NO_WRAP));
                //encrypter.decrypt(msgBody.getBytes("UTF-8"));
                msgBody = new String(decrypted,"UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(msgBody.length()!=18) {
                Log.e("SSap","Length="+msgBody.length());
                return;
            }if(!msgBody.substring(0,2).equals("$_")) {
                Log.e("SSap-",msgBody.substring(0,2)+"_");
                return;
            }
            String filePath = context.getFilesDir().getPath() + "/state.txt";
            Log.e("SSap",filePath);
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
            }
            catch (IOException e) {
                Log.e("Sap",e.toString());
                e.printStackTrace();
            }
            try {
                fr.write("1"+" "+msgBody.substring(2));
                fr.close();
                Log.e("Sap","File Re-written");
                Intent intent = new Intent(context, TransferMoney.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                Log.e("Sap","Navigate Reached");
            }
            catch (IOException e) {
                Log.e("Sap",e.toString());
                e.printStackTrace();
            }
            return;
        }
        if(msgBody.equals("Transaction Successful")){
            Toast.makeText(context,"Transaction Successful",Toast.LENGTH_LONG).show();
            Log.e("sap","Transaction Successful");
            return;
        }

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
