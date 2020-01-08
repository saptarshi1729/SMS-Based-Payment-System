package com.example.spayserver;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ShowUsers extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);
//        ArrayList<String> ar1=getIntent().getExtras().getStringArrayList("users");
//        User u1=new User()
        ArrayList<String> ar=new ArrayList<>();
        DatabaseHandler db = new DatabaseHandler(this);
        ArrayList<User> ar1=db.showUsers();
        if(ar1.size()==0){
            ar.add(("There are no users registered in the database").toUpperCase());
        }
        else{
            for(User u : ar1){
                ar.add("Mobile Number : "+u.getMob()+"\nName : "+u.getUserName()+"\nPassword : "+u.getPassword()+"\nKey : "+u.getKey()+"\nAmount : "+u.getAmount()+"\n");
            }
        }


//        transaction t1 = new transaction(v1,v2,v3,v4,str);
//        list.add(t1.getid()+"\n"+t1.getTo()+"\n"+t1.getFrom()+"\n"+t1.getAmount()+"\n"+t1.getDate()+"\n");
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.activity_listview, ar);

        ListView listView = findViewById(R.id.mobile_list);
        listView.setAdapter(adapter);
    }
}
