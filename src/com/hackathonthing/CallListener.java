package com.hackathonthing;

import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallListener extends BroadcastReceiver
{
	public void onReceive(Context context, Intent intent) {
	    try
	    {
	        TelephonyManager tmgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	        MyPhoneStateListener PhoneListener = new MyPhoneStateListener();
	        PhoneListener.setContext(context);
	        tmgr.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
	    } catch (Exception e) {}
	}
	private class MyPhoneStateListener extends PhoneStateListener
	{
        public void onCallStateChanged(int state, String num)
        {
            if (state == 1)
            {
                CustomRinger.ring(num, context, 1);
            }
        }
        private Context context;
        protected void setContext(Context c)
        {
        	context=c;
        }
    }
}