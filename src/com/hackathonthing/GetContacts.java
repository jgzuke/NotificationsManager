package com.hackathonthing;

import java.util.ArrayList;
import java.util.HashSet;

import android.app.Activity;
import android.app.ListFragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
 
public class GetContacts extends Activity
{
	int type;
	private ArrayList<String> populatedIDList;
	private ArrayList<String> populatedList;
	@Override  
	 protected void onCreate(Bundle savedInstanceState)
	{  
	  super.onCreate(savedInstanceState);
	  type = getIntent().getExtras().getInt("type");
	  FragmentManager fm = getFragmentManager();  
	  if (fm.findFragmentById(android.R.id.content) == null) {  
		  ActualList list = new ActualList();  
	   fm.beginTransaction().add(android.R.id.content, list).commit();  
	  }  
	 }  
	private void contactChosen(int pos)
	{
	    Bundle conData = new Bundle();
	    conData.putString("contact", populatedList.get(pos));
	    conData.putString("identity", populatedIDList.get(pos));
	    Intent intent = new Intent();
	    intent.putExtras(conData);
	    setResult(1, intent);
	    finish();
	}
	public class ActualList extends ListFragment  
	{ 
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
	    {
	        ArrayAdapter<String> adapter = new ArrayAdapter<String>(inflater.getContext(), android.R.layout.simple_list_item_1,getNameEmailDetails());
	        setListAdapter(adapter);
	        return super.onCreateView(inflater, container, savedInstanceState);
	    }
	    @Override  
	    public void onListItemClick(ListView l, View v, int position, long id)
	    {  
	    	contactChosen(position);
	    }  
	    public ArrayList<String> getNameEmailDetails() // type 0:phone, 1:email, 2:facebook
		{
		    populatedList = new ArrayList<String>();
		    populatedIDList = new ArrayList<String>();
		    Context context = getActivity();
		    ContentResolver cr = context.getContentResolver();
		    String[] PROJECTION = new String[] {ContactsContract.RawContacts._ID, 
		            ContactsContract.Contacts.DISPLAY_NAME,
		            ContactsContract.CommonDataKinds.Email.DATA, 
		            ContactsContract.CommonDataKinds.Phone.DATA};
		    String order = "CASE WHEN " 
		            + ContactsContract.Contacts.DISPLAY_NAME 
		            + " NOT LIKE '%@%' THEN 1 ELSE 2 END, " 
		            + ContactsContract.Contacts.DISPLAY_NAME 
		            + ", " 
		            + ContactsContract.CommonDataKinds.Email.DATA
		            + " COLLATE NOCASE";
		    String filter;
		    Cursor cur;
		    if(type==0)
		    {
		    	Log.e("asg", "asdgasadgsg");
		    	filter = ContactsContract.CommonDataKinds.Phone.DATA + " NOT LIKE ''";
			    cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, filter, null, order);
		    } else if(type==1)
		    { 
		    	Log.e("asg", "asdgasg");
		    	filter = ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ''";
			    cur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION, filter, null, order);
		    } else
		    {
		    	filter = ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ''";
			    cur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION, filter, null, order);
		    }
		    if (cur.moveToFirst())
		    {
		        do {
		        	if(type==0)
				    {
		        		populatedList.add(cur.getString(1));
		        		populatedIDList.add(cur.getString(3));
				    } else if(type==1)
				    { 
				    	populatedList.add(cur.getString(2));
				    	populatedIDList.add(cur.getString(2));
				    } else
				    {
				    	populatedList.add(cur.getString(1));
				    	populatedIDList.add(cur.getString(3));
				    }
		        } while (cur.moveToNext());
		    }
		    cur.close();
		    return populatedList;
		}
	}
	String saveID = "mysharedpreferencesfortestingtings";
	private void readData()
	{
		SharedPreferences settings = getApplicationContext().getSharedPreferences(saveID, 0); //this is all making default stuff
		int presetCount = -1;
		presetCount = settings.getInt("presetCount", -1);
		if(presetCount!=-1) //TODO change to true for reset
		{
			for(int l = 0; l < presetCount; l++)
			{
				String lS = Integer.toString(l);
				presets.add(settings.getString("presetNames"+lS, null));
			}
			for(int j = 0; j < presetCount-1; j++)
			{
				rules.add(new ArrayList < String[] > ());
				times.add(new ArrayList < int[][] > ());
				String jS = Integer.toString(j);
				int rulesCount = settings.getInt("ruleCount"+jS, 0);
				for(int k = 0; k < rulesCount; k++) 				// for every rule in given preset
				{
					String kS = Integer.toString(k);
					String[] ruleArray = new String[4];
					for(int l = 0; l < 4; l++)
					{
						String lS = Integer.toString(l);
						ruleArray[l] = settings.getString("rule"+kS+"Preset"+jS+"pos"+lS, null);
					}
					rules.get(j).add(ruleArray);
				}
				int timesCount = settings.getInt("timeCount"+jS, 0);
				for(int k = 0; k < timesCount; k++) 				// for every rule in given preset
				{
					String kS = Integer.toString(k);
					int[][] timeArray = new int[2][3];
					for(int l = 0; l < 3; l++)
					{
						String lS = Integer.toString(l);
						timeArray[0][l] = settings.getInt("time"+kS+"Preset"+jS+"pos0"+lS, 0);
						timeArray[1][l] = settings.getInt("time"+kS+"Preset"+jS+"pos1"+lS, 0);
					}
					times.get(j).add(timeArray);
				}
			}
		} else
		{
			presets.add("Home"); presets.add("Work"); presets.add("Sleep");
			presets.add("    +");
			for (int i = 0; i < 3; i++)
			{
				times.add(new ArrayList < int[][] > ());
				rules.add(new ArrayList < String[] > ());
			}
			Log.e("myid", "h"+ Integer.toString(presets.size()));
			Log.e("myid", "h"+ Integer.toString(rules.size()));
			Log.e("myid", "h"+ Integer.toString(times.size()));
			setUpDefaultPresets();
		}
	}
}

