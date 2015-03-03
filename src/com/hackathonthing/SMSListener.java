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
import android.telephony.SmsMessage;
import android.util.Log;

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
                    String action = getAction(getPreset(context), context, num);
                    Log.e("myid", "Text From " + num + " Action " + action);
                    
                    final AudioManager am;
                    am= (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    final int oldRingerMode = am.getRingerMode();
                    
                    if(action.equalsIgnoreCase("ring")) am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    if(action.equalsIgnoreCase("silent")) am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    if(action.equalsIgnoreCase("vibrate")) am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
                    Runnable task = new Runnable()
                    {
                        public void run()
                        {
                        	am.setRingerMode(oldRingerMode);
                        }
                    };
                    worker.schedule(task, 3, TimeUnit.SECONDS);
                } catch(Exception e){}
            }
        }
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
		String action = settings.getString("ruleByNum"+"Preset"+Integer.toString(preset)+"ProgramTextNum"+Number, null);
		if(action == null)
		{
			String defaultAction = settings.getString("ruleByNum"+"Preset"+Integer.toString(preset)+"ProgramTextNumDefault", null);
			Log.e("myid", "ruleByNum"+"Preset"+Integer.toString(preset)+"ProgramTextNumDefault");
			Log.e("myid", defaultAction);
			action = defaultAction;
			if(action == null) action = "none"; 
		}
		return action;
	}
}
