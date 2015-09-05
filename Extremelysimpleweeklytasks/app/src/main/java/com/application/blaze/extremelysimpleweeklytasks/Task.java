package com.application.blaze.extremelysimpleweeklytasks;

import android.util.Log;

import com.application.blaze.extremelysimpleweeklytasks.util.CalendarUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pratikprakash on 8/16/15.
 */
public class Task {

    private Boolean hasPriority;
    private Boolean isCompleted;
    private String name;
    private String description;
    private Date date;
    private String time;
    private SimpleDateFormat dateFormatter;

    public Task() {
        this.hasPriority = false;
        this.isCompleted = false;
        this.name = "";
        this.description = "";
        this.date = new Date();
        this.time = "";
        dateFormatter = new SimpleDateFormat(CalendarUtils.FORMAT);
    }

    public Boolean getHasPriority() {
        return hasPriority;
    }

    public void setHasPriority(Boolean hasPriority) {
        this.hasPriority = hasPriority;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public String getDateFormattedAsString() { return dateFormatter.format(date); }

    public void setDateFormattedAsString(String date) {
        try {
            this.date = dateFormatter.parse(date);
        }
        catch (ParseException e) {
            Log.e("Task.java", "Exception when parsing date as string");
            this.date = new Date();
        }
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
