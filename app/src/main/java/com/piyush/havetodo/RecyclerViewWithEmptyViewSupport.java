package com.piyush.havetodo;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class RecyclerViewWithEmptyViewSupport extends RecyclerView {
    private View emptyView;

    public RecyclerViewWithEmptyViewSupport(Context context) {
        super(context);
    }
    public RecyclerViewWithEmptyViewSupport(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    public RecyclerViewWithEmptyViewSupport(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    void checkIfEmpty(){
        Adapter<?> adapter = getAdapter();
        if (adapter != null && emptyView != null){
            if (adapter.getItemCount() == 0){
                this.setVisibility(GONE);
                emptyView.setVisibility(VISIBLE);
            } else {
                this.setVisibility(VISIBLE);
                emptyView.setVisibility(GONE);
            }
        }
    }
    void setEmptyView(View view){
        emptyView = view;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter != null)
            adapter.registerAdapterDataObserver(observer);
        checkIfEmpty();
    }

    AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            Log.e("check_","OnChanged()");
            checkIfEmpty();
        }
        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            checkIfEmpty();
        }
        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            checkIfEmpty();
        }
        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            checkIfEmpty();
        }
        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            checkIfEmpty();
        }
    };

//
//    The checkIfEmpty() method in EmptyRecyclerView checks if both the empty view and adapter are not null. Then if the item count provided by the adapter is equal to zero the empty view is shown and the EmptyRecyclerView is hidden. If the item count provided by the adapter is not zero then the empty view is hidden and the EmptyRecyclerView is shown.
//
//    The EmptyRecyclerView overrides the setAdapter() method of its superclass and registers an AdapterObserver whenever an adapter is set. It also unregisters the observer whenever the adapter is changed or unset. The AdapterObserver calls checkIfEmpty() every time it observes an event that changes the content of the adapter. checkIfEmpty() is also called when the adapter or empty view are set.

    //    private View emptyView;
//
//    private AdapterDataObserver observer = new AdapterDataObserver() {
//        @Override
//        public void onChanged() {
//            showEmptyView();
//        }
//
//        @Override
//        public void onItemRangeInserted(int positionStart, int itemCount) {
//            super.onItemRangeInserted(positionStart, itemCount);
//            showEmptyView();
//        }
//
//        @Override
//        public void onItemRangeRemoved(int positionStart, int itemCount) {
//            super.onItemRangeRemoved(positionStart, itemCount);
//            showEmptyView();
//        }
//    };
//    public RecyclerViewWithEmptyViewSupport(Context context) {
//        super(context);
//    }
//    public void showEmptyView(){
//        Adapter<?> adapter = getAdapter();
//        if(adapter!=null && emptyView!=null){
//            if(adapter.getItemCount()==0){
//                emptyView.setVisibility(VISIBLE);
//                RecyclerViewWithEmptyViewSupport.this.setVisibility(GONE);
//            }
//            else{
//                emptyView.setVisibility(GONE);
//                RecyclerViewWithEmptyViewSupport.this.setVisibility(VISIBLE);
//            }
//        }
//
//    }
//    public RecyclerViewWithEmptyViewSupport(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//    public RecyclerViewWithEmptyViewSupport(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//    }
//
//    @Override
//    public void setAdapter(Adapter adapter) {
//        super.setAdapter(adapter);
//        if(adapter!=null){
//            adapter.registerAdapterDataObserver(observer);
//            observer.onChanged();
//        }
//    }
//    public void setEmptyView(View v){
//        emptyView = v;
//    }
}
