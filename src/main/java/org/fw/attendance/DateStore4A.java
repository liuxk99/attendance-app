package org.fw.attendance;

import android.content.SharedPreferences;
import android.util.Log;

import com.sj.attendance.bl.TimeUtils;
import com.sj.time.DateStore;

import java.text.ParseException;
import java.util.Date;

public class DateStore4A implements DateStore {
    final private String TAG = DateStore4A.class.getSimpleName();

    private SharedPreferences sharedPreferences;
    private String key;

    public DateStore4A(SharedPreferences sharedPreferences, String key) {
        this.sharedPreferences = sharedPreferences;
        this.key = key;
    }

    @Override
    public Date load() throws ParseException {
        String dateStr = sharedPreferences.getString(key, "");
        Date date = TimeUtils.fromISO8601(dateStr);
        if (TimeUtils.isSameDay(date, new Date())) {
            return date;
        }

        return null;
    }

    @Override
    public void save(Date date) {
        Log.i(TAG, "saveDate(" + date + ")");
        if (sharedPreferences != null) {
            sharedPreferences.edit().putString(key, TimeUtils.toISO8601(date)).apply();
        }
    }
}
