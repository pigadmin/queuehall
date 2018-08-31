package com.xboot.stdcall;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DataforHandle {

	private static final String TAG = DataforHandle.class.getName();
	Context mcont;
	//开机时间，关机时间，设置状态
	String ontime="",offtime="",state="";
	//定义标准数组"1","12:00","13:00"
	public void setonoff(Context cont,String[] data){
		mcont = cont;
		if(data==null){
			//传值为null
			System.out.println("kong");
		}
		else{
			if(data.length==3){
				ontime=data[1];
				offtime=data[2];
				state=data[0];
				//缺一个判断状态
				try {
					if(Integer.parseInt(state)==0){
						//关闭定时开关机
						Log.i(TAG, "DataforHandle --- stop");
						setPowerOnOff((byte)0, (byte)4, (byte)0, (byte)4, (byte)0);
					}else{
						//
						Log.i(TAG, "DataforHandle --- start");
						judge(ontime,offtime,state);
					}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//状态值不为数字
					Log.i(TAG, "DataforHandle --- State values are not for the digital");
				}
			}else{
				//数据量不对
				Log.i(TAG, "DataforHandle --- Amount of data is wrong");
			}
		}
	}

	public void judge(String on_time,String off_time,String _state){
		if(num(on_time)&&num(off_time)&&num(_state)){
			settings(
					Integer.parseInt(nowtime()[0]),
					Integer.parseInt(nowtime()[1]),
					Integer.parseInt(on_time.split(":")[0]),
					Integer.parseInt(on_time.split(":")[1]),
					Integer.parseInt(off_time.split(":")[0]),
					Integer.parseInt(off_time.split(":")[1])
			);
		}
		else{
			//格式有误
			Log.i(TAG, "DataforHandle --- Presentation Error ");
		}
	}
	public String[] nowtime(){
		String NOW = (new SimpleDateFormat("yyyy-MM-dd kk:mm")).format(Calendar.getInstance().getTime());
		String nowtime[] =NOW.split(" ")[1].split(":");
		return nowtime;
	}

	public boolean num(String thisnum){
		String [] num =thisnum.split(":");
		try {
			Log.i(TAG,""+Integer.parseInt(num[0]));
			if(num.length>1){
				Log.i(TAG, ""+Integer.parseInt(num[1]));
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	//现在时间 开机时间 关机时间
	public void settings(int now_hour,int now_min,int on_hour,int on_min,int off_hour,int off_min){
		now_hour=now_hour==24?0:now_hour;
		//时间相等的情况
		if(off_hour==on_hour&&off_min==on_min){
			//时间相等设置不成功
			Log.i(TAG, "DataforHandle --- failed to set datafor ");
		}

		else{
			boolean byte_off_m = off_min-now_min<0;
			boolean byte_on_m=on_min-off_min<0;
			//关机参数
			int off_h=
					(off_hour-now_hour<0?
							(byte_off_m?(off_hour-now_hour+24-1):(off_hour-now_hour+24))
							:
							(byte_off_m?(off_hour-now_hour-1):(off_hour-now_hour))
					);
			int off_m=
					(byte_off_m?(off_min-now_min+60):(off_min-now_min));
			//开机参数
			int on_h=(byte_on_m?(on_hour-off_hour-1):(on_hour-off_hour));
			int on_m=(byte_on_m?(on_min-off_min+60):(on_min-off_min));
			off_h=off_h<0?(off_h+24):off_h;
			on_h=on_h<0?(on_h+24):on_h;
			String NOW = (new SimpleDateFormat("yyyy-MM-dd kk:mm")).format(Calendar.getInstance().getTime());
			Log.i(TAG, "---------------------------"+NOW);
			Log.i(TAG, "For the set of parameters=="+on_h+"==="+on_m+"==="+off_h+"==="+off_m);
			Log.i(TAG, "--------------------------- ");


			if(on_h==0&&on_m<3||off_h==0&&off_m<3){//小于3分钟设置不成功
				Log.i(TAG, "DataforHandle --- stop Time is too short to 3 minutes");
				if(mcont!=null){
					Toast.makeText(mcont, "设置失败", 1).show();
				}
			}else{
				if(mcont!=null){
					if(setPowerOnOff((byte)on_h, (byte)on_m, (byte)off_h, (byte)off_m, (byte)3)!=0){
						Toast.makeText(mcont, "设置失败", 1).show();
					}
				}

			}
		}
	}

	int setPowerOnOff(byte off_h, byte off_m, byte on_h, byte on_m, byte enable) {
		int fd, ret;
		// byte buf[] = { 0, 3, 0, 3 };
		fd = posix.open("/dev/McuCom", posix.O_RDWR, 0666);
		if(fd<0){
			Log.i(TAG, "DataforHandle --- stop  fd<0 ===");
			return -1;
		}
		ret = posix.poweronoff(off_h, off_m, on_h, on_m, enable, fd);
		if(ret!=0){
			Log.i(TAG, "DataforHandle --- stop  ret!=0 ===");
			return -1;
		}
		posix.close(fd);
		return 0;
	}
}
