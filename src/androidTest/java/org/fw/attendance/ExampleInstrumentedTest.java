package org.fw.attendance;

import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.sj.attendance.bl.CheckRecord;
import com.sj.attendance.bl.FixWorkTimePolicy;
import com.sj.attendance.bl.TimeUtils;
import com.sj.attendance.bl.WorkTimePolicySet;
import com.sj.attendance.provider.CheckRecordAdapter;
import com.sj.attendance.provider.WorkTimePolicyDataAdapter;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private final String TAG = ExampleInstrumentedTest.class.getSimpleName();

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("org.fw.attendance", appContext.getPackageName());

        WorkTimePolicySetConfig config = WorkTimePolicySetConfigFactory.getInstance();
        if (config != null) {
            if (config instanceof WorkTimePolicySetConfig4A) {
                WorkTimePolicySetConfig4A config4A = (WorkTimePolicySetConfig4A) config;
                config4A.initialize(appContext);
                config4A.load();

                if (WorkTimePolicySetConfig4A.getWorkTimePolicySetList().size() == 0) {
                    config4A.generateDef();
                    config4A.save();
                }
            }
        }

        List<WorkTimePolicySet> policySetList = WorkTimePolicySetConfig4A.getWorkTimePolicySetList();
        int r = (int) (Math.random() * policySetList.size());
        int idx = r % policySetList.size();

        WorkTimePolicySet policySet = policySetList.get(idx);
        r = (int) (Math.random() * policySet.getWorkTimePolicyList().size());
        idx = r % policySet.getWorkTimePolicyList().size();

        FixWorkTimePolicy policy = policySet.getWorkTimePolicyList().get(idx);
        Log.i(TAG, "policy: " + policy);

        Date now = new Date();
        long checkInOffset = (long) ((Math.random() - 0.5) * TimeUtils.HOUR);
        long realCheckInTime = TimeUtils.getDayDate(now) + policy.getCheckInTime() + checkInOffset;

        long checkOutOffset = (long) ((Math.random() - 0.5) * TimeUtils.HOUR);
        long realCheckOutTime = TimeUtils.getDayDate(now) + policy.getCheckOutTime() + checkOutOffset;

        WorkTimePolicyDataAdapter policyAdapter = new WorkTimePolicyDataAdapter(appContext);
        long policyId = policyAdapter.insert(policy);

        Date realCheckInDate = new Date();
        realCheckInDate.setTime(realCheckInTime);

        Date realCheckOutDate = new Date();
        realCheckOutDate.setTime(realCheckOutTime);

        CheckRecord checkRecord = new CheckRecord(policySet.getName(), policy,
                realCheckInDate, realCheckOutDate);
        Log.i(TAG, "record: " + checkRecord);

        CheckRecordAdapter checkRecordAdapter = new CheckRecordAdapter(appContext);
        long recordId = checkRecordAdapter.insert(checkRecord);

        // read policies
        List<FixWorkTimePolicy> policyList = policyAdapter.getAll();
        // read check records
        List<CheckRecord> recordList = checkRecordAdapter.getAll();

        FixWorkTimePolicy policy1 = policyAdapter.getById(policyId);
        CheckRecord checkRecord1 = checkRecordAdapter.getById(recordId);

        Log.i(TAG, "policy: " + policy1);
        Log.i(TAG, "record: " + checkRecord1);

//        assertEquals(policy, policy1);
//        assertEquals(checkRecord, checkRecord1);
    }
}
