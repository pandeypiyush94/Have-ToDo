package com.piyush.havetodo;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.support.v7.widget.Toolbar;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.piyush.havetodo.AppDB.Db;
import com.piyush.havetodo.AppDB.DbOperations;
import com.piyush.havetodo.AppDB.ToDo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.piyush.havetodo.AppConstants.DARK_THEME;
import static com.piyush.havetodo.AppConstants.LIGHT_THEME;
import static com.piyush.havetodo.AppConstants.TO_DOITEM;
import static com.piyush.havetodo.AppConstants.TO_DOITEM1;

/**
 Created by Piyush on 2/20/2018.
 */

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton fab;
    private RecyclerViewWithEmptyViewSupport recyclerView;

    private Adapter adapter;
    private AppPreferences appPref;
    private List<ToDo> list;
    private ItemTouchHelper itemTouchHelper;

    private DatReceiver receiver;
    private ListReceiver listReceiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                    /*--------------Set Activity's Theme--------------*/
        appPref = new AppPreferences(MainActivity.this);
        int currentTheme;
        if (appPref.getTheme().equals(DARK_THEME))
            currentTheme = R.style.CustomStyle_DarkTheme;
        else
            currentTheme = R.style.CustomStyle_LightTheme;
        this.setTheme(currentTheme);

                    /*--------------Set Activity's View--------------*/
        setContentView(R.layout.activity_main);

                    /*-------------Initialize Views & Variables--------------*/
        initializeData();

                    /*-------------Set Activity's Toolbar & Title-------------*/
        toolbar.inflateMenu(R.menu.main_menu);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.app_name);


        receiver = new DatReceiver();
        listReceiver = new ListReceiver();
        registerReceiver(listReceiver, new IntentFilter("com.haveToDo.list"));
        registerReceiver(receiver,new IntentFilter("com.haveToDo.data"));

        TapTargetSequence targetSequence = new TapTargetSequence(this)
                .targets(TapTarget.forView(fab,"Add To Do","Tap Here to Add To Do or Set It").cancelable(false).transparentTarget(true),
                        TapTarget.forToolbarMenuItem(toolbar,R.id.archive,"View Archived To Do","Tap Here to View Archived To Do").cancelable(false).dimColor(R.color.primary).targetRadius(30).transparentTarget(true),
                        TapTarget.forToolbarMenuItem(toolbar,R.id.settings,"View Settings","Tap Here To Go to App Settings").cancelable(false).dimColor(R.color.primary).targetRadius(30).transparentTarget(true),
                        TapTarget.forToolbarOverflow(toolbar,"More Options Here","Tap Here To Go to About Page").cancelable(false).dimColor(R.color.primary).targetRadius(30).transparentTarget(true))
                .listener(new TapTargetSequence.Listener() {
                    @Override
                    public void onSequenceFinish() {
                        appPref.setWalkThrough(true);
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {

                    }
                });

        if (!appPref.isWalkThroughed()) {
            targetSequence.start();
        }

                    /*-------------Set View's Click Listeners--------------*/
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,AddActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("check_","OnStart()"+" : "+list.size());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("check_","OnPause()"+" : "+list.size());
        for(int i=0;i<list.size();i++)
            DbOperations.deleteSingleSync(Db.getInstance(this),this,list.get(i).toDoId);

        for (int i=0;i<list.size();i++)
            DbOperations.insertAsync(Db.getInstance(this),list.get(i),false,this);
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
    protected void onRestart() {
        super.onRestart();
        Log.e("check_","OnReStart()"+" : "+list.size());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        unregisterReceiver(listReceiver);
        Log.e("check_","OnDestroy()"+" : "+list.size());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                startActivity(new Intent(this,SettingsActivity.class));
                finish();
                break;
            case R.id.archive:
                startActivity(new Intent(this,ArchiveActivity.class));
                finish();
                break;
            case R.id.about:
                startActivity(new Intent(this,AboutActivity.class));
                finish();
                break;
        }
        return true;
    }

    private void initializeData()  {
        list = new ArrayList<>();

        toolbar = findViewById(R.id.toolbar_main);
        fab = findViewById(R.id.addToDoItemFAB);
        LinearLayout emptyLayout = findViewById(R.id.toDoEmptyView);
        recyclerView = findViewById(R.id.toDoRecyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setEmptyView(emptyLayout);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (appPref.getTheme().equals(LIGHT_THEME))
            recyclerView.setBackgroundColor(getResources().getColor(R.color.primary_lightest));
    }
    private void updateUi(){
        DbOperations.fetchAllAsync(Db.getInstance(this),this);
    }
    private class DatReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Intent intent1 = new Intent(context,AddActivity.class);
            if (intent != null) {
                intent1.putExtra(TO_DOITEM1, intent.getSerializableExtra(TO_DOITEM1));
                startActivity(intent1);
                finish();
            }
        }
    }
    private class ListReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<ToDo> ii = new ArrayList<>();
            ii = intent.getParcelableArrayListExtra(TO_DOITEM);
            list.clear();
            list.addAll(ii);
            adapter = new Adapter(MainActivity.this,list,fab,true,"Main");
            ItemTouchHelper.SimpleCallback callback = new ItemTouchHelperClass(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT,context,adapter,list.size());
            itemTouchHelper = new ItemTouchHelper(callback);
            itemTouchHelper.attachToRecyclerView(recyclerView);
            recyclerView.setAdapter(adapter);
        }
    }
}