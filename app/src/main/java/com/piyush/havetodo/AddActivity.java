package com.piyush.havetodo;

import android.animation.Animator;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.piyush.havetodo.AppDB.Db;
import com.piyush.havetodo.AppDB.DbOperations;
import com.piyush.havetodo.AppDB.ToDo;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static com.piyush.havetodo.AppConstants.DARK_THEME;
import static com.piyush.havetodo.AppConstants.TODO_DATE;
import static com.piyush.havetodo.AppConstants.TODO_TEXT;
import static com.piyush.havetodo.AppConstants.TODO_ID;
import static com.piyush.havetodo.AppConstants.TO_DOITEM;
import static com.piyush.havetodo.AppConstants.TO_DOITEM1;

public class AddActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private ImageButton clockImg;
    private SwitchCompat switchView;
    private FloatingActionButton fab;
    private TextView tvViewTime, tvRemind;
    private EditText editToDo, editDate, editTime;
    private LinearLayout containerLayout, setDateLayout;

    private UUID toDoId;
    private int toDoColor;
    private String enteredToDoTxt,prevText;
    private boolean hasToDoReminder;
    private boolean isExisted;

    private InsertedReceived receiver;

    private Date todoRemindDate,prevDate;
    private ToDo toDoModel;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        Db.destroyInstance();
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

                    /*--------------Set Activity's Theme--------------*/
        AppPreferences appPref = new AppPreferences(this);
        final int currentTheme;
        if (appPref.getTheme().equals(DARK_THEME))
            currentTheme = R.style.CustomStyle_DarkTheme;
        else
            currentTheme = R.style.CustomStyle_LightTheme;
        this.setTheme(currentTheme);

                    /*--------------Set Activity's View--------------*/
        setContentView(R.layout.activity_add);

                    /*-------------Set Activity's Toolbar & Title-------------*/
        Toolbar toolbar = findViewById(R.id.toolbar_addtodo);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.addToDo);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.icon_close));
        }

                     /*-------------Initialize Views & Variables--------------*/
        initializeData();
        receiver = new InsertedReceived();
        registerReceiver(receiver,new IntentFilter("com.piyush.todo_inserted"));

                     /*-------------All Method Calls--------------*/
        if (getIntent().getSerializableExtra(TO_DOITEM1) != null) {
            toDoModel = (ToDo) getIntent().getSerializableExtra(TO_DOITEM1);
            isExisted = true;
        }
        else
            toDoModel = new ToDo(UUID.randomUUID(), ColorGenerator.MATERIAL.getRandomColor(), null, "",false, false);

        toDoId = toDoModel.toDoId;
        toDoColor = toDoModel.toDoColor;
        enteredToDoTxt = toDoModel.toDoTxt;
        if (toDoModel.toDoDate != new Date(1751826600000L))
            todoRemindDate = toDoModel.toDoDate;
        else
            todoRemindDate = null;
        hasToDoReminder = toDoModel.hasToDoReminder;
        prevDate = todoRemindDate;
        prevText = enteredToDoTxt;

        editToDo.requestFocus();
        editToDo.setText(enteredToDoTxt);

        if (hasToDoReminder && (todoRemindDate != null)) {
            setReminderText();
            changeTimeDateVisibility(true);
        }
        if (todoRemindDate == null)
            switchView.setChecked(false);

        setDateTimeText(false);
        setReminderText();
        setDateTimeVisibility(switchView.isChecked());
        switchView.setChecked(hasToDoReminder && (todoRemindDate != null));

        if (currentTheme == R.style.CustomStyle_DarkTheme){
            tvRemind.setTextColor(getResources().getColor(R.color.white_icons));
            clockImg.setImageDrawable(getDrawable(R.drawable.icon_add_remider_white));
        }

                    /*--------------Set View's Click Listeners--------------*/
        containerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(editToDo);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(editToDo);
                if (editToDo.getText().length() <= 0)
                    editToDo.setError(getResources().getString(R.string.todo_error));
                else if (todoRemindDate != null && todoRemindDate.before(new Date()))
                    makeResult();
                else {
                    makeResult();
                }
            }
        });
        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(editToDo);
                Date date;
                if(toDoModel.toDoDate!=null)
                    date = todoRemindDate;
                else
                    date = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = DatePickerDialog.newInstance(AddActivity.this,year,month,day);
                dialog.show(getFragmentManager(),"DateFragment");

                if (currentTheme == R.style.CustomStyle_DarkTheme)
                    dialog.setThemeDark(true);
                dialog.setMinDate(calendar);
            }
        });
        editTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date;
                hideKeyboard(editToDo);
                if(toDoModel.toDoDate!=null)
                    date = todoRemindDate;
                else
                    date = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int hour =  calendar.get(Calendar.HOUR);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog dialog = TimePickerDialog.newInstance(AddActivity.this,hour,minute, DateFormat.is24HourFormat(AddActivity.this));
                dialog.show(getFragmentManager(),"TimeFragment");

                if (currentTheme == R.style.CustomStyle_DarkTheme)
                    dialog.setThemeDark(true);
            }
        });
        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setDateTimeText(true);
                if (!isChecked)
                    todoRemindDate = null;
                hideKeyboard(editToDo);
                hasToDoReminder = isChecked;
                changeTimeDateVisibility(isChecked);
            }
        });
        editToDo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enteredToDoTxt = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            startActivity(new Intent(this,MainActivity.class));
            finish();
        return true;
    }

    private void makeResult() {
        if (todoRemindDate != null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(todoRemindDate);
            calendar.set(Calendar.SECOND,0);
            todoRemindDate = calendar.getTime();
        } else
            todoRemindDate = new Date(1751826600000L);
        if (enteredToDoTxt.length() > 0)
            toDoModel.toDoTxt = Character.toUpperCase(enteredToDoTxt.charAt(0))+enteredToDoTxt.substring(1);
        else
            toDoModel.toDoTxt = enteredToDoTxt;
        toDoModel.toDoId = toDoId;
        toDoModel.moveToArchive = false;
        toDoModel.toDoColor = toDoColor;
        toDoModel.toDoDate = todoRemindDate;
        toDoModel.hasToDoReminder = hasToDoReminder;
        if (hasToDoReminder) {
            if (!enteredToDoTxt.equals(prevText) && todoRemindDate != prevDate){
                cancelReminder(prevText, toDoId, prevDate);
                setReminder();
            } if (!enteredToDoTxt.equals(prevText)) {
                cancelReminder(prevText, toDoId, todoRemindDate);
                setReminder();
            } else if (todoRemindDate != prevDate) {
                cancelReminder(enteredToDoTxt, toDoId, prevDate);
                setReminder();
            }
        } else
            cancelReminder(enteredToDoTxt, toDoId, todoRemindDate);
        if (!isExisted)
            DbOperations.insertAsync(Db.getInstance(this), toDoModel,true,this);
        else
            DbOperations.updateAsync(Db.getInstance(this), toDoModel,this);
    }
    private void setReminder(){
        if(toDoModel != null){
            if (hasToDoReminder && todoRemindDate != null) {
                if (todoRemindDate.before(new Date())) {
                    todoRemindDate = null;
                    Toast.makeText(getApplicationContext(),"There is a Problem in Calendar. Try Again", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(this, AlarmReceiver.class);
                    i.putExtra(TODO_TEXT,enteredToDoTxt);
                    i.putExtra(TODO_ID, toDoId);
                    i.putExtra(TODO_DATE, todoRemindDate);
                    createReminder(i, toDoId.hashCode(), todoRemindDate.getTime());
                }
            }
        }
    }
    private void initializeData(){
        fab = findViewById(R.id.addToDoFab);
        clockImg = findViewById(R.id.clockImg);
        editToDo = findViewById(R.id.editToDo);
        editDate = findViewById(R.id.editDate);
        editTime = findViewById(R.id.editTime);
        tvRemind = findViewById(R.id.remindTxt);
        tvViewTime = findViewById(R.id.tvViewTime);
        switchView = findViewById(R.id.dateSwitch);
        setDateLayout = findViewById(R.id.setDateLayout);
        containerLayout = findViewById(R.id.containerLayout);

        editToDo.setFilters(new InputFilter[]{new EmojiExcluder()});
    }
    private void setReminderText(){
        if (todoRemindDate != null){
            tvViewTime.setVisibility(View.VISIBLE);
            if (todoRemindDate.before(new Date())) {
                tvViewTime.setTextColor(Color.RED);
                tvViewTime.setText(getResources().getString(R.string.date_error_check_again));
            }

            Date dte = todoRemindDate;
            String date = Adapter.formatDate("d, MMM yyyy",dte);
            String time, amPm="";
            if (DateFormat.is24HourFormat(AddActivity.this))
                time = Adapter.formatDate("k:mm",dte);
            else {
                amPm = Adapter.formatDate("a",dte);
                time = Adapter.formatDate("h:mm",dte);
            }

            String waqt = String.format(getResources().getString(R.string.remind_date_and_time),date,time,amPm);
            tvViewTime.setText(waqt);
            tvViewTime.setTextColor(getResources().getColor(R.color.secondary_text));
        } else
            tvViewTime.setVisibility(View.GONE);
    }
    private void setDateTimeText(boolean val){
        if (toDoModel.hasToDoReminder && todoRemindDate != null){
            String userDate = Adapter.formatDate("d MMM, yyyy", todoRemindDate);
            String formatToUse;
            if(DateFormat.is24HourFormat(this))
                formatToUse = "k:mm";
            else
                formatToUse = "h:mm a";
            String userTime = Adapter.formatDate(formatToUse, todoRemindDate);
            editTime.setText(userTime);
            editDate.setText(userDate);
        } else {
            editDate.setText(getResources().getString(R.string.date_reminder_default));
            Calendar calendar = Calendar.getInstance();
            String format;
            if (DateFormat.is24HourFormat(this)) {
                format = "k:mm";
                calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
            } else {
                format = "h:mm";
                calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 1);
            }
            calendar.set(Calendar.MINUTE, 0);
            if (val)
                todoRemindDate = calendar.getTime();
            editTime.setText(Adapter.formatDate(format, calendar.getTime()));
        }
    }
    private void hideKeyboard(EditText edit){
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(edit.getWindowToken(),0);
    }
    private void showKeyboard(EditText edit){
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        edit.setSelection(edit.length());
    }
    private void setDateTimeVisibility(boolean isChecked){
        if (isChecked)
            setDateLayout.setVisibility(View.VISIBLE);
        else
            setDateLayout.setVisibility(View.GONE);
    }
    private void changeTimeDateVisibility(boolean isChecked){
        if (isChecked){
            setDateLayout.animate().alpha(1.0f).setDuration(500).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    setDateLayout.setVisibility(View.VISIBLE);
                }
                @Override
                public void onAnimationEnd(Animator animation) {

                }
                @Override
                public void onAnimationCancel(Animator animation) {

                }
                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        } else {
            setDateLayout.animate().alpha(0.0f).setDuration(500).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }
                @Override
                public void onAnimationEnd(Animator animation) {
                    setDateLayout.setVisibility(View.GONE);
                }
                @Override
                public void onAnimationCancel(Animator animation) {

                }
                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }
    private void createReminder(Intent i, int reqCode, long time){
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        PendingIntent pi = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
//            pi = PendingIntent.getForegroundService(this,reqCode,i,PendingIntent.FLAG_UPDATE_CURRENT);
//        else
        Log.e("check_id",reqCode+"");
            pi = PendingIntent.getBroadcast(this,reqCode,i,PendingIntent.FLAG_UPDATE_CURRENT);
        if (manager != null) {
            manager.setExact(AlarmManager.RTC_WAKEUP, time, pi);
        }
    }
    private void cancelReminder(String toDoTxt,UUID toDoId, Date toDoDate){
        Intent i = new Intent(AddActivity.this, AlarmReceiver.class);
        i.putExtra(TODO_TEXT, toDoTxt);
        i.putExtra(TODO_ID, toDoId);
        i.putExtra(TODO_DATE, toDoDate);
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        Log.e("check_id1",toDoId.hashCode()+"");
        PendingIntent pi = PendingIntent.getBroadcast(this,toDoId.hashCode(),i,PendingIntent.FLAG_UPDATE_CURRENT);
        if (manager != null) {
            manager.cancel(pi);
            Log.e("check_boo","cancek");
        }
    }
    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        if (todoRemindDate != null)
            calendar.setTime(todoRemindDate);

        int year, month, day;
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(year,month,day,hourOfDay,minute,0);
        todoRemindDate = calendar.getTime();
        String format;
        if (DateFormat.is24HourFormat(AddActivity.this))
            format = "k:mm";
        else
            format = "h:mm a";
        editTime.setText(Adapter.formatDate(format,todoRemindDate));
        setReminderText();
    }
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        int hour, minute;

        if(todoRemindDate!=null)
            calendar.setTime(todoRemindDate);

        if (DateFormat.is24HourFormat(this))
            hour = calendar.get(Calendar.HOUR_OF_DAY);
        else
            hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);
        calendar.set(year,monthOfYear,dayOfMonth,hour,minute);
        todoRemindDate = calendar.getTime();
        String format = "d, MMM, yyyy";
        editDate.setText(Adapter.formatDate(format,todoRemindDate));
        setReminderText();
    }

    private class InsertedReceived extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("check_ins","inserted");
            startActivity(new Intent(context,MainActivity.class));
            finish();
        }
    }
}