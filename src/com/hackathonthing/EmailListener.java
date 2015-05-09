package com.hackathonthing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class EmailListener extends BroadcastReceiver
{
	private static int NOTIFICATION_TYPE_EMAIL = 1;
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
                    String textNumber = SmsMessage.createFromPdu((byte[])pdus[0]).getOriginatingAddress();
                    CustomRinger.setContext(context);
                    CustomRinger.performAction(textNumber, NOTIFICATION_TYPE_EMAIL);
                } catch(Exception e){}
            }
        }
    }
}