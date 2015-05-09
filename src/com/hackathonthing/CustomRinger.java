package com.hackathonthing;

import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.util.Log;

public class CustomRinger
{
    private static final String saveID = "mysharedpreferencesfortestingtings";
    private static final String [] typeStrings = {"ProgramTextNum", "ProgramCallNum", "ProgramEmailNum"};
	protected static final void performAction(String num, int type, Context context)
	{
		String action = getAction(getPreset(context), num, type, context);
        Log.e("myid", "Call From " + num + " Action " + action);
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
        worker.schedule(task, 10, TimeUnit.SECONDS);
	}
	private static final String getAction(int preset, String Number, int type, Context context)
	{
		SharedPreferences settings = context.getSharedPreferences(saveID, 0);
		String program = typeStrings[type];
		String action = settings.getString("ruleByNum"+"Preset"+Integer.toString(preset)+"Program"+program+"Num"+Number, null);
		if(action == null)
		{
			action = settings.getString("ruleByNum"+"Preset"+Integer.toString(preset)+"Program"+program+"Num"+"Default", null);
			Log.e("myid", "ruleByNum"+"Preset"+Integer.toString(preset)+"Program"+program+"Num"+"Default");
			Log.e("myid", action);
			if(action == null) action = "none"; 
		}
		return action;
	}
	private static final int getPreset(Context c)
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
}