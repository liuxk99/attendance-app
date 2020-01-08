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

//        initProvider();
        WorkTimePolicySetConfig config = WorkTimePolicySetConfigFactory.getInstance();
        if (config != null) {
            if (config instanceof WorkTimePolicySetConfig4A) {
                WorkTimePolicySetConfig4A config4A = (WorkTimePolicySetConfig4A) config;
                config4A.initialize(this);
                config4A.load();

                if (WorkTimePolicySetConfig4A.getWorkTimePolicySetList().size() == 0) {
                    config4A.generateDef();
                    config4A.save();
                }
            }
        }
    }

    private void initProvider() {
        WorkTimePolicyAdapter adapter = new WorkTimePolicyAdapter(this);

        adapter.insert(WorkTimePolicyFactory.generateFlexPolicy());

        LinkedList<FixWorkTimePolicy> policyList = adapter.getAll();
        for (FixWorkTimePolicy policy : policyList) {
            Log.d(TAG, policy.toString());
        }
    }
}
