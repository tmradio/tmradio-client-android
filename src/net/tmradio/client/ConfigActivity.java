package net.tmradio.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class ConfigActivity extends PreferenceActivity
{
    @Override
	protected void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	    addPreferencesFromResource(R.xml.preferences);
	}
    
    public String[] preferences(Context context)
    {
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    	
    	String tmradio_token = preferences.getString("auth_token", null);
	    
	    return new String[] {tmradio_token};
    }
}
