package com.iot.exercise_5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Intent intent;
    ListView listView;
    static ArrayList<String> artNames;
    static ArrayList<Bitmap> artImage;
    ArrayAdapter arrayAdapter ;
    int indexForDelete;
    byte[] byteArray;
    String temp;


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
            registerForContextMenu(listView);// Listview üzerinde menu açabilmek için tanımlama işlemi yapıyoruz.

        artNames = new ArrayList<String>();
        artImage = new ArrayList<Bitmap>();
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,artNames);  //İsimleri kaydettiğimiz arrayList i bağlıyoruz.
        listView.setAdapter(arrayAdapter);

        gatheringDataFromSQL();
        listViewItemSelected();
        longclick();

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.menu2,menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        indexForDelete =info.position;

        switch (item.getItemId()){
            case R.id.delete_ID:

                artImage.remove(indexForDelete);
                artNames.remove(indexForDelete);

                delete();

                arrayAdapter.notifyDataSetChanged();

                Toast.makeText(this, "'"+temp+"'"+" DELETED", Toast.LENGTH_SHORT).show();
                    return true;

                default :
                    return super.onContextItemSelected(item);
        }
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

                    byteArray = cursor.getBlob(indexImage);
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

    public void longclick(){
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                temp = (String) adapterView.getItemAtPosition(i);
                indexForDelete=i;
                return false;
            }
        });

    }

    public void delete(){
        try{
            Main2Activity.database = this.openOrCreateDatabase("DOSYA",MODE_PRIVATE,null);
            String sqlString = "DELETE FROM arts WHERE name = ?";
            SQLiteStatement statement = Main2Activity.database.compileStatement(sqlString);
            statement.bindString(1,temp);
            statement.execute();

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
