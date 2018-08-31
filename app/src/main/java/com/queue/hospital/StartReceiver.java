package com.queue.hospital;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartReceiver extends BroadcastReceiver {
    public StartReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, ConfigActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);
    }
}
