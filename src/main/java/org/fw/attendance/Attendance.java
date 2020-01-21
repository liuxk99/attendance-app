package org.fw.attendance;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.sj.attendance.bl.CheckRecord;
import com.sj.attendance.bl.FixWorkTimePolicy;
import com.sj.attendance.provider.CheckRecordAdapter;
import com.sj.attendance.provider.WorkTimePolicyDataAdapter;
import com.sj.time.DateTimeUtils;
import com.sj4a.utils.SjLog;
import com.sj4a.utils.SjLogGen;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Attendance {
    private final String TAG = Attendance.class.getSimpleName();

    private SjLogGen sjLogGen = new SjLogGen(TAG);

    List<FixWorkTimePolicy> workTimePolicyList = new ArrayList<>();
    private static Attendance instance;
    private static WorkTimePolicyDataAdapter policyDataAdapter;
    private static CheckRecordAdapter checkRecordAdapter;

    public List<CheckRecord> getCheckRecordList() {
        return checkRecordList;
    }

    private List<CheckRecord> checkRecordList = new ArrayList<>();

    public static Attendance getInstance() {
        if (instance == null) {
            instance = new Attendance();
        }
        return instance;
    }

    FixWorkTimePolicy findPolicyByUuid(UUID uuid) {
        FixWorkTimePolicy workTimePolicy = null;
        for (FixWorkTimePolicy policy : workTimePolicyList) {
            if (policy.getUuid().equals(uuid)) {
                workTimePolicy = policy;
                break;
            }
        }
        return workTimePolicy;
    }

    public List<FixWorkTimePolicy> getWorkTimePolicyList() {
        return workTimePolicyList;
    }

    void init(Context context) {
        policyDataAdapter = new WorkTimePolicyDataAdapter(context);

        checkRecordAdapter = new CheckRecordAdapter(context);
    }

    void reload() {
        workTimePolicyList.clear();
        workTimePolicyList.addAll(policyDataAdapter.getAll());

        checkRecordList.clear();
        checkRecordList.addAll(checkRecordAdapter.getAll());

        for (CheckRecord checkRecord : checkRecordList) {
            if (checkRecord.getPolicyUuid() != null) {
                checkRecord.policy = findPolicyByUuid(checkRecord.getPolicyUuid());
            }
        }
    }

    @NonNull
    @Override
    public String toString() {
        String LF = "\n";
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(LF).append("policy list: ");
        for (FixWorkTimePolicy policy : workTimePolicyList) {
            sb.append(LF).append(policy.toString());
        }
        sb.append(LF).append("CheckRecord: ");
        for (CheckRecord checkRecord : checkRecordList) {
            sb.append(LF).append(checkRecord.toString());
        }
        return sb.toString();
    }

    public void save(CheckRecord checkRecord) {
        SjLog sjLog = sjLogGen.build("save(" + checkRecord + ")");
        sjLog.in();
        {
            if (!workTimePolicyList.contains(checkRecord.policy)) {
                policyDataAdapter.insert(checkRecord.policy);
            }
            if (checkRecord.getId() < 0) {
                long id = checkRecordAdapter.insert(checkRecord);
                checkRecord.setId(id);
            } else {
                boolean res = checkRecordAdapter.update(checkRecord);
                Log.i(TAG, "res: " + res);
            }
            reload();
        }
        sjLog.out();
    }

    public CheckRecord findCheckRecord(FixWorkTimePolicy policy, Date date) {
        for (CheckRecord checkRecord : checkRecordList) {
            long recordDate = DateTimeUtils.getDayDate(checkRecord.realCheckInTime);
            long dayDate = DateTimeUtils.getDayDate(date);
            if (recordDate == dayDate && checkRecord.policy.equals(policy)) {
                return checkRecord;
            }
        }
        return null;
    }

    public void exportRecords() {

    }
}
