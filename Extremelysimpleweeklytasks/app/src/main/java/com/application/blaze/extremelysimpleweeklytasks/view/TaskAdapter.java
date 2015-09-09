package com.application.blaze.extremelysimpleweeklytasks.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.application.blaze.extremelysimpleweeklytasks.ParentHeader;
import com.application.blaze.extremelysimpleweeklytasks.R;
import com.application.blaze.extremelysimpleweeklytasks.Task;
import com.application.blaze.extremelysimpleweeklytasks.TaskGrid;
import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;

import java.util.List;

/**
 * This adapter is used by the RecycleView to display the tasks as a list. It adapts the data type
 * Task to display in the RecycleView.
 * <p/>
 * <p/>
 * <p/>
 * Created by pratikprakash on 8/17/15.
 */
public class TaskAdapter extends ExpandableRecyclerAdapter<TaskAdapter.HeaderHolder, TaskAdapter.TaskViewHolder> {

    private final LayoutInflater inflator;
    private static final int ITEM_TYPE = 1;

    /**
     * @param context : The current context calling the TaskAdapter
     * @param headers : The list of tasks to be displayed
     */
    public TaskAdapter(Context context, List<ParentObject> headers) {
        super(context, headers);
        this.inflator = LayoutInflater.from(context);
    }

    /*
     * Expandable view
     ****************************************/
    @Override
    public HeaderHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        View view = inflator.inflate(R.layout.header, viewGroup, false);
        return new HeaderHolder(view);
    }

    @Override
    public TaskViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        View view = inflator.inflate(R.layout.task_row, viewGroup, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(HeaderHolder headerHolder, int position, Object o) {
        ParentHeader header = (ParentHeader) o;
        headerHolder.headerName.setText(header.getName());
    }

    @Override
    public void onBindChildViewHolder(TaskViewHolder taskViewHolder, int position, Object o) {
        final Task task = (Task) o;
        taskViewHolder.taskName.setText(task.getName());
        taskViewHolder.taskTime.setText(task.getDateFormattedAsString());

        Log.e("Taskname: " + task.getName(), "Task day: " + task.getDay() + "pos: " + position + " itemListSize " + mItemList.size() + "parentSize " + mParentItemList.size());
    }

    /*
     * View changes
     ******************************************/

    /**
     * Removes a task from the taskList
     *
     * @param position : The position from where the task should be removed
     */
    public Task remove(int position) {
        if (position < getItemCount()) {
            Task t = (Task) mItemList.get(position); //Guarranteed to be a task
            ParentObject p = mParentItemList.get(t.getDay());
            p.getChildObjectList().remove(t);
            mItemList.remove(t);
            notifyDataSetChanged();
            return t;
        }
        return null;
    }

    public void update(Task newTask, int position) {
        //Remove old task
        Task oldTask = (Task) mItemList.get(position); //Guarranteed to be a task
        ParentObject p = mParentItemList.get(oldTask.getDay());

        //mItemList.remove(position);
        p.getChildObjectList().remove(oldTask);

        //Add updated task
        addTaskToHeader(newTask);
    }

    public Task getTask(int position) {
        if (position < getItemCount()) {
            Object o = this.mItemList.get(position);
            if (o instanceof Task) {
                return (Task) o;
            }
        }
        return null;
    }

    public boolean isViewATask(int position) throws Exception{
        if(position > -1 && position < mItemList.size()) {
            Object obj = mItemList.get(position);
            if(obj instanceof Task) {
                return true;
            }
            return false;
        }
        else {
            throw new Exception("Invalid position");
        }
    }

    public void addTaskToHeader(Task t) {
            ParentObject p = mParentItemList.get(t.getDay());
            List<Object> objs = p.getChildObjectList();
            objs.add(t);
            p.setChildObjectList(objs);
            notifyDataSetChanged();
    }

    private ParentObject getHeader(int position) {
        return mParentItemList.get(position);
    }

    /*
     * Holder classes
     **********************************/
    class HeaderHolder extends ParentViewHolder {

        public Button headerName;

        public HeaderHolder(View itemView) {
            super(itemView);
            headerName = (Button) itemView.findViewById(R.id.header_name);
        }
    }

    class TaskViewHolder extends ChildViewHolder {

        public TextView taskName;
        public TextView taskTime;

        public TaskViewHolder(final View itemView) {
            super(itemView);

            taskName = (TextView) itemView.findViewById(R.id.taskName);
            taskTime = (TextView) itemView.findViewById(R.id.taskTime);
        }
    }
}
