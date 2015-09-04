package com.application.blaze.extremelysimpleweeklytasks;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
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

import com.application.blaze.extremelysimpleweeklytasks.Insert.NewTask;
import com.application.blaze.extremelysimpleweeklytasks.update.UpdateTask;
import com.application.blaze.extremelysimpleweeklytasks.util.CalendarUtils;

import java.util.ArrayList;
import java.util.List;

public class TaskGrid extends AppCompatActivity implements UpdateTask.UpdateDialogListener {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private static TaskAdapter adapter;
    private List<SectionedTaskAdapter.Section> sections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_grid);

        //Toolbar initialization
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        //Get the current week
        String today = CalendarUtils.getTodaysDate();
        setTitle(today);

        //Gets the recyclerView tag we made in the xml file
        recyclerView = (RecyclerView) findViewById(R.id.recycle);

        //Create a sectioned list
        //*********************************
        SectionedTaskAdapter sectionedAdapter = initializeSections();
        recyclerView.setAdapter(adapter);

        //Creates a linear list view with the data
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);


        //Create the touch listeners for the recycle view
        //********************************
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Shows the update dialog
                Task t = adapter.getTask(position);
                showUpdateDialogFragment(t, position);
            }

            @Override
            public void onLongClick(View view, int position) {
                //For now deletes the selected task
                Task t = adapter.remove(position);
                if (t != null) {
                    MyApplication.getWritableDatabase().deleteTask(t.getName());
                }
            }
        }));

        //Floating Action Button listener
        //********************************
        ImageButton floatingButton = (ImageButton) findViewById(R.id.addButton);
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("TaskGrid", "Clicked Floating button");
                startActivity(new Intent(view.getContext(), NewTask.class));
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
        List<Task> taskList = MyApplication.getWritableDatabase().getAllTasksBasedOnDay(date);
        return taskList;
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

    /**
     * Update Dialog callback
     * @param t : The updated task
     * @param position : The old position of the task
     */
    @Override
    public void onFinishUpdateDialog(Task t, int position) {
        Toast.makeText(this, "Task is at, " + position, Toast.LENGTH_SHORT).show();
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
                    if(child != null && clickListener!= null) {
                        clickListener.onLongClick(child, recyclerView.getChildLayoutPosition(child));
                    }
                }

            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            Log.e("Recycler", "Intercept event" + e);

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if(child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {

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

    /**
     * When a task has been inserted into the database, call this method to update the recyclerView
     * @param t
     */
    public static void notifyOnInsert(Task t) {
        adapter.add(t, 1);
    }

    public static interface ClickListener {
        public void onClick(View view, int position);
        public void onLongClick(View view, int position);
    }

    /**
     * This is only called when the onCreate method is called, once when the activity is first started.
     */
    private SectionedTaskAdapter initializeSections() {
        //1.) Set the first section to position 0
        //2.) Get from the database all the tasks corrosponding to that day and store it in the section
        //3.) Set the next section to the previous section position + the number of tasks.
        //4.) Create the sectionAdapter object and return it.
        sections = new ArrayList<SectionedTaskAdapter.Section>();
        List<String> dates = CalendarUtils.getWeekOfDay(CalendarUtils.getTodaysDate());

        SectionedTaskAdapter.Section monday = new SectionedTaskAdapter.Section(0, getString(R.string.Monday_text));
        List<Task> mTasks = getTaskListsByDate(dates.get(0));
        if (mTasks != null) {
            for (Task t : mTasks) {
                monday.addTaskToList(t);
            }
        }

        int tuPosition = monday.firstPosition + monday.getNumTasks() + 1;
        SectionedTaskAdapter.Section tuesday = new SectionedTaskAdapter.Section(tuPosition, getString(R.string.Tuesday_text));
        List<Task> tuTasks = getTaskListsByDate(dates.get(1));
        if (tuTasks != null) {
            for (Task t : tuTasks) {
                tuesday.addTaskToList(t);
            }
        }

        int wPosition = tuesday.firstPosition + tuesday.getNumTasks() + 1;
        SectionedTaskAdapter.Section wednesday = new SectionedTaskAdapter.Section(wPosition, getString(R.string.Wednesday_text));
        List<Task> wTasks = getTaskListsByDate(dates.get(2));
        if (wTasks != null) {
            for (Task t : wTasks) {
                wednesday.addTaskToList(t);
            }
        }

        int thPosition = wednesday.firstPosition + wednesday.getNumTasks() + 1;
        SectionedTaskAdapter.Section thursday = new SectionedTaskAdapter.Section(thPosition, getString(R.string.Thursday_text));
        List<Task> thTasks = getTaskListsByDate(dates.get(3));
        if (thTasks != null) {
            for (Task t : thTasks) {
                thursday.addTaskToList(t);
            }
        }

        int fPosition = thursday.firstPosition + thursday.getNumTasks() + 1;
        SectionedTaskAdapter.Section friday = new SectionedTaskAdapter.Section(fPosition, getString(R.string.Friday_text));
        List<Task> fTasks = getTaskListsByDate(dates.get(4));
        if (fTasks != null) {
            for (Task t : fTasks) {
                friday.addTaskToList(t);
            }
        }

        int saPosition = friday.firstPosition + friday.getNumTasks() + 1;
        SectionedTaskAdapter.Section saturday = new SectionedTaskAdapter.Section(saPosition, getString(R.string.Saturday_text));
        List<Task> saTasks = getTaskListsByDate(dates.get(5));
        if (saTasks != null) {
            for (Task t : saTasks) {
                saturday.addTaskToList(t);
            }
        }

        int suPosition = saturday.firstPosition + saturday.getNumTasks() + 1;
        SectionedTaskAdapter.Section sunday = new SectionedTaskAdapter.Section(suPosition, getString(R.string.Sunday_text));
        List<Task> suTasks = getTaskListsByDate(dates.get(6));
        if (suTasks != null) {
            for (Task t : suTasks) {
                sunday.addTaskToList(t);
            }
        }

        sections.add(monday);
        sections.add(tuesday);
        sections.add(wednesday);
        sections.add(thursday);
        sections.add(friday);
        sections.add(saturday);
        sections.add(sunday);

        mTasks.addAll(tuTasks);
        mTasks.addAll(wTasks);
        mTasks.addAll(thTasks);
        mTasks.addAll(fTasks);
        mTasks.addAll(saTasks);
        mTasks.addAll(suTasks);
        adapter = new TaskAdapter(this, mTasks);
        SectionedTaskAdapter.Section[] dummy = new SectionedTaskAdapter.Section[sections.size()];
        SectionedTaskAdapter sectionedAdapter = new SectionedTaskAdapter(this, R.layout.section, R.id.section_text, adapter);
        sectionedAdapter.setSections(sections.toArray(dummy));
        return null;
    }

    /**
     * Shows the update dialog fragment when a task is pressed
     * @param t : The task that has been pressed
     * @param pos : The position of the task in the recyclerView
     */
    private void showUpdateDialogFragment(Task t, int pos) {
        UpdateTask updateFragment = new UpdateTask();
        FragmentManager manager = getFragmentManager();
        updateFragment.show(manager, "update_fragment");
        updateFragment.setCurrentTask(t, pos);
        //FragmentTransaction transaction = manager.beginTransaction();
        //transaction.add(R.layout.fragment_update_task, updateFragment, "UpdateFragment");
        //transaction.commit();
    }

}


