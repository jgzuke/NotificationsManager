package com.hackathonthing;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class CallListener extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE); 
        if(tm.getCallState()==TelephonyManager.CALL_STATE_RINGING)
        {
        	Intent intent2 = new Intent();
            intent2.putExtra("Number", intent.getStringExtra("incoming_number"));
            intent2.setAction("com.hackathonthing.CALL");
            context.sendBroadcast(intent);
        } 
    }
}