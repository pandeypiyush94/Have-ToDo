package com.piyush.havetodo;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.piyush.havetodo.AppDB.Db;
import com.piyush.havetodo.AppDB.DbOperations;
import com.skyfishjy.library.RippleBackground;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static com.piyush.havetodo.AppConstants.DATE_TIME_FORMAT_12_HOUR;
import static com.piyush.havetodo.AppConstants.DATE_TIME_FORMAT_24_HOUR;
import static com.piyush.havetodo.AppConstants.TODO_DATE;
import static com.piyush.havetodo.AppConstants.TODO_ID;
import static com.piyush.havetodo.AppConstants.TODO_TEXT;

public class ViewAlarmActivity extends AppCompatActivity {

    private TextView txtTv, dateTv;
    private RippleBackground rippleBackground;
    private FloatingActionButton offAlarmFab, snoozeAlarmFab;

    private Vibrator vibrator;
    private MediaPlayer player;
    private AppPreferences pref;

    private UUID toDoId;
    private Date toDoDate;
    private String toDoTxt;

    private FinishReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_alarm);

        Log.e("check_","oncreate");

        final AppPreferences pref = new AppPreferences(this);
        receiver = new FinishReceiver();
        registerReceiver(receiver, new IntentFilter("com.piyush.finishReminder"));
        Intent intent = getIntent();
        if (intent != null){
            toDoTxt = intent.getStringExtra(TODO_TEXT);
            toDoId = (UUID)intent.getSerializableExtra(TODO_ID);
            toDoDate = (Date) intent.getSerializableExtra(TODO_DATE);
        }
        initializeData();

        rippleBackground.startRippleAnimation();
        startVibrationAndSound();

        snoozeAlarmFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pref.setAlarmOff(true);
                toDoDate = snoozeTime();
                cancelReminder();
                hideNotification();
                stopVibrationAndSound();
                setReminder();
                DbOperations.updateDateAsync(Db.getInstance(ViewAlarmActivity.this),ViewAlarmActivity.this, toDoId, toDoDate);
            }
        });
        offAlarmFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pref.setAlarmOff(true);
                cancelReminder();
                hideNotification();
                stopVibrationAndSound();
                finish();
            }
        });
    }


    private Uri getSoundUri(){
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null)
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (alert == null)
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        return alert;
    }
    private Date snoozeTime(){
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, 1);
        calendar.set(Calendar.SECOND,0);
        Toast.makeText(getApplicationContext(),"Snoozed For 1 Minute", Toast.LENGTH_SHORT).show();
        return calendar.getTime();
    }
    private void setReminder() {
        Intent i = new Intent(this, NotifyService.class);
        i.putExtra(TODO_TEXT, toDoTxt);
        i.putExtra(TODO_ID, toDoId);
        i.putExtra(TODO_DATE, toDoDate);
        createReminder(i, toDoId.hashCode(), toDoDate.getTime());
    }
    private void initializeData(){
        txtTv = findViewById(R.id.todoTxt);
        dateTv = findViewById(R.id.todoDate);
        offAlarmFab = findViewById(R.id.alarmOffFab);
        snoozeAlarmFab = findViewById(R.id.alarmSnoozeFab);
        rippleBackground = findViewById(R.id.rippleBg);

        player = new MediaPlayer();
        pref = new AppPreferences(this);
        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);

        String time;
        if (DateFormat.is24HourFormat(this))
            time = Adapter.formatDate(DATE_TIME_FORMAT_24_HOUR, toDoDate);
        else
            time = Adapter.formatDate(DATE_TIME_FORMAT_12_HOUR, toDoDate);
        dateTv.setText(time);
        txtTv.setText(toDoTxt);
    }
    private void cancelReminder(){
        Intent i = new Intent(ViewAlarmActivity.this, NotifyService.class);
        i.putExtra(TODO_TEXT, toDoTxt);
        i.putExtra(TODO_ID, toDoId);
        i.putExtra(TODO_DATE, toDoDate);
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getService(ViewAlarmActivity.this,toDoDate.hashCode(),i,PendingIntent.FLAG_UPDATE_CURRENT);
        if (manager != null)
            manager.cancel(pi);
    }
    private void hideNotification(){
        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if (manager != null)
            manager.cancel(toDoId.hashCode());
    }
    private void stopVibrationAndSound(){
        player.stop();
        if (pref.isVibrationEnabled())
            vibrator.cancel();
    }
    private void startVibrationAndSound(){
        try{
            player.setDataSource(this,getSoundUri());
            AudioManager manager = (AudioManager)getSystemService(AUDIO_SERVICE);
            if (manager != null && manager.getStreamVolume(AudioManager.STREAM_ALARM) != 0){
                player.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build());
                player.prepare();
                player.setLooping(true);
                player.start();
                if (pref.isVibrationEnabled())
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
                        vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0,1000,2000},0));
                    else
                        vibrator.vibrate(new long[]{0,1000,2000},0);
            }
        } catch(Exception e){e.printStackTrace();}
    }
    private void createReminder(Intent i, int reqCode, long time){
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getService(this,reqCode,i,PendingIntent.FLAG_UPDATE_CURRENT);
        if (manager != null) {
            manager.setExact(AlarmManager.RTC_WAKEUP, time, pi);
            Log.e("check_","alarm set "+": "+" : "+new Date(time)+" : "+new Date(manager.getNextAlarmClock().getTriggerTime()));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("check_","onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("check_","onResume");
        if (pref.isAlarmOff()){
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("check_","onDestroy");
        if (receiver != null)
            unregisterReceiver(receiver);
        hideNotification();
        cancelReminder();
        stopVibrationAndSound();
    }

    @Override
    public void onBackPressed() {
    }

    private class FinishReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("check_view","in receiver");
            ViewAlarmActivity.this.finish();
        }
    }
}
