package com.application.blaze.extremelysimpleweeklytasks.util;

import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by pratikprakash on 8/27/15.
 */
public class CalendarUtils {

    public static final String FORMAT = "EEEE, MMMM d, yyyy";
    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern(FORMAT).withLocale(Locale.US);

    //Need to get the week's date all at one time in an array. today's date, tomorrow's date as EEEE

    public static String getTodaysDate() {
       Calendar cal = new GregorianCalendar();
        SimpleDateFormat format = new SimpleDateFormat(FORMAT);
        return format.format(cal.getTime());
    }

    public static String getTomorrowsDate(String today) {
        LocalDate t = formatter.parseLocalDate(today);
        LocalDate n = t.plusDays(1);
        return n.toString(formatter);
    }

    public static String getNextWeeksDate(String today) {
        LocalDate t = formatter.parseLocalDate(today);
        LocalDate n = t.plusWeeks(1);
        return n.toString(formatter);
    }

    public static String getNextMonthsDate(String today) {
        LocalDate t = formatter.parseLocalDate(today);
        LocalDate n = t.plusMonths(1);
        return n.toString(formatter);
    }

    public static String getNextYearsDate(String today) {
        LocalDate t = formatter.parseLocalDate(today);
        LocalDate n = t.plusYears(1);
        return n.toString(formatter);
    }

    public static List<String> getWeekOfDay(String today) {
        List<String> week = new ArrayList<String>();

        LocalDate t = formatter.parseLocalDate(today);
        LocalDate monday = t.withDayOfWeek(DateTimeConstants.MONDAY);
        LocalDate tuesday = monday.plusDays(1);
        LocalDate wednesday = tuesday.plusDays(1);
        LocalDate thursday = wednesday.plusDays(1);
        LocalDate friday = thursday.plusDays(1);
        LocalDate saturday = friday.plusDays(1);
        LocalDate sunday = saturday.plusDays(1);

        week.add(monday.toString(formatter));
        week.add(tuesday.toString(formatter));
        week.add(wednesday.toString(formatter));
        week.add(thursday.toString(formatter));
        week.add(friday.toString(formatter));
        week.add(saturday.toString(formatter));
        week.add(sunday.toString(formatter));

        return week;
    }
}
