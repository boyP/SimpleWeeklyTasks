package com.application.blaze.extremelysimpleweeklytasks.update;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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

public class UpdateTask extends DialogFragment {

    private DataChangedObserver observer;

    private Task mOldTask;
    private int mPosition;
    private EditText mTitle;
    private EditText mDescription;
    private Switch mPriority;
    private Button mDate;

    //Calendar variables
    private int mYear;
    private int mMonth;
    private int mDay;
    private Calendar newDate;

    public UpdateTask() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_task, container, false);

        //Initialize and set the view fields
        //********************************************
        mTitle = (EditText) view.findViewById(R.id.update_title);
        mDescription = (EditText) view.findViewById(R.id.update_task_description);
        mPriority = (Switch) view.findViewById(R.id.update_priority_switch);
        mDate = (Button) view.findViewById(R.id.update_date);
        Button mDelete = (Button) view.findViewById(R.id.delete_button);
        updateFieldsFromTask();


        //Initialize and set the calendar fields
        //********************************************
        newDate = Calendar.getInstance();
        mYear = newDate.get(Calendar.YEAR);
        mMonth = newDate.get(Calendar.MONTH);
        mDay = newDate.get(Calendar.DAY_OF_MONTH);


        //Android toolbar configuration
        //*********************************************
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.app_bar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_update_save:
                        saveUpdatedTask();
                        dismiss();
                    case R.id.action_update_cancel:
                        dismiss();
                }
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.menu_dialog);
        toolbar.setTitle("Edit task");

        /**
         * Set the datepicker dialog onClickListener
         **********************************************/
        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePickerDialog();
            }
        });

        /**
         * Set the delete onClickListener
         **********************************************/
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Deletes the current task
                long id = MyApplication.getWritableDatabase().deleteTask(mOldTask.getName());
                if (id < 0) {
                    Log.e("Database", "Failed to delete task");
                } else {
                    observer.onDelete(mOldTask, mPosition);
                }

                dismiss();
            }
        });

        /*
        // Show soft keyboard automatically
        mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mEditText.setOnEditorActionListener(this);
        */

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_dialog, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_update_save:
                saveUpdatedTask();
                dismiss();
            case android.R.id.home:
                dismiss();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets the task to be updated and the position of that task in the list view
     *
     * @param t   : Task to be updated
     * @param pos : Position of task in view
     */
    public void setCurrentTask(Task t, int pos) {
        mOldTask = t;
        mPosition = pos;
    }


    /**
     * Populates the view fields from the old task
     */
    private void updateFieldsFromTask() {
        mTitle.setText(mOldTask.getName());
        mDate.setText(mOldTask.getDateFormattedAsString());
        mPriority.setChecked(mOldTask.getHasPriority());
        String description = mOldTask.getDescription() == null ? "" : mOldTask.getDescription();
        mDescription.setText(description);
    }

    /**
     * Saves the updated task to the database and alerts the main activity that the view has changed
     */
    private void saveUpdatedTask() {
        //Populate the new task with the updated fields
        Task newTask = new Task();
        Date taskDate = newDate.getTime();
        newTask.setName(mTitle.getText().toString());
        newTask.setHasPriority(mPriority.getShowText());
        newTask.setDate(taskDate);
        newTask.setDescription(mDescription.getText().toString());

        //Save the new task into the database
        long id = MyApplication.getWritableDatabase().updateTask(mOldTask, newTask);
        if (id < 0) {
            Log.e("Database", "Failed to update task");
        } else {
            //Alert the main activity to update the view
            observer.onUpdate(newTask, mPosition);
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
                mDate.setText(dayOfMonth + "-"
                        + (monthOfYear + 1) + "-" + year);
                newDate.set(year, monthOfYear, dayOfMonth);
            }
        }, mYear, mMonth, mDay);
        dpd.show();
    }

    /**
     * Sets the observer to to enable callbacks to update the main Activity's view
     *
     * @param o
     */
    public void setDataChangedObserver(DataChangedObserver o) {
        observer = o;
    }
}
