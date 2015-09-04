package com.application.blaze.extremelysimpleweeklytasks;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.application.blaze.extremelysimpleweeklytasks.db.TaskContract;

/**
 * Created by pratikprakash on 8/26/15.
 */
public class TaskDataProvider extends ContentProvider {

    private static final String AUTH = "com.appliction.blaze.extremelysimpleweeklytasks.util";
    public static final Uri TASK_URI = Uri.parse("content://" + AUTH + "/tasks");

    private static final int TASKS = 1;

    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
