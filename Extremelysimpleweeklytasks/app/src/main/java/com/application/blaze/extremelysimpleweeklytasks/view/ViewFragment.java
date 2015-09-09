package com.application.blaze.extremelysimpleweeklytasks.view;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.blaze.extremelysimpleweeklytasks.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewFragment extends Fragment {

    private RecyclerView recyclerView;
    private TaskAdapter adapter;

    public ViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Gets the recyclerView tag we made in the xml file
        recyclerView = (RecyclerView) view.findViewById(R.id.recycle);
        recyclerView.setAdapter(adapter);

        //Creates a linear list view with the data
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
    }
}
