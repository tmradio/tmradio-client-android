package net.tmradio.client;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChatActivity extends Activity
{
	private MainActivityReceiver activityReceiver;
	private IntentFilter chatUpdateFilter;
	
	private EditText messageEdit;
	private LinearLayout chatMessages;
	private ScrollView scrollView;
	
	ArrayList<String> messageIds = new ArrayList<String>();
	
	public final static String INTENT_CMSS_URL = "url";
	public final static String INTENT_CMSS_NICK = "nick";
	public final static String INTENT_CMSS_TEXT = "text";
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        
        // event listener creation and registration
        activityReceiver = new MainActivityReceiver();
        
        chatUpdateFilter = new IntentFilter(ChatUpdateService.ACTION_UPDATED);
        getApplicationContext().registerReceiver(activityReceiver, chatUpdateFilter);
        
        messageEdit = (EditText)findViewById(R.id.messageEdit);
        chatMessages = (LinearLayout)findViewById(R.id.linearLayoutMessages);
        scrollView = (ScrollView)findViewById(R.id.scrollView); 
/*        
        scrollView.post(new Runnable() 
        {
            public void run() 
            {
            	scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });        
*/        
        startService(new Intent(this, ChatUpdateService.class));
    }
    
	public void sendMessage(View v)
	{
		String text = messageEdit.getText().toString();
		
		if(text != null && text.length() > 0)
		{
			ConfigActivity config = new ConfigActivity();
			String nickname = config.preferences(getApplicationContext())[1]; 
			
			Intent i = new Intent(this, ChatMessageSendingService.class);
			i.putExtra(INTENT_CMSS_URL, getString(R.string.tmradio_chat_server_url));
			i.putExtra(INTENT_CMSS_NICK, nickname);
			i.putExtra(INTENT_CMSS_TEXT, text);
			
			startService(i);
		}
		
		messageEdit.setText("");
	}	
	
	class MainActivityReceiver extends BroadcastReceiver 
	{     
	    @Override
	    public void onReceive(Context context, Intent i) 
	    {
			if(i.getAction().equals(ChatUpdateService.ACTION_UPDATED))
			{
				Bundle b = i.getExtras();
				JSONArray data = null;
				
				try
				{
					JSONObject json = new JSONObject(b.getString(ChatUpdateService.INTENT_UPDATED_JSON));
					data = json.getJSONArray("data");
					
					LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					llp.setMargins(0, 5, 0, 0);

					for (int j = 0; j < data.length(); j++)
					{
					    JSONObject row = data.getJSONObject(j);
					    String nick = row.getString("nick");
					    String text = row.getString("text");
					    String id = row.getString("id");
					    
					    if(!messageIds.contains(id))
					    {
					    	TextView tv = new TextView(getApplicationContext());
					        tv.setText(nick+": "+text);
					        tv.setLayoutParams(llp);
					        
					        chatMessages.addView(tv);
					        
						    messageIds.add(id);
					    }
					}	
					
					scrollView.fullScroll(ScrollView.FOCUS_DOWN);
				} 
				catch (JSONException e)
				{
					e.printStackTrace();
				}
			}
	    }
	}
}
