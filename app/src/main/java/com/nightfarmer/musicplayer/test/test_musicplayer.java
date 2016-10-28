package com.nightfarmer.musicplayer.test;
  
import android.app.Activity;  
import android.os.Bundle;  
import android.view.View;  
import android.view.View.OnClickListener;  
import android.widget.Button;  
import android.widget.SeekBar;  
  
public class test_musicplayer extends Activity {  
    private Button btnPlayUrl;  
    private SeekBar skbProgress;  
    private Player player;  
  
    /** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
//        setContentView(R.layout.main);
    
        this.setTitle("MediaPlayer+Proxy --- hellogv编写");  
  
//        btnPlayUrl = (Button) this.findViewById(R.id.btnPlayUrl);
//        btnPlayUrl.setOnClickListener(new ClickEvent());
 
//        skbProgress = (SeekBar) this.findViewById(R.id.skbProgress);
        skbProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());  
        player = new Player(skbProgress);  
    }  
  
    public void onStop(){
    	
    	super.onStop();
    	System.exit(0);
    }
    class ClickEvent implements OnClickListener {  
  
        @Override  
        public void onClick(View arg0) {  
            if (arg0 == btnPlayUrl) {  
                //在百度MP3里随便搜索到的,大家可以试试别的链接  
                String url="http://conteadiwagner.com/audio/sf.mp3";
                player.playUrl(url);
            }
        }  
    }  
  
    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {  
        int progress;  
  
        @Override  
        public void onProgressChanged(SeekBar seekBar, int progress,  
                boolean fromUser) {  
            // 原本是(progress/seekBar.getMax())*player.mediaPlayer.getDuration()  
            this.progress = progress * player.mediaPlayer.getDuration()  
                    / seekBar.getMax();  
        }  
  
        @Override  
        public void onStartTrackingTouch(SeekBar seekBar) { }  
  
        @Override  
        public void onStopTrackingTouch(SeekBar seekBar) {  
            // seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字  
            player.mediaPlayer.seekTo(progress);  
        }  
    }  
}  