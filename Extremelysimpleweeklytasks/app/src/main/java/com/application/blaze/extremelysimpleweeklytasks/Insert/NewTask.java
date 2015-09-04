package com.application.blaze.extremelysimpleweeklytasks.Insert;

import android.app.DatePickerDialog;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.support.v7.widget.Toolbar;

import com.application.blaze.extremelysimpleweeklytasks.MyApplication;
import com.application.blaze.extremelysimpleweeklytasks.R;
import com.application.blaze.extremelysimpleweeklytasks.Task;
import com.application.blaze.extremelysimpleweeklytasks.TaskGrid;
import com.application.blaze.extremelysimpleweeklytasks.update.UpdateTask;

import java.util.Calendar;
import java.util.Date;

/**
 * This activity is for creating a new task to add to the taskList
 */
public class NewTask extends AppCompatActivity {

    /* Layout fields */
    private EditText taskName;
    private Switch priority;
    private EditText description;
    private Button date;
    private String time;

    //Calendar variables
    private int mYear;
    private int mMonth;
    private int mDay;
    private Calendar newDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        //UI/UX component initializations
        taskName = (EditText) findViewById(R.id.taskTitle);
        priority = (Switch) findViewById(R.id.prioritySwitch);
        date = (Button) findViewById(R.id.date);

        //Setting the initial text of the button
        newDate = Calendar.getInstance();
        mYear = newDate.get(Calendar.YEAR);
        mMonth = newDate.get(Calendar.MONTH);
        mDay = newDate.get(Calendar.DAY_OF_MONTH);
        date.setText(mDay + "-"
                + (mMonth + 1) + "-" + mYear);

        //Android toolbar configuration
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);                   // Setting toolbar as the ActionBar with setSupportActionBar() call
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayShowTitleEnabled(false);
            bar.setHomeButtonEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
        }

        //Set OnClickListeners
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePickerDialog();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_save:
                addTask();
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * The onClick method
     */
    public void addTask() {

        //Gets the task information from the UI/UX
        String name = taskName.getText().toString();
        Date taskDate = newDate.getTime();

        //Initializes the task
        Task t = new Task();
        t.setName(name);
        t.setHasPriority(priority.getShowText());
        t.setDate(taskDate);

        //Inserts the task into the database
        long id = MyApplication.getWritableDatabase().insertTask(t);

        if (id < 0) {
            Log.e("Database", "Failed to add task");
        } else {
            //Tells the view to load the new task
            TaskGrid.notifyOnInsert(t);
        }
    }

    /**
     * Responsible for opening the date picker dialog
     */
    public void openDatePickerDialog() {
        newDate = Calendar.getInstance();
        mYear = newDate.get(Calendar.YEAR);
        mMonth = newDate.get(Calendar.MONTH);
        mDay = newDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.setText(dayOfMonth + "-"
                        + (monthOfYear + 1) + "-" + year);
                newDate.set(year, monthOfYear, dayOfMonth);
            }
        }, mYear, mMonth, mDay);
        dpd.show();
    }

}