package com.iot.exercise_5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Intent intent;
    ListView listView;
    static ArrayList<String> artNames;
    static ArrayList<Bitmap> artImage;
    ArrayAdapter arrayAdapter ;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =getMenuInflater();
        inflater.inflate(R.menu.menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.menuID){                //Ekle butonuna Tıklandığında
            intent = new Intent(getApplicationContext(),Main2Activity.class);
            intent.putExtra("SetVisibilityButtonController",100); // 100 = EKLE
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);

        artNames = new ArrayList<String>();
        artImage = new ArrayList<Bitmap>();
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,artNames);  //İsimleri kaydettiğimiz arrayList i bağlıyoruz.
        listView.setAdapter(arrayAdapter);

        gatheringDataFromSQL();
        listViewItemSelected();
    }

    public void gatheringDataFromSQL(){

        try {
            Main2Activity.database = this.openOrCreateDatabase("DOSYA",MODE_PRIVATE,null);
            Main2Activity.database.execSQL("CREATE TABLE IF NOT EXISTS arts (name VARCHAR, image BLOB)");

            Cursor cursor = Main2Activity.database.rawQuery("SELECT * FROM arts ",null);

            int indexName = cursor.getColumnIndex("name");
            int indexImage = cursor.getColumnIndex("image");

            cursor.moveToFirst();
                while(cursor != null){

                    artNames.add(cursor.getString(indexName)); //Bulduğun ismi ArrayList e ekle

                    byte[] byteArray = cursor.getBlob(indexImage);
                    Bitmap receivedImageFromSQL = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);

                    artImage.add(receivedImageFromSQL);

                    cursor.moveToNext();

                    arrayAdapter.notifyDataSetChanged();//Eğer bir bilgi değiştirilmiş ise listview e haber ederek güncelleme yapar.

                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void listViewItemSelected(){

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                intent = new Intent(MainActivity.this,Main2Activity.class);
                intent.putExtra("Görüntüle",i);
                startActivity(intent);
            }
        });
    }

}
