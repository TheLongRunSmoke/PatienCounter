package com.tlrs.patiencounter;

import java.util.Date;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.Toast;

public class Widget extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		for (int i : appWidgetIds) {
			updateWidget(context, appWidgetManager, i);
		}
	}

	static void updateWidget(Context context,
			AppWidgetManager appWidgetManager, int widgetID) {
		
		SharedPreferences sp = context.getSharedPreferences(
				ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE);

		RemoteViews widgetView = new RemoteViews(context.getPackageName(),
				R.layout.widget);

		switch (sp.getInt("Type", 0)) {
		case 3: {
			Log.d("LOG", "Set medicalatention");
			widgetView.setImageViewResource(R.id.MedicalAtention,
					android.R.drawable.radiobutton_on_background);
			widgetView.setImageViewResource(R.id.Surgery,
					android.R.drawable.radiobutton_off_background);
			widgetView.setImageViewResource(R.id.Trauma,
					android.R.drawable.radiobutton_off_background);
			break;
		}
		case 2: {
			Log.d("LOG", "Set trauma");
			widgetView.setImageViewResource(R.id.MedicalAtention,
					android.R.drawable.radiobutton_off_background);
			widgetView.setImageViewResource(R.id.Trauma,
					android.R.drawable.radiobutton_on_background);
			widgetView.setImageViewResource(R.id.Surgery,
					android.R.drawable.radiobutton_off_background);
			break;
		}
		case 1: {
			Log.d("LOG", "Set surgery");
			widgetView.setImageViewResource(R.id.MedicalAtention,
					android.R.drawable.radiobutton_off_background);
			widgetView.setImageViewResource(R.id.Surgery,
					android.R.drawable.radiobutton_on_background);
			widgetView.setImageViewResource(R.id.Trauma,
					android.R.drawable.radiobutton_off_background);
			break;
		}
		case 0: {
			Log.d("LOG", "Clear type");
			widgetView.setImageViewResource(R.id.MedicalAtention,
					android.R.drawable.radiobutton_off_background);
			widgetView.setImageViewResource(R.id.Surgery,
					android.R.drawable.radiobutton_off_background);
			widgetView.setImageViewResource(R.id.Trauma,
					android.R.drawable.radiobutton_off_background);
			break;
		}
		}
		switch (sp.getInt("Age", 0)) {
		case 3: {
			Log.d("LOG", "Set over60");
			widgetView.setImageViewResource(R.id.Over60,
					android.R.drawable.radiobutton_on_background);
			widgetView.setImageViewResource(R.id.A1760,
					android.R.drawable.radiobutton_off_background);
			widgetView.setImageViewResource(R.id.Ander17,
					android.R.drawable.radiobutton_off_background);
			break;
		}
		case 2: {
			Log.d("LOG", "Set a1760");
			widgetView.setImageViewResource(R.id.Over60,
					android.R.drawable.radiobutton_off_background);
			widgetView.setImageViewResource(R.id.A1760,
					android.R.drawable.radiobutton_on_background);
			widgetView.setImageViewResource(R.id.Ander17,
					android.R.drawable.radiobutton_off_background);
			break;
		}
		case 1: {
			Log.d("LOG", "Set ander 17");
			widgetView.setImageViewResource(R.id.Over60,
					android.R.drawable.radiobutton_off_background);
			widgetView.setImageViewResource(R.id.A1760,
					android.R.drawable.radiobutton_off_background);
			widgetView.setImageViewResource(R.id.Ander17,
					android.R.drawable.radiobutton_on_background);
			break;
		}
		case 0: {
			Log.d("LOG", "Clear age");
			widgetView.setImageViewResource(R.id.Over60,
					android.R.drawable.radiobutton_off_background);
			widgetView.setImageViewResource(R.id.A1760,
					android.R.drawable.radiobutton_off_background);
			widgetView.setImageViewResource(R.id.Ander17,
					android.R.drawable.radiobutton_off_background);
			break;
		}
		}
		switch (sp.getInt("Expand", 0)) {
		case 1: {
			widgetView.setViewVisibility(R.id.Expand, View.GONE);
			widgetView.setViewVisibility(R.id.MedicalinhomeCB, View.GONE);
			widgetView.setImageViewResource(R.id.ImageExpand,
					android.R.drawable.arrow_up_float);
			widgetView.setViewVisibility(R.id.MedicalinhomeCB, View.VISIBLE);
			widgetView.setViewVisibility(R.id.Expand, View.VISIBLE);
			break;
		}
		case 0: {
			widgetView.setViewVisibility(R.id.MedicalinhomeCB, View.GONE);
			widgetView.setImageViewResource(R.id.ImageExpand,
					android.R.drawable.arrow_down_float);
			break;
		}
		}
		switch (sp.getInt("Mih", 0)) {
		case 1: {
			widgetView.setImageViewResource(R.id.Medicalinhome,
					android.R.drawable.checkbox_on_background);
			break;
		}
		case 0: {
			widgetView.setImageViewResource(R.id.Medicalinhome,
					android.R.drawable.checkbox_off_background);
			break;
		}
		}

		Intent configIntent = new Intent(context, ConfigActivity.class);
		configIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
		configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
		PendingIntent pIntent = PendingIntent.getActivity(context, widgetID,
				configIntent, 0);
		widgetView.setOnClickPendingIntent(R.id.DaySum, pIntent);

		Intent addIntent = new Intent(context, Widget.class);
		addIntent.setAction("com.tlrs.patiencounter.tap.add");
		addIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
		pIntent = PendingIntent.getBroadcast(context, widgetID, addIntent, 0);
		widgetView.setOnClickPendingIntent(R.id.AddButton, pIntent);

		Intent surgIntent = new Intent(context, Widget.class);
		surgIntent.setAction("com.tlrs.patiencounter.tap.surge");
		surgIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
		pIntent = PendingIntent.getBroadcast(context, widgetID, surgIntent, 0);
		widgetView.setOnClickPendingIntent(R.id.SurgeryRB, pIntent);

		Intent traumaIntent = new Intent(context, Widget.class);
		traumaIntent.setAction("com.tlrs.patiencounter.tap.trauma");
		traumaIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
		pIntent = PendingIntent
				.getBroadcast(context, widgetID, traumaIntent, 0);
		widgetView.setOnClickPendingIntent(R.id.TraumaRB, pIntent);

		Intent ander17Intent = new Intent(context, Widget.class);
		ander17Intent.setAction("com.tlrs.patiencounter.tap.ander17");
		ander17Intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
		pIntent = PendingIntent.getBroadcast(context, widgetID, ander17Intent,
				0);
		widgetView.setOnClickPendingIntent(R.id.Ander17RB, pIntent);

		Intent a1760Intent = new Intent(context, Widget.class);
		a1760Intent.setAction("com.tlrs.patiencounter.tap.a1760");
		a1760Intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
		pIntent = PendingIntent.getBroadcast(context, widgetID, a1760Intent, 0);
		widgetView.setOnClickPendingIntent(R.id.A1760RB, pIntent);

		Intent over60Intent = new Intent(context, Widget.class);
		over60Intent.setAction("com.tlrs.patiencounter.tap.over60");
		over60Intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
		pIntent = PendingIntent
				.getBroadcast(context, widgetID, over60Intent, 0);
		widgetView.setOnClickPendingIntent(R.id.Over60RB, pIntent);

		Intent MedicalAIntent = new Intent(context, Widget.class);
		MedicalAIntent.setAction("com.tlrs.patiencounter.tap.medicalatention");
		MedicalAIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
		pIntent = PendingIntent.getBroadcast(context, widgetID, MedicalAIntent,
				0);
		widgetView.setOnClickPendingIntent(R.id.MedicalAtentionRB, pIntent);

		Intent ExpandIntent = new Intent(context, Widget.class);
		ExpandIntent.setAction("com.tlrs.patiencounter.tap.expand");
		ExpandIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
		pIntent = PendingIntent
				.getBroadcast(context, widgetID, ExpandIntent, 0);
		widgetView.setOnClickPendingIntent(R.id.Expand, pIntent);

		if (sp.getInt("Expand", 0) == 1) {
			Intent MihIntent = new Intent(context, Widget.class);
			MihIntent.setAction("com.tlrs.patiencounter.tap.mih");
			MihIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
			pIntent = PendingIntent.getBroadcast(context, widgetID, MihIntent,
					0);
			widgetView.setOnClickPendingIntent(R.id.MedicalinhomeCB, pIntent);
		}

		DBHelper dbHelper = new DBHelper(context);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String[] date = {DateFormat.format("dd.MM.yyyy", new Date()).toString()};
		Cursor c = db.query("Stat", null, "date = ?", date, null, null, null);
		widgetView.setTextViewText(R.id.DaySum, Integer.toString(c.getCount()));
		dbHelper.close();
		
		appWidgetManager.updateAppWidget(widgetID, widgetView);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		// Обработка хирургии
		if (intent.getAction().equalsIgnoreCase(
				"com.tlrs.patiencounter.tap.surge")) {
			Log.d("LOG", "surgery tap");
			int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
			Bundle extras = intent.getExtras();
			if (extras != null) {
				mAppWidgetId = extras.getInt(
						AppWidgetManager.EXTRA_APPWIDGET_ID,
						AppWidgetManager.INVALID_APPWIDGET_ID);
			}
			if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
				SharedPreferences sp = context.getSharedPreferences(
						ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE);
				if (sp.getInt("Type", 0) != 1) {
					Log.d("LOG", "Type 1");
					Editor editor = sp.edit();
					editor.putInt("Type", 1).commit();
				}
				updateWidget(context, AppWidgetManager.getInstance(context),
						mAppWidgetId);
			}
		}
		// Обработка травмы
		if (intent.getAction().equalsIgnoreCase(
				"com.tlrs.patiencounter.tap.trauma")) {
			Log.d("LOG", "trauma tap");
			int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
			Bundle extras = intent.getExtras();
			if (extras != null) {
				mAppWidgetId = extras.getInt(
						AppWidgetManager.EXTRA_APPWIDGET_ID,
						AppWidgetManager.INVALID_APPWIDGET_ID);
			}
			if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
				SharedPreferences sp = context.getSharedPreferences(
						ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE);
				if (sp.getInt("Type", 0) != 2) {
					Log.d("LOG", "Type 2");
					Editor editor = sp.edit();
					editor.putInt("Type", 2).commit();
				}
				updateWidget(context, AppWidgetManager.getInstance(context),
						mAppWidgetId);
			}
		}
		// Обработка медосмотра
		if (intent.getAction().equalsIgnoreCase(
				"com.tlrs.patiencounter.tap.medicalatention")) {
			Log.d("LOG", "medicalatention tap");
			int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
			Bundle extras = intent.getExtras();
			if (extras != null) {
				mAppWidgetId = extras.getInt(
						AppWidgetManager.EXTRA_APPWIDGET_ID,
						AppWidgetManager.INVALID_APPWIDGET_ID);
			}
			if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
				SharedPreferences sp = context.getSharedPreferences(
						ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE);
				if (sp.getInt("Type", 0) != 3) {
					Log.d("LOG", "Type 3");
					Editor editor = sp.edit();
					editor.putInt("Type", 3).commit();
					editor.putInt("Mih", 0).commit();
					editor.putInt("Expand", 0).commit();
				}
				updateWidget(context, AppWidgetManager.getInstance(context),
						mAppWidgetId);
			}
		}
		// Обработка "менее 17"
		if (intent.getAction().equalsIgnoreCase(
				"com.tlrs.patiencounter.tap.ander17")) {
			Log.d("LOG", "ander17 tap");
			int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
			Bundle extras = intent.getExtras();
			if (extras != null) {
				mAppWidgetId = extras.getInt(
						AppWidgetManager.EXTRA_APPWIDGET_ID,
						AppWidgetManager.INVALID_APPWIDGET_ID);
			}
			if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
				SharedPreferences sp = context.getSharedPreferences(
						ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE);
				if (sp.getInt("Age", 0) != 1) {
					Log.d("LOG", "Age 1");
					Editor editor = sp.edit();
					editor.putInt("Age", 1).commit();
				}
				updateWidget(context, AppWidgetManager.getInstance(context),
						mAppWidgetId);
			}
		}
		// Обработка "от 17 до 60"
		if (intent.getAction().equalsIgnoreCase(
				"com.tlrs.patiencounter.tap.a1760")) {
			Log.d("LOG", "a1760 tap");
			int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
			Bundle extras = intent.getExtras();
			if (extras != null) {
				mAppWidgetId = extras.getInt(
						AppWidgetManager.EXTRA_APPWIDGET_ID,
						AppWidgetManager.INVALID_APPWIDGET_ID);
			}
			if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
				SharedPreferences sp = context.getSharedPreferences(
						ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE);
				if (sp.getInt("Age", 0) != 2) {
					Log.d("LOG", "Age 2");
					Editor editor = sp.edit();
					editor.putInt("Age", 2).commit();
				}
				updateWidget(context, AppWidgetManager.getInstance(context),
						mAppWidgetId);
			}
		}
		// Обработка "старше 60"
		if (intent.getAction().equalsIgnoreCase(
				"com.tlrs.patiencounter.tap.over60")) {
			Log.d("LOG", "over60 tap");
			int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
			Bundle extras = intent.getExtras();
			if (extras != null) {
				mAppWidgetId = extras.getInt(
						AppWidgetManager.EXTRA_APPWIDGET_ID,
						AppWidgetManager.INVALID_APPWIDGET_ID);
			}
			if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
				SharedPreferences sp = context.getSharedPreferences(
						ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE);
				if (sp.getInt("Age", 0) != 3) {
					Log.d("LOG", "Age 3");
					Editor editor = sp.edit();
					editor.putInt("Age", 3).commit();
				}
				updateWidget(context, AppWidgetManager.getInstance(context),
						mAppWidgetId);
			}
		}
		// Обработка развертывания меню
		if (intent.getAction().equalsIgnoreCase(
				"com.tlrs.patiencounter.tap.expand")) {
			Log.d("LOG", "tap expand");
			int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
			Bundle extras = intent.getExtras();
			if (extras != null) {
				mAppWidgetId = extras.getInt(
						AppWidgetManager.EXTRA_APPWIDGET_ID,
						AppWidgetManager.INVALID_APPWIDGET_ID);
			}
			if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
				SharedPreferences sp = context.getSharedPreferences(
						ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE);
				if (sp.getInt("Expand", 0) == 0) {
					Log.d("LOG", "Expand 1");
					Editor editor = sp.edit();
					editor.putInt("Expand", 1).commit();
				} else {
					Log.d("LOG", "Expand 0");
					Editor editor = sp.edit();
					editor.putInt("Expand", 0).commit();
				}
				updateWidget(context, AppWidgetManager.getInstance(context),
						mAppWidgetId);
			}
		}
		// Обработка "на дому"
		if (intent.getAction().equalsIgnoreCase(
				"com.tlrs.patiencounter.tap.mih")) {
			Log.d("LOG", "tap mih");
			int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
			Bundle extras = intent.getExtras();
			if (extras != null) {
				mAppWidgetId = extras.getInt(
						AppWidgetManager.EXTRA_APPWIDGET_ID,
						AppWidgetManager.INVALID_APPWIDGET_ID);
			}
			if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
				SharedPreferences sp = context.getSharedPreferences(
						ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE);
				if (sp.getInt("Mih", 0) == 0) {
					Log.d("LOG", "Mih 1");
					Editor editor = sp.edit();
					editor.putInt("Mih", 1).commit();
				} else {
					Log.d("LOG", "Mih 0");
					Editor editor = sp.edit();
					editor.putInt("Mih", 0).commit();
				}
				updateWidget(context, AppWidgetManager.getInstance(context),
						mAppWidgetId);
			}
		}
		// Обработка добавления записи
		if (intent.getAction().equalsIgnoreCase(
				"com.tlrs.patiencounter.tap.add")) {
			Log.d("LOG", "add tap");
			int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
			Bundle extras = intent.getExtras();
			if (extras != null) {
				mAppWidgetId = extras.getInt(
						AppWidgetManager.EXTRA_APPWIDGET_ID,
						AppWidgetManager.INVALID_APPWIDGET_ID);
			}
			if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
				SharedPreferences sp = context.getSharedPreferences(
						ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE);
				if ((sp.getInt("Type", 0) != 0) & (sp.getInt("Age", 0) != 0)) {
					// Если введено всё, то обрабатываю
					ContentValues cv = new ContentValues();
					DBHelper dbHelper = new DBHelper(context);
					SQLiteDatabase db = dbHelper.getWritableDatabase();
					cv.put("date", DateFormat.format("dd.MM.yyyy", new Date())
							.toString());
					cv.put("type", sp.getInt("Type", 0));
					cv.put("age", sp.getInt("Age", 0));
					cv.put("spec", sp.getInt("Mih", 0));
					Log.d("LOG_TAG", cv.toString());
					db.insert("Stat", null, cv);
					dbHelper.close();
					if (sp.getInt("Type", 0) != 0) {
						Editor editor = sp.edit();
						editor.putInt("Type", 0).commit();
					}
					if (sp.getInt("Age", 0) != 0) {
						Editor editor = sp.edit();
						editor.putInt("Age", 0).commit();
					}
					if (sp.getInt("Mih", 0) != 0) {
						Editor editor = sp.edit();
						editor.putInt("Mih", 0).commit();
					}
					if (sp.getInt("Expand", 0) != 0) {
						Editor editor = sp.edit();
						editor.putInt("Expand", 0).commit();
					}
					Toast toast = Toast.makeText(context, "Запись добавлена",
							Toast.LENGTH_SHORT);
					LinearLayout toastView = (LinearLayout) toast.getView();
					ImageView image = new ImageView(context);
					image.setImageResource(android.R.drawable.ic_input_add);
					toastView.addView(image, 0);
					toast.show();
					
				} else // При ошибке ввода
				{
					Toast toast = Toast
							.makeText(
									context,
									"Проверьте введеные данные! Тип и возраст обязательны!",
									Toast.LENGTH_LONG);
					LinearLayout toastView = (LinearLayout) toast.getView();
					ImageView image = new ImageView(context);
					image.setImageResource(android.R.drawable.ic_delete);
					toastView.addView(image, 0);
					toast.show();
				}
				updateWidget(context, AppWidgetManager.getInstance(context),
						mAppWidgetId);
			}
		}
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
	}

}
