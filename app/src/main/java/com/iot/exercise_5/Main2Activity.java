package com.iot.exercise_5;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Main2Activity extends AppCompatActivity {

    ImageView imageView;
    EditText editText;
    Intent intent;
    Button button;
    static SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        imageView = findViewById(R.id.imageView);
        editText  = findViewById(R.id.editText);
        button    = findViewById(R.id.button);


        SetVisibilityButtonController();

    }
    public void SetVisibilityButtonController(){
        int No = getIntent().getIntExtra("SetVisibilityButtonController",-1);

            if(No != 100){
                button.setVisibility(View.INVISIBLE);
                goruntule();

            }else{
                button.setVisibility(View.VISIBLE);             //EKLE KISMINA GİRİS
                editText.setText("");
            }
    }

    public void getThereAndPickImage(){  //dosyaya gitme ve işlem yapma metodu
        intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);  // IMAGES/MEDIA dosyasına GİT
        startActivityForResult(intent,1);// Dosyaya gittiğine dair kanıt 1 dönmesi
    }

    public void imageViewOnClickAction(View view){  //Image a tıkladığımızda izin olup olmadığını kontrol edecek.
        //when it is clicked the image it will be checked whether there is  permission for accessing the storage

        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            //iznimiz yok ise;
            //If there is not permission for accessing the storage,
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2);//izin isteme ve alınırsa kanıtı 2
        }
        else{                       //*** INTENT ile dosyaya girip resim seçme işleminden önce izinler kontrol edilir(yukarıda)
            getThereAndPickImage(); //iznimiz var ve işleme başladık
        }
    }

    //İzin işlemleri sonrası;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // izin alındı ve kanıt olarak 2 döndü
        //grantResults  --> alınan izin sayısı

        if(requestCode ==2 ){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getThereAndPickImage();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // Kullanıcı Dosyaya erişti ve işlem yaptı veya yapmadı;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Kullanıcı dosyada işlem yaptığı için 1 sonucu dönüyor ve buradaki metod ile kontrol ediyoruz.
        //requestCode --> işlem sonucu 1 mi?
        //resultCode --> işlem başarılı mı?
        //data      --> data karşılığı var mı ?(yani resim seçildi mi)

        if(requestCode ==1 && resultCode==RESULT_OK && data !=null){
            Uri secilenResim = data.getData();

            try {
                Bitmap resminBitmapHali = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),secilenResim); //seçilen resim bitmap'e dönüstürme
                    imageView.setImageBitmap(resminBitmapHali);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //3.Aşama Verileri Database'e kaydetme

    public void save(View view){
            String kaydedilenIsim = editText.getText().toString();

            Bitmap kaydedilenImage = ((BitmapDrawable)imageView.getDrawable()).getBitmap();//Imageview daki seçili resmi bitmap'e dönüştürmek

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                kaydedilenImage.compress(Bitmap.CompressFormat.PNG,100,outputStream); //Bitmap formatında olan imageview den alınmış resim dosyası sıkıştırılır.
            byte[] byteArray = outputStream.toByteArray();

            DataBase(kaydedilenIsim,byteArray);

                Toast.makeText(this, "SAVED", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(Main2Activity.this,MainActivity.class);
                startActivity(intent);

        }

    public void DataBase(String isim, byte[] bytArray){
            try{
                database = this.openOrCreateDatabase("DOSYA",MODE_PRIVATE,null);
                    database.execSQL("CREATE TABLE IF NOT EXISTS arts (name VARCHAR, image BLOB)");

                String sqlString ="INSERT INTO arts (name, image) VALUES (?, ?)";

                SQLiteStatement statement = database.compileStatement(sqlString);
                    statement.bindString(1,isim);
                    statement.bindBlob(2,bytArray);
                    statement.execute();

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    public void goruntule(){
            intent = getIntent();       // Intent ile seçilen resim numarasını alıyoruz.Bu numara ArraList'e kaydedilen datanın numara sırası ile aynı
            int No = intent.getIntExtra("Görüntüle",-1);

            imageView.setImageBitmap(MainActivity.artImage.get(No));
            editText.setText(MainActivity.artNames.get(No));
            editText.setFocusableInTouchMode(false);
            imageView.setEnabled(false);

        }

}

