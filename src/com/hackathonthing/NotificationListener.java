package com.hackathonthing;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

public class NotificationListener
{
	private MainActivity mainActivity;
	private Context context;
    public NotificationListener(MainActivity activitySet, Context contextSet)
    {
    	mainActivity = activitySet;
    	context = contextSet;
    	new SMSListener(this);
    	new CallListener(this);
    	//new EmailListener(this);
    }	
    protected void notifFrom(String program, String identification)
    {
    	ArrayList<String[]> rules = mainActivity.getRulesByProgram(program);
    	String action = "";
    	String defaultAct = "";
    	for(int i = 0; i < rules.size(); i++)
    	{
    		if(rules.get(i)[3].equals("Default"))
    		{
    			defaultAct = rules.get(i)[2];
    		}
    		if(rules.get(i)[3].equals(identification))
    		{
    			action = rules.get(i)[2];
    		}
    	}
    	if(action.length()==0) action = defaultAct;
    	if(action.length()==0) action = "silent";
    	Log.e("Notification", program + " From " + identification + " Action " + action);
    }
}