package com.application.blaze.extremelysimpleweeklytasks;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

/**
 * This adapter is used by the RecycleView to display the tasks as a list. It adapts the data type
 * Task to display in the RecycleView.
 * <p>
 *
 *
 * Created by pratikprakash on 8/17/15.
 */
public class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private final LayoutInflater inflator;
    private List<Task> taskList = Collections.emptyList();

    private static final int ITEM_TYPE = 1;

    /**
     *
     * @param context : The current context calling the TaskAdapter
     * @param tasks   : The list of tasks to be displayed
     */
    public TaskAdapter(Context context, List<Task> tasks) {
        this.context = context;
        this.inflator = LayoutInflater.from(context);
        this.taskList = tasks;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = inflator.inflate(R.layout.task_row, parent, false);
            return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        /**
         * Gets the current task from the list of tasks and tells the holder to set the
         * task information to the view.
         */

            TaskViewHolder tHolder = (TaskViewHolder) holder;
            int pos = position >= taskList.size() ? taskList.size()-1 : position;
            Task currentTask = taskList.get(pos);
            tHolder.taskName.setText(currentTask.getName());
            tHolder.taskTime.setText(currentTask.getDateFormattedAsString());
    }
/*
    @Override
    public int getItemViewType(int position) {
        int type = position == 0 ? SECTION_TYPE : ITEM_TYPE;
        return type;
    }
*/
    /**
     * Adds a task to the taskList
     *
     * @param t : The task to be added
     * @param position : The position to where the task is to be added
     */
    public void add(Task t,int position) {
        position = position == -1 ? getItemCount()  : position;
        taskList.add(position,t);
        notifyItemInserted(position);
    }

    /**
     * Adds a task to the taskList
     *
     * @param t : The task to be added
     *
     */
    public void add(Task t) {
        taskList.add(t);
        notifyItemInserted(taskList.size() - 1);
    }

    /**
     * Removes a task from the taskList
     * @param position : The position from where the task should be removed
     */
    public Task remove(int position){
        if (position < getItemCount()  ) {
            Task t = taskList.remove(position);
            notifyItemRemoved(position);
            return t;
        }
        return null;
    }

    public Task getTask(int position) {
        if (position < getItemCount()  ) {
            Task t = taskList.get(position);
            return t;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    /**
     * The holder's purpose is to get the information from the row and display it without
     * having to call findViewById every single time by saving into the recycleView cache.
     */
    class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView taskName;
        public TextView taskTime;

        public TaskViewHolder(View itemView) {
            super(itemView);

            taskName = (TextView) itemView.findViewById(R.id.taskName);
            taskTime = (TextView) itemView.findViewById(R.id.taskTime);

            //Sets the onClickListeners to the task
            taskName.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            /**
             * When the task is clicked, the details of the task should show up.
             */
        }
    }

    /**
     * The holder's purpose is to get the information from the row and display it without
     * having to call findViewById every single time by saving into the recycleView cache.
     */
    class SectionViewHolder extends RecyclerView.ViewHolder {

        public TextView sectionTitle;

        public SectionViewHolder(View itemView) {
            super(itemView);
            sectionTitle = (TextView) itemView.findViewById(R.id.section_text);
        }
    }
}
