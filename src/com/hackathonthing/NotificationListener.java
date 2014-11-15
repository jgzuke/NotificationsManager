package com.hackathonthing;

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
    }	
    protected void textFrom(String number)
    {
    	Log.e("Text", "Text From"+number);
    }
    protected void callFrom(String number)
    {
    	Log.e("Text", "Call From"+number);
    }
}