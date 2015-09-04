package com.application.blaze.extremelysimpleweeklytasks;

/**
 * Created by pratikprakash on 8/27/15.
 */
public interface DataChangedObserver {

    void onInsert(Task t);
    void onDelete(Task t);
    void onUpdate(Task t);

    void registerAsObserver();
}
