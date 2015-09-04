package com.application.blaze.extremelysimpleweeklytasks.CalendarTests;

import android.test.InstrumentationTestCase;

import com.application.blaze.extremelysimpleweeklytasks.util.CalendarUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by pratikprakash on 8/27/15.
 */
public class CalendarUtilsTest extends InstrumentationTestCase {

    private boolean dataParseSuccessfully;
    private SimpleDateFormat format;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        format = new SimpleDateFormat("EEEE, MMMM d, yyyy");
    }

    public void testGetTodaysDate() {
        Calendar cal = new GregorianCalendar();
        String expectedDate = format.format(cal.getTime());
        String actualDate = CalendarUtils.getTodaysDate();

        assertEquals(expectedDate, actualDate);
    }

    public void testGetTomorrowsDate() {
        String today = givenValidDate();
        String tom = CalendarUtils.getTomorrowsDate(today);
        dataParseSuccessfully = true;
        assertTrue(tom.equals("Tuesday, September 8, 2015"));
    }

    public void testGetNextWeekDate() {
        String today = givenValidDate();
        String tom = CalendarUtils.getNextWeeksDate(today);
        dataParseSuccessfully = true;
        assertEquals(tom, "Monday, September 14, 2015");
    }

    public void testGetNextMonthDate() {
        String today = givenValidDate();
        String tom = CalendarUtils.getNextMonthsDate(today);
        dataParseSuccessfully = true;
        assertTrue(tom.equals("Wednesday, October 7, 2015"));
    }

    public void testGetNextYearsDate() {
        String today = givenValidDate();
        String tom = CalendarUtils.getNextYearsDate(today);
        dataParseSuccessfully = true;
        assertTrue(tom.equals("Wednesday, September 7, 2016"));
    }

    public void testGetAllDaysOfWeek() {
        String today = givenValidDate();
        List<String> tom = CalendarUtils.getWeekOfDay(today);

        assertTrue(tom.size() == 7);
        assertEquals(tom.get(0), "Monday, September 7, 2015");
        assertEquals(tom.get(1), "Tuesday, September 8, 2015");
        assertEquals(tom.get(2), "Wednesday, September 9, 2015");
        assertEquals(tom.get(3), "Thursday, September 10, 2015");
        assertEquals(tom.get(4), "Friday, September 11, 2015");
        assertEquals(tom.get(5), "Saturday, September 12, 2015");
        assertEquals(tom.get(6), "Sunday, September 13, 2015");
    }

    private String givenValidDate() {
        return "Monday, September 7, 2015";
    }

    private String givenEdgeDate() {
        return "Sunday, February 24, 2013";
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
