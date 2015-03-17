package com.flashcard_prime;

/*
 * HomeScreenActivity.java - This is the first screen that you will see.  It's the home screen
 *   that allows you to do the quick on-screen tutorial of a flashcard, proceed to the selection
 *   of decks, and allow to set various properties.
 *
 * Further details are listed below for each appropriate section.
 *
 */

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.InputStream;
import java.io.IOException;

public class HomeScreenActivity extends Activity {
	String[] string_array_home_screen_flash_cards;

	RelativeLayout relative_layout_home_screen;

	View home_screen_activity_view;

	private ImageView front_image;
	private ImageView back_image;

	int int_front_image_id;
	int int_back_image_id;

	Intent intent_start_flash_card_main;

	boolean boolean_is_first_image = true;

	int i = 0;

	/*
	 * onCreate()
	 *   Here we:
	 *   	1.  Get a list of the pictures in assets/flashcard_flip/* and store them in a String[]
	 *   	2.  Get an mp3 to play on the home screen from assets/songs/*
	 *
	 */

	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.home_screen_activity);

		home_screen_activity_view = (View) findViewById (R.layout.home_screen_activity);

		// Ok so somehow we got to get the pictures loaded.  Well lets start with the AssetManager

		AssetManager asset_manager = getAssets();

		try {
			string_array_home_screen_flash_cards = asset_manager.list ("flashcard_flip");
		}

		catch (IOException e) {
			System.err.println ("error is asset_manager.list\n");
		}

		
		// Mp3 time:
		try {
			AssetFileDescriptor asset_file_descriptor_intro = getAssets().openFd ("songs/amin_thing_intro.mp3");
			MediaPlayer media_player = new MediaPlayer();
			media_player.setDataSource (asset_file_descriptor_intro.getFileDescriptor(), asset_file_descriptor_intro.getStartOffset(), asset_file_descriptor_intro.getLength());
			media_player.prepare();
			media_player.start();
		}

		catch (IOException e) {
			System.err.println ("something didn't work with the intro mp3 song\n" + e);
		}
	}

	/*
	 * onStart() 
	 *   Simply call the display_card() routine to well, display the flashcard on the home screen
	 *
	 */

	@Override
	public void onStart() {
		super.onStart();
		
		display_card();
	}

	/*
	 * applyRotation()
	 *   Call the backend code to actually flip the card.
	 *
	 */

	private void applyRotation (float start, float end) {
		// Find the center of the image
		final float centerX = front_image.getWidth() / 2.0f;
		final float centerY = front_image.getHeight() / 2.0f;

		// Create a new 3D rotation with the supplied parameter
		// The animation listener is used to trigger the next animation
	
			final Flip3dAnimation rotation = new Flip3dAnimation (start, end, centerX, centerY);

			rotation.setDuration (500);
			rotation.setFillAfter (true);
			rotation.setInterpolator (new AccelerateInterpolator());
			rotation.setAnimationListener (new DisplayNextView (boolean_is_first_image, front_image, back_image));

			if (boolean_is_first_image) {
				front_image.startAnimation (rotation);
			}

			else {
				back_image.startAnimation (rotation);
			}
	}

	/*
	 * display_card()
	 *   Here is where we display the card.  A few notes about this:
	 *
	 *   1.  We are getting the .png files from assets/flashcard_flip/*
	 *   
	 *   2.  We create our own RelativeLayout here that essentially sits on top of 
	 *       the existing layout.  This is done to avoid having to use a .xml file
	 *       as well as keeping it dynamic and programatic.  This code will 
	 *       almost start up a flame war on any forum.  However it stems from 
	 *       being able to make things dymanic (this is seen much more apparent
	 *       in the FlashCardDisplayActivity class).  I'll give further 
	 *       justification there for why this is all done programatically in that 
	 *       .java file.
	 */

	public void display_card() {
		InputStream input_stream_a;
		InputStream input_stream_b;

		Drawable drawable_a;
		Drawable drawable_b;

		// So I decided to go the RelativeLayout from scratch method since we already got a good example of that.
		relative_layout_home_screen = new RelativeLayout (this);
		
		RelativeLayout.LayoutParams relative_layout_params = new RelativeLayout.LayoutParams (
				RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.FILL_PARENT);
		
		// Not sure if this is the best place for this or if doesn't really matter:
		setContentView (relative_layout_home_screen, relative_layout_params);

		front_image = new ImageView (this);
		back_image = new ImageView (this);
	
		try {
			input_stream_a = getAssets().open ("flashcard_flip/" + string_array_home_screen_flash_cards[0]);			
			input_stream_b = getAssets().open ("flashcard_flip/" + string_array_home_screen_flash_cards[1]);			

			drawable_a = Drawable.createFromStream (input_stream_a, null);
			drawable_b = Drawable.createFromStream (input_stream_b, null);

			front_image.setImageDrawable (drawable_a);
			back_image.setImageDrawable (drawable_b);
		}

		catch (IOException e) {
			System.err.println ("if you see this, contact the local developer.\n");
		}

		
		RelativeLayout.LayoutParams flashcard_params = new RelativeLayout.LayoutParams (
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
			
		flashcard_params.addRule (RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		flashcard_params.addRule (RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

		front_image.setLayoutParams (flashcard_params);
		back_image.setLayoutParams (flashcard_params);

		back_image.setVisibility (View.GONE);

		relative_layout_home_screen.addView (front_image);
		relative_layout_home_screen.addView (back_image);
	
		final Button button_proceed_to_decks = new Button (this);

		RelativeLayout.LayoutParams relative_layout_button_params = new RelativeLayout.LayoutParams (
				RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		button_proceed_to_decks.setLayoutParams (relative_layout_button_params);
		button_proceed_to_decks.setText ("To Decks");
		button_proceed_to_decks.setTextSize (28);
		button_proceed_to_decks.setTextColor (0xff0000ff);

		relative_layout_home_screen.addView (button_proceed_to_decks);

		front_image.setOnClickListener (new View.OnClickListener() {
			public void onClick (View view) {
				if (boolean_is_first_image) {
       					applyRotation (0, 90);
       				}

        			else { 
                			applyRotation (0, -90);
        			}
				
				boolean_is_first_image = !boolean_is_first_image;
			}
		});		

		button_proceed_to_decks.setOnClickListener (new View.OnClickListener() {
			public void onClick (View view) {
				button_proceed_to_decks.setText ("Loading...");
				view.setTag (0);

				intent_start_flash_card_main = new Intent (HomeScreenActivity.this, FlashCardMainActivity.class);
				startActivity (intent_start_flash_card_main);
			}
		});
	}
}
