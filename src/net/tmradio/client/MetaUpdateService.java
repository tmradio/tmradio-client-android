package net.tmradio.client;

import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

public class MetaUpdateService extends Service {
    private Handler mHandler = new Handler();
    public static final int UPDATE_PERIOD = 5000;
    public final static String ACTION_UPDATED = "android.media.META_UPDATED";
    
    public static final String INTENT_UPDATED_ID = "id";
    public static final String INTENT_UPDATED_LENGTH = "length";
    public static final String INTENT_UPDATED_POPULARITY = "popularity";
    public static final String INTENT_UPDATED_ARTIST = "artist";
    public static final String INTENT_UPDATED_SONG = "song";
    
    private Runnable periodicTask = new Runnable() 
    {
        public void run() 
        {
    		JSONObject json = JSONProxy.getJSONfromURL(getString(R.string.tmradio_meta_url), "get", new ArrayList<NameValuePair>(2));
    		
    		Intent result = new Intent();
    		result.setAction(ACTION_UPDATED);
    		try 
    		{
    			int length = json.getInt("length");
    			String time_length = String.valueOf(length/60) + ":" + String.valueOf(length%60); 
    			
    			result.putExtra(INTENT_UPDATED_ID, json.getInt("id"));
    			result.putExtra(INTENT_UPDATED_LENGTH, time_length);
    			result.putExtra(INTENT_UPDATED_POPULARITY, String.format("%.2f", (float)json.getDouble("weight")));
    			result.putExtra(INTENT_UPDATED_ARTIST, json.getString("artist"));
    			result.putExtra(INTENT_UPDATED_SONG, json.getString("title"));
    		} 
    		catch (JSONException e) 
    		{
    			result.putExtra(INTENT_UPDATED_ID, "");
    			result.putExtra(INTENT_UPDATED_LENGTH, "");
    			result.putExtra(INTENT_UPDATED_POPULARITY, "");
    			result.putExtra(INTENT_UPDATED_ARTIST, "");
    			result.putExtra(INTENT_UPDATED_SONG, "");
    		}
     	  
      	    sendBroadcast(result);
      	    
            mHandler.postDelayed(periodicTask, UPDATE_PERIOD);
        }
    };
 
   @Override
    public IBinder onBind(Intent intent) 
    {
        return null;
    }

    @Override
    public void onCreate() 
    {
        mHandler.postDelayed(periodicTask, UPDATE_PERIOD);
    }

    @Override
    public void onDestroy() 
    {
        super.onDestroy();
        mHandler.removeCallbacks(periodicTask);
    }
}
