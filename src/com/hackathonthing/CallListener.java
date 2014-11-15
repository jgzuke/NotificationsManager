package com.hackathonthing;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class CallListener extends BroadcastReceiver
{
	NotificationListener listener;
	public CallListener(NotificationListener listenerSet)
	{
		listener = listenerSet;
	}
    @Override
    public void onReceive(Context context, Intent intent)
    {
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE); 
        if(tm.getCallState()==TelephonyManager.CALL_STATE_RINGING)
        {
            listener.callFrom(intent.getStringExtra("incoming_number"));
        } 
    }
}