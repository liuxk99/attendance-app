package org.fw.attendance;

import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.sj.attendance.bl.CheckRecord;
import com.sj.attendance.bl.FixWorkTimePolicy;
import com.sj.attendance.bl.WorkTimePolicySetConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class AttendanceTest {

    private static final String LOG_TAG = AttendanceTest.class.getSimpleName();

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testcase_001() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("org.fw.attendance", appContext.getPackageName());

        WorkTimePolicySetConfig config;
        {
            ConfigPersist4A configPersist = ConfigPersist4A.getInstance();
            configPersist.initialize(appContext);
            config = configPersist.load();
            if (config == null) {
                config = new WorkTimePolicySetConfig();
                config.generateDef();
                boolean res = configPersist.save(config);
                Log.i(LOG_TAG, "res = " + res);
            }
        }
        Attendance attendance = Attendance.getInstance();
        attendance.init(appContext);
        attendance.reload();
        Log.i(LOG_TAG, attendance.toString());

        FixWorkTimePolicy policy = config.generateRandomPolicy();
        Log.i(LOG_TAG, "policy: " + policy);

        CheckRecord checkRecord = CheckRecord.randomInstance("xxx", policy);
        Log.i(LOG_TAG, "checkRecord-1: " + checkRecord);
        attendance.save(checkRecord);

        checkRecord.realCheckOutTime = CheckRecord.randomCheckOutTime(policy);
        Log.i(LOG_TAG, "checkRecord-2: " + checkRecord);
        attendance.save(checkRecord);

        Log.i(LOG_TAG, attendance.toString());
    }
}