package com.example.android_wei24;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {
    public MyService() {


    }
    /*
    啟動型
    綁定型
     */

    private MediaPlayer mediaplayer;
    private int music_length;
    private Timer timer;
    private File sdroot;


    @Override
    public IBinder onBind(Intent intent) {//綁定型
        // TODO: Return the communication channel to the service.
        // throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate() {//啟動型
        super.onCreate();
        Log.v("leo","oncreate");
        timer=new Timer();
        timer.schedule(new Mytask(),0,100);//每0.1秒回傳拉桿的位置

        //mediaplayer=MediaPlayer.create(this,R.raw.mother);

        mediaplayer=new MediaPlayer();
        mediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//完成prepared
        //遠端 外部 都需要先prepared ，APP內可以不用，這裡還是用



        //要傳回MainActivity讓它處理拉桿位置
        music_length=mediaplayer.getDuration();//得到播放時間
        Intent i=new Intent("fromService");
        i.putExtra("length",music_length);//將總時間傳回去MainActivity
        sendBroadcast(i);//廣播
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("leo","onstartcommand");
        //可以視作一個receiver
        //控制播放的狀態，藉由從activity傳回的東西判斷
        String act=intent.getStringExtra("action");
        if (act.equals("start")){
            mediaplayer.start();
        }else if(act.equals("pause")){
            mediaplayer.pause();
        }else if (act.equals("seekto")&&mediaplayer!=null){
            int where=intent.getIntExtra("where",-1);
            if(where>=0){
                mediaplayer.seekTo(where);//將音樂拉到where的秒數
            }
        }else if (){
            mediaplayer.
        }

        mediaplayer.start();
        return super.onStartCommand(intent, flags, startId);

    }
    // 間隔要不斷送出拉桿的位置讓它移動，這裡設定要傳出什麼訊息
    private class Mytask extends TimerTask {
        @Override
        public void run() {
            if (mediaplayer.isPlaying() && mediaplayer != null) {//播放時且 mediaplayer 也存在的時候
                Intent i=new Intent("fromService");//設定要傳的
                i.putExtra("length",music_length);//類似hashmap的格式，service就能利用length拿到音樂長度
                sendBroadcast(i);//發送
            }
        }
    }
    @Override
    public void onDestroy() {
        if(timer != null){
            timer.cancel();
            timer.purge();
            timer=null;
        }
        if(mediaplayer!=null){
            if(mediaplayer.isPlaying()){
                mediaplayer.stop();
            }
            mediaplayer.release();//釋放記憶體
        }
        Log.v("leo","ondestroy");
        super.onDestroy();
    }

}
