package com.queue.hospital;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xboot.stdcall.DataforHandle;
import com.queue.hospital.util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ConfigActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText editTextIP;//IP地址
    private EditText editTextPort;//端口
    private EditText editDepartmentNum;//部门编码
    private Button buttonConfirm;//确认按钮
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private int limit = 7;
    private TextView textView;  //  警告信息
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除标题
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_config);
        // 初始化试图组件
        initViews();

        // 初始化配置文件
        initConfig();
    }
    /**
     * 初始化配置文件
     */
    private void initConfig() {
        sp = this.getSharedPreferences("configParameter", Context.MODE_PRIVATE);
        editor = sp.edit();
        if(null!=sp && !"".equals(sp.getString("IP","")) && !"".equals(sp.getString("Port","")) && !"".equals(sp.getString("DepartmentNum",""))){
            editTextIP.setText(sp.getString("IP",""));
            editTextPort.setText(sp.getString("Port",""));
            editDepartmentNum.setText(sp.getString("DepartmentNum",""));
            validate();
            setAutoPower(sp.getString("IP", ""), sp.getString("Port", ""));
        }
    }

    /**
     * 关闭自动开关机
     */
    private void cancelAlarm() {
        DataforHandle handle = new DataforHandle();
        handle.setonoff(this, new String[]{"0", "6:00", "20:00"});
    }

    /**
     * 验证地址是否合法
     */
    private void validate() {
        String address = "http://" + editTextIP.getText().toString() + ":" +
                editTextPort.getText().toString() + "/queuecall/facade/department/retrieve" +
                "?departmentCode=" + editDepartmentNum.getText().toString() +
                "&pageNo=" + 1 +
                "&countPerPage=" + limit;

        try {
            HttpUtil.sendOkHttpRequest(address, null, new okhttp3.Callback(){
                /*
                 * 请求失败回调函数
                 * @param call
                 * @param e
                */
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ConfigActivity.this,"IP地址或者端口错误，请验证后输入！", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                /*
                 * 请求成功回调函数
                 * @param call
                 * @param response
                 * @throws IOException
                  */
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String responseData = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final JSONObject resultData = new JSONObject(responseData);
                                // 地址配置正确，验证请求是否成功
                                if (!resultData.getBoolean("success")){
                                    Toast.makeText(ConfigActivity.this, "服务器请求失败,请联系管理员!", Toast.LENGTH_LONG).show();
                                    return;
                                } else if (resultData.getBoolean("success") && null == resultData.getJSONObject("data").getString("department")){
                                    // 请求成功后验证输入的部门代码是否存在
                                    Toast.makeText(ConfigActivity.this, "部门不存在，请重新输入部门代码!", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                if (saveConfig()){
                                    //  验证软件使用期限
                                    valioVerdue(editTextIP.getText().toString(), editTextPort.getText().toString());
                                } else {
                                    Toast.makeText(ConfigActivity.this, "配置文件保存失败,请联系管理员!", Toast.LENGTH_LONG).show();
                                    return;
                                }
                            } catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                    });
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 初始化视图组件
     */
    private void initViews() {
        editTextIP = (EditText) findViewById(R.id.server_ip);
        editTextPort = (EditText) findViewById(R.id.server_port);
        editDepartmentNum = (EditText) findViewById(R.id.department_num);
        buttonConfirm = (Button) findViewById(R.id.button_commit);
        buttonConfirm.setOnClickListener(this);
        textView = (TextView) findViewById(R.id.info_view);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_commit :
                if ("".equals(editTextIP.getText().toString())){
                    Toast.makeText(ConfigActivity.this, "服务器IP不能为空!", Toast.LENGTH_LONG).show();
                    return;
                }
                if ("".equals(editTextPort.getText().toString())){
                    Toast.makeText(ConfigActivity.this, "服务器端口号不能为空!", Toast.LENGTH_LONG).show();
                    return;
                }
                if ("".equals(editDepartmentNum.getText().toString())){
                    Toast.makeText(ConfigActivity.this, "部门代码不能为空!", Toast.LENGTH_LONG).show();
                    return;
                }
                validate();
                setAutoPower(editTextIP.getText().toString(), editTextPort.getText().toString());
                break;
            default:
                break;
        }

    }



    private boolean saveConfig(){
        boolean bResult = true;
        try {
            editor.putString("IP",editTextIP.getText().toString());
            editor.putString("Port", editTextPort.getText().toString());
            editor.putString("DepartmentNum",editDepartmentNum.getText().toString());
            editor.commit();
        } catch (Exception e){
            bResult = false;
            e.printStackTrace();
        }
        return bResult;
    }

    private void startMainActivity(){
        Intent intent = new Intent(ConfigActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void valioVerdue(String ip, String port) {
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        /*获取自动开关机配置*/
        final Request powerRequest = new Request.Builder()
                .url("http://"+ ip +":"+ port +"/queuecall/time/authorizedTime/retrieve").build();
        Call powerCall = mOkHttpClient.newCall(powerRequest);
        powerCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        Toast.makeText(ConfigActivity.this,"软件授权信息获取失败！", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String resultJson = response.body().string();//response.body().string()只能被调用一次
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonResult = new JSONObject(resultJson);
                            if(!jsonResult.getBoolean("success")){
                                textView.setText(R.string.getDataFailInfo);
                                return;
                            }
                            verifyAuthorizationDate(jsonResult.getJSONObject("data"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ConfigActivity.this,"返回结果格式不正确！", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                });
            }
        });
    }

    private void verifyAuthorizationDate(JSONObject date) throws JSONException {
        //  校验软件使用时间是否过期
        Integer year = "null".equals(date.getString("year")) ? 0 : Integer.parseInt(date.getString("year"));
        Integer month = "null".equals(date.getString("month")) ? 0 : Integer.parseInt(date.getString("month"));
        Integer day = "null".equals(date.getString("day")) ? 0 : Integer.parseInt(date.getString("day"));
        Integer hours = "null".equals(date.getString("hours")) ? 0 : Integer.parseInt(date.getString("hours"));
        Integer minutes = "null".equals(date.getString("minutes")) ? 0 : Integer.parseInt(date.getString("minutes"));
        Integer seconds = "null".equals(date.getString("seconds")) ? 0 : Integer.parseInt(date.getString("seconds"));
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month-1, day, hours, minutes, seconds);
        if (calendar.getTimeInMillis() < System.currentTimeMillis()){
            //  软件已过期
            textView.setText(R.string.warn);
            Toast.makeText(this, R.string.warn, Toast.LENGTH_LONG).show();
            return;
        } else {
            textView.setText("");
            startMainActivity();
        }
    }

    private void setAutoPower(String ip, String port) {
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        /*获取自动开关机配置*/
        final Request powerRequest = new Request.Builder()
                .url("http://"+ ip +":"+ port +"/queuecall/time/terminalStartupAndShutdownTime/retrieve").build();
        Call powerCall = mOkHttpClient.newCall(powerRequest);
        powerCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        Toast.makeText(ConfigActivity.this,"自动开关机配置信息获取失败！", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String resultJson = response.body().string();//response.body().string()只能被调用一次
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonResult = new JSONObject(resultJson);
                            if(!jsonResult.getBoolean("success")){
                                // 清空本地自动开关机配置，关闭自动开关机功能
                                cancelAlarm();
                            } else {
                                String onTime = jsonResult.getJSONObject("data").getString("startup");
                                String offTime = jsonResult.getJSONObject("data").getString("shutdown");
                                DataforHandle handle = new DataforHandle();
                                handle.setonoff(ConfigActivity.this, new String[]{"1", onTime, offTime});
                                Toast.makeText(ConfigActivity.this, "开机时间:" + onTime + ",关机时间:" + offTime, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(ConfigActivity.this,"返回结果格式不正确！", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                });
            }
        });
    }
}
