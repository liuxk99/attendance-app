package org.fw.attendance;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.sj.attendance.bl.WorkTimePolicySetConfig;
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
            initConfig(this);

            Attendance attendance = Attendance.getInstance();
            attendance.init(this);
            attendance.reload();
        }
        sjLog.out();
    }

    private void initConfig(Context context) {
        ConfigPersist4A configPersist = ConfigPersist4A.getInstance();
        configPersist.initialize(context);
        WorkTimePolicySetConfig config = configPersist.load();
        if (config == null) {
            config = new WorkTimePolicySetConfig();
            config.generateDef();
            boolean res = configPersist.save(config);
            Log.i(TAG, "res = " + res);
        }
    }
}
