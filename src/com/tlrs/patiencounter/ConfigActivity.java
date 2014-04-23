package com.tlrs.patiencounter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

public class ConfigActivity extends FragmentActivity {

	final static String VALUE = "value";
	final static String DATE = "date";

	public int TabVisible = 1;

	public final static String WIDGET_PREF = "widget_pref";

	int widgetID = AppWidgetManager.INVALID_APPWIDGET_ID;
	Intent resultValue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		}
		if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
			finish();
		}

		resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);

		setResult(RESULT_CANCELED, resultValue);

		setContentView(R.layout.config);
		
		if (Build.VERSION.SDK_INT >= 11){
			findViewById(R.id.nmdb).setVisibility(View.VISIBLE);
		}
		
		TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();
		TabHost.TabSpec tabSpec;

		tabSpec = tabHost.newTabSpec("surgery"); // Созданю новую вкладку и
													// присваиваю тэг
		tabSpec.setIndicator(getResources().getText(R.string.surgery_text));
		tabSpec.setContent(R.id.surgeryScroll);
		tabHost.addTab(tabSpec);

		tabSpec = tabHost.newTabSpec("trauma");
		tabSpec.setIndicator(getResources().getText(R.string.trauma));
		tabSpec.setContent(R.id.traumaScroll);
		tabHost.addTab(tabSpec);

		tabHost.setCurrentTabByTag("surgery");

		for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
			tabHost.getTabWidget().getChildAt(i).getLayoutParams().height /= 2;

		}

		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) { // tabId - теги
				if (tabId == "surgery") {
					TabVisible = 1;
				}
				if (tabId == "trauma") {
					TabVisible = 2;
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "Удалить последнюю запись");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
		case 1:{
			DialogFragment deldialog = new DeletDialog();
			deldialog.show(getSupportFragmentManager(), "deldialog");
			break;
		}
		}
		return super.onOptionsItemSelected(item);
	}

	public void onClick(View v) {
		Log.d("LOG", Integer.toString(v.getId()));
		switch (v.getId()) {
		case R.id.forDay: {
			fillTable task = new fillTable(this, 1);
			task.execute();
			break;
		}
		case R.id.forMonth: {
			fillTable task = new fillTable(this, 2);
			task.execute();
			break;
		}
		case R.id.nmdb: {
			DialogFragment nmdeld = new DeletDialog();
			nmdeld.show(getSupportFragmentManager(), "nmdeld");
			break;
		}
		case R.id.close: {
			setResult(RESULT_OK, resultValue);
			finish();
		}
		}
	}

	public class fillTable extends AsyncTask<Void, String, Void> {

		private WeakReference<Activity> mActivity;
		private int inttime;
		ArrayList<Map<String, Object>> data;

		public fillTable(Activity activity, int time) {
			this.mActivity = new WeakReference<Activity>(activity);
			inttime = time;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			publishProgress("");
			((LinearLayout) mActivity.get().findViewById(R.id.progress))
					.setVisibility(View.VISIBLE);
			((LinearLayout) mActivity.get().findViewById(R.id.buttons))
					.setVisibility(View.GONE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			data = null;
			DatePicker dp = (DatePicker) mActivity.get()
					.findViewById(R.id.date);
			int month = dp.getMonth();
			String year = Integer.toString(dp.getYear());
			switch (TabVisible) {
			case 1: { // Хирургия
				int[] fromint = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
				DBHelper dbHelper = new DBHelper(mActivity.get());
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				switch (inttime) {
				case 1: { // День
					publishProgress("");
					String day = Integer.toString(dp.getDayOfMonth());
					String[] date = { day + '.'
							+ String.format("%02d", (month + 1)) + '.' + year };
					data = new ArrayList<Map<String, Object>>(fromint.length);
					data.add(GetRowFromDBs(db, date));
					dbHelper.close();
					break;
				}
				case 2: { // Месяц
					int dayinmonth = (new GregorianCalendar(
							Integer.parseInt(year), month, 1))
							.getActualMaximum(Calendar.DAY_OF_MONTH);
					data = new ArrayList<Map<String, Object>>(fromint.length);
					for (int i = 1; i < dayinmonth + 1; i++) {
						String[] date = { String.format("%02d", i) + '.'
								+ String.format("%02d", (month + 1)) + '.'
								+ year };
						data.add(GetRowFromDBs(db, date));
						publishProgress(String.format("%02d", i) + '/'
								+ dayinmonth);
					}
					dbHelper.close();
					break;
				}
				}
				break;
			}
			case 2: { // Травма
				int[] fromint = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
				DBHelper dbHelper = new DBHelper(mActivity.get());
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				switch (inttime) {
				case 1: {
					publishProgress("");
					String day = Integer.toString(dp.getDayOfMonth());
					String[] date = { day + '.'
							+ String.format("%02d", (month + 1)) + '.' + year };
					data = new ArrayList<Map<String, Object>>(fromint.length);
					data.add(GetRowFromDBt(db, date));
					dbHelper.close();
					break;
				}
				case 2: {
					int dayinmonth = (new GregorianCalendar(
							Integer.parseInt(year), month, 1))
							.getActualMaximum(Calendar.DAY_OF_MONTH);
					data = new ArrayList<Map<String, Object>>(fromint.length);
					for (int i = 1; i < dayinmonth + 1; i++) {
						String[] date = { String.format("%02d", i) + '.'
								+ String.format("%02d", (month + 1)) + '.'
								+ year };
						data.add(GetRowFromDBt(db, date));
						publishProgress(String.format("%02d", i) + '/'
								+ dayinmonth);
					}
					dbHelper.close();
					break;
				}
				}
				break;
			}
			}
			return null;
		}

		// Эта фенкция подсчитывает пациентов в бвзе db за день date и
		// возвращает
		// строку в виде мапы.
		public Map<String, Object> GetRowFromDBs(SQLiteDatabase db,
				String[] date) {
			int[] fromint = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			Map<String, Object> m = null;
			String rowdate = null;
			Cursor c = db.query("Stat", null, "date = ?", date, null, null,
					null);
			Log.d("LOG_TAG", Integer.toString(c.getCount()));
			if ((c != null) & (c.getCount() != 0)) {
				if (c.moveToFirst()) {
					do {
						int type = c.getInt(c.getColumnIndex("type"));
						Log.d("LOG_TAG", "type = " + Integer.toString(type));
						int age = c.getInt(c.getColumnIndex("age"));
						Log.d("LOG_TAG", "age = " + Integer.toString(age));
						int spec = c.getInt(c.getColumnIndex("spec"));
						Log.d("LOG_TAG", "spec = " + Integer.toString(spec));
						rowdate = c.getString(c.getColumnIndex("date"));
						switch (type) {
						case 1: {
							fromint[0]++; // Всего увеличиваю на единицу
							fromint[3]++; // Всего для заболеваний
							switch (age) {
							case 1: { // До 17
								fromint[1]++; // Общее
								fromint[4]++; // Заболеваний
								break;
							}
							case 2: {
								break;
							}
							case 3: { // Старше 60
								fromint[2]++; // Общее
								fromint[5]++; // Заболеваний
								break;
							}
							}
							if (c.getInt(c.getColumnIndex("spec")) == 1) {
								fromint[8]++; // Всего на дому
								switch (age) {
								case 1: { // До 17
									fromint[9]++; // На дому
									break;
								}
								case 2: {
									break;
								}
								case 3: { // Старше 60
									fromint[10]++; // На дому
									break;
								}
								}
							}
							break;
						}
						case 2: {
							// Для травмы
							break;
						}
						case 3: {
							fromint[0]++; // Всего
							switch (age) {
							case 1: { // До 17
								fromint[1]++; // Общее
								break;
							}
							case 3: { // Старше 60
								fromint[2]++; // Общее
								break;
							}
							}
							if (age > 1) {
								fromint[7]++; // Взрослые медосмотры
							} else {
								fromint[6]++; // Детские медосмотры
							}
							break;
						}
						}
					} while (c.moveToNext());
				}
				m = new HashMap<String, Object>();
				m.put(DATE, rowdate);
				for (int i = 0; i < fromint.length; i++) {
					if (fromint[i] == 0) {
						m.put(VALUE + Integer.toString(i), "-");
					} else {
						m.put(VALUE + Integer.toString(i),
								Integer.toString(fromint[i]));
					}
				}
			} else {
				m = new HashMap<String, Object>();
				m.put(DATE, date[0]);
				for (int i = 0; i < fromint.length; i++) {
					m.put(VALUE + Integer.toString(i), "-");
				}
			}
			return m;
		}

		// Эта фенкция подсчитывает пациентов в бвзе db за день date и
		// возвращает
		// строку в виде мапы.
		public Map<String, Object> GetRowFromDBt(SQLiteDatabase db,
				String[] date) {
			int[] fromint = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			Map<String, Object> m = null;
			String rowdate = null;
			Cursor c = db.query("Stat", null, "date = ?", date, null, null,
					null);
			Log.d("LOG_TAG", Integer.toString(c.getCount()));
			if ((c != null) & (c.getCount() != 0)) {
				if (c.moveToFirst()) {
					do {
						int type = c.getInt(c.getColumnIndex("type"));
						Log.d("LOG_TAG", "type = " + Integer.toString(type));
						int age = c.getInt(c.getColumnIndex("age"));
						Log.d("LOG_TAG", "age = " + Integer.toString(age));
						int spec = c.getInt(c.getColumnIndex("spec"));
						Log.d("LOG_TAG", "spec = " + Integer.toString(spec));
						rowdate = c.getString(c.getColumnIndex("date"));
						switch (type) {
						case 1: {
							break;
						}
						case 2: { // Для травмы
							fromint[0]++;
							fromint[3]++;
							switch (age) {
							case 1: { // До 17
								fromint[1]++;
								fromint[4]++;
								break;
							}
							case 2: {
								break;
							}
							case 3: { // Старше 60
								fromint[2]++; // Общее
								fromint[5]++; // Заболеваний
								break;
							}
							}
							if (c.getInt(c.getColumnIndex("spec")) == 1) {
								fromint[6]++; // Всего на дому
								switch (age) {
								case 1: { // До 17
									fromint[7]++; // На дому
									break;
								}
								case 2: {
									break;
								}
								case 3: { // Старше 60
									fromint[8]++; // На дому
									break;
								}
								}
							}
							break;
						}
						case 3: {
							break;
						}
						}
					} while (c.moveToNext());
				}
				m = new HashMap<String, Object>();
				m.put(DATE, rowdate);
				for (int i = 0; i < fromint.length; i++) {
					if (fromint[i] == 0) {
						m.put(VALUE + Integer.toString(i), "-");
					} else {
						m.put(VALUE + Integer.toString(i),
								Integer.toString(fromint[i]));
					}
				}
			} else {
				m = new HashMap<String, Object>();
				m.put(DATE, date[0]);
				for (int i = 0; i < fromint.length; i++) {
					m.put(VALUE + Integer.toString(i), "-");
				}
			}
			return m;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			((TextView) mActivity.get().findViewById(R.id.progressText))
					.setText(values[0]);
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			switch (TabVisible) {
			case 1: {
				String from[] = { DATE, VALUE + 0, VALUE + 1, VALUE + 2,
						VALUE + 3, VALUE + 4, VALUE + 5, VALUE + 6, VALUE + 7,
						VALUE + 8, VALUE + 9, VALUE + 10 };
				int[] to = { R.id.date, R.id.c0, R.id.c1, R.id.c2, R.id.c3,
						R.id.c4, R.id.c5, R.id.c6, R.id.c7, R.id.c8, R.id.c9,
						R.id.c10 };
				SimpleAdapter sa = new SimpleAdapter(mActivity.get(), data,
						R.layout.surgeryadapter, from, to);
				((ListView) mActivity.get().findViewById(R.id.SurgeryList))
						.setAdapter(sa);
				break;
			}
			case 2: {
				String from[] = { DATE, VALUE + 0, VALUE + 1, VALUE + 2,
						VALUE + 3, VALUE + 4, VALUE + 5, VALUE + 6, VALUE + 7,
						VALUE + 8 };
				int[] to = { R.id.date, R.id.c0, R.id.c1, R.id.c2, R.id.c3,
						R.id.c4, R.id.c5, R.id.c8, R.id.c9, R.id.c10 };
				SimpleAdapter sa = new SimpleAdapter(mActivity.get(), data,
						R.layout.traumaadapter, from, to);
				((ListView) mActivity.get().findViewById(R.id.TraumaList))
						.setAdapter(sa);
				break;
			}
			}
			((LinearLayout) mActivity.get().findViewById(R.id.buttons))
					.setVisibility(View.VISIBLE);
			((LinearLayout) mActivity.get().findViewById(R.id.progress))
					.setVisibility(View.GONE);
			Log.d("LOG", "Задача завершена");
		}

	}
}
