package com.flashcard_prime;

/*
 * FlashCardDisplayActivity - Class to display the framework for flashcards 
 *
 *
*/

import android.app.Activity;

import android.content.res.AssetManager;

import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

import android.graphics.drawable.Drawable;

import android.os.Bundle;

import android.view.animation.AccelerateInterpolator;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Random;

public class FlashCardDisplayActivity extends Activity {
	View flashcard_screen_view;

	DBHelper dbHelper;
	
	SQLiteDatabase db_get_stuff;
	Cursor cursor_db_get_stuff;

	SQLiteDatabase db_select_flashcards;
	Cursor cursor_select_count_flashcards;
	Cursor cursor_select_flashcards;

	private ImageView front_image;
	private ImageView back_image;

	private boolean is_first_image = true;

	String flashcard_name_base;

	boolean deck_not_empty = true;

	int i = 0;

	RelativeLayout flashcard_display_relative_layout;

	boolean bool_at_first_record = true;
	boolean bool_cursor_status = false;

	boolean boolean_at_beginning_of_deck = true;
	boolean boolean_acceptable_random_number = false;

	String background_color_string_from_db;

	String string_isactive;
	String string_deck_name;

	String string_flashcard_name_base;

	int int_drawable_a;
	int int_drawable_b;

	String string_select_count_flashcards;

	int int_select_count_flashcards;

	int int_random_number_card_id;
	int int_min_random_number_card_id = 1;
	int int_max_random_number_card_id;

	ArrayList<Integer> array_list_black_list_card_ids = new ArrayList<Integer>();

	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.flashcard_display_activity);

		flashcard_screen_view = (View) findViewById (R.layout.flashcard_display_activity);

		dbHelper = new DBHelper(this);
	}


	@Override
	public void onStart() {
		super.onStart();
		
		// setting the background color used to be here.  But it doesn't work anymore, so we'll skip it for
		// the time being.
		
		// Now we call the routine to start the whole display thing
		start_display_of_deck();
	}


	private void applyRotation (float start, float end) {
		// Find the center of image
		final float centerX = front_image.getWidth() / 2.0f;
		final float centerY = front_image.getHeight() / 2.0f;

		// Create a new 3D rotation with the supplied parameter
		// The animation listenter is used to trigger the next animation

		final Flip3dAnimation rotation = new Flip3dAnimation (start, end, centerX, centerY);

		rotation.setDuration (500);
		rotation.setFillAfter (true);
		rotation.setInterpolator (new AccelerateInterpolator());
		rotation.setAnimationListener (new DisplayNextView (is_first_image, front_image, back_image));

		if (is_first_image) {
			front_image.startAnimation (rotation);
		}

		else {
			back_image.startAnimation (rotation);
		}	
	}
	

	public void start_display_of_deck() {
		db_get_stuff = dbHelper.getWritableDatabase();

		// Just doing a temp select to see what we got...
		cursor_db_get_stuff = db_get_stuff.rawQuery ("select * from list_of_decks where isactive='Y';", null);
		cursor_db_get_stuff.moveToFirst();
		
		string_deck_name = cursor_db_get_stuff.getString(1);
		string_isactive = cursor_db_get_stuff.getString(4);

		design_and_display_flashcard (string_deck_name);		
	}


	public void design_and_display_flashcard (String passed_in_deck_name) {
		InputStream input_stream_a;
		InputStream input_stream_b;
		Drawable drawable_a;
		Drawable drawable_b;

		db_select_flashcards = dbHelper.getWritableDatabase();

		// I have in my notes that we want to set this to true here to prevent goofiness in the rotation of the images on a new card.
		// Seems fair enough to me.
		is_first_image = true;		

		// So first we need to setup a RelativeLayout.  However how does that affect the existing layout, whatever that may be,
		// on called this Activity?  aka, I think we are laying this RelativeLayout ontop of the base layer, and that's causing
		// some confusion.  Anyway keep this in mind.

		flashcard_display_relative_layout = new RelativeLayout (this);

		RelativeLayout.LayoutParams flashcard_relative_layout_parms = new RelativeLayout.LayoutParams (
				RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.FILL_PARENT);

		// Now we'll create a button to go to the next card.  Ideally want to swipe here to get to the next card
		Button next_flashcard_button = new Button (this);
		RelativeLayout.LayoutParams next_flashcard_button_parms = new RelativeLayout.LayoutParams (
				RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		next_flashcard_button.setLayoutParams (next_flashcard_button_parms);
		next_flashcard_button.setText ("Next Flashcard");
		next_flashcard_button.setTextSize (28);
		next_flashcard_button.setTextColor (0xff0000ff);

		flashcard_display_relative_layout.addView (next_flashcard_button);

		setContentView (flashcard_display_relative_layout, flashcard_relative_layout_parms);

		// Here we determine if we are at the first record
		// actually we need do a select and gather all of our card names now. 
		//   Curious, do we have this setup on the back end yet?  If not we may need to do so.

		if (bool_at_first_record == true) {
			// selecting all data here, but we are really only concerned with the flashcard_name_base here.
			cursor_select_flashcards = db_select_flashcards.rawQuery ("select * from flashcards_" + passed_in_deck_name + ";", null); 
			
			// Originall was doing moveToPosition(0), but it sure seems like
			// moveToFirst() will do the same thing.  We'll see soon for sure.
			bool_cursor_status = cursor_select_flashcards.moveToFirst();
			bool_at_first_record = false;	
		}			

		else {
			bool_cursor_status = cursor_select_flashcards.moveToNext();
		}

		// Now we need to figure out if we are at the end of the records or not.
		// If bool_cursor_status is true, we got a cursor.  Thus we grab the name of the 
		// flashcard_base.
		// 
		// If boold_cursor_status is false, we are at the end an we want to back to the beginning of the deck.
		//
		// This may be close to the spot, where if we want to randomize which card is selected, we at least
		// partitally account for that here.

///*---------------------------------------------------------------------------------------------------------------------------------------------------

		if (bool_cursor_status == true) {
			string_flashcard_name_base = cursor_select_flashcards.getString(1);
		}

		else {
			cursor_select_flashcards = db_select_flashcards.rawQuery ("select * from flashcards_" + passed_in_deck_name + ";", null); 
			cursor_select_flashcards.moveToFirst();
			string_flashcard_name_base = cursor_select_flashcards.getString(1);
		}

//----------------------------------------------------------------------------------------------------------------------------------------------------*/

/*		
		// First lets get a count of the records that are stored in the table requested:
		cursor_select_count_flashcards = db_select_flashcards.rawQuery ("select COUNT (*) from flashcards_" + passed_in_deck_name + ";", null);
		cursor_select_count_flashcards.moveToFirst();
		string_select_count_flashcards = cursor_select_count_flashcards.getString(0);

		int_select_count_flashcards = Integer.valueOf (string_select_count_flashcards);

		Toast.makeText (this, string_select_count_flashcards, Toast.LENGTH_LONG).show();

		// Now that we have the count, lets do this?:
		// 1.  Place each number from 1 to int_select_count_flashcards into some type of array
		// 2.  Generate a random number between 1 and int_select_count_flashcards
		// 3.  select the card at the random position chosen in step 2
		// 4.  remove the random number chosen from the array
		// 5.  Once there are no more values left, we are at the end of deck.  "re-shuffle and start all over again"

		//array_list_black_list_card_ids.add (i);
				
		// 2.  Generate a random number between 1 and select_count_flashcards
		Random random_object = new Random();
		
		int_random_number_card_id = (random_object.nextInt (int_max_random_number_card_id) + 1);

		while (array_list_black_list_card_ids.contains (int_random_number_card_id) {
			int_random_number_card_id = (random_object.nextInt (int_max_random_number_card_id) + 1);
		
		}

		array_list_black_list_card_ids.add (int_random_number_card_id);


		// 3.  select the card at the random position chosen in step 2
*/			

		// ok so now we are getting into the flashcards themseleves.

		// looks like we need to do some xml haxory here yet?  Queue this comment for removal.
		
		// Ok, so here comes the tricky part.  How can we get images in our assets folder as a drawble?  That is the question...
		front_image = new ImageView (this);
		back_image = new ImageView (this);

		//AssetManager asset_manager = getAssets();
		try {
			input_stream_a = getAssets().open ("decks/" + passed_in_deck_name + "/" + string_flashcard_name_base + "_a.png");
			input_stream_b = getAssets().open ("decks/" + passed_in_deck_name + "/" + string_flashcard_name_base + "_b.png");

			drawable_a = Drawable.createFromStream (input_stream_a, null);
			drawable_b = Drawable.createFromStream (input_stream_b, null);

			front_image.setImageDrawable (drawable_a);
			back_image.setImageDrawable (drawable_b);
		}

		catch (IOException e) {
			System.err.println ("oops debug me better\n");
			e.printStackTrace (System.err);
		}

		RelativeLayout.LayoutParams flashcard_image_parms = new RelativeLayout.LayoutParams (
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		flashcard_image_parms.addRule (RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		flashcard_image_parms.addRule (RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

		front_image.setLayoutParams (flashcard_image_parms);
		back_image.setLayoutParams (flashcard_image_parms);

		back_image.setVisibility (View.GONE);

		flashcard_display_relative_layout.addView (front_image);
		flashcard_display_relative_layout.addView (back_image);

		// I think we can control the way the rotation works here.  Somethign to keep in mind.
		// Either different rotation angles, or perhaps some swiping code might go here.
		front_image.setOnClickListener (new View.OnClickListener() {
			public void onClick (View view) {
				if (is_first_image) {
					applyRotation (0, 90);
				}

				else {
					applyRotation (0, -90);
				}

				is_first_image = !is_first_image;
			}
		});

		next_flashcard_button.setOnClickListener (new View.OnClickListener() {
			public void onClick (View view) {
				setContentView (R.layout.flashcard_display_activity);

				design_and_display_flashcard(string_deck_name);
			}
		});		
	}
}
