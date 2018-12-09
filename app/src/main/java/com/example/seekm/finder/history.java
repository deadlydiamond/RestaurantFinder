package com.example.seekm.finder;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class history extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final DatabaseHelper myDB;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        myDB = new DatabaseHelper(history.this);
        final TextView tv = (TextView)findViewById(R.id.textView2);

        Cursor res = myDB.getData();
        if (res.getCount()==0){
            Toast.makeText(this, "Not found", Toast.LENGTH_SHORT).show();;
        }
        else{
            StringBuffer buffer = new StringBuffer();
            while(res.moveToNext()){
                buffer.append("Name: " + res.getString(1)+"\n");
                buffer.append("Timestamp: " + res.getString(2)+"\n\n");

            }
            tv.setText(buffer.toString());
        }

    }


}
