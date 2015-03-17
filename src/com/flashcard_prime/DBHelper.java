package com.flashcard_prime;

/*
 * DBHelper.java - Do initial database creation
 *
 * Code taken and modified from Neil Davies: http://www.inter-fuser.com/2009/08/android-animations-3d-flip.html
 *
 * This class is used in the contex of other .java files like this:
 *
 * *********************************************************
 * DBHelper dbHelper;
 * SQliteDatabse db_get_stuff;
 * Cursor cursor_db_get_stuff;
 *
 * dbHelper = new DBHelper (this);
 * db_get_stuff = dbHelper.getWritabledatabase();
 * *********************************************************
 *
 * We create the flashcard_preferences.db sqlite3 file.  The name is misleading, as it's more than 
 * just preferences now.  We should look to change this name to something more appropriate.
 *
 * We also create 2 different tables, 'flashcard_preferences' and 'list_of_decks'.
 * They are created differently.  The 'flashcard_preferences' is done in method I got from the link
 * listed above.  The 'list_of_decks' was created using my style, as I like to use more direct sql
 * as it is easier for me to see what is going on. 
 *
 * The onUpgrade() routine doesn't really do anything of what we want, to we'll need to revist that 
 * later.  
 *
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	static final String TAG = "DBHelper";
	static final String DB_NAME = "flashcard_preferences.db";
	static final int DB_VERSION = 1;
	static final String TABLE = "flashcard_preferences";
	static final String C_ID = BaseColumns._ID;
	static final String C_BACKGROUND_COLOR = "background_color";
	Context context;

	// Constructor
	public DBHelper (Context context) {
		super (context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}

	// onCreate()
	@Override
	public void onCreate (SQLiteDatabase db) {
		// Create Preferences table
		String sql = "create table " + TABLE + " (" + C_ID + " int primary key, "
		+ C_BACKGROUND_COLOR + " text)";

		db.execSQL (sql);

		Log.d (TAG, "onCreated sql: " + sql);

		// Create a table that is a list of all of the decks that we currently have:
		String sql_list_of_decks_table = "create table list_of_decks (" + C_ID + " int primary key, deck_name text, category_class text, category_specific text, isactive text)";
		
		db.execSQL (sql_list_of_decks_table);
	}
	
	// called when there is an update to the database schema
	@Override
	public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
		//typically do alter statements, but just do drops / create for now
		db.execSQL ("drop table if exists " + TABLE);
		db.execSQL ("drop table if exists flashcards_spanish");
		Log.d (TAG, "onUpdated");
		onCreate (db);
	}
}	
