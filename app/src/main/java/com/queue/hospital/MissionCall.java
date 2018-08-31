package com.queue.hospital;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.queue.hospital.adapter.ViewInfoAdapter;
import com.queue.hospital.pojo.Patient;
import com.queue.hospital.pojo.RoomInfo;
import com.queue.hospital.util.HttpUtil;
import com.queue.hospital.util.Util;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Adim on 2018/2/19.
 */

public class MissionCall extends TimerTask implements View.OnClickListener {
    private WeakReference<MainActivity> mainActivityWeakReference;      // 防止内存泄漏
    private String serverIP;
    private String serverPort;
    private String departmentNum;
    private String serverAddress;
    public ViewInfoAdapter adapter;
    private TextView viewDate;
    private TextView viewTime;
    private TextView viewCalling;
    private TextView viewDepartment;
    private ListView listView;
    private MainActivity mainActivity;
    public List<RoomInfo> dataList;
    private int limit = 9;
    private Integer index;
    private int count = 15;
    private Context mContext;
    private int speakCount = 3;     // 语音播报次数
    public MissionCall(MainActivity activity) {
        mainActivityWeakReference = new WeakReference<MainActivity>(activity);
        mainActivity = mainActivityWeakReference.get();
        mContext = mainActivity.getApplicationContext();
        // 初始化视图组件
        initViews();
        // 初始化配置
        initServerConfig();
        // 初始化数据
        initData();
    }

    /**
     * 初始化服务器地址配置
     */
    private void initServerConfig() {
        SharedPreferences preferences = mainActivity.getSharedPreferences("configParameter", MODE_PRIVATE);
        serverIP = preferences.getString("IP", "");
        serverPort = preferences.getString("Port", "");
        departmentNum = preferences.getString("DepartmentNum", "");
        serverAddress = "http://" + serverIP + ":" + serverPort + "/queuecall/facade/department/retrieve" +
                "?departmentCode="  + departmentNum;
    }


    /**
     * 初始化视图组件
     */
    private void initViews() {
        viewDate = (TextView) mainActivity.findViewById(R.id.view_date);
        viewTime = (TextView) mainActivity.findViewById(R.id.view_time);
        viewCalling = (TextView) mainActivity.findViewById(R.id.view_calling);
        listView = (ListView) mainActivity.findViewById(R.id.list_view);
        viewDepartment = (TextView) mainActivity.findViewById(R.id.view_depaerment);
    }

    /**
     * 初始化页面数据
     */
    private void initData(){
        dataList = new ArrayList<>();
        adapter = new ViewInfoAdapter(mContext,R.layout.list_adapter, dataList);
        listView.setAdapter(adapter);
    }
    @Override
    public void run() {
        index = Util.getPageIndex(index, count, limit);
        String address = serverAddress + "&pageNo=" + index + "&countPerPage=" + limit;
        HttpUtil.sendOkHttpRequest(address, null, new okhttp3.Callback(){
            @Override
            public void onFailure(Call call, IOException e) {
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "IP地址或者端口错误，请验证后输入！", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject responseData = new JSONObject(response.body().string());
                            if (responseData.getBoolean("success")){
                                JSONObject jsonData =responseData.getJSONObject("data");
                                count = jsonData.getInt("roomCount");
                                updateTime(jsonData.getString("currentTime"), jsonData.getString("todayDate"));
                                updateData(jsonData);
                            } else {
                                Toast.makeText(mContext, "服务器请求失败,请联系管理员!", Toast.LENGTH_LONG).show();
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });

            }
        });
    }

    /**
     * 更新界面上的数据
     * @param jsonData
     */
    private void updateData(JSONObject jsonData) {
        try {
            // 更新部门信息
            viewDepartment.setText(jsonData.getJSONObject("department").getString("name"));
            List<RoomInfo> list = Util.parseJsonToRoomInfo(jsonData.getJSONArray("roomPatients"));
            dataList.clear();
            dataList.addAll(list);
            adapter.notifyDataSetChanged();
            startSpeak(list);
            clearCallingView();
        } catch (Exception e){
            e.printStackTrace();
        }


    }

    private void clearCallingView() {
        if (null != mainActivity.textToSpeech && !mainActivity.textToSpeech.isSpeaking()){
            // 清空正在呼叫View
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    viewCalling.setText("");
                }
            });
        }
    }

    private void updateTime(String time, String date){
        viewTime.setText(time);
        viewDate.setText(date);
    }

    private void startSpeak(List<RoomInfo> list) throws Exception{
        for (RoomInfo roomInfo : list){
            Patient patient = roomInfo.getFirstCallingPatient();
            if (!"".equals(patient.getName())){
                final String str = " 请第" + patient.getQueueNo() + "号患者" + patient.getName() +"到" + roomInfo.getName() + "就诊";
                if (!mainActivity.textToSpeech.isSpeaking()){
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            viewCalling.setText(str);
                        }
                    });
                }
                if (null != mainActivity.textToSpeech && !mainActivity.textToSpeech.isSpeaking()){
                    for (int i = 0; i < speakCount; i++){
                        if (i == 0){
                            mainActivity.textToSpeech.speak("请" + str, TextToSpeech.QUEUE_ADD, null);
                        } else {
                            mainActivity.textToSpeech.speak(str, TextToSpeech.QUEUE_ADD, null);
                        }
                    }
                    // 更改状态
                    changeStatus(roomInfo.getCode(), patient.getQueueNo());
                }
            }

        }
    }
    private void changeStatus(String roomCode, String queueNo) throws Exception{
        String url = "http://" + serverIP + ":" + serverPort +
                "/queuecall/queue/update/departmentStatusToCalled?roomCode=" + roomCode + "&&queueNo=" + queueNo;
        HttpUtil.sendOkHttpRequest(url,null, new okhttp3.Callback(){
            @Override
            public void onFailure(Call call, IOException e) { }
            @Override
            public void onResponse(Call call, Response response) throws IOException { }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.view_time:
                mainActivity.finish();
                break;
            default:
                break;

        }
    }
}
