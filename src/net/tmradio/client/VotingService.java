package net.tmradio.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class VotingService extends Service
{
	public final static HashMap<String, String> vote_mapping = new HashMap<String, String>();
	static
 	{
		vote_mapping.put("rocks", "http://music.tmradio.net/api/track/rocks.json");
		vote_mapping.put("sucks", "http://music.tmradio.net/api/track/sucks.json");
 	}
	
	@Override
	public void onStart(Intent intent, int startId)
	{
		super.onStart(intent, startId);
		
		ConfigActivity config = new ConfigActivity();
		String auth_token = config.preferences(getApplicationContext())[0]; 

		Bundle b = intent.getExtras();
		String vote = b.getString("vote");
		String track_id = b.getString("track_id");
		
		if(vote_mapping.containsKey(vote) && auth_token != null && auth_token.length() > 0)
		{
			send_vote(track_id, vote, auth_token);
		}		
	}
	
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
	
	private void send_vote(String track, String vote, String auth_token)
	{
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("token", auth_token));
        nameValuePairs.add(new BasicNameValuePair("track_id", track));
        
		JSONProxy.getJSONfromURL(vote_mapping.get(vote), "post", nameValuePairs);
	}
}
