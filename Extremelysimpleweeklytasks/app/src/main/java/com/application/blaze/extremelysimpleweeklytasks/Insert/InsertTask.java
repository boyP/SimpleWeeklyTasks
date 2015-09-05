package com.application.blaze.extremelysimpleweeklytasks.insert;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;

import com.application.blaze.extremelysimpleweeklytasks.DataChangedObserver;
import com.application.blaze.extremelysimpleweeklytasks.MyApplication;
import com.application.blaze.extremelysimpleweeklytasks.R;
import com.application.blaze.extremelysimpleweeklytasks.Task;

import java.util.Calendar;
import java.util.Date;


public class InsertTask extends DialogFragment {

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
    private DataChangedObserver observer;


    public InsertTask() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Initialize and set the view fields
        //********************************************
        taskName = (EditText) view.findViewById(R.id.taskTitle);
        priority = (Switch) view.findViewById(R.id.prioritySwitch);
        date = (Button) view.findViewById(R.id.date);

        //Initialize and set the calendar fields
        //********************************************
        newDate = Calendar.getInstance();
        mYear = newDate.get(Calendar.YEAR);
        mMonth = newDate.get(Calendar.MONTH);
        mDay = newDate.get(Calendar.DAY_OF_MONTH);
        date.setText(mDay + "-"
                + (mMonth + 1) + "-" + mYear);

        //Android toolbar configuration
        //*********************************************
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.app_bar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_update_save:
                        addTask();
                        dismiss();
                    case R.id.action_update_cancel:
                        dismiss();
                }
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.menu_dialog);
        toolbar.setTitle("New Task");

        //Set OnClickListeners
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePickerDialog();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_new_task, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * The onClick method
     */
    private void addTask() {
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
            observer.onInsert(t, 1);
        }
    }

    /**
     * Responsible for opening the date picker dialog
     */
    private void openDatePickerDialog() {
        newDate = Calendar.getInstance();
        mYear = newDate.get(Calendar.YEAR);
        mMonth = newDate.get(Calendar.MONTH);
        mDay = newDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.setText(dayOfMonth + "-"
                        + (monthOfYear + 1) + "-" + year);
                newDate.set(year, monthOfYear, dayOfMonth);
            }
        }, mYear, mMonth, mDay);
        dpd.show();
    }

    /**
     * Sets the observer to to enable callbacks to update the main Activity's view
     * @param o
     */
    public void setDataChangedObserver(DataChangedObserver o) {
        observer = o;
    }

}
