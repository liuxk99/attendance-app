package org.fw.attendance;

import android.app.Application;
import android.util.Log;

import com.sj.attendance.bl.FixWorkTimePolicy;
import com.sj.attendance.bl.WorkTimePolicyFactory;
import com.sj.attendance.provider.WorkTimePolicyAdapter;

import java.util.LinkedList;

public class ClockApplication extends Application {

    private static final String TAG = ClockApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        WorkTimePolicyAdapter adapter = new WorkTimePolicyAdapter(this);

        adapter.insert(WorkTimePolicyFactory.generateFlexPolicy());

        LinkedList<FixWorkTimePolicy> policyList = adapter.getAll();
        for (FixWorkTimePolicy policy: policyList) {
            Log.d(TAG, policy.toString());
        }
    }
}
