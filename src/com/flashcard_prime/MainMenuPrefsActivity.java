package com.flashcard_prime;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class MainMenuPrefsActivity extends PreferenceActivity {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		addPreferencesFromResource (R.xml.main_preferences);
	}
}
