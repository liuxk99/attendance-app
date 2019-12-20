package org.fw.attendance;

import com.sj.attendance.bl.FixWorkTimePolicy;
import com.sj.attendance.bl.WorkTimePolicySet;

import java.util.Date;

public class CheckInOutInfo {
    public WorkTimePolicySet workTimePolicySet;
    public FixWorkTimePolicy workTimePolicy;
    public Date realCheckInTime;
    public Date realCheckOutTime;
}
