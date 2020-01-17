package org.fw.attendance;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.sj.time.DateStore;
import com.sj.time.DateTimeUtils;

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
        if (!TextUtils.isEmpty(dateStr)) {
            Date date = DateTimeUtils.fromISO8601(dateStr);
            if (DateTimeUtils.isSameDay(date, new Date())) {
                return date;
            }
        }

        return null;
    }

    @Override
    public void save(Date date) {
        Log.i(TAG, "saveDate(" + date + ")");
        if (sharedPreferences != null) {
            sharedPreferences.edit().putString(key, DateTimeUtils.toISO8601(date)).commit();
        }
    }
}
