package com.hackathonthing;

import java.util.ArrayList;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
	private Button presetButtons [] = new Button[6];
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
	private ImageButton editPresets;
	private ImageButton editRules;
	private ImageButton editTimes;
	private TextView notificationRules;
	private TextView activeTimes;
	private TableLayout.LayoutParams tableLayout;
	private LayoutInflater layoutInflater;
	private View editScreen;
	private PopupWindow popup;
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        myself = this;
        setContentView(R.layout.activity_main);
        r = getResources();
        layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);  
        imageLibrary = new ImageLibrary(this);
        dpToPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1000, r.getDisplayMetrics())/1000;
        setStaticTablesButtons();
        makePresetBase();
        setUpDefaultPresets();
        buildPresetButtons();
        loadPreset(0);
    }
	private void setStaticTablesButtons()
	{
		mainLayout = (LinearLayout) findViewById( R.id.mainLayout);
		notificationRules = (TextView)findViewById( R.id.notificationRules);
        activeTimes = (TextView)findViewById( R.id.activeTimes);
        notificationRules.setTextSize(20);
        activeTimes.setTextSize(20);
        editPresets = (ImageButton) findViewById( R.id.editPresets);
    	editRules = (ImageButton) findViewById( R.id.editRules);
    	editTimes = (ImageButton) findViewById( R.id.editTimes);
        presetsTable = (TableLayout) findViewById( R.id.presetsTable);
        rulesTable = (TableLayout) findViewById( R.id.rulesTable);
        rulesTable.setStretchAllColumns(true);
        rulesTable.setShrinkAllColumns(true);
        timesTable = (TableLayout) findViewById( R.id.timesTable);
        timesTable.setStretchAllColumns(true);
        timesTable.setShrinkAllColumns(true);
        editPresets.setImageDrawable(imageLibrary.edit);
        editRules.setImageDrawable(imageLibrary.plus);
        editTimes.setImageDrawable(imageLibrary.plus);
        editPresets.setOnClickListener(editPresetsClickHandler);
        editRules.setOnClickListener(editRulesClickHandler);
        editTimes.setOnClickListener(editTimesClickHandler);
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
        makeTime(1, 2, 3, 1, 2, 4);
        makeTime(1, 2, 3, 1, 2, 4);
	}
    private void buildPresetButtons()
    {
    	presetButtonRow = new TableRow(this);
    	presetsTable.addView(presetButtonRow);
        for(int i = 0; i < numPresets; i++)
        {
        	presetButtons[i] = new Button(this);
        	presetButtons[i].setText(presets[i]);
        	presetButtons[i].setTextSize(20);
        	presetButtons[i].setWidth(pixels(100));
        	presetButtons[i].setHeight(pixels(60));
        	presetButtons[i].setId(i+10000);
        	presetButtons[i].setOnClickListener(presetClickHandler);
        	presetButtonRow.addView(presetButtons[i], i);
        }
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
        	Button delete = (Button)r.getChildAt(3);
        	String [] values = rules.get(current).get(i);
        	program.setText(values[0]);
        	person.setText(values[1]);
        	action.setBackground(imageLibrary.notifOpts[actToID(values[2])]);
        	delete.setBackground(imageLibrary.delete);
        	
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
        	Button delete = (Button)t.getChildAt(2);
        	int [][] values = times.get(current).get(i);
        	start.setText(timeToString(values[0])+ " to ");
        	end.setText(timeToString(values[1]));
        	delete.setBackground(imageLibrary.delete);
        	
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
    private void makePreset()
    {
    	numPresets++;
    	Button b = new Button(this);
        b.setText(presets[numPresets-1]);
        b.setTextSize(20);
        b.setId(numPresets-1+100);
        b.setOnClickListener(presetClickHandler);
        b.setHeight(pixels(60));
        presetButtons[numPresets-1] = b;
        for(int i = 0; i < numPresets; i++)
        {
        	presetButtons[i].setWidth(pixels(300/numPresets));
        }
        presetButtonRow.addView(b, numPresets-1);
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
    	
    	buildRuleRows();
    	buildTimeRows();
    }
    private int pixels(int dp)
    {
    	return (int)(dp*dpToPx);
    }
	View.OnClickListener presetClickHandler = new View.OnClickListener()
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
	    public void deleteRuleClickHandler(final View v)
	    {
	        View popupView = layoutInflater.inflate(R.layout.popup_delete, null);  
	        popup = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);  
	        Button yes = (Button) popupView.findViewById(R.id.yes);
	        Button no = (Button) popupView.findViewById(R.id.no);
	        yes.setOnClickListener(new View.OnClickListener()
	        {
	            public void onClick(View b)
	            {
	            	deleteRule(v.getId()-24000);
	            	popup.dismiss();
	            }
	        });
	        no.setOnClickListener(new View.OnClickListener()
	        {
	            public void onClick(View v)
	            {
	            	popup.dismiss();
	            }
	        });
	        popup.setContentView(popupView);
	        popup.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
	    }
	public void deleteTimeClickHandler(final View v)
	{
		View popupView = layoutInflater.inflate(R.layout.popup_delete, null);  
        popup = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);  
        Button yes = (Button) popupView.findViewById(R.id.yes);
        Button no = (Button) popupView.findViewById(R.id.no);
        yes.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View b)
            {
            	deleteRule(v.getId()-24000);
            	popup.dismiss();
            }
        });
        no.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
            	popup.dismiss();
            }
        });
        popup.setContentView(popupView);
        popup.showAtLocation(mainLayout, Gravity.CENTER, 0, 0);
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