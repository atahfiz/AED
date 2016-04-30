package com.example.tahfiz.aed.Settings;

import android.content.SharedPreferences;

public class Settings {

	private static final String ENABLE_BACKGROUND_KEY = "enable_background";
	private static final String USERNAME_KEY = "username";
	private static final String CATEGORY_AGE_KEY = "category_age";
	private static final String SWITCH_KEY = "switch";

	private boolean enableBackground;
	private String username;
	private String categoryAge;
	private boolean switchValue;

	public boolean isEnableBackground() {
		return enableBackground;
	}

	public void setEnableBackground(boolean enableBackground) {
		this.enableBackground = enableBackground;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCategoryAge() {
		return categoryAge;
	}

	public void setCategoryAge(String categoryAge) {
		this.categoryAge = categoryAge;
	}

	public boolean isSwitchValue() {
		return switchValue;
	}

	public void setSwitchValue(boolean switchValue) {
		this.switchValue = switchValue;
	}

	public void load(SharedPreferences prefs) {
		enableBackground = prefs.getBoolean(ENABLE_BACKGROUND_KEY, false);
		username = prefs.getString(USERNAME_KEY, "John Doe");
		categoryAge = prefs.getString(CATEGORY_AGE_KEY, null);
		switchValue = prefs.getBoolean(SWITCH_KEY, false);
	}

	public void save(SharedPreferences prefs) {
		SharedPreferences.Editor editor = prefs.edit();
		save(editor);
		editor.commit();
	}

	public void saveDeferred(SharedPreferences prefs) {
		SharedPreferences.Editor editor = prefs.edit();
		save(editor);
		editor.apply();
	}

	public void save(SharedPreferences.Editor editor) {
		editor.putBoolean(ENABLE_BACKGROUND_KEY, enableBackground);
		editor.putString(USERNAME_KEY, username);
		editor.putString(CATEGORY_AGE_KEY, categoryAge);
		editor.putBoolean(SWITCH_KEY, switchValue);
	}
}
