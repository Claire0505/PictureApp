package com.claire.pictureapp;

import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class DetailActivity extends AppCompatActivity {
    private int position;
    private ImageView image;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //取得由上一個Activity所傳遞的點擊項目位置，用整數position儲存，並將其由區域變數提昇為屬性
        position = getIntent().getIntExtra("POSITION", 0);
        image = findViewById(R.id.imageView);

        //產生CursorLoader物件並提供它查詢的位置與條件
        CursorLoader loader = new CursorLoader(this,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,null,null, null);

        //要求它以背景方法查詢，查詢得到的結果儲存於cursor物件，並將其由區域變數提昇為屬性
        cursor = loader.loadInBackground();
        cursor.moveToPosition(position);
        updateImage();
    }

    //設計一個專門更新圖檔的方法
    private void updateImage() {
        //取得查詢結果中該列的原圖在裝置中儲存的路徑
        String imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        //使用BitmapFactory的 decodeFile 方法讀取路徑中的圖檔，並轉換為Bitmap物件
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        image.setImageBitmap(bitmap);
    }
}
