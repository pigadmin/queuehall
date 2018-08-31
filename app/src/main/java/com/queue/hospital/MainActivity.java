package com.queue.hospital;

import android.os.StrictMode;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Locale;
import java.util.Timer;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    public TextToSpeech textToSpeech; // TTS对象
    private Timer timer;
    private MissionCall missionCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除标题
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // 初始化配置
        initConfig();
        TextView timeView = (TextView) findViewById(R.id.view_time);
        timeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 启动定时器
        timer.schedule(missionCall, 0, 1*5000);
    }



    /**
     * 初始化配置
     */
    private void initConfig() {
        textToSpeech = new TextToSpeech(this, this);
        textToSpeech.setPitch(1.0f);// 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
        timer = new Timer();
        missionCall = new MissionCall(MainActivity.this);
    }

    /**
     * 语音播报初始化
     * @param status
     */
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.CHINA);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "数据丢失或不支持", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        textToSpeech.stop();
        textToSpeech.shutdown();
        textToSpeech = null;
        timer.cancel();
        timer = null;
    }
}
