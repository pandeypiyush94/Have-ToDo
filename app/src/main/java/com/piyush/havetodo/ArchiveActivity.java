package com.piyush.havetodo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.piyush.havetodo.AppDB.Db;
import com.piyush.havetodo.AppDB.DbOperations;
import com.piyush.havetodo.AppDB.ToDo;

import java.util.ArrayList;
import java.util.List;

import static com.piyush.havetodo.AppConstants.DARK_THEME;
import static com.piyush.havetodo.AppConstants.LIGHT_THEME;
import static com.piyush.havetodo.AppConstants.TO_DOITEM;
import static com.piyush.havetodo.AppConstants.TO_DOITEM1;

public class ArchiveActivity extends AppCompatActivity {

    private Adapter adapter;
    private List<ToDo> list;
    private AppPreferences appPref;
    private RecyclerViewWithEmptyViewSupport recyclerView;

    private PastListReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

          /*--------------Set Activity's Theme--------------*/
        appPref = new AppPreferences(this);
        int currentTheme;
        if (appPref.getTheme().equals(DARK_THEME))
            currentTheme = R.style.CustomStyle_DarkTheme;
        else
            currentTheme = R.style.CustomStyle_LightTheme;
        this.setTheme(currentTheme);

         /*--------------Set Activity's View--------------*/
        setContentView(R.layout.activity_archive);

         /*-------------Set Activity's Toolbar-------------*/
        Toolbar toolbar = findViewById(R.id.toolbar_archive);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(R.string.archive);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

          /*-------------Initialize Views & Variables--------------*/
        initializeData();
        receiver = new PastListReceiver();
        registerReceiver(receiver,new IntentFilter("com.haveToDo.past_list"));
    }

    private void initializeData()  {
        list = new ArrayList<>();

        LinearLayout emptyLayout = findViewById(R.id.toDoEmptyView_archive);
        recyclerView = findViewById(R.id.toDoRecyclerView_archive);
        recyclerView.setHasFixedSize(true);
        recyclerView.setEmptyView(emptyLayout);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (appPref.getTheme().equals(LIGHT_THEME))
            recyclerView.setBackgroundColor(getResources().getColor(R.color.primary_lightest));
    }
    private void updateUi(){
        DbOperations.fetchAllPastAsync(Db.getInstance(this),this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.e("check_","OnResume()"+" : "+list.size());
        if (appPref.isActivityRecreated()) {
            appPref.recreateActivity(false);
        }
        updateUi();
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        return true;
    }

    private class PastListReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            list = intent.getParcelableArrayListExtra(TO_DOITEM);
            adapter = new Adapter(ArchiveActivity.this,list,findViewById(R.id.container_archive), false,"Archive");
            recyclerView.setAdapter(adapter);
        }
    }
}
