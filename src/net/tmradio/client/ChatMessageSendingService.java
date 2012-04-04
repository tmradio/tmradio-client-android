package net.tmradio.client;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.apache.http.NameValuePair;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class ChatMessageSendingService extends Service
{
	@Override
	public void onStart(Intent intent, int startId)
	{
		super.onStart(intent, startId);
		
		Bundle b = intent.getExtras();
		String url = b.getString(ChatActivity.INTENT_CMSS_URL);
		String nick = b.getString(ChatActivity.INTENT_CMSS_NICK);
		String text = b.getString(ChatActivity.INTENT_CMSS_TEXT);
		
		send_message(url, nick, text);
	}

	@Override
	public IBinder onBind(Intent arg0)
	{
		return null;
	}

	private void send_message(String url, String nick, String text)
	{
		try
		{
			nick = URLEncoder.encode(nick, "UTF-8");
			text = URLEncoder.encode(text, "UTF-8");
		} catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		url += "/ajax/send/?nick="+nick+"&text="+text;
        
		JSONProxy.getJSONfromURL(url, "get", new ArrayList<NameValuePair>(2));
	}
}
