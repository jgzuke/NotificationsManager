package com.hackathonthing;

import java.util.ArrayList;
import java.util.HashSet;

import android.app.Activity;
import android.app.ListFragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.FragmentManager;
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
	    conData.putString("idNum", populatedIDList.get(pos));
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
		            ContactsContract.CommonDataKinds.Photo.CONTACT_ID };
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
		    	filter = ContactsContract.CommonDataKinds.Phone.DATA + " NOT LIKE ''";
			    cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, filter, null, order);
		    } else if(type==1)
		    { 
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
		        	populatedIDList.add(cur.getString(0));
		        	if(type==0)
				    {
		        		populatedList.add(cur.getString(1));
				    } else if(type==1)
				    { 
				    	populatedList.add(cur.getString(2));
				    } else
				    {
				    	populatedList.add(cur.getString(1));
				    }
		        } while (cur.moveToNext());
		    }
		    cur.close();
		    return populatedList;
		}
	}
}

