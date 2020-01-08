package com.example.spayserver;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ShowTransactions extends AppCompatActivity {

    ArrayList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);

        list = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + "TRANSACTIONS_TABLE";

        String ar[]={"Transaction ID : ", "Sender Mob No. : ", "Recipient Mob No. : ","Amount Sent : ","Date and Time : "};

        SQLiteDatabase db = new DatabaseHandler(this).getReadableDatabase();
        try {

            Cursor cursor = db.rawQuery(selectQuery, null);
            try {

                if (cursor.moveToLast()) {
                    do {
                        String s="";
                        for(int i=0;i<5;i++){
                            String h = cursor.getString(i);
                            if(i==4)
                                h=h.substring(20);
                            s+=ar[i]+h+((i==4)?"":"\n");
                        }
                        list.add(s);
                    } while (cursor.moveToPrevious());
                }

            } finally {
                try { cursor.close(); } catch (Exception ignore) {}
            }

        }
        finally {
            try { db.close(); } catch (Exception ignore) {}
        }
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.activity_listview, list);
        ListView listView = findViewById(R.id.mobile_list);
        listView.setAdapter(adapter);

    }

}
