package com.application.blaze.extremelysimpleweeklytasks;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.application.blaze.extremelysimpleweeklytasks.insert.InsertTask;
import com.application.blaze.extremelysimpleweeklytasks.update.UpdateTask;
import com.application.blaze.extremelysimpleweeklytasks.util.CalendarUtils;
import com.application.blaze.extremelysimpleweeklytasks.view.TaskAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TaskGrid extends AppCompatActivity implements DataChangedObserver {

    public TaskGrid() {
    }

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private Context mContext;

    //    private List<SectionedTaskAdapter.Section> sections;
    private List<ParentHeader> headers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_grid);
        mContext = this;

        //Toolbar initialization
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        //Get the current week
        String today = CalendarUtils.getTodaysDate();
        setTitle(today);

        //Gets the recyclerView tag we made in the xml file
        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);


        //Create a sectioned list
        //*********************************
        taskAdapter = new TaskAdapter(this, initializeSections());
        taskAdapter.setParentAndIconExpandOnClick(true);
        taskAdapter.onRestoreInstanceState(savedInstanceState);

        recyclerView.setAdapter(taskAdapter);


        //Create the touch listeners for the recycle view
        //********************************
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Shows the update dialog
                try {
                    if (taskAdapter.isViewATask(position)) {
                        Task t = taskAdapter.getTask(position);
                        showUpdateDialogFragment(t, position);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //showUpdateDialogFragment(t, position);
                Toast.makeText(mContext, "pos: " + position, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        //Floating Action Button listener
        //********************************
        ImageButton floatingButton = (ImageButton) findViewById(R.id.addButton);
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("TaskGrid", "Clicked Floating button");
                //startActivity(new Intent(view.getContext(), NewTask.class));
                showInsertDialogFragment();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_grid, menu);
        return true;
    }

    /**
     * For now it creates dummy tasks, but in reality it should be getting data either from the database or from the User
     */
    private List<Task> getTaskListsByDate(String date) {
        return MyApplication.getWritableDatabase().getAllTasksBasedOnDay(date);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInsert(Task t, int position) {
        taskAdapter.addTaskToHeader(t);
    }

    @Override
    public void onDelete(Task t, int position) {
        try {
            if (taskAdapter.isViewATask(position)) {
                taskAdapter.remove(position);
            }
            else {
                Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdate(Task t, int position) {
        try {
            if (taskAdapter.isViewATask(position)) {
                taskAdapter.update(t, position);
            }
            else {
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            Log.e("Recycler", "Constructor invoked");

            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildLayoutPosition(child));
                    }
                }

            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            Log.e("Recycler", "Intercept event" + e);

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {

                clickListener.onClick(child, rv.getChildLayoutPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            Log.e("Recycler", "TouchEvent" + e);
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            //Required to override
        }
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    /**
     * This is only called when the onCreate method is called, once when the activity is first started.
     */
    private List<ParentObject> initializeSections() {

        List<ParentObject> headers = new ArrayList<>();
        List<String> dates = CalendarUtils.getWeekOfDay(CalendarUtils.getTodaysDate());

        ParentHeader monday = new ParentHeader(getString(R.string.Monday_text));
        List<Task> mTasks = getTaskListsByDate(dates.get(0));
        if (mTasks != null) {
            ArrayList<Object> childList = new ArrayList<>();
            childList.addAll(mTasks);
            monday.setChildObjectList(childList);
        }
        headers.add(monday);

        ParentHeader tuesday = new ParentHeader(getString(R.string.Tuesday_text));
        List<Task> tuTasks = getTaskListsByDate(dates.get(1));
        if (tuTasks != null) {
            ArrayList<Object> childList = new ArrayList<>();
            childList.addAll(tuTasks);
            tuesday.setChildObjectList(childList);
        }
        headers.add(tuesday);

        ParentHeader wednesday = new ParentHeader(getString(R.string.Wednesday_text));
        List<Task> wTasks = getTaskListsByDate(dates.get(2));
        if (wTasks != null) {
            ArrayList<Object> childList = new ArrayList<>();
            childList.addAll(wTasks);
            wednesday.setChildObjectList(childList);
        }
        headers.add(wednesday);

        ParentHeader thursday = new ParentHeader(getString(R.string.Thursday_text));
        List<Task> thTasks = getTaskListsByDate(dates.get(3));
        if (thTasks != null) {
            ArrayList<Object> childList = new ArrayList<>();
            childList.addAll(thTasks);
            thursday.setChildObjectList(childList);
        }
        headers.add(thursday);

        ParentHeader friday = new ParentHeader(getString(R.string.Friday_text));
        List<Task> fTasks = getTaskListsByDate(dates.get(4));
        if (fTasks != null) {
            ArrayList<Object> childList = new ArrayList<>();
            childList.addAll(fTasks);
            friday.setChildObjectList(childList);
        }
        headers.add(friday);

        ParentHeader saturday = new ParentHeader(getString(R.string.Saturday_text));
        List<Task> saTasks = getTaskListsByDate(dates.get(5));
        if (saTasks != null) {
            ArrayList<Object> childList = new ArrayList<>();
            childList.addAll(saTasks);
            saturday.setChildObjectList(childList);
        }
        headers.add(saturday);

        ParentHeader sunday = new ParentHeader(getString(R.string.Sunday_text));
        List<Task> suTasks = getTaskListsByDate(dates.get(6));
        if (suTasks != null) {
            ArrayList<Object> childList = new ArrayList<>();
            childList.addAll(suTasks);
            sunday.setChildObjectList(childList);
        }
        headers.add(sunday);


//        mTasks.addAll(tuTasks);
//        mTasks.addAll(wTasks);
//        mTasks.addAll(thTasks);
//        mTasks.addAll(fTasks);
//        mTasks.addAll(saTasks);
//        mTasks.addAll(suTasks);
//

        return headers;
    }

    /**
     * Shows the update dialog fragment when a task is pressed
     *
     * @param t   : The task that has been pressed
     * @param pos : The position of the task in the recyclerView
     */
    private void showUpdateDialogFragment(Task t, int pos) {
        FragmentManager fragmentManager = getFragmentManager();
        UpdateTask updateFragment = new UpdateTask();
        updateFragment.setDataChangedObserver(this);
        updateFragment.setCurrentTask(t, pos);

        // The device is smaller, so show the fragment fullscreen
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        transaction.add(android.R.id.content, updateFragment)
                .addToBackStack(null).commit();
    }

    /**
     * Shows the insert dialog fragment when the floating button is pressed
     */
    private void showInsertDialogFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        InsertTask insertTask = new InsertTask();
        insertTask.setDataChangedObserver(this);

        // The device is smaller, so show the fragment fullscreen
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // For a little polish, specify a transition animation
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        // To make it fullscreen, use the 'content' root view as the container
        // for the fragment, which is always the root view for the activity
        transaction.add(android.R.id.content, insertTask)
                .addToBackStack(null).commit();
    }

}


