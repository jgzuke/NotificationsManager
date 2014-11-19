package com.hackathonthing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SMSListener extends BroadcastReceiver
{
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
                    String num = SmsMessage.createFromPdu((byte[])pdus[0]).getOriginatingAddress();
                    Intent intent2 = new Intent();
                    intent2.putExtra("Number", num);
                    intent2.setAction("com.hackathonthing.TEXT");
                    context.sendBroadcast(intent); 
                } catch(Exception e){}
            }
        }
    }
}
