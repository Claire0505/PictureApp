package com.claire.pictureapp;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//加入 Android 6.0以上的危險權限
import android.Manifest;
import static android.Manifest.permission.*;

import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleCursorAdapter;


public class MainActivity extends AppCompatActivity implements
                        LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {
    //定義一個常數，代表向使用者要求讀取外部儲存裝置辦識值
    private static final int REQUEST_READ_STORAGE = 3;

    SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //讀取外部儲存裝置權限
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED){
            //未取得權限，向使用者要求允許權限
            //第一個參數傳入 Context 物件
            //第二個參數字串陣列則是欲要求的權限
            //第三個參數 int 是本次請求的辦識編號
            ActivityCompat.requestPermissions(this,
                    new String[]{READ_EXTERNAL_STORAGE},
                    REQUEST_READ_STORAGE);
        } else {
            //已有權限，可進行檔案存取
            readThumbnails();
        }
    }

    //不論使用者選擇Deny拒絕或Allow允許，都會自動執行onRequestPermissionsResult方法
    // 覆寫 onRequestPermissionsResult方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case REQUEST_READ_STORAGE:
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){
                    //取得讀取外部權限，進行存取
                    readThumbnails();
                } else {
                    //使用者拒絕權限，顯示對話框告知
                    new AlertDialog.Builder(this)
                            .setMessage("必須允許讀取外部儲存權限才能顯示圖檔")
                            .setPositiveButton("OK", null)
                            .show();
                }
        }
    }

    //查詢縮圖
    private void readThumbnails() {
        GridView grid = findViewById(R.id.grid);
        String[] from = {MediaStore.Images.Thumbnails.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME};
        int[] to = new int[] {R.id.thumb_image, R.id.thumb_text};

        adapter = new SimpleCursorAdapter(
                getBaseContext(),
                R.layout.thumb_item,  //自建立單項縮圖的版面
                null,
                from,
                to,
                0);
        grid.setAdapter(adapter);
        getLoaderManager().initLoader(0, null, this);

        //點擊後顯示圖檔，實作項目點擊事件
        grid.setOnItemClickListener(this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //使用android.net.Uri 先儲存查詢的資料位置
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        //產生並回傳資料讀取物件，並將uri傳遞給它
        return  new CursorLoader(this, uri, null, null, null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //當資料讀取器客內容提供者查詢完成時，會自動呼叫onLoadFinished方法，
        //此時，即呼叫Adapter的swapCursor方法，替換adapter內的Cursor物件
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    //實作「onItemClick()」方法
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, DetailActivity.class);
        //將目前點擊的項目位置放入Intent物件中
        intent.putExtra("POSITION", position);
        startActivity(intent);

    }
}
