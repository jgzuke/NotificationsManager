package com.hackathonthing;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.views.ButtonFloatSmall;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.SnackBar;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

public class MainActivity extends Activity
{
	private MainActivity myself;
	private LinearLayout mainLayout;
	private ArrayList<String> presets = new ArrayList<String>();
	private Resources r;
	private int iconSize = 25;
	private double dpToPx;
	private ArrayList<ArrayList<int[][]>> times =  new ArrayList<ArrayList<int[][]>>(); // preset, rule, [start/end][day, hour, min]
	private ArrayList<ArrayList<String[]>> rules = new ArrayList<ArrayList<String[]>>();
	private TableLayout rulesTable;
	private TableLayout timesTable;
	private int current = 0;
	private ImageLibrary imageLibrary;
	private ButtonFloatSmall editRules;
	private ButtonFloatSmall editTimes;
	private TextView presetRulesText;
	private TextView presetTimesText;
	private TableLayout.LayoutParams tableLayout;
	private LayoutInflater layoutInflater;
	private View editScreen;
	private PopupWindow popup;
	private AttributeSet presetAttributes;
	private ListView navDrawer;
	private DrawerLayout navLayout;
	private ActionBarDrawerToggle navToggle;
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        myself = this;
        setContentView(R.layout.activity_main);
        r = getResources();
        makePreset("Home");
        makePreset("Work");
        makePreset("Sleep");
        setUpNavBar();
        layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);  
        imageLibrary = new ImageLibrary(this);
        dpToPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1000, r.getDisplayMetrics())/1000;
        setStaticTablesButtons();
        makePresetBase();
        setUpDefaultPresets();
        loadPreset(0);
    }
	private void setUpNavBar()
	{
		setTitle(presets.get(0));
		navLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navDrawer = (ListView) findViewById(R.id.left_drawer);
        navDrawer.setAdapter(new ArrayAdapter<String>(this, R.layout.navlistitem, presets));
        navDrawer.setOnItemClickListener(new DrawerItemClickListener());

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        navToggle = new ActionBarDrawerToggle(this, navLayout, R.string.drawer_open, R.string.drawer_close)
        {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(presets.get(current));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(presets.get(current));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        navLayout.setDrawerListener(navToggle);
	}
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        navToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        navToggle.onConfigurationChanged(newConfig);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (navToggle.onOptionsItemSelected(item)) return true;
        return false;
    }
	private void selectItem(int position)
	{
        loadPreset(position);
        navDrawer.setItemChecked(position, true);
        setTitle(presets.get(current));
        navLayout.closeDrawer(navDrawer);
    }
	private AttributeSet getAttributeSet(int res)
	{
		AttributeSet as = null;
		XmlResourceParser parser = r.getLayout(res);
		int state = 0;
        do {
            try {
                state = parser.next();
            } catch (XmlPullParserException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }       
            if (state == XmlPullParser.START_TAG) {
                if (parser.getName().equals("TextView")) {
                    as = Xml.asAttributeSet(parser);
                    break;
                }
            }
        } while(state != XmlPullParser.END_DOCUMENT);
        return as;
	}
	private void setStaticTablesButtons()
	{
		mainLayout = (LinearLayout) findViewById( R.id.mainLayout);
		presetRulesText = (TextView)findViewById( R.id.notificationRules);
		presetTimesText = (TextView)findViewById( R.id.activeTimes);
        presetRulesText.setTextSize(20);
        presetTimesText.setTextSize(20);
    	editRules = (ButtonFloatSmall) findViewById( R.id.editRules);
    	editTimes = (ButtonFloatSmall) findViewById( R.id.editTimes);
    	editRules.setOnClickListener(editRulesClickHandler);
    	editTimes.setOnClickListener(editTimesClickHandler);
        rulesTable = (TableLayout) findViewById( R.id.rulesTable);
        timesTable = (TableLayout) findViewById( R.id.timesTable);
    }
	private void makePresetBase()
	{
		for(int i = 0; i < 3; i ++)
		{
			times.add(new ArrayList<int[][]>());
			rules.add(new ArrayList<String[]>());
		}
	}
	private void setUpDefaultPresets()
	{
        makeRule("Calls", "Default", "silent");
        makeRule("Texts", "Default", "vibrate");
        makeRule("Email", "Default", "ring");
        makeRule("Calls", "Default", "silent");
        makeRule("Texts", "Default", "vibrate");
        makeRule("Email", "Default", "ring");
        makeRule("Calls", "Default", "silent");
        makeRule("Texts", "Default", "vibrate");
        makeRule("Email", "Default", "ring");
        makeTime(1, 2, 3, 1, 2, 4);
        makeTime(1, 2, 3, 1, 2, 4);
	}
    private void makePreset(String preset)
    {
    	presets.add(preset);
		times.add(new ArrayList<int[][]>());
		rules.add(new ArrayList<String[]>());
    }
    private void removePreset(int toRemove)
    {
    	presets.remove(toRemove);
		times.remove(toRemove);
		rules.remove(toRemove);
    }
    private void makeRule(String program, String person, String action)
    {
    	String[] newRule = new String[3];
    	newRule[0]=program;
    	newRule[1]=person;
    	newRule[2]=action;
    	rules.get(current).add(newRule);
    }
    private void makeTime(int startD, int startH, int startM, int endD, int endH, int endM)
    {
    	int[][] newTime = new int[2][3];
    	newTime[0][0]=startD;
    	newTime[0][1]=startH;
    	newTime[0][2]=startM;
    	newTime[1][0]=endD;
    	newTime[1][1]=endH;
    	newTime[1][2]=endM;
    	times.get(current).add(newTime);
    }
    private void buildRuleRows()
    {
    	rulesTable.removeAllViews();
    	for(int i = 0; i < rules.get(current).size(); i ++)
    	{
    		TableRow r = (TableRow)layoutInflater.inflate(R.layout.rulerow, null, false);
        	TextView program = (TextView)r.getChildAt(0);
        	TextView person = (TextView)r.getChildAt(1);
        	Button action = (Button)r.getChildAt(2);
        	ButtonFloatSmall delete = (ButtonFloatSmall)r.getChildAt(3);
        	String [] values = rules.get(current).get(i);
        	program.setText(values[0]);
        	person.setText(values[1]);
        	action.setBackground(imageLibrary.notifOpts[actToID(values[2])]);
        	
        	r.setId(i+20000);
        	program.setId(21000+i);
        	person.setId(22000+i);
        	action.setId(23000+i);
        	delete.setId(24000+i);

        	rulesTable.addView(r);
        }
    }
    private int actToID(String action)
    {
    	if(action.equals("silent")) return 0;
    	if(action.equals("vibrate")) return 1;
    	return 2;
    }
    private void buildTimeRows()
    {
    	timesTable.removeAllViews();
    	for(int i = 0; i < times.get(current).size(); i ++)
    	{
    		TableRow t = (TableRow)layoutInflater.inflate(R.layout.timerow, null, false);
        	TextView start = (TextView)t.getChildAt(0);
        	TextView end = (TextView)t.getChildAt(1);
        	ButtonFloatSmall delete = (ButtonFloatSmall)t.getChildAt(2);
        	int [][] values = times.get(current).get(i);
        	start.setText(timeToString(values[0])+ " to ");
        	end.setText(timeToString(values[1]));
        	
        	t.setId(i+30000);
        	start.setId(31000+i);
        	end.setId(32000+i);
        	delete.setId(33000+i);
        	
        	timesTable.addView(t);
    	}
    }
    private void notifClick(int index)
    {
    	switch(actToID(rules.get(current).get(index)[2]))
    	{
    		case 0:
    			rules.get(current).get(index)[2]="vibrate";
    			break;
    		case 1:
    			rules.get(current).get(index)[2]="ring";
    			break;
    		case 2:
    			rules.get(current).get(index)[2]="silent";
    			break;
    	}
    	buildRuleRows();
    }
    private void deleteRule(int index)
    {
    	rules.get(current).remove(index);
    	TableRow row;
    	for(int i = index; i < rules.get(current).size(); i++)
    	{
    		row = (TableRow) rulesTable.getChildAt(index);
    		int ID = row.getId()-20001;
    		row.setId(20000+ID);
    		row.getChildAt(0).setId(21000+ID);
    		row.getChildAt(1).setId(22000+ID);
    		row.getChildAt(2).setId(23000+ID);
    		row.getChildAt(3).setId(24000+ID);
    	}
    	buildRuleRows();
    }
    private void deleteTime(int index)
    {
    	times.get(current).remove(index);
    	TableRow row;
    	for(int i = index; i < rules.get(current).size(); i++)
    	{
    		row = (TableRow) rulesTable.getChildAt(index);
    		int ID = row.getId()-30001;
    		row.setId(30000+ID);
    		row.getChildAt(0).setId(31000+ID);
    		row.getChildAt(1).setId(32000+ID);
    		row.getChildAt(2).setId(33000+ID);
    	}
    	buildTimeRows();
    }
    private String timeToString(int [] time)
    {
    	return intToDay(time[0])+" "+Integer.toString(time[1])+":"+Integer.toString(time[2]);
    }
    private String intToDay(int day)
    {
    	switch(day)
    	{
    		case 1: return "Sun";
    		case 2: return "Mon";
    		case 3: return "Tue";
    		case 4: return "Wed";
    		case 5: return "Thu";
    		case 6: return "Fri";
    		case 7: return "Sat";
    	}
    	return "";
    }
    private void loadPreset(int preset)
    {
    	current = preset;
    	presetRulesText.setText(presets.get(current)+" rules");
    	presetTimesText.setText(presets.get(current)+" times");
    	buildRuleRows();
    	buildTimeRows();
    }
    private int pixels(int dp)
    {
    	return (int)(dp*dpToPx);
    }
	public void notifClickHandler(View v)
	    {
	    	notifClick(v.getId()-23000);
	    }
	public void deleteRuleClickHandler(final View firstV)
	    {
	    	new SnackBar(this, "Are you sure you want to delete this rule?", "Yes",
	    	new OnClickListener()
	   		{
	    		@Override
	    		public void onClick(View v)
	    		{
	    			deleteRule(firstV.getId()-24000);
	    		}
	    	}).show();
	    }
	public void deletePresetClickHandler(final View firstV)
    {
    	new SnackBar(this, "Are you sure you want to delete this preset?", "Yes",
    	new OnClickListener()
   		{
    		@Override
    		public void onClick(View v)
    		{
    			deleteRule(firstV.getId()-24000);
    		}
    	}).show();
    }
	public void deleteTimeClickHandler(final View firstV)
	{
		new SnackBar(this, "Are you sure you want to delete this time?", "Yes",
		new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				deleteTime(firstV.getId()-33000);
			}
		}).show();
    }
	View.OnClickListener editRulesClickHandler = new View.OnClickListener()
	{
	    public void onClick(View v)
	    {
	    	//editScreen = layoutInflater.inflate(R.layout.activity_rules, null);  
	    	//TODO two text boxes, and three button, highlight one, add button
	    }
	};
	View.OnClickListener editTimesClickHandler = new View.OnClickListener()
	{
	    public void onClick(View v)
	    {
	    	//editScreen = layoutInflater.inflate(R.layout.activity_times, null);  
	    	//TODO two week scrolls and clocks Add button
	    }
	};

}