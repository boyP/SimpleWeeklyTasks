package com.application.blaze.extremelysimpleweeklytasks;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Adds the section headers to the list of tasks
 */
public class SectionedTaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private static final int SECTION_TYPE = 0;

    private boolean mValid = true;
    private int mSectionResourceId;
    private int mTextResourceId;
    private LayoutInflater mLayoutInflater;
    private RecyclerView.Adapter mBaseAdapter;
    private SparseArray<Section> mSections = new SparseArray<Section>();

    /**
     *
     * @param context : The current context of the caller
     * @param sectionResourceId :
     * @param textResourceId :
     * @param baseAdapter : The base adapter the SectionedTaskAdapter uses to add sections to
     */
    public SectionedTaskAdapter(Context context, int sectionResourceId, int textResourceId,
                                RecyclerView.Adapter baseAdapter) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSectionResourceId = sectionResourceId;
        mTextResourceId = textResourceId;
        mBaseAdapter = baseAdapter;
        mContext = context;

        //Observer for when an item in the list view changes
        mBaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                mValid = mBaseAdapter.getItemCount()>0;
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                mValid = mBaseAdapter.getItemCount()>0;
                notifyItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mValid = mBaseAdapter.getItemCount()>0;
                notifyItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                mValid = mBaseAdapter.getItemCount()>0;
                notifyItemRangeRemoved(positionStart, itemCount);
            }
        });
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {

        //The section header
        public TextView title;

        public SectionViewHolder(View view,int mTextResourceid) {
            super(view);
            title = (TextView) view.findViewById(mTextResourceid);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == SECTION_TYPE) {
            final View view = LayoutInflater.from(mContext).inflate(mSectionResourceId, parent, false);
            return new SectionViewHolder(view,mTextResourceId);
        }else{
            return mBaseAdapter.onCreateViewHolder(parent, viewType -1);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isSectionHeaderPosition(position)) {
            ((SectionViewHolder)holder).title.setText(mSections.get(position).title);
        }else{
            mBaseAdapter.onBindViewHolder(holder,sectionedPositionToPosition(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return isSectionHeaderPosition(position)
                ? SECTION_TYPE
                : mBaseAdapter.getItemViewType(sectionedPositionToPosition(position)) +1 ;
    }

    @Override
    public int getItemCount() {
        return (mValid ? mBaseAdapter.getItemCount() + mSections.size() : 0);
    }

    @Override
    public long getItemId(int position) {
        return isSectionHeaderPosition(position)
                ? Integer.MAX_VALUE - mSections.indexOfKey(position)
                : mBaseAdapter.getItemId(sectionedPositionToPosition(position));
    }

    public static class Section {
        int firstPosition;
        int sectionedPosition;
        CharSequence title;
        List<Task> taskList;
        int numTasks;

        public Section(int firstPosition, CharSequence title) {
            this.firstPosition = firstPosition;
            this.title = title;
            this.taskList = new ArrayList<Task>();
            numTasks = 0;
        }

        public CharSequence getTitle() {
            return title;
        }
        public List<Task> getTaskList() { return taskList; }

        public void addTaskToList(Task t) {
            taskList.add(t);
            numTasks++;
        }

        public Boolean removeTaskFromList(Task t) {
            return taskList.remove(t);
        }

        public int getNumTasks() {
            return numTasks;
        }
    }

    public void setSections(Section[] sections) {
        mSections.clear();

        Arrays.sort(sections, new Comparator<Section>() {
            @Override
            public int compare(Section o, Section o1) {
                return (o.firstPosition == o1.firstPosition)
                        ? 0
                        : ((o.firstPosition < o1.firstPosition) ? -1 : 1);
            }
        });

        int offset = 0; // offset positions for the headers we're adding
        for (Section section : sections) {
            section.sectionedPosition = section.firstPosition + offset;
            mSections.append(section.sectionedPosition, section);
            ++offset;
        }

        notifyDataSetChanged();
    }

    public int positionToSectionedPosition(int position) {
        int offset = 0;
        for (int i = 0; i < mSections.size(); i++) {
            if (mSections.valueAt(i).firstPosition > position) {
                break;
            }
            ++offset;
        }
        return position + offset;
    }

    /**
     * Gets the subView that is under the correct section view
     *
     * @param sectionedPosition
     * @return
     */
    public int sectionedPositionToPosition(int sectionedPosition) {
        if (isSectionHeaderPosition(sectionedPosition)) {
            return RecyclerView.NO_POSITION;
        }

        int offset = 0;
        for (int i = 0; i < mSections.size(); i++) {
            if (mSections.valueAt(i).sectionedPosition > sectionedPosition) {
                break;
            }
            --offset;
        }
        return sectionedPosition + offset;
    }

    public boolean isSectionHeaderPosition(int position) {
        return mSections.get(position) != null;
    }
}