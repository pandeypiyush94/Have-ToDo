package com.piyush.havetodo;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Parcel;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.Date;
import java.util.UUID;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 Created by Piyush on 2/26/2018.
 */

public class NotifyService extends IntentService {

    private UUID toDoId;
    private Date todoDate;
    private String toDoTxt;

    public NotifyService() {
        super("ReminderService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            toDoTxt = intent.getStringExtra(AppConstants.TODO_TEXT);
            toDoId = (UUID) intent.getSerializableExtra(AppConstants.TODO_ID);
            todoDate = (Date) intent.getSerializableExtra(AppConstants.TODO_DATE);
        }
        new AppPreferences(this).setAlarmOff(false);
        Log.e("check_","in service");
        Intent sendIntent = new Intent(this, ViewAlarmActivity.class);
        showNotification(sendIntent);

        sendIntent.putExtra(AppConstants.TODO_ID, toDoId);
        sendIntent.putExtra(AppConstants.TODO_TEXT,toDoTxt);
        sendIntent.putExtra(AppConstants.TODO_DATE, todoDate);
        sendIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(sendIntent);
    }
    private void showNotification(Intent intent){
        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.alarm_txt))
                .setSound(alarmSound)
                .setContentText(toDoTxt)
                .setOngoing(true)
                .setSmallIcon(R.drawable.notify_alarm);
        if (manager != null)
            manager.notify(toDoId.hashCode(),notificationBuilder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("check_","service stopped");
    }
}
