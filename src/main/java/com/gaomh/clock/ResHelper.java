package com.gaomh.clock;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ResHelper {
    public Map<Integer, Integer> weekDayMap = new HashMap<>();
    static ResHelper instance;

    public static ResHelper getInstance() {
        if (instance == null) {
            instance = new ResHelper();
        }
        return instance;
    }

    private ResHelper() {
        weekDayMap.put(Calendar.SUNDAY, R.string.WEEK_SUNDAY);
        weekDayMap.put(Calendar.MONDAY, R.string.WEEK_MONDAY);
        weekDayMap.put(Calendar.TUESDAY, R.string.WEEK_TUESDAY);
        weekDayMap.put(Calendar.WEDNESDAY, R.string.WEEK_WEDNESDAY);
        weekDayMap.put(Calendar.THURSDAY, R.string.WEEK_THURSDAY);
        weekDayMap.put(Calendar.FRIDAY, R.string.WEEK_FRIDAY);
        weekDayMap.put(Calendar.SATURDAY, R.string.WEEK_SATURDAY);
    }

    public int toWeekday(int dayOfWeek) {
        int resId = -1;
        if (!weekDayMap.isEmpty()) {
            Integer myInt = weekDayMap.get(dayOfWeek);
            if (myInt != null) {
                resId = myInt;
            }
        }
        return resId;
    }
}
