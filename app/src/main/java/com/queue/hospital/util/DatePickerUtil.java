package com.queue.hospital.util;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;


import com.queue.hospital.pojo.Date;

import java.util.Calendar;

/**
 * Created by csq on 2018/4/16.
 */

public class DatePickerUtil {

    public static Date getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        return new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
    }


    //  日期选择器
    public static void showDatePickerDialog(DatePickerDialog.OnDateSetListener onDateSetListener, AppCompatActivity activity){
        new DatePickerDialog(activity, onDateSetListener, getCurrentDate().getYear(), getCurrentDate().getMonth(),getCurrentDate().getDay()).show();
    }

    //  时间选择器
    public static void showTimePickerDialog(TimePickerDialog.OnTimeSetListener onTimeSetListener, AppCompatActivity activity){
        new TimePickerDialog(activity, onTimeSetListener, getCurrentDate().getHour(), getCurrentDate().getMinute(), true).show();
    }

}
