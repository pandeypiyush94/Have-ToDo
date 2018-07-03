package com.piyush.havetodo;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.piyush.havetodo.AppDB.Db;
import com.piyush.havetodo.AppDB.DbOperations;
import com.piyush.havetodo.AppDB.ToDo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static com.piyush.havetodo.AppConstants.DARK_THEME;
import static com.piyush.havetodo.AppConstants.DATE_TIME_FORMAT_12_HOUR;
import static com.piyush.havetodo.AppConstants.DATE_TIME_FORMAT_24_HOUR;

/**
 Created by Piyush on 2/21/2018.
 */

class Adapter extends RecyclerViewWithEmptyViewSupport.Adapter<Adapter.Holder> implements ItemTouchHelperClass.ItemTouchHelperAdapter{

    private AppCompatActivity context;
    private AppPreferences appPref;
    private LayoutInflater inflater;
    private List<ToDo> list;

    private boolean val;
    private View layout;
    private String fromWhere;
    private ToDo swipedToDo;
    private int swipedToDoPosition;

    private boolean multiSelect = false;
    private ArrayList<ToDo> selectedItems = new ArrayList<>();

    Adapter(AppCompatActivity context, List<ToDo> list, View layout, boolean val, String fromWhere) {
        this.val = val;
        this.list = list;
        this.layout = layout;
        this.context = context;
        this.fromWhere = fromWhere;
        appPref = new AppPreferences(context);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.custom_list,parent,false));
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final ToDo model = list.get(position);
        int bgColor, toDoTxtColor;
        if (appPref.getTheme().equals(DARK_THEME)){
            bgColor = Color.DKGRAY;
            toDoTxtColor = Color.WHITE;
        } else {
            bgColor = Color.WHITE;
            toDoTxtColor = context.getResources().getColor(R.color.secondary_text);
        }

        holder.toDoTxt.setText(model.toDoTxt);
        if (model.toDoDate != null && !new Date(model.toDoDate.getTime()).equals(new Date(1751826600000L))) {
            String time;
            if (DateFormat.is24HourFormat(context))
                time = formatDate(DATE_TIME_FORMAT_24_HOUR, model.toDoDate);
            else
                time = formatDate(DATE_TIME_FORMAT_12_HOUR, model.toDoDate);
            holder.toDoDate.setText(time);
        } else
            holder.toDoDate.setVisibility(View.GONE);

        holder.toDoBg.setBackgroundColor(bgColor);
        holder.toDoTxt.setTextColor(toDoTxtColor);

        TextDrawable drawable = TextDrawable.builder().beginConfig()
                .textColor(Color.WHITE)
                .toUpperCase()
                .endConfig()
                .buildRoundRect(model.toDoTxt.substring(0,1),model.toDoColor,8);
        holder.toDoImg.setImageDrawable(drawable);

        if (fromWhere.equals("Archive"))
            holder.update(model);
        else
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DbOperations.fetchSingleAsync(Db.getInstance(context),context,model.toDoId);
            }
            });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onItemMoved(int fromPosition, int toPosition) {
        if (fromPosition<toPosition)
            for (int i=fromPosition;i<toPosition;i++)
                Collections.swap(list,i,i+1);
        else
            for (int i=fromPosition;i>toPosition;i--)
                Collections.swap(list,i,i-1);
        notifyItemMoved(fromPosition,toPosition);
    }
    @Override
    public void onItemRemoved(int position,int direction) {
        final int dir = direction;
        swipedToDo = list.remove(position);
        swipedToDoPosition = position;
        String snackText="";
        notifyItemRemoved(position);
        if (dir == ItemTouchHelper.START) {
            DbOperations.deleteSingleSync(Db.getInstance(context), context, swipedToDo.toDoId);
            snackText = "To Do Deleted";
        } else if (dir == ItemTouchHelper.END){
            DbOperations.updateArchiveAsync(Db.getInstance(context),context,swipedToDo.toDoId,true);
            snackText = "Moved To Archive";
        }
        Snackbar.make(layout,snackText,Snackbar.LENGTH_SHORT).setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.add(swipedToDoPosition, swipedToDo);
                notifyItemInserted(swipedToDoPosition);
                if (dir == ItemTouchHelper.START)
                    DbOperations.insertAsync(Db.getInstance(context), swipedToDo, false,context);
                else if (dir == ItemTouchHelper.END)
                    DbOperations.updateArchiveAsync(Db.getInstance(context),context,swipedToDo.toDoId,false);
            }
        }).show();
    }

    class Holder extends RecyclerViewWithEmptyViewSupport.ViewHolder{

        ImageView toDoImg;
        LinearLayout toDoBg;
        TextView toDoTxt, toDoDate;

        Holder(View itemView) {
            super(itemView);
            toDoBg = itemView.findViewById(R.id.listItemLinearLayout);
            toDoTxt = itemView.findViewById(R.id.toDoListItemTextView);
            toDoDate = itemView.findViewById(R.id.todoListItemTimeTextView);
            toDoImg = itemView.findViewById(R.id.toDoListItemColorImageView);
        }

        void selectItem(ToDo item) {
            if (multiSelect) {
                if (selectedItems.contains(item)) {
                    selectedItems.remove(item);
                    itemView.setBackgroundColor(Color.WHITE);
                } else {
                    selectedItems.add(item);
                    itemView.setBackgroundColor(Color.LTGRAY);
                }
            } else {
                if (val)
                    DbOperations.fetchSingleAsync(Db.getInstance(context),context,item.toDoId);
            }
        }
        void update(final ToDo value){
            if (selectedItems.contains(value))
                itemView.setBackgroundColor(Color.LTGRAY);
            else {
                if (appPref.getTheme().equals(DARK_THEME))
                    itemView.setBackgroundColor(Color.DKGRAY);
                else
                    itemView.setBackgroundColor(Color.WHITE);
            }
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ((AppCompatActivity)v.getContext()).startSupportActionMode(actionModeCallbacks);
                    selectItem(value);
                    return true;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectItem(value);
                }
            });
        }
    }

    private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            multiSelect = true;
            mode.getMenuInflater().inflate(R.menu.menu_actionmode_archive, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                /*-------For MainActivity's Menu--------*/
                case R.id.action_move_to_archive:
                    list.removeAll(selectedItems);
                    moveToArchive(selectedItems);
                    Toast.makeText(context,"Moved To Archive",Toast.LENGTH_SHORT).show();
                    mode.finish();
                    break;
                /*-------For ArchiveActivity's Menu--------*/
                case R.id.action_delete:
                    list.removeAll(selectedItems);
                    deleteToDo(selectedItems);
                    if (selectedItems.size()==1)
                        Toast.makeText(context,"Item Deleted",Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context,"Items Deleted",Toast.LENGTH_SHORT).show();
                    mode.finish();
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            multiSelect = false;
            selectedItems.clear();
            notifyDataSetChanged();
        }
    };

    private void moveToArchive(ArrayList<ToDo> list) {
        try {
            for (int i=0;i<list.size();i++) {
              UUID id = list.get(i).toDoId;
              DbOperations.updateArchiveAsync(Db.getInstance(context),context,id,true);
            }
        } catch(Exception e){
            Log.e("check_ex",e.toString());
            e.printStackTrace();
        }
    }
    private void deleteToDo(ArrayList<ToDo> list){
        try {
            for (int i=0;i<list.size();i++) {
                UUID id = list.get(i).toDoId;
                DbOperations.deleteSingleSync(Db.getInstance(context),context,id);
            }
        } catch(Exception e){
            Log.e("check_ex",e.toString());
            e.printStackTrace();
        }
    }

    static String formatDate(String format, Date dateToFormat){return new SimpleDateFormat(format, Locale.US).format(dateToFormat);}
}
