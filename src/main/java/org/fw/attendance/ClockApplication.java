package org.fw.attendance;

import android.app.Application;

import com.sj4a.utils.SjLog;
import com.sj4a.utils.SjLogGen;

public class ClockApplication extends Application {
    private static final String TAG = ClockApplication.class.getSimpleName();

    SjLogGen sjLogGen = new SjLogGen(TAG);

    @Override
    public void onCreate() {
        SjLog sjLog = sjLogGen.build("onCreate()");
        sjLog.in();
        {
            super.onCreate();
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
        sjLog.out();
    }
}
