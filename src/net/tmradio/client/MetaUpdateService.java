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
    public static final int FIVE_SECONDS = 5000;
    public final static String ACTION_UPDATED = "android.media.META_UPDATED";
    
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
    			
    			result.putExtra("id", json.getInt("id"));
    			result.putExtra("length", time_length);
    			result.putExtra("popularity", String.format("%.2f", (float)json.getDouble("weight")));
    			result.putExtra("artist", json.getString("artist"));
    			result.putExtra("song", json.getString("title"));
    		} 
    		catch (JSONException e) 
    		{
    			result.putExtra("id", "");
    			result.putExtra("played", "");
    			result.putExtra("popularity", "");
    			result.putExtra("artist", "");
    			result.putExtra("song", "");
    		}
     	  
      	    sendBroadcast(result);
      	    
            mHandler.postDelayed(periodicTask, FIVE_SECONDS);
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
        mHandler.postDelayed(periodicTask, FIVE_SECONDS);
    }

    @Override
    public void onDestroy() 
    {
        super.onDestroy();
        mHandler.removeCallbacks(periodicTask);
    }
}
