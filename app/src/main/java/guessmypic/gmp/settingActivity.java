package guessmypic.gmp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Window;
import android.widget.SeekBar;

/**
 * Created by HONGWEI on 2018/7/7.
 */
public class settingActivity extends Activity {
    private SeekBar volumeSeekbar,brightSeekbar;
    private AudioManager audioManager;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(checkSystemWritePermission()) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            setContentView(R.layout.activity_setting);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            try {
                volumeSeekbar = (SeekBar) findViewById(R.id.seekBar);
                brightSeekbar = (SeekBar) findViewById(R.id.seekBar2);
                context = getApplicationContext();
                audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                volumeSeekbar.setMax(audioManager
                        .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
                volumeSeekbar.setProgress(audioManager
                        .getStreamVolume(AudioManager.STREAM_MUSIC));
                volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onStopTrackingTouch(SeekBar arg0) {
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar arg0) {
                    }

                    @Override
                    public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                    }
                });
                brightSeekbar.setMax(255);
                brightSeekbar.setProgress(Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0));
                brightSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
            } catch (Exception e) { e.printStackTrace(); }
        }else finish();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (checkSystemWritePermission()) {
            volumeSeekbar.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));
            brightSeekbar.setProgress(Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0));
        }
    }
    private boolean checkSystemWritePermission() {
        boolean retVal = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            retVal = Settings.System.canWrite(this);
            if(!retVal) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                startActivity(intent);
            }
        }
        return retVal;
    }
}

