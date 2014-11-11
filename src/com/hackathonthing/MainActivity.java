package com.hackathonthing;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.views.ButtonFloatSmall;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.SnackBar;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

public class MainActivity extends Activity
{
	private MainActivity myself;
	private LinearLayout mainLayout;
	private byte numPresets = 3;
	private String [] presets = {"Home", "Sleep", "Work", "", "", ""};
	private ButtonRectangle presetButtons [] = new ButtonRectangle[6];
	private Resources r;
	private int iconSize = 25;
	private double dpToPx;
	private TableLayout presetsTable;
	private TableRow presetButtonRow;
	private ArrayList<ArrayList<int[][]>> times =  new ArrayList<ArrayList<int[][]>>(); // preset, rule, [start/end][day, hour, min]
	private ArrayList<ArrayList<String[]>> rules = new ArrayList<ArrayList<String[]>>();
	private TableLayout rulesTable;
	private TableLayout timesTable;
	private int current = 0;
	private ImageLibrary imageLibrary;
	private ButtonFloatSmall editPresets;
	private ButtonFloatSmall editRules;
	private ButtonFloatSmall editTimes;
	private TextView notificationRules;
	private TextView activeTimes;
	private TableLayout.LayoutParams tableLayout;
	private LayoutInflater layoutInflater;
	private View editScreen;
	private PopupWindow popup;
	private AttributeSet presetAttributes;
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        myself = this;
        setContentView(R.layout.activity_main);
        r = getResources();
        //presetAttributes = getAttributeSet(R.xml.presetbutton); // just inflated layouts instead
        layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);  
        imageLibrary = new ImageLibrary(this);
        dpToPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1000, r.getDisplayMetrics())/1000;
        setStaticTablesButtons();
        makePresetBase();
        setUpDefaultPresets();
        buildPresetButtons();
        loadPreset(0);
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
		notificationRules = (TextView)findViewById( R.id.notificationRules);
        activeTimes = (TextView)findViewById( R.id.activeTimes);
        notificationRules.setTextSize(20);
        activeTimes.setTextSize(20);
        editPresets = (ButtonFloatSmall) findViewById( R.id.editPresets);
    	editRules = (ButtonFloatSmall) findViewById( R.id.editRules);
    	editTimes = (ButtonFloatSmall) findViewById( R.id.editTimes);
    	editPresets.setOnClickListener(editPresetsClickHandler);
    	editRules.setOnClickListener(editRulesClickHandler);
    	editTimes.setOnClickListener(editTimesClickHandler);
        presetsTable = (TableLayout) findViewById( R.id.presetsTable);
        rulesTable = (TableLayout) findViewById( R.id.rulesTable);
        timesTable = (TableLayout) findViewById( R.id.timesTable);
    }
	private void makePresetBase()
	{
		for(int i = 0; i < 3; i ++)
		{
			ArrayList<int[][]> t =  new ArrayList<int[][]>();
			ArrayList<String[]> r = new ArrayList<String[]>();
			times.add(t);
			rules.add(r);
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
    private void buildPresetButtons()
    {
    	presetButtonRow = new TableRow(this);
    	presetsTable.addView(presetButtonRow);
        for(int i = 0; i < numPresets; i++)
        {
        	presetButtons[i] = (ButtonRectangle)layoutInflater.inflate(R.xml.presetbutton, null, false);
        	presetButtons[i].setText(presets[i]);
        	presetButtons[i].setId(i+10000);
        	presetButtons[i].setOnClickListener(presetsClickHandler);
        	presetButtonRow.addView(presetButtons[i], i);
        }
    }
    private void makePreset()
    {
    	numPresets++;
    	ButtonRectangle b = (ButtonRectangle)layoutInflater.inflate(R.xml.presetbutton, null, false);
        b.setText(presets[numPresets-1]);
        b.setId(numPresets-1+10000);
        b.setOnClickListener(presetsClickHandler);
        presetButtons[numPresets-1] = b;
        presetButtonRow.addView(b, numPresets-1);
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
    private void removePreset(int toRemove)
    {
    	numPresets--;
    	for(int i = toRemove; i < numPresets; i ++)
    	{
    		presetButtons[i]=presetButtons[i+1];
    		presetButtonRow.removeViewAt(i);
    		presetButtonRow.addView(presetButtons[i], i);
    	}
    	presetButtons[numPresets]=null;
        presetButtonRow.removeViewAt(numPresets);
    }
    private void loadPreset(int preset)
    {
    	current = preset;
    	notificationRules.setText(presets[current]+" rules");
    	notificationRules.setText(presets[current]+" times");
    	buildRuleRows();
    	buildTimeRows();
    }
    private int pixels(int dp)
    {
    	return (int)(dp*dpToPx);
    }
    View.OnClickListener presetsClickHandler = new View.OnClickListener()
	{
	    public void onClick(View v)
	    {
	    	loadPreset(v.getId()-10000);
	    }
	};
	    public void notifClickHandler(View v)
	    {
	    	notifClick(v.getId()-23000);
	    }
	    public void deleteRuleClickHandler(final View firstV)
	    {
	    	new SnackBar(this, "Are you sure you want to delete rule?", "Yes",
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
		new SnackBar(this, "Are you sure you want to delete time?", "Yes",
		new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				deleteTime(firstV.getId()-33000);
			}
		}).show();
    }
	View.OnClickListener editPresetsClickHandler = new View.OnClickListener()
	{
	    public void onClick(View v)
	    {
	    	//editScreen = layoutInflater.inflate(R.layout.activity_presets, null);  
	        //final PopupWindow popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);  
	        //TODO text box and plus for new
	    	//TODO list of old 
	    }
	};
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