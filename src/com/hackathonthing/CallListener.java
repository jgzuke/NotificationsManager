package com.hackathonthing;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
                String action = getAction(getPreset(context), context, num);
                Log.e("myid", "Call From " + num + " Action " + action);
                if(action.equalsIgnoreCase("vibrate"))
                {
                	Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                	v.vibrate(500);
                }
                if(action.equalsIgnoreCase("ring"))
                {
                	Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                	v.vibrate(500);
                }
            }
        }
        private Context context;
        protected void setContext(Context c)
        {
        	context=c;
        }
        String saveID = "mysharedpreferencesfortestingtings";
        private int getPreset(Context c)
        {
        	SharedPreferences settings = c.getSharedPreferences(saveID, 0); //this is all making default stuff
    		int presetCount = settings.getInt("presetCount", -1);
    		Calendar now = Calendar.getInstance(); 
    		int dNow = 1440*now.get(Calendar.DAY_OF_WEEK);
    		int hNow = 60*now.get(Calendar.HOUR_OF_DAY);
    		int mNow = now.get(Calendar.MINUTE);
    		int timeNow = dNow+hNow+mNow;
    		for(int j = 0; j < presetCount-1; j++)
    		{
    			String jS = Integer.toString(j);
    			int timesCount = settings.getInt("timeCount"+jS, 0);
    			for(int k = 0; k < timesCount; k++) 				// for every rule in given preset
    			{
    				String kS = Integer.toString(k);
    				int start = 1440*settings.getInt("time"+kS+"Preset"+jS+"pos00", 0)
    				+ 60*settings.getInt("time"+kS+"Preset"+jS+"pos01", 0)
    				+ settings.getInt("time"+kS+"Preset"+jS+"pos02", 0);
    				int end = 1440*settings.getInt("time"+kS+"Preset"+jS+"pos10", 0) //convert times into minutes since start of week
    				+ 60*settings.getInt("time"+kS+"Preset"+jS+"pos11", 0)
    				+ settings.getInt("time"+kS+"Preset"+jS+"pos12", 0);
    				if(end>start)
    				{
    					if(timeNow>start && timeNow<end) return j; //start and end are in order
    				} else											//starts one week ends the next
    				{
    					if(timeNow>start || timeNow<end) return j;
    				}
    			}
    		}
        	return 0;
        }
    	private String getAction(int preset, Context c, String Number)
    	{
    		SharedPreferences settings = c.getSharedPreferences(saveID, 0);
    		String action = settings.getString("ruleByNum"+"Preset"+Integer.toString(preset)+"ProgramCallNum"+Number, null);
    		if(action == null)
    		{
    			String defaultAction = settings.getString("ruleByNum"+"Preset"+Integer.toString(preset)+"ProgramCallNumDefault", null);
    			Log.e("myid", "ruleByNum"+"Preset"+Integer.toString(preset)+"ProgramCallNumDefault");
    			Log.e("myid", defaultAction);
    			action = defaultAction;
    			if(action == null) action = "silent"; 
    		}
    		return action;
    	}
    }
}