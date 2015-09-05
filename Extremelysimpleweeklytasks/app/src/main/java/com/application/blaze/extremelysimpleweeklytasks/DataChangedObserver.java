package com.application.blaze.extremelysimpleweeklytasks;

/**
 * Created by pratikprakash on 8/27/15.
 */
public interface DataChangedObserver {

    void onInsert(Task t, int position);
    void onDelete(Task t, int position);
    void onUpdate(Task t, int position);
}
