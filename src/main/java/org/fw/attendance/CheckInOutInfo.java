package org.fw.attendance;

import com.sj.attendance.bl.FixWorkTimePolicy;
import com.sj.attendance.bl.WorkTimePolicySet;

import java.util.Date;

public class CheckInOutInfo {
    public WorkTimePolicySet workTimePolicySet;
    public FixWorkTimePolicy workTimePolicy;
    public Date realCheckInTime;
    public Date realCheckOutTime;

    public CheckInOutInfo(WorkTimePolicySet workTimePolicySet, FixWorkTimePolicy workTimePolicy, Date realCheckInDate, Date realCheckOutDate) {
        this.workTimePolicySet = workTimePolicySet;
        this.workTimePolicy = workTimePolicy;
        this.realCheckInTime = realCheckInDate;
        this.realCheckOutTime = realCheckOutDate;
    }
}
