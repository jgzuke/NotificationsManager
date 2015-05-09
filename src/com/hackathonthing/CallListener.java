package com.hackathonthing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class CallListener extends BroadcastReceiver
{
	private static int NOTIFICATION_TYPE_CALL = 1;
	@Override
	public void onReceive(Context context, Intent intent)
	{
	    try
	    {
	        TelephonyManager tmgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	        MyPhoneStateListener phoneListener = new MyPhoneStateListener(context);
	        tmgr.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
	    } catch (Exception e) {}
	}
	private class MyPhoneStateListener extends PhoneStateListener
	{
		private Context context;
		public MyPhoneStateListener(Context contextSet)
		{
			context = contextSet;
		}
        public void onCallStateChanged(int state, String callNumber)
        {
            if(state == 1)
            {
                CustomRinger.performAction(callNumber, NOTIFICATION_TYPE_CALL, context);
            }
        }
    }
}