package com.queue.hospital;

import java.util.Calendar;

/**
 * Created by Magic on 2018/4/26.
 */

public class test {
    public static void main(String[] args) {
        Calendar a = Calendar.getInstance();
        Calendar b = Calendar.getInstance();
        a.set(2018,4,21,22,23);
        b.set(2018,4,21,10,23);
        System.out.println(a.getTimeInMillis() < b.getTimeInMillis());
    }
}
