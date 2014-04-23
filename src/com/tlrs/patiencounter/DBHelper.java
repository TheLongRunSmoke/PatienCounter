package com.tlrs.patiencounter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DBHelper extends SQLiteOpenHelper {

	public DBHelper(Context context) {
		super(context, "Stat", null, 2);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table Stat ("
				+ "_id integer primary key autoincrement,"
				+ "date string, " + "type integer," + "age integer,"
				+ "spec integer" + ");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
