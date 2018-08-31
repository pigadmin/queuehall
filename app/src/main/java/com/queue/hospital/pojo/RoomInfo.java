package com.queue.hospital.pojo;

import java.util.List;

/**
 * Created by Adim on 2018/1/30.
 */

public class RoomInfo {
    private Integer id;
    private String code;
    private String name;
    private List<Patient> waitingPatients;
    private Patient firstCallingPatient;
    private Patient seeingPatient;
    private String waitingStr;
    private String seeingStr;

    public String getSeeingStr() {
        return seeingStr;
    }

    public void setSeeingStr(String seeingStr) {
        this.seeingStr = seeingStr;
    }

    public String getWaitingStr() {
        return waitingStr;
    }

    public void setWaitingStr(String waitingStr) {
        this.waitingStr = waitingStr;
    }

    public RoomInfo() {
        this.waitingStr = "";
    }

    public Patient getFirstCallingPatient() {
        return firstCallingPatient;
    }

    public void setFirstCallingPatient(Patient firstCallingPatient) {
        this.firstCallingPatient = firstCallingPatient;
    }

    public Patient getSeeingPatient() {
        return seeingPatient;
    }

    public void setSeeingPatient(Patient seeingPatient) {
        this.seeingPatient = seeingPatient;
        if ( !"".equals(seeingPatient.getName())){
            this.seeingStr = seeingPatient.getName() + "(" + seeingPatient.getQueueNo() + ")";
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Patient> getWaitingPatients() {
        return waitingPatients;
    }

    public void setWaitingPatients(List<Patient> waitingPatients) {
        this.waitingPatients = waitingPatients;
        String str = "";
        for (Patient patient : waitingPatients){
            str += patient.getName() + "(" + patient.getQueueNo() + ")" + ",";
        }
        if (!"".equals(str)){
            this.waitingStr = str.substring(0, str.length() - 1);
        } else {
            this.waitingStr = "";
        }
    }
}
