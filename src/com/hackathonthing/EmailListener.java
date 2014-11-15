package com.hackathonthing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class EmailListener extends BroadcastReceiver
{
	NotificationListener listener;
	public EmailListener(NotificationListener listenerSet)
	{
		listener = listenerSet;
	}
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED"))
        {
            Bundle bundle = intent.getExtras();
            if (bundle != null)
            {
                try
                {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    listener.notifFrom("Text", SmsMessage.createFromPdu((byte[])pdus[0]).getOriginatingAddress());
                } catch(Exception e){}
            }
        }
    }
}
