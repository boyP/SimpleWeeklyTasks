package com.application.blaze.extremelysimpleweeklytasks.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.application.blaze.extremelysimpleweeklytasks.Task;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pratikprakash on 8/16/15.
 */
public class TaskContract {

    private Context context;
    private TaskDBHelper mHelper;

    public TaskContract(Context context) {
        this.context = context;
        mHelper = new TaskDBHelper(context, null);

    }

    /**
     * Inserts the task t into the database
     * @param t
     */
    public long insertTask(Task t) {

        SQLiteDatabase mDatabase = mHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(mHelper.COLUMN_NAME, t.getName());
        contentValues.put(mHelper.COLUMN_PRIORITY, t.getHasPriority());
        contentValues.put(mHelper.COLUMN_DATE, t.getDateFormattedAsString());

        long id = mDatabase.insert(mHelper.TABLE_NAME, null, contentValues);
        mDatabase.close();
        return id;
    }

    /**
     * Queries the database for tasks based on the task date.
     * 
     * @param date
     * @return
     */
    public List<Task> getAllTasksBasedOnDay(String date) {

        List<Task> tasks = new ArrayList<Task>();
        SQLiteDatabase mDatabase = mHelper.getWritableDatabase();
        String[] columns = {
                mHelper.COLUMN_NAME,
                mHelper.COLUMN_PRIORITY,
                mHelper.COLUMN_DATE
        };

        //SELECT Table from TABLE_NAME where date=day
        Cursor cursor = mDatabase.query(mHelper.TABLE_NAME, columns,
                mHelper.COLUMN_DATE + "= '" + date + "' ", null, null, null, null);

        while (cursor.moveToNext()) {

            //Retrieves the task information from the database
            String name = cursor.getString(cursor.getColumnIndex(mHelper.COLUMN_NAME));
            boolean priority = (cursor.getInt(cursor.getColumnIndex(mHelper.COLUMN_PRIORITY)) == 1) ? true : false;
            String taskDate = cursor.getString(cursor.getColumnIndex(mHelper.COLUMN_DATE));

            //Creates the task from the database and adds it to the list of tasks
            Task t = new Task();
            t.setHasPriority(priority);
            t.setName(name);
            t.setDateFormattedAsString(taskDate);
            tasks.add(t);
        }
        mDatabase.close();
        return tasks;
    }

    /**
     * Deletes a task from the database based on the given taskName
     * @param taskName
     */
    public int deleteTask(String taskName) {
        SQLiteDatabase mDatabase = mHelper.getWritableDatabase();
        int pass = 0;

        try {
            pass = mDatabase.delete(mHelper.TABLE_NAME, mHelper.COLUMN_NAME + " = ? ", new String[] { taskName } );
            Log.e("Database", "Deleting " + taskName + " succeeded");
        }
        catch (SQLiteException e) {
            Log.e("Database", "Deleting " + taskName + " failed");
        }
        finally {
            mDatabase.close();
            return pass;
        }
    }

    /**
     * Deletes all tasks from the database
     */
    public void deleteAllTasks() {
        SQLiteDatabase mDatabase = mHelper.getWritableDatabase();
        mDatabase.delete(mHelper.TABLE_NAME, null, null);
        mDatabase.close();
    }

    /**
     * Updates the task in the database with the new task newTask
     * @param newTask
     */
    public int updateTask(Task oldTask, Task newTask) {
        //SET NAME = '' WHERE NAME = ''
        SQLiteDatabase mDatabase = mHelper.getWritableDatabase();
        int pass = 0;
        String oldTaskName = oldTask.getName();

        ContentValues contentValues = new ContentValues();
        contentValues.put(mHelper.COLUMN_NAME, newTask.getName());
        contentValues.put(mHelper.COLUMN_PRIORITY, newTask.getHasPriority());
        contentValues.put(mHelper.COLUMN_DATE, newTask.getDateFormattedAsString());

        //An issue here is if the taskname is the one that changed so I need to somehow get the id instead
        try {
            pass = mDatabase.update(mHelper.TABLE_NAME, contentValues, mHelper.COLUMN_NAME + " = ? ", new String[]{oldTaskName});
            Log.e("Database", "Updating " + oldTaskName + " to " + newTask.getName() + " succeeded");
        }
        catch (SQLiteException e) {
            Log.e("Database", "Updating " + oldTaskName + " to " + newTask.getName() + " failed");
        }
        finally {
            mDatabase.close();
            return pass;
        }
    }

    static class TaskDBHelper extends SQLiteOpenHelper {

        private static final String DB_NAME = "tasks_db";
        private static final int DB_VERSION = 5;

        public static final String TABLE_NAME = "tasks";
        private static final String COLUMN_UID = "_id";
        private static final String COLUMN_NAME = "name";
        private static final String COLUMN_PRIORITY = "priority";
        private static final String COLUMN_DATE = "date";
        private static final String COLUMN_TIME = "time";
        private static final String COLUMN_DESCRIPTION = "description";

        private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " TEXT," +
                COLUMN_PRIORITY + " BOOLEAN," +
                COLUMN_DATE + " TEXT," +
                COLUMN_TIME + " TEXT," +
                COLUMN_DESCRIPTION + " TEXT" +

                ");";

        private static final String DROP_TABLE = " DROP TABLE IF EXISTS " + TABLE_NAME;


        public TaskDBHelper(Context context, SQLiteDatabase.CursorFactory factory) {
            super(context, DB_NAME, factory, DB_VERSION);
            Log.e("DATABASE OPERATIONS", "Database created / opened...");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            try {
                db.execSQL(DROP_TABLE);
                onCreate(db);
            }

            catch (android.database.SQLException e) {
                Log.e("Database", "Error updating table: " + e);
            }
        }


        //The initial data if any needs to be put in here
        @Override
        public void onCreate(SQLiteDatabase db) {

            try {
                db.execSQL(CREATE_TABLE);
                Log.e("Database", "Create table executed");
            }
            catch (android.database.SQLException e) {
                Log.e("Database", "Error creating table: " + e);
            }
        }
    }
}
