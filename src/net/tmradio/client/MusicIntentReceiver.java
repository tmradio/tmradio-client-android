package net.tmradio.client;

import android.content.Context;
import android.content.Intent;

public class MusicIntentReceiver extends android.content.BroadcastReceiver {
   public static final String INTENT_AUDIO_SHUT_UP = "android.media.AUDIO_SHUT_UP";

   @Override
   public void onReceive(Context ctx, Intent intent) {
      if (intent.getAction().equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) 
      {
    	  Intent i = new Intent(); 
    	  i.setAction(INTENT_AUDIO_SHUT_UP);
    	  
    	  ctx.startActivity(i);
      }
   }
}