package com.queue.hospital.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.queue.hospital.R;
import com.queue.hospital.pojo.RoomInfo;

import java.util.List;

/**
 * Created by Adim on 2018/1/23.
 */

public class ViewInfoAdapter extends ArrayAdapter<RoomInfo> {
    private int resurceId;

    public ViewInfoAdapter(Context context, int resource, List<RoomInfo> objects) {
        super(context.getApplicationContext(), resource, objects);
        resurceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RoomInfo roomInfo = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resurceId, parent, false);
        TextView roomName = (TextView) view.findViewById(R.id.list_room);
        TextView waiting = (TextView) view.findViewById(R.id.list_waiting);
        TextView seeing = (TextView) view.findViewById(R.id.list_ready);
        roomName.setText(roomInfo.getName());
        waiting.setText(roomInfo.getWaitingStr());
        seeing.setText(roomInfo.getSeeingStr());
        return view;
    }
}
