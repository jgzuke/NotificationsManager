package com.hackathonthing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class CallListener extends BroadcastReceiver
{
	private static int NOTIFICATION_TYPE_CALL = 1;
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
		private Context context;
        public void onCallStateChanged(int state, String callNumber)
        {
            if(state == 1)
            {
                CustomRinger.ring(callNumber, context, NOTIFICATION_TYPE_CALL);
            }
        }
        protected void setContext(Context c)
        {
        	context=c;
        }
    }
}