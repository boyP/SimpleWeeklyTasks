package com.application.blaze.extremelysimpleweeklytasks.update;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.application.blaze.extremelysimpleweeklytasks.MyApplication;
import com.application.blaze.extremelysimpleweeklytasks.R;
import com.application.blaze.extremelysimpleweeklytasks.Task;

public class UpdateTask extends DialogFragment {

    private Task mOldTask;
    private int mPosition;
    private EditText mTitle;
    private EditText mDescription;
    private Switch mPriority;
    private Button mDate;
    private Button mDelete;

    public UpdateTask() {
        // Required empty public constructor
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
        mDelete = (Button) view.findViewById(R.id.delete_button);
        updateFieldsFromTask();


        //Android toolbar configuration
        //*********************************************
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.app_bar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        ActionBar bar = activity.getSupportActionBar();
        if (bar != null) {
            bar.setDisplayShowTitleEnabled(false);
            bar.setHomeButtonEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
        }

        /**
         * Set the delete onClickListener
         **********************************************/
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Deletes the current task
                MyApplication.getWritableDatabase().deleteTask(mOldTask.getName());
            }
        });

        getDialog().setTitle("Edit Task");

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveUpdatedTask();
            case android.R.id.home:
                getActivity().onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets the task to be updated and the position of that task in the list view
     * @param t : Task to be updated
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
        newTask.setName(mTitle.getText().toString());
        newTask.setHasPriority(mPriority.getShowText());
        newTask.setDateFormattedAsString(mDate.getText().toString());
        newTask.setDescription(mDescription.getText().toString());

        //Save the new task into the database
        MyApplication.getWritableDatabase().updateTask(mOldTask, newTask);

        //Alert the main activity to update the view
        UpdateDialogListener activity = (UpdateDialogListener) getActivity();
        activity.onFinishUpdateDialog(newTask, mPosition);
    }

    public interface UpdateDialogListener {

        /**
         * A callback when the task update dialog has been saved
         * @param t : The updated task
         * @param position : The old position of the task
         */
        void onFinishUpdateDialog(Task t, int position);
    }
}
