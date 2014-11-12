package com.hackathonthing;
import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.gc.materialdesign.views.ButtonFloatSmall;
import com.gc.materialdesign.widgets.SnackBar;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TimePicker;
import android.widget.TextView;
public class MainActivity extends Activity
{
	private MainActivity myself;
	private ArrayList < String > presets = new ArrayList < String > ();
	private ArrayList < ArrayList < int[][] >> times = new ArrayList < ArrayList < int[][] >> (); // preset, rule, [start/end][day, hour, min]
	private ArrayList < ArrayList < String[] >> rules = new ArrayList < ArrayList < String[] >> ();
	private TableLayout rulesTable;
	private TableLayout timesTable;
	private int current = 0;
	private ImageLibrary imageLibrary;
	private ButtonFloatSmall editRules;
	private ButtonFloatSmall editTimes;
	private TextView presetRulesText;
	private TextView presetTimesText;
	private LayoutInflater layoutInflater;
	private ListView navDrawer;
	private DrawerLayout navLayout;
	private ActionBarDrawerToggle navToggle;
	private ArrayAdapter < String > navAdapter;@
	Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		myself = this;
		setContentView(R.layout.activity_main);
		presets.add("Home");
		presets.add("Work");
		presets.add("Sleep");
		for (int i = 0; i < 3; i++)
		{
			times.add(new ArrayList < int[][] > ());
			rules.add(new ArrayList < String[] > ());
		}
		setUpNavBar();
		layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
		imageLibrary = new ImageLibrary(this);
		setStaticTablesButtons();
		makePresetBase();
		setUpDefaultPresets();
		loadPreset(0);
	}
	private void setUpNavBar()
	{
		setTitle(presets.get(0) + " Setting");
		navLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		navDrawer = (ListView) findViewById(R.id.left_drawer);
		navAdapter = new ArrayAdapter < String > (this, R.layout.navlistitem, presets);
		presets.add("    +");
		navDrawer.setAdapter(navAdapter);
		navDrawer.setOnItemClickListener(new DrawerItemClickListener());
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		navToggle = new ActionBarDrawerToggle(this, navLayout, R.string.drawer_open, R.string.drawer_close)
		{
			public void onDrawerClosed(View view)
			{
				getActionBar().setTitle(presets.get(current) + " Setting");
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
			public void onDrawerOpened(View drawerView)
			{
				getActionBar().setTitle(presets.get(current) + " Setting");
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};
		navLayout.setDrawerListener(navToggle);
	}
	private class DrawerItemClickListener implements ListView.OnItemClickListener
	{@
		Override
		public void onItemClick(AdapterView <? > parent, View view, int position, long id)
		{
			selectItem(position);
		}
	}@
	Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		navToggle.syncState();
	}@
	Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		navToggle.onConfigurationChanged(newConfig);
	}@
	Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (navToggle.onOptionsItemSelected(item)) return true;
		switch (item.getItemId())
		{
			case R.id.editPresetName:
				AlertDialog.Builder builder = new AlertDialog.Builder(myself);
				final EditText input = new EditText(this);
				builder.setView(input);
				builder.setPositiveButton(R.string.set, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						presets.set(current, input.getText().toString());
						loadPreset(current);
						setTitle(presets.get(current));
					}
				});
				builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						// User cancelled the dialog
					}
				});
				builder.setTitle("Name Preset");
				AlertDialog dialog = builder.create();
				dialog.show();
				return true;
			case R.id.removePreset:
				AlertDialog.Builder removeBuilder = new AlertDialog.Builder(myself);
				removeBuilder.setMessage("Delete Preset?").setPositiveButton("Yes", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						removePreset(current);
						loadPreset(0);
						navAdapter.notifyDataSetChanged();
					}
				}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						// User cancelled the dialog
					}
				});
				AlertDialog removeDialog = removeBuilder.create();
				removeDialog.show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}@
	Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	private void selectItem(int position)
	{
		Boolean makeNew = false;
		if (position == presets.size() - 1)
		{
			makePreset("New");
			makeNew = true;
		}
		loadPreset(position);
		navDrawer.setItemChecked(position, true);
		setTitle(presets.get(current));
		navLayout.closeDrawer(navDrawer);
		if (makeNew)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(myself);
			final EditText input = new EditText(this);
			builder.setView(input);
			builder.setPositiveButton(R.string.set, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
					presets.set(current, input.getText().toString());
					loadPreset(current);
					setTitle(presets.get(current));
				}
			});
			builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
					// User cancelled the dialog
				}
			});
			builder.setTitle("Name Preset");
			AlertDialog dialog = builder.create();
			dialog.show();
		}
	}
	private void setStaticTablesButtons()
	{
		presetRulesText = (TextView) findViewById(R.id.notificationRules);
		presetTimesText = (TextView) findViewById(R.id.activeTimes);
		presetRulesText.setTextSize(20);
		presetTimesText.setTextSize(20);
		editRules = (ButtonFloatSmall) findViewById(R.id.editRules);
		editTimes = (ButtonFloatSmall) findViewById(R.id.editTimes);
		editRules.setOnClickListener(editRulesClickHandler);
		editTimes.setOnClickListener(editTimesClickHandler);
		rulesTable = (TableLayout) findViewById(R.id.rulesTable);
		timesTable = (TableLayout) findViewById(R.id.timesTable);
	}
	private void makePresetBase()
	{
		for (int i = 0; i < 3; i++)
		{
			times.add(new ArrayList < int[][] > ());
			rules.add(new ArrayList < String[] > ());
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
		presets.add(presets.size() - 1, preset);
		navAdapter.notifyDataSetChanged();
		times.add(new ArrayList < int[][] > ());
		rules.add(new ArrayList < String[] > ());
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
		newRule[0] = program;
		newRule[1] = person;
		newRule[2] = action;
		rules.get(current).add(newRule);
	}
	private void makeTime(int startD, int startH, int startM, int endD, int endH, int endM)
	{
		int[][] newTime = new int[2][3];
		newTime[0][0] = startD;
		newTime[0][1] = startH;
		newTime[0][2] = startM;
		newTime[1][0] = endD;
		newTime[1][1] = endH;
		newTime[1][2] = endM;
		times.get(current).add(newTime);
	}
	private void makeRule()
	{
		String[] newRule = {"Alert", "Person", "silent"};
		rules.get(current).add(newRule);
	}
	private void makeTime()
	{
		int[][] newTime = {{1, 8, 30}, {1, 9, 30}};
		times.get(current).add(newTime);
	}
	private void buildRuleRows()
	{
		rulesTable.removeAllViews();
		for (int i = 0; i < rules.get(current).size(); i++)
		{
			TableRow r = (TableRow) layoutInflater.inflate(R.layout.rulerow, rulesTable, false);
			TextView program = (TextView) r.getChildAt(0);
			TextView person = (TextView) r.getChildAt(1);
			Button action = (Button) r.getChildAt(2);
			ButtonFloatSmall delete = (ButtonFloatSmall) r.getChildAt(3);
			String[] values = rules.get(current).get(i);
			program.setText(values[0]);
			person.setText(values[1]);
			action.setBackground(imageLibrary.notifOpts[actToID(values[2])]);
			r.setId(i + 20000);
			program.setId(21000 + i);
			person.setId(22000 + i);
			action.setId(23000 + i);
			delete.setId(24000 + i);
			rulesTable.addView(r);
		}
	}
	private int actToID(String action)
	{
		if (action.equals("silent")) return 0;
		if (action.equals("vibrate")) return 1;
		return 2;
	}
	private void buildTimeRows()
	{
		timesTable.removeAllViews();
		for (int i = 0; i < times.get(current).size(); i++)
		{
			TableRow t = (TableRow) layoutInflater.inflate(R.layout.timerow, timesTable, false);
			TextView startD = (TextView) t.getChildAt(0);
			TextView startH = (TextView) t.getChildAt(1);
			TextView endD = (TextView) t.getChildAt(2);
			TextView endH = (TextView) t.getChildAt(3);
			ButtonFloatSmall delete = (ButtonFloatSmall) t.getChildAt(4);
			int[][] values = times.get(current).get(i);
			startD.setText(intToDay(values[0][0]) + " ");
			startH.setText(timeToString(values[0]) + " to ");
			endD.setText(intToDay(values[1][0]) + " ");
			endH.setText(timeToString(values[1]));
			t.setId(i + 30000);
			startD.setId(31000 + i);
			startH.setId(32000 + i);
			endD.setId(33000 + i);
			endH.setId(34000 + i);
			delete.setId(35000 + i);
			timesTable.addView(t);
		}
	}
	private int notifClick(int index)
	{
		switch (actToID(rules.get(current).get(index)[2]))
		{
			case 0:
				rules.get(current).get(index)[2] = "vibrate";
				return 1;
			case 1:
				rules.get(current).get(index)[2] = "ring";
				return 2;
			case 2:
				rules.get(current).get(index)[2] = "silent";
				return 0;
		}
		buildRuleRows();
		return 0;
	}
	private void deleteRule(int index)
	{
		rules.get(current).remove(index);
		TableRow row;
		for (int i = index; i < rules.get(current).size(); i++)
		{
			row = (TableRow) rulesTable.getChildAt(index);
			int ID = row.getId() - 20001;
			row.setId(20000 + ID);
			row.getChildAt(0).setId(21000 + ID);
			row.getChildAt(1).setId(22000 + ID);
			row.getChildAt(2).setId(23000 + ID);
			row.getChildAt(3).setId(24000 + ID);
		}
		buildRuleRows();
	}
	private void deleteTime(int index)
	{
		times.get(current).remove(index);
		TableRow row;
		for (int i = index; i < rules.get(current).size(); i++)
		{
			row = (TableRow) timesTable.getChildAt(index);
			int ID = row.getId() - 30001;
			row.setId(30000 + ID);
			row.getChildAt(0).setId(31000 + ID);
			row.getChildAt(1).setId(32000 + ID);
			row.getChildAt(2).setId(33000 + ID);
			row.getChildAt(3).setId(34000 + ID);
			row.getChildAt(4).setId(35000 + ID);
		}
		buildTimeRows();
	}
	private String timeToString(int[] time)
	{
		String minutes = Integer.toString(time[2]);
		if (minutes.length() == 1)
		{
			minutes = "0" + minutes;
		}
		return Integer.toString(time[1]) + ":" + minutes;
	}
	private String intToDay(int day)
	{
		switch (day)
		{
			case 1:
				return "Sun";
			case 2:
				return "Mon";
			case 3:
				return "Tue";
			case 4:
				return "Wed";
			case 5:
				return "Thu";
			case 6:
				return "Fri";
			case 7:
				return "Sat";
		}
		return "";
	}
	private void loadPreset(int preset)
	{
		current = preset;
		presetRulesText.setText(presets.get(current) + " rules");
		presetTimesText.setText(presets.get(current) + " times");
		buildRuleRows();
		buildTimeRows();
	}
	public void changeTimeStartHandler(final View v)
	{
		Log.e("hi", "changeTimeStartHandler");
		//TableRow row = (TableRow) timesTable.getChildAt(v.getId() - 32000);
		final TextView text = (TextView) v;
		TimePickerDialog mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener()
		{@
			Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute)
			{
				times.get(current).get(v.getId() - 32000)[0][1] = hourOfDay;
				times.get(current).get(v.getId() - 32000)[0][2] = minute;
				text.setText(timeToString(times.get(current).get(v.getId() - 32000)[0]));
				buildTimeRows();
			}
		}, times.get(current).get(v.getId() - 32000)[0][1], times.get(current).get(v.getId() - 32000)[0][2], true); //Yes 24 hour time
		mTimePicker.setTitle("Select Start Time");
		mTimePicker.show();
	}
	public void changeDayStartHandler(final View v)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(myself);
		builder.setTitle("Pick start Day");
		builder.setItems(R.array.days_array, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				times.get(current).get(v.getId() - 31000)[0][0] = which + 1;
				TextView v2 = (TextView)v;
				v2.setText(intToDay(which + 1));
				buildTimeRows();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
		Log.e("hi", "changeDayStartHandler");
	}
	public void changeTimeEndHandler(final View v)
	{
		Log.e("hi", "changeTimeEndHandler");
		
		//TableRow row = (TableRow) timesTable.getChildAt(v.getId() - 34000);
		final TextView text = (TextView) v;
		TimePickerDialog mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener()
		{@
			Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute)
			{
				times.get(current).get(v.getId() - 34000)[1][1] = hourOfDay;
				times.get(current).get(v.getId() - 34000)[1][2] = minute;
				text.setText(timeToString(times.get(current).get(v.getId() - 34000)[1]));
				buildTimeRows();
			}
		}, times.get(current).get(v.getId() - 34000)[1][1], times.get(current).get(v.getId() - 34000)[1][2], true); //Yes 24 hour time
		mTimePicker.setTitle("Select End Time");
		mTimePicker.show();
	}
	public void changeDayEndHandler(final View v)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(myself);
		builder.setTitle("Pick end Day");
		builder.setItems(R.array.days_array, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				times.get(current).get(v.getId() - 33000)[1][0] = which + 1;
				TextView v2 = (TextView)v;
				v2.setText(intToDay(which + 1));
				buildTimeRows();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
		Log.e("hi", "changeDayEndHandler");
	}
	public void changeProgramHandler(View v)
	{
		Log.e("hi", "changeProgramHandler");
	}
	public void changePersonHandler(View v)
	{
		Log.e("hi", "changePersonHandler");
	}
	public void notifClickHandler(View v)
	{
		int i = notifClick(v.getId() - 23000);
		Button v2 = (Button) v;
		v2.setBackground(imageLibrary.notifOpts[i]);
	}
	public void deleteRuleClickHandler(final View firstV)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(myself);
		builder.setMessage("Delete Rule?").setPositiveButton("Yes", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				deleteRule(firstV.getId() - 24000);
			}
		}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				// User cancelled the dialog
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	public void deletePresetClickHandler(final View firstV)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(myself);
		builder.setMessage("Delete Preset?").setPositiveButton("Yes", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				removePreset(firstV.getId() - 10000);
			}
		}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				// User cancelled the dialog
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	public void deleteTimeClickHandler(final View firstV)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(myself);
		builder.setMessage("Delete Time?").setPositiveButton("Yes", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				deleteTime(firstV.getId() - 35000);
			}
		}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				// User cancelled the dialog
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	View.OnClickListener editRulesClickHandler = new View.OnClickListener()
	{
		public void onClick(View v)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(myself);
			LayoutInflater inflater = getLayoutInflater();
			makeRule();
			TableRow makeRuleRow = (TableRow) layoutInflater.inflate(R.layout.makerule, rulesTable, false);
			TextView program = (TextView) makeRuleRow.getChildAt(0);
			TextView person = (TextView) makeRuleRow.getChildAt(1);
			Button action = (Button) makeRuleRow.getChildAt(2);
			String[] values = rules.get(current).get(rules.get(current).size()-1);
			program.setText(values[0]);
			person.setText(values[1]);
			action.setBackground(imageLibrary.notifOpts[actToID(values[2])]);
			int i = rules.get(current).size()-1;
			makeRuleRow.setId(i + 20000);
			program.setId(21000 + i);
			person.setId(22000 + i);
			action.setId(23000 + i);
			//rulesTable.addView(makeRuleRow);
			
			builder.setView(makeRuleRow)
			.setTitle("New Time").setPositiveButton("Create", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
					buildRuleRows();
				}
			}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
					rules.get(current).remove(rules.get(current).size()-1);
					buildRuleRows();
				}
			});
			AlertDialog dialog = builder.create();
			dialog.show();
		}
	};
	View.OnClickListener editTimesClickHandler = new View.OnClickListener()
	{
		public void onClick(View v)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(myself);
			LayoutInflater inflater = getLayoutInflater();
			int[][] valuesStart = {{8, 30},{9, 30}};
			makeTime();
			TableRow makeTimeRow = (TableRow) inflater.inflate(R.layout.maketime, null);
			TextView startD = (TextView) makeTimeRow.getChildAt(0);
			TextView startH = (TextView) makeTimeRow.getChildAt(1);
			TextView endD = (TextView) makeTimeRow.getChildAt(2);
			TextView endH = (TextView) makeTimeRow.getChildAt(3);
			int[][] values = times.get(current).get(times.get(current).size()-1);
			startD.setText(intToDay(values[0][0]) + " ");
			startH.setText(timeToString(values[0]) + " to ");
			endD.setText(intToDay(values[1][0]) + " ");
			endH.setText(timeToString(values[1]));
			int i = times.get(current).size()-1;
			makeTimeRow.setId(i + 30000);
			startD.setId(31000 + i);
			startH.setId(32000 + i);
			endD.setId(33000 + i);
			endH.setId(34000 + i);
			//timesTable.addView(makeTimeRow);
			
			builder.setView(makeTimeRow)
			.setTitle("New Time").setPositiveButton("Create", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
					buildTimeRows();
				}
			}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
					times.get(current).remove(times.get(current).size()-1);
					buildTimeRows();
				}
			});
			AlertDialog dialog = builder.create();
			dialog.show();
		}
	};
}