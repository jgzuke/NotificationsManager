package com.hackathonthing;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class GetContacts extends android.support.v4.app.FragmentActivity
{
	@ Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		FragmentManager fm = getSupportFragmentManager();
		if (fm.findFragmentById(android.R.id.content) == null)
		{  
			GetContactsList list = new GetContactsList();  
			fm.beginTransaction().add(android.R.id.content, list).commit();  
		}
	}
	private void contactChosen(String contact)
	{
	    Bundle conData = new Bundle();
	    conData.putString("contactPicked", contact);
	    Intent intent = new Intent();
	    intent.putExtras(conData);
	    setResult(1, intent);
	    finish();
	}
	private class GetContactsList extends ListFragment implements LoaderCallbacks<Cursor>
	{
	    private String[] infoToGet = {Contacts._ID, Contacts.DISPLAY_NAME_PRIMARY,Phone.NUMBER}; // retreive name/id
	    private String[] infoSource = {Contacts.DISPLAY_NAME_PRIMARY };
	    private int[] TO = {android.R.id.text1};
	    private CursorAdapter mAdapter;
	    @Override
	    public void onCreate(Bundle savedInstanceState)
	    {
	        super.onCreate(savedInstanceState);
	        Context context = getActivity();
	        int layout = android.R.layout.simple_list_item_1;
	        Cursor c = null;
	        mAdapter = new SimpleCursorAdapter(context, layout, c, infoSource, TO, 0);
	    }
	    @Override
	    public void onActivityCreated(Bundle savedInstanceState)
	    {
	        super.onActivityCreated(savedInstanceState);
	        setListAdapter(mAdapter);
	        getLoaderManager().initLoader(0, null, this);
	    }
	    @Override
	    public Loader<Cursor> onCreateLoader(int id, Bundle args)
	    {
	        Uri contentUri = Contacts.CONTENT_URI;
	        //String selection = AddDBHelper.KEY_DATE + "=?";
	        //String[] selectionArgs = { String.valueOf(btn_logbook_date.getText().toString()) };
	        // no sub-selection, no sort order, simply every row
	        // projection says we want just the _id and the name column
	        return new CursorLoader(getActivity(), contentUri, infoToGet, null, null, null);
	    }
	    @Override
	    public void onListItemClick(ListView l, View v, int position, long id)
	    {
	    	String contact = ((TextView) v).getText().toString();
	    	contactChosen(contact);
	    }
	    @Override
	    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
	    {
	        mAdapter.swapCursor(data);
	    }
	    @Override
	    public void onLoaderReset(Loader<Cursor> loader)
	    {
	        mAdapter.swapCursor(null);
	    }
	}
}