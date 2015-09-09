package com.application.blaze.extremelysimpleweeklytasks;

import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Model.ParentObject;

import java.util.Collections;
import java.util.List;

/**
 * Created by pratikprakash on 9/6/15.
 */
public class ParentHeader implements ParentObject {

    private List<Object> mChildObjectList;
    private String name;

    public ParentHeader(String name) {
        this.name = name;
        mChildObjectList = Collections.EMPTY_LIST;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<Object> getChildObjectList() {
        return mChildObjectList;
    }

    @Override
    public void setChildObjectList(List<Object> list) {
        mChildObjectList = list;
    }

    public void addChildToList(Object o) {
        this.mChildObjectList.add(o);
    }
}
