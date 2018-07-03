package com.piyush.havetodo.AppDB;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.piyush.havetodo.AppConstants.TO_DOITEM;
import static com.piyush.havetodo.AppConstants.TO_DOITEM1;

/**
 Created by Piyush on 2/23/2018.
 */

public class DbOperations {

    public static void deleteAsync(Db db){
        DeleteAll delete = new DeleteAll(db);
        delete.execute();
    }
    public static void insertAsync(Db db, ToDo toDo, boolean val, Context context){
        InsertTodo insert = new InsertTodo(db,toDo,context,val);
        Log.e("check_data","insert1");
        insert.execute();
    }
    public static void updateAsync(Db db, ToDo model,Context context){
        UpdateTodo update = new UpdateTodo(db, model,context);
        update.execute();
    }
    public static void fetchAllAsync(Db db, Context context){
        FetchAll fetch = new FetchAll(db, context);
        fetch.execute();
    }
    public static void fetchAllPastAsync(Db db, Context context){
        FetchAllPast fetch = new FetchAllPast(db, context);
        fetch.execute();
    }
    public static void deleteSingleSync(Db db, Context context, UUID id){
        DeleteSingleToDo delete = new DeleteSingleToDo(db, context);
        delete.execute(id);
    }
    public static void fetchSingleAsync(Db db, Context context, UUID id){
        FetchSingleToDo fetch = new FetchSingleToDo(db, context);
        fetch.execute(id);
    }
    public static void updateArchiveAsync(Db db, Context context, UUID id, boolean val){
        UpdateTodoArchive update = new UpdateTodoArchive(db, context, id, val);
        update.execute();
    }
    public static void updateDateAsync(Db db, Context context, UUID id, Date toDoDate){
        UpdateTodoDate update = new UpdateTodoDate(db, context, id, toDoDate);
        update.execute();
    }

    private static class DeleteAll extends AsyncTask<Void,Void,Void>{
        Db db;
        DeleteAll(Db db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            db.toDoDao().deleteAll(new Date().getTime());
            return null;
        }
    }
    private static class InsertTodo extends AsyncTask<Void,Void,Void>{
        private long id;
        private boolean val;
        private final Db db;
        @SuppressLint("StaticFieldLeak")
        private Context context;
        private final ToDo toDo;
        InsertTodo(Db instance, ToDo toDo, Context context,boolean val) {
            db = instance;
            this.val = val;
            this.toDo = toDo;
            this.context = context;
            Log.e("check_data","insert2");
        }
        @Override
        protected Void doInBackground(Void... voids) {
            id = db.toDoDao().insertToDo(toDo);
            Log.e("check_data",id+" insert3");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.e("check_data",id+" insert4");
            if (val && id > 0)
                context.sendBroadcast(new Intent("com.piyush.todo_inserted"));
        }
    }
    private static class UpdateTodo extends AsyncTask<Void, Void, Void>{
        Db db;
        int id = 0;
        ToDo model;
        @SuppressLint("StaticFieldLeak")
        private Context context;
        UpdateTodo(Db db, ToDo model, Context context) {
            this.db = db;
            this.model = model;
            this.context = context;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            id = db.toDoDao().updateTodo(model);
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            Log.e("check_","update");
            if(id > 0)
                context.sendBroadcast(new Intent("com.piyush.todo_inserted"));
        }
    }
    private static class UpdateTodoDate extends AsyncTask<Void, Void, Void>{
        Db db;
        UUID id;
        Date toDoDate;
        @SuppressLint("StaticFieldLeak")
        Context context;
        UpdateTodoDate(Db db, Context context, UUID id, Date toDoDate) {
            this.db = db;
            this.id = id;
            this.context = context;
            this.toDoDate = toDoDate;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            db.toDoDao().updateToDoDate(id,true,toDoDate);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            context.sendBroadcast(new Intent("com.piyush.finishReminder"));
        }
    }
    private static class FetchAll extends AsyncTask<Void, Void, List<ToDo>> {
        private Db db;
        @SuppressLint("StaticFieldLeak")
        private Context context;
        FetchAll(Db db, Context context) {
            this.db = db;
            this.context = context;
        }
        @Override
        protected List<ToDo> doInBackground(Void... voids) {
            return db.toDoDao().fetchAllToDo(false);
        }
        @Override
        protected void onPostExecute(List<ToDo> aVod) {
            Intent intent = new Intent("com.haveToDo.list");
            intent.putParcelableArrayListExtra(TO_DOITEM,(ArrayList<ToDo>)aVod);
            context.sendBroadcast(intent);
        }
    }
    private static class DeleteSingleToDo extends AsyncTask<UUID, Void, Void>{
        private Db db;
        @SuppressLint("StaticFieldLeak")
        private Context context;
        DeleteSingleToDo(Db db, Context context) {
            this.db = db;
            this.context = context;
        }

        @Override
        protected Void doInBackground(UUID... uuids) {
            db.toDoDao().deleteToDo(uuids[0]);
            return null;
        }
    }
    private static class FetchSingleToDo extends AsyncTask<UUID, Void, ToDo> {
        private Db db;
        @SuppressLint("StaticFieldLeak")
        private Context context;
        FetchSingleToDo(Db db, Context context) {
            this.db = db;
            this.context = context;
        }
        @Override
        protected ToDo doInBackground(UUID... voids) {
            return db.toDoDao().fetchToDo(voids[0]);
        }
        @Override
        protected void onPostExecute(ToDo aVod) {
            Intent intent = new Intent("com.haveToDo.data");
            intent.putExtra(TO_DOITEM1, (Serializable) aVod);
            context.sendBroadcast(intent);
            Log.e("check_","broadcast send");
        }
    }
    private static class UpdateTodoArchive extends AsyncTask<Void, Void, Void>{
        Db db;
        UUID id;
        boolean val;
        @SuppressLint("StaticFieldLeak")
        Context context;
        UpdateTodoArchive(Db db, Context context, UUID id, boolean val) {
            this.db = db;
            this.id = id;
            this.val = val;
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            db.toDoDao().updateArchive(id,val);
            return null;
        }
    }
    private static class FetchAllPast extends AsyncTask<Void, Void, List<ToDo>> {
        private Db db;
        @SuppressLint("StaticFieldLeak")
        private Context context;
        FetchAllPast(Db db, Context context) {
            this.db = db;
            this.context = context;
        }
        @Override
        protected List<ToDo> doInBackground(Void... voids) {
            return db.toDoDao().fetchPastToDo(true);
        }
        @Override
        protected void onPostExecute(List<ToDo> aVod) {
            Intent intent = new Intent("com.haveToDo.past_list");
            intent.putParcelableArrayListExtra(TO_DOITEM,(ArrayList<ToDo>)aVod);
            context.sendBroadcast(intent);
        }
    }
}
