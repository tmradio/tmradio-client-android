package net.tmradio.client;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

public class ChatUpdateService extends Service
{
    private Handler mHandler = new Handler();
    public static final int UPDATE_PERIOD = 7000;
	public final static String ACTION_UPDATED = "android.media.CHAT_UPDATED";

    private Runnable periodicTask = new Runnable() 
    {
        public void run() 
        {
        	String url = ChatActivity.TMRADIO_CHAT_SERVER_ADDRESS + "/ajax/getChatMessages/1?format=json"; 
    		JSONObject json = JSONProxy.getJSONfromURL(url, "get", new ArrayList<NameValuePair>(2));
    		
    		Intent result = new Intent();
    		result.setAction(ACTION_UPDATED);
   			result.putExtra("json", json.toString());
     	  
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
