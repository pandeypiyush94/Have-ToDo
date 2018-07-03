package com.piyush.havetodo;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import static com.piyush.havetodo.AppConstants.TODO_DATE;
import static com.piyush.havetodo.AppConstants.TODO_ID;
import static com.piyush.havetodo.AppConstants.TODO_TEXT;

/**
 Created by Piyush on 2/28/2018.
 */

public class AlarmReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            Intent i = new Intent(context, NotifyService.class);
            i.putExtra(TODO_TEXT, intent.getStringExtra(TODO_TEXT));
            i.putExtra(TODO_ID, intent.getSerializableExtra(TODO_ID));
            i.putExtra(TODO_DATE, intent.getSerializableExtra(TODO_DATE));
            startWakefulService(context, i);
        } else
            Log.e("check_","Intent is Null");
    }
}
