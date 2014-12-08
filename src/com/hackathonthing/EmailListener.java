package com.hackathonthing;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class EmailListener extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED"))
        {
        	Log.e("myid", "zg");
            Bundle bundle = intent.getExtras();
            if (bundle != null)
            {
                try
                {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    String num = SmsMessage.createFromPdu((byte[])pdus[0]).getOriginatingAddress();
                    Log.e("Notification", "Text From " + num + " Action ");
                } catch(Exception e){}
            }
        }
    }
}