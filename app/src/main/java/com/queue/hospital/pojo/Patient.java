package com.queue.hospital.pojo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Adim on 2018/1/30.
 */

public class Patient {
    private String name;
    private String queueNo;

    public String getName() {
        return name;
    }

    public Patient() {
    }

    public Patient(JSONObject json, String key) {
        try {
            if (!"null".equals(json.getString(key))){
                JSONObject data = json.getJSONObject(key);
                this.name = data.getString("name");
                this.queueNo = data.getString("queueNo");
            } else {
                this.name = "";
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
    public Patient(JSONObject json) {
        try {
            this.name = json.getString("name");
            this.queueNo = json.getString("queueNo");
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQueueNo() {
        return queueNo;
    }

    public void setQueueNo(String queueNo) {
        this.queueNo = queueNo;
    }
}
