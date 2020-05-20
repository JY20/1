package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Button playBtn, switchBtn, nextBtn;
    SeekBar positionBar;
    SeekBar volumeBar;
    TextView elapsedTimeLabel, console, remainingTimeLabel;
    MediaPlayer [] mp;
    int totalTime, songNum, numSong, modePlay;
    Random random;
    int [] songs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songNum = 1;
        numSong = 6;
        random = new Random();
        playBtn = (Button) findViewById(R.id.playBtn);
        switchBtn = (Button) findViewById(R.id.button);
        nextBtn = (Button) findViewById(R.id.button2);
        elapsedTimeLabel = (TextView) findViewById(R.id.elapsedTimeLabel);
        remainingTimeLabel = (TextView) findViewById(R.id.remainingTimeLabel);
        console = (TextView) findViewById(R.id.textView);
        mp = new MediaPlayer [numSong+1];
        songs = new int [] {R.raw.music, R.raw.music2, R.raw.music3, R.raw.music4, R.raw.music5, R.raw.music6};
        modePlay = 1;
        // Media Player
        for(int i = 1; i < (songs.length+1); i++){
            mp[i] = MediaPlayer.create(this, songs[i-1]);
        }
        songSet();
        console.setText("2222222222222222222222");
    }

    public void songSet () {
        mp[songNum].setLooping(false);
        mp[songNum].seekTo(0);
        mp[songNum].setVolume(0.5f, 0.5f);
        totalTime = mp[songNum].getDuration();

        // Position Bar
        positionBar = (SeekBar) findViewById(R.id.positionBar);
        positionBar.setMax(totalTime);
        positionBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            mp[songNum].seekTo(progress);
                            positionBar.setProgress(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );

        // Volume Bar
        volumeBar = (SeekBar) findViewById(R.id.volumeBar);
        volumeBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        float volumeNum = progress / 100f;
                        mp[songNum].setVolume(volumeNum, volumeNum);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );

        // Thread (Update positionBar & timeLabel)
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mp[songNum] != null) {
                    try {
                        Message msg = new Message();
                        msg.what = mp[songNum].getCurrentPosition();
                        handler.sendMessage(msg);
                        Thread.sleep(1000);
                        if ((totalTime-msg.what) <= 3000) {
                            modeSong();
                        }
                    } catch (InterruptedException e) {}
                }
            }
        }).start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int currentPosition = msg.what;
            // Update positionBar.
            positionBar.setProgress(currentPosition);

            // Update Labels.
            String elapsedTime = createTimeLabel(currentPosition);
            elapsedTimeLabel.setText(elapsedTime);

            String remainingTime = createTimeLabel(totalTime-currentPosition);
            remainingTimeLabel.setText("- " + remainingTime);
            if ((totalTime-msg.what) <= 3000) {
                modeSong();
            }
        }
    };

    public String createTimeLabel(int time) {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }

    public void playBtnClick(View view) {
        if (!mp[songNum].isPlaying()) {
            // Stopping
            mp[songNum].start();
            playBtn.setBackgroundResource(R.drawable.stop);

        } else {
            // Playing
            mp[songNum].pause();
            playBtn.setBackgroundResource(R.drawable.play);
        }
    }

    public void switchMode (View view) {
        console.setText("" + switchBtn.getText());
        if (switchBtn.getText().equals("In Order")) {
            switchBtn.setText("Random");
            modePlay = 1;
        } else if (switchBtn.getText().equals("Random")){
            switchBtn.setText("In Order");
            modePlay = 2;
        }
    }

    public void nextSong (View view) {
        modeSong();
    }

    public void modeSong () {
        if (modePlay == 1) {
            random();
        } else if (modePlay == 2) {
            inOrder();
        }
    }

    public void random (){
        mp[songNum].pause();
        int lastOne = songNum;
        while (songNum == lastOne) {
            songNum = (int)(Math.random() * numSong - 0 + 1) - 0;
        }
        console.setText(""+songNum);
        songSet();
        mp[songNum].start();
        playBtn.setBackgroundResource(R.drawable.stop);
    }

    public void inOrder () {
        mp[songNum].pause();
        if (songNum < numSong) {
            songNum ++;
        } else if (songNum == numSong) {
            songNum = 1;
        }
        console.setText(""+songNum);
        songSet();
        mp[songNum].start();
        playBtn.setBackgroundResource(R.drawable.stop);
    }


    // dark mode or light mode
}
