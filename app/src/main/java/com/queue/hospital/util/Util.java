package com.queue.hospital.util;

import com.queue.hospital.pojo.Department;
import com.queue.hospital.pojo.Patient;
import com.queue.hospital.pojo.RoomInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adim on 2018/2/19.
 */

public class Util {
    public static int getPageIndex(Integer currentIndex, int count, int limit){
        if (null != currentIndex){
            double pageCountNum = (double) count / limit;
            Integer pageCount = Integer.valueOf((int) Math.ceil(pageCountNum)) ;
            if (!currentIndex.equals(pageCount) ){
                return ++currentIndex;
            }
        }
        return 1;
    }

    public static List<RoomInfo> parseJsonToRoomInfo(JSONArray array){
        List<RoomInfo> result = new ArrayList<RoomInfo>();
        try {
            for (int i = 0; i < array.length(); i++){
                JSONObject obj = array.getJSONObject(i);
                JSONObject roomJSON = obj.getJSONObject("room");
                RoomInfo roomInfo = new RoomInfo();
                roomInfo.setId(roomJSON.getInt("id"));
                roomInfo.setName(roomJSON.getString("name"));
                roomInfo.setCode(roomJSON.getString("code"));
                List<Patient> waitingPatients = new ArrayList<Patient>();
                JSONArray patientList = obj.getJSONArray("waitingPatients");
                for (int j = 0; j < patientList.length(); j++){
                    JSONObject patientJSON = patientList.getJSONObject(j);
                    Patient patient = new Patient(patientJSON);
                    waitingPatients.add(patient);
                }
                roomInfo.setWaitingPatients(waitingPatients);
                // 设置正在就诊和正在呼叫信息
                Patient firstCallingPatient = new Patient(obj, "firstCallingPatient");
                roomInfo.setFirstCallingPatient(firstCallingPatient);
                Patient seeingPatient = new Patient(obj, "seeingPatient");
                roomInfo.setSeeingPatient(seeingPatient);
                result.add(roomInfo);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public static Department parseJsonToDepartment(String params){
        Department department = new Department();
        try {
            JSONObject json = new JSONObject(params);
            department.setName(json.getString("name"));
            department.setId(json.getInt("id"));
            department.setCode(json.getString("code"));
        } catch (Exception e){
            e.printStackTrace();
        }
        return department;
    }
}
