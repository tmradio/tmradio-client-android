package net.tmradio.client;

import java.io.IOException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;

public class PlayerService extends Service implements AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnErrorListener 
{
	private final IBinder mBinder = new MyBinder();
	private boolean isPlaying;
	private MediaPlayer mp;
	private WifiLock wifilock;
	private AudioManager audioManager;

	public void onCreate() 
	{
		super.onCreate();
		
		isPlaying = false;
		wifilock = ((WifiManager) getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "tmradio_lock");
		
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int resultOfAudioManagerFetching = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

		if (resultOfAudioManagerFetching != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) 
		{
		    throw new RuntimeException("Player service. Can't get Audio Focus.");
		}
	}	

	public void onDestroy() 
	{
		super.onDestroy();
	}	

	public void startPlayer()
	{
		initMediaPlayer();
		       		
		wifilock.acquire();
		
		try 
		{
            mp.setDataSource(getString(R.string.tmradio_stream_url));
            mp.prepareAsync();                    
        } catch (IOException e) 
        {
        }
		
		isPlaying = true;
	}

	public void stopPlayer()
	{
		mp.release();
	    mp = null;
		isPlaying = false;
		
		wifilock.release();
	}
	
	public boolean status()
	{
		return isPlaying;
	}
	
	@Override
	public IBinder onBind(Intent arg0) 
	{
		return mBinder;
	}

	public class MyBinder extends Binder 
	{
		PlayerService getService() 
		{
			return PlayerService.this;
		}
	}

	public void onAudioFocusChange(int focusChange) 
	{
	    switch (focusChange) 
	    {
	        case AudioManager.AUDIOFOCUS_GAIN:
	            // resume playback
	            if (mp == null) initMediaPlayer();
	            else if (!mp.isPlaying()) mp.start();
	            mp.setVolume(1.0f, 1.0f);
	            break;

	        case AudioManager.AUDIOFOCUS_LOSS:
	            // Lost focus for an unbounded amount of time: stop playback and release media player
	        	if(mp != null)
	        	{
		            if (mp.isPlaying()) mp.stop();
		            mp.release();
		            mp = null;
	        	}
	            break;

	        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
	            // Lost focus for a short time, but we have to stop
	            // playback. We don't release the media player because playback
	            // is likely to resume
	            if (mp.isPlaying()) mp.pause();
	            break;

	        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
	            // Lost focus for a short time, but it's ok to keep playing
	            // at an attenuated level
	            if (mp.isPlaying()) mp.setVolume(0.1f, 0.1f);
	            break;
	    }
	}
	
	public void initMediaPlayer() 
	{
		mp = new MediaPlayer();
		mp.setOnPreparedListener(new OnPreparedListener() 
		{
			public void onPrepared(MediaPlayer mp) 
			{
				mp.start();	
			}
		});
		mp.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mp.setOnErrorListener(this);
    }

	public boolean onError(MediaPlayer mp, int what, int extra) 
	{
        if (mp.isPlaying()) mp.stop();
        mp.release();
        mp = null;
		
        initMediaPlayer();
        
		return false;
	}	
}
