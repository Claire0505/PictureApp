package com.claire.pictureapp;

import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

//實作手勢偵測事件傾聽者介面 GestureDetector.OnGestureListener
public class DetailActivity extends AppCompatActivity implements
        GestureDetector.OnGestureListener {
    private int position;
    private ImageView image;
    private Cursor cursor;

    GestureDetector detector;

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

        detector = new GestureDetector(this, this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    //設計一個專門更新圖檔的方法
    private void updateImage() {
        //取得查詢結果中該列的原圖在裝置中儲存的路徑
        String imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        //使用BitmapFactory的 decodeFile 方法讀取路徑中的圖檔，並轉換為Bitmap物件
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        image.setImageBitmap(bitmap);
    }

    //在畫面中按下時
    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    //輕碰螢幕當未放開時
    @Override
    public void onShowPress(MotionEvent e) {

    }

    //輕碰螢幕放開時
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    //使用者按下後，移動時
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    //長觸螢幕時，約兩秒鐘左右
    @Override
    public void onLongPress(MotionEvent e) {

    }

    //使用者快速滑動螢幕後放開時
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        //MotionEvent e1 -- 滑動的起始點，座標 X 值(畫面橫向值)可由 e1.getX()取得，而 Y 值(畫面直向值)e1.getY()取得
        //MotionEvent e2 -- 滑動的結束點，手勢放開的位置
        //float velocityX -- 橫向移動的速度值
        //float velocityY -- 直向移動的速度值
        //取得橫向的距離值，以放開X軸位置減掉 按下時的X軸值，可以得到橫向滑動的距離值
        float distance = e2.getX() - e1.getX(); //取得水平距離
        //假如距離大於100，代表由左往右滑，往前一張圖
        if (distance > 100){
            //向右，往前一張圖，先介斷指標往前一筆是否有資料，若無資料代表目前在第一筆，直接跳到最後一筆，並更新圖檔
           if(!cursor.moveToPrevious()){
               cursor.moveToLast();
           }
           updateImage();

        } else if (distance < -100){  //假如距離小於100，代表由右往左滑，往後一張圖
            //向左，往後一張圖
            if(!cursor.moveToNext()){
                cursor.moveToFirst();
            }
            updateImage();
        }

        return false;
    }
}
