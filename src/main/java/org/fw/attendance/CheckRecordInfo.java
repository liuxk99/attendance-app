package org.fw.attendance;

import com.sj.attendance.bl.FixWorkTimePolicy;
import com.sj.attendance.bl.WorkTimePolicySet;

import java.util.Date;

public class CheckRecordInfo {
    public WorkTimePolicySet policySet;
    public FixWorkTimePolicy policy;
    public Date realCheckInTime;
    public Date realCheckOutTime;

    public CheckRecordInfo(WorkTimePolicySet policySet, FixWorkTimePolicy policy, Date realCheckInDate, Date realCheckOutDate) {
        this.policySet = policySet;
        this.policy = policy;
        this.realCheckInTime = realCheckInDate;
        this.realCheckOutTime = realCheckOutDate;
    }
}
