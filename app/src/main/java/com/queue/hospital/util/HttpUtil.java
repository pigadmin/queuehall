package com.queue.hospital.util;

/**
 * Created by Magic on 2018/1/10.
 */


import android.os.AsyncTask;

import com.queue.hospital.pojo.RoomInfo;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * http请求工具
 * 使用okhttp
 */
public class HttpUtil {
    public static void sendOkHttpRequest(String address, String params, okhttp3.Callback callback){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = null;
        if (null != params){
            RequestBody requestBody = new FormBody.Builder().add("params",params).build();
            request = new Request.Builder().post(requestBody).url(address).build();
        } else {
            request = new Request.Builder().url(address).build();
        }
        okHttpClient.newCall(request).enqueue(callback);
    }
}
