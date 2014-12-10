package com.hackathonthing;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSListener extends BroadcastReceiver
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
                    String action = getAction(getPreset(context), context, num);
                    Log.e("Notification", "Text From " + num + " Action " + action);
                } catch(Exception e){}
            }
        }
    }
    String saveID = "mysharedpreferencesfortestingtings";
    private int getPreset(Context c)
    {
    	//TODO use times to find current preset
    	return 0;
    }
	private String getAction(int pre, Context c, String Number)
	{
		SharedPreferences settings = c.getSharedPreferences(saveID, 0); //this is all making default stuff
		String preset = Integer.toString(pre);
		int ruleCount = settings.getInt("ruleCount"+preset, -1);
		if(ruleCount!=-1) //TODO change to true for reset
		{
			for(int k = 0; k < ruleCount; k++) 				// for every rule in given preset
			{
				String kS = Integer.toString(k);
				String number = settings.getString("rule"+kS+"Preset"+preset+"pos3", null);
				if(number.equals(Number))
				{
					return settings.getString("rule"+kS+"Preset"+preset+"pos2", null);
				}
			}
		}
		return null;
	}
}
