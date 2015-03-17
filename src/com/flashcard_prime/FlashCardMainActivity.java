package com.flashcard_prime;

/*
 * FlashCardMainActivity.java - Show the decks that are available to view.
 *   We actually do more here, including setting up the database
 *   will all of the stuff needed to allow other routines to 
 *   access what in the database.    
 *
 */

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.database.Cursor;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class FlashCardMainActivity extends Activity
{
	View main_screen_view;
	//Button spanish_main_button;

	DBHelper dbHelper;

	// Probably don't need to have multiple instances of SQLiteDatabase, but it makes it easier
	// to see what belongs to what for now
	SQLiteDatabase db;
	SQLiteDatabase db_get_color;
	SQLiteDatabase db_populate_flashcards;
	SQLiteDatabase db_really;  // This is used in the menu selection routine
	SQLiteDatabase db_prepare_deck_databases; // Used in prepare_deck_databases()

	int select_counter_list_of_decks = 0;

	String select_deck_name;
	String select_category_class;
	String select_category_specific;
	String select_isactive;

	Cursor cursor_list_of_decks;

	ArrayList<String> list_of_decks_values = new ArrayList<String>();
	//String formated_deck_name_to_display;
		
	ContentValues update_values = new ContentValues();

	// Not sure where we should close this file at just yet
	//flashcardmainactivity_log.close();

	FileWriter flashcardmainactivity_log;

	int records_in_list_of_decks;

	Cursor cursor_temp;
				 
	Intent start_flashcard_viewer_intent;

	String string_category_class;	
	String string_category_specific;
	int int_index_underscore;	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
    		super.onCreate(savedInstanceState);
        	setContentView(R.layout.flash_card_main);

		/* Open a log file on the given android device for debugging purposes */

		try {
			flashcardmainactivity_log = new FileWriter ("/data/data/com.flashcard_prime/flashcardmainactivity.log", true);
		}

		catch (IOException e) {
			System.err.println ("leave Britney alone\n");
		}
		
		try {
	           	flashcardmainactivity_log.write ("S0: begin onCreate()\n");
			flashcardmainactivity_log.flush();
		}

		catch (IOException e) {
			System.err.println ("blah\n");
		}

		main_screen_view = (View) findViewById (R.id.main_screen_view_id);

		// Start the database context, i think this is where it would upgrade if necessary
		dbHelper = new DBHelper(this);

		// Lets set a default color:  ...lets not
		//main_screen_view.setBackgroundColor (0xcc0000dd);

		// This is kind of bad, but it's the only way I can figure out how to make this work right now
		// We need a way to populate the database which flashcards that we have.  So we'll populate that
		// here in the main onCreate().
		// To prevent future attempts at inserts, we'll write a lock file to indicate that we have 
		// already inserted the data.

		File does_flag_file_exist = new File ("/data/data/com.flashcard_prime/flashcard_inserts_complete.flag");
		
		try {
	        	flashcardmainactivity_log.write ("S1: before if flag file exists\n");
			flashcardmainactivity_log.flush();
		}

		catch (IOException e) {
			System.err.println ("blah\n");
		}

		db_populate_flashcards = dbHelper.getWritableDatabase();
		
		if (! does_flag_file_exist.exists()) {
			try {
	        		flashcardmainactivity_log.write ("S2: begin if flag file exists\n");
				flashcardmainactivity_log.flush();
			}

			catch (IOException e) {
				System.err.println ("blah\n");
			}

			// Call Prepare database decks routine
			prepare_deck_databases();

			// BEGIN INSERT OF flashcards_spanish
			ContentValues insert_values = new ContentValues();

			try {	
				FileWriter flag_file = new FileWriter ("/data/data/com.flashcard_prime/flashcard_inserts_complete.flag");
				flag_file.write ("just addinging something here.");
				flag_file.close();
			}

			catch (IOException e) {
				System.err.println ("something bad happened with the flag file: " + e);
			}
		}

		try {
	        	flashcardmainactivity_log.write ("S3: outside if file exists\n");
			flashcardmainactivity_log.flush();
		}

		catch (IOException e) {
			System.err.println ("blah\n");
		}

		// Lets have fun with select...

		cursor_list_of_decks = db_populate_flashcards.rawQuery ("select * from list_of_decks;", null);
		try {
	        	flashcardmainactivity_log.write ("S3a: after select\n");
			flashcardmainactivity_log.flush();
		}

		catch (IOException e) {
			System.err.println ("blah\n");
		}

		cursor_list_of_decks.moveToFirst();
		records_in_list_of_decks = cursor_list_of_decks.getCount();

		// The current crash happens in this for loop:	
		//
		// DOH!  The index here returns the data for each column in a given record.
		// So save off each value, then move the cursor.
		
		try {
	        	flashcardmainactivity_log.write ("S4: before for loop to populate ListView\n");
			flashcardmainactivity_log.flush();
		}

		catch (IOException e) {
			System.err.println ("blah\n");
		}

		list_of_decks_values.clear();

		for (select_counter_list_of_decks = 1; select_counter_list_of_decks <= records_in_list_of_decks; select_counter_list_of_decks++) {
			select_deck_name = cursor_list_of_decks.getString(1);
			select_category_class = cursor_list_of_decks.getString(2);
			select_category_specific = cursor_list_of_decks.getString(3);
			select_isactive = cursor_list_of_decks.getString(4);

			list_of_decks_values.add (select_category_class + ":\t\t" + select_category_specific);

			cursor_list_of_decks.moveToNext();
		}

		try {
	        	flashcardmainactivity_log.write ("S5: after for loop to populate ListView\n");
			flashcardmainactivity_log.flush();
		}

		catch (IOException e) {
			System.err.println ("blah\n");
		}

		// Not using button anymore.  Now we try ListView:
		ListView list_of_decks = (ListView) findViewById (R.id.list_of_decks);

		ArrayAdapter<String> list_of_decks_adapter = new ArrayAdapter<String> (this, 
				android.R.layout.simple_list_item_1, android.R.id.text1, list_of_decks_values);

		// By setting this to 0, it prevents the flicker between black and the background color when scrolling.
		list_of_decks.setCacheColorHint (0);

		list_of_decks.setAdapter (list_of_decks_adapter);

		// Now we define the Listener to react to selecting items.
		list_of_decks.setOnItemClickListener (new OnItemClickListener() {

			@Override
			public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
				start_flashcard_viewer_intent = new Intent (FlashCardMainActivity.this, FlashCardDisplayActivity.class);
				db_really = dbHelper.getWritableDatabase();
				
				int int_db_position = (position + 1);

				// Here we are attempting to set all of the decks isactive field to 'N'.  Bascically at this point,
				// we are going to select a new deck to be active, so we want to make sure nothing is active.
			
				db_really.execSQL ("update list_of_decks set isactive='N';");
				db_really.execSQL ("update list_of_decks set isactive='Y' where _id=" + int_db_position + ";");					

				// Now that we have set which deck is the active one, let's start the Activity:
				startActivity (start_flashcard_viewer_intent);
			}
		});
			
    	}

	@Override
	public void onStart() {
		super.onStart();

		// Code for preferences...but really it goes in the main java file?  ok...
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences (this);
		
		// Defaulting to white I'm guessing:
		String background_color_string = sharedPrefs.getString ("background_color", "white");

		/*
		 *
		 * Ok this is the wrong spot for this insert/update code, but 
		 * I don't know how to do this onclicking a listperference,
		 * so we'll just do it onStart() in the FlashCardMainActivity for now
		 *
		*/

		// Prepare database for writing
		db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.clear();
		//Bad to hard-code this, only having 1 record right now...
		values.put (DBHelper.C_ID, 1);
		values.put (DBHelper.C_BACKGROUND_COLOR, background_color_string);

		Cursor c = db.rawQuery ("select * from flashcard_preferences", null);
		
		int records_in_table_int = c.getCount();
		String records_in_table_string = Integer.toString(records_in_table_int);

		if (records_in_table_int == 0) {
			//insert
			db.insertOrThrow(DBHelper.TABLE, null, values);
		}

		else {
			//update
			db.update (DBHelper.TABLE, values, DBHelper.C_ID + "=1" ,null);
		}

		db.close();

		/*
		 *
		 * End wrongly placed code.
		 *
		 */

		/*
		 *
		 * Ok now here is the code to find the color in the database and set it.
		 * This routine needs to go in each Activity's onStart() method.
		 *
		 */

		db_get_color = dbHelper.getWritableDatabase();
		Cursor cursor = db_get_color.rawQuery ("select background_color from flashcard_preferences where _id=1;", null);
	
		cursor.moveToFirst();
		String background_color_string_from_db = cursor.getString(0);

		Toast.makeText (this, background_color_string_from_db, Toast.LENGTH_LONG).show();

		if (background_color_string_from_db.equals ("red")) {
			main_screen_view.setBackgroundColor (0xffff4400);
		}

		else if (background_color_string_from_db.equals ("orange")) {
			main_screen_view.setBackgroundColor (0xffffaa44);
		}

		else if (background_color_string_from_db.equals ("yellow")) {
			main_screen_view.setBackgroundColor (0xffffff00);
		}

		else if (background_color_string_from_db.equals ("green")) {
			main_screen_view.setBackgroundColor (0xff33ff00);
		}	
					
		else if (background_color_string_from_db.equals ("blue")) {
			main_screen_view.setBackgroundColor (0xff00aadd);
		}
	}

	// Function to inflate the Menu xml
	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate (R.menu.main_menu, menu);
		return true;
	}

	// What to do when the menu option is selected
	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		switch (item.getItemId()) {
			case R.id.choose_background_id:
				startActivity (new Intent (this, MainMenuPrefsActivity.class));
				break;
		}

		return true;
	}

	// Im still somewhat at a loss on what to do here.  This is for inserting records in the database
	// for each flashcard.  This routine currently is called from onCreate().
	// What we have will work, it's just an ugly spot to have this code in.  Maybe it is the only way.
	// At least here we have it broken into its own routine.
	
	void prepare_deck_databases() {
		db_prepare_deck_databases = dbHelper.getWritableDatabase();

		// Stuff for regex in loop 2:		
		String string_regex_underscore_a_dot_png = "_a.png";
		Pattern pattern_regex_underscore_a_dot_png = Pattern.compile (string_regex_underscore_a_dot_png, Pattern.CASE_INSENSITIVE);

		// So I guess first we need to get a list of our decks that we know about:

		AssetManager asset_manager = getAssets();

		try {
			String[] deck_names = asset_manager.list ("decks");

			for (int i = 0; i < deck_names.length; i++) {
				String [] png_names = asset_manager.list ("decks/" + deck_names[i]);
			
				// Let's insert our rows into list_of_decks.  Previous it was hard coded, but we can generate everything we want
				// from the deck name.
			
				int_index_underscore = deck_names[i].indexOf ('_');
				string_category_class = deck_names[i].substring (0,1).toUpperCase() + deck_names[i].substring(1);
				string_category_class = string_category_class.substring (0, int_index_underscore);

				string_category_specific = deck_names[i].substring ((int_index_underscore+1));
				string_category_specific = string_category_specific.substring (0,1).toUpperCase() + string_category_specific.substring(1);	


				// After long hard thought, we need to get the category_class and category_specifc values inserted here
				// since we use that for the display of which deck to choose.
				db_prepare_deck_databases.execSQL ("insert into list_of_decks values (" + (i+1) + ", " + "'" + deck_names[i] + "'" + ", '" + string_category_class + "', '" + string_category_specific + "', 'N');");

				// Lets create the table for each deck here:
				// This will create tables with the name: flashcards_$deckname
				// where $deckname is the name of each directory in assets/decks/* 
				db_prepare_deck_databases.execSQL ("create table flashcards_" + deck_names[i] + " (" + BaseColumns._ID + " int primary key, flashcard_name_base text);");

				for (int j = 0; j < png_names.length; j++) {
					// now for each picture, insert it into the table deck_names[i]	
					// Heres the thing though.  We are going to be going through _a and _b, so lets 
					// just look at _a.png files.  Grab just the basename, and that's what we will enter
					// into the table.
				
					Matcher matcher_object = pattern_regex_underscore_a_dot_png.matcher (png_names[j]);

					if (matcher_object.find()) {
						// This replaceAll removes the _a.png, thus giving us the basename:
						png_names[j] = png_names[j].replaceAll ("_a.png", "");
					
						// Now after all that, we finally insert!
						db_prepare_deck_databases.execSQL ("insert into flashcards_" + deck_names[i] + " values ( " + (j/2+1) + ", " + "'" +  png_names[j] + "'" +  ");");
					}					
				}
			}
		}

		catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
