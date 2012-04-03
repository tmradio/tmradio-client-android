package net.tmradio.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONProxy
{
	public static JSONObject getJSONfromURL(String url, String mode, List<NameValuePair> params)
	{
		InputStream is = null;
		String result = "";
		JSONObject jArray = null;

		if(mode == "get")
		{
			//HTTP get
			try
			{
				HttpClient httpclient = new DefaultHttpClient();
		        HttpGet httpget = new HttpGet(url);
		        HttpResponse response = httpclient.execute(httpget);
		        HttpEntity entity = response.getEntity();
		        is = entity.getContent();
		    }
			catch(Exception e)
		    {
		        Log.e("log_tag", "Error in http connection "+e.toString());
		    }
		}
		else if (mode == "post") 
		{
			//HTTP post
			try
			{
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

		        httppost.setEntity(new UrlEncodedFormEntity(params));
		        
		        HttpResponse res = httpclient.execute(httppost);
		        HttpEntity entity = res.getEntity();
		        is = entity.getContent();
		    }
			catch(Exception e)
		    {
		        Log.e("log_tag", "Error in http connection "+e.toString());
		    }
		}

		//convert response to string
	    try
	    {
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"),8);
	        StringBuilder sb = new StringBuilder();
	        String line = null;

	        while ((line = reader.readLine()) != null) 
	        {
	            sb.append(line + "\n");
	        }

	        is.close();

	        result=sb.toString();
	    }
	    catch(Exception e)
	    {
	        Log.e("log_tag", "Error converting result "+e.toString());
	    }

	    //try parse the string to a JSON object
	    try
	    {
            jArray = new JSONObject(result);
	    }
	    catch(JSONException e)
	    {
	        Log.e("log_tag", "Error parsing data "+e.toString());
	    }

	    return jArray;
	}
}
