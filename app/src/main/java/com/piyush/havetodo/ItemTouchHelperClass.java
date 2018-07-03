package com.piyush.havetodo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ItemTouchHelperClass extends ItemTouchHelper.SimpleCallback{
    int size;
    private Context context;
    private ColorDrawable backGround;
    private ItemTouchHelperAdapter adapter;
    private Drawable deleteIcon, archiveIcon;

    ItemTouchHelperClass(int dragDirs, int swipeDirs, Context context, ItemTouchHelperAdapter adapter, int size) {
        super(dragDirs, swipeDirs);
        this.size = size;
        this.context = context;
        this.adapter = adapter;
        backGround = new ColorDrawable();
        deleteIcon = ContextCompat.getDrawable(context,R.drawable.menu_delete);
        archiveIcon = ContextCompat.getDrawable(context,R.drawable.menu_archive);
    }

    public interface ItemTouchHelperAdapter{
        void onItemMoved(int fromPosition, int toPosition);
        void onItemRemoved(int position, int direction);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View item = viewHolder.itemView;
        int itemHeight = item.getHeight();

        Log.e("check_ac",actionState+"");

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            Log.e("check_d", dX + "");
            if (dX < 0) {
            /*---------------Draw Red Background----------------*/
                backGround.setColor(Color.RED);
                backGround.setBounds(item.getRight() + (int) dX, item.getTop(), item.getRight(), item.getBottom());
                backGround.draw(canvas);

            /*---------------Set Delete Icon----------------*/
                int deleteIconMargin = (itemHeight - deleteIcon.getIntrinsicHeight()) / 2;
                int deleteIconToLeft = item.getRight() - deleteIconMargin - deleteIcon.getIntrinsicWidth();
                int deleteIconToTop = item.getTop() + (itemHeight - deleteIcon.getIntrinsicHeight()) / 2;
                int deleteIconToRight = item.getRight() - deleteIconMargin;
                int deleteIconToBottom = deleteIconToTop + deleteIcon.getIntrinsicHeight();
                deleteIcon.setBounds(deleteIconToLeft, deleteIconToTop, deleteIconToRight, deleteIconToBottom);
                deleteIcon.draw(canvas);
            } else if (dX > 0) {
                /*---------------Draw Green Background----------------*/
                backGround.setColor(context.getResources().getColor(R.color.green));
                backGround.setBounds(item.getLeft(), item.getTop(), item.getLeft() + (int) dX, item.getBottom());
                backGround.draw(canvas);

            /*---------------Set Archive Icon----------------*/
                int archiveIconMargin = (itemHeight - archiveIcon.getIntrinsicHeight()) / 2;
                int archiveIconToLeft = item.getLeft() + archiveIconMargin;
                int archiveIconToTop = item.getTop() + (itemHeight - archiveIcon.getIntrinsicHeight()) / 2;
                int archiveIconToRight = archiveIconMargin + archiveIcon.getIntrinsicWidth();
                int archiveIconToBottom = archiveIconToTop + archiveIcon.getIntrinsicHeight();
                archiveIcon.setBounds(archiveIconToLeft, archiveIconToTop, archiveIconToRight, archiveIconToBottom);
                archiveIcon.draw(canvas);
            }
        }
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        View item = viewHolder.itemView;
        super.clearView(recyclerView, viewHolder);
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int upFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(upFlags, swipeFlags);
    }
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        adapter.onItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        adapter.onItemRemoved(viewHolder.getAdapterPosition(),direction);
    }
}
