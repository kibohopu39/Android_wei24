package com.example.android_wei24;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
//控制service
//activity 死掉，service 繼續
//activity再啟動，繼續控制service
//這裡做從app本身播放
//從外部播放
public class MainActivity extends AppCompatActivity {
    private SeekBar seekBar;
    private MyReceiver myReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {//啟動app初始運行
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //問權限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    123);

        }else{//已經有權限的情況
            init();
        }

        seekBar=findViewById(R.id.seek);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){//當播放桿位置變動的時候，我們要確定是不是玩家拉的
                    Seekto(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {//追蹤開始後的碰觸

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    //按下同意權限的情況
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        init();
    }
    //MediaPlayer 有一個方法可以透過傳送已播放的時間來跳到該處，這裡就是要把已播放時間送到service

    private void Seekto(int seekto){
        Intent intent=new Intent(this,MyService.class);
        intent.putExtra("action","seekto");//name、where都是用來搜尋後面值的索引
        intent.putExtra("where",seekto);
        startService(intent);

    }

    @Override
    protected void onStart() {
        super.onStart();
        myReceiver=new MyReceiver();
        IntentFilter filter=new IntentFilter("fromService");//只收帶有"fromService"的intent
        registerReceiver(myReceiver,filter);

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(myReceiver);
    }
///////////////////////////////////////
    public void test1(View view) {
        Intent intent=new Intent(this,MyService.class);
        intent.putExtra("action","start");
        startService(intent);
    }
    public void test2(View view) {
        Intent intent=new Intent(this,MyService.class);

        stopService(intent);
    }
    public void test3(View view) {
        Intent intent=new Intent(this,MyService.class);
        intent.putExtra("action","pause");
        startService(intent);
    }

    //廣播接受器
    private class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            int len=intent.getIntExtra("length",-1);
            Log.v("leo","length:"+len);

            if(len>0) seekBar.setMax(len);
            int now=intent.getIntExtra("now",-1);
            if(now<len) seekBar.setMax(now);
        }
    }
}
