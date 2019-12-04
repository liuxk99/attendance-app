package com.gaomh.clock;

import com.sj.attendance.bl.FixWorkTimePolicy;
import com.sj.attendance.bl.WorkTimePolicyFactory;
import com.sj.attendance.bl.WorkTimePolicySet;

import java.util.ArrayList;
import java.util.List;

public class WorkTimePolicySetConfig {
    private static WorkTimePolicySetConfig workTimePolicySetConfig;
    private static List<WorkTimePolicySet> workTimePolicySetList = new ArrayList<>();

    public static List<WorkTimePolicySet> getWorkTimePolicySetList() {
        return workTimePolicySetList;
    }

    public WorkTimePolicySet getWorkTimePolicySet() {
        return workTimePolicySetList.get(workTimePolicySetIndex);
    }

    public void setWorkTimePolicySetIndex(int workTimePolicySetIndex) {
        if (workTimePolicySetIndex != this.workTimePolicySetIndex) {
            this.workTimePolicySetIndex = workTimePolicySetIndex;
        }
    }

    private int workTimePolicySetIndex = 0;

    static public WorkTimePolicySetConfig getInstance() {
        if (workTimePolicySetConfig == null) {
            workTimePolicySetConfig = new WorkTimePolicySetConfig();
        }
        return workTimePolicySetConfig;
    }

    private WorkTimePolicySetConfig() {
        workTimePolicySetList = WorkTimePolicyFactory.createWorkTimePolicySetList();
//        workTimePolicySetIndex = 0;
        workTimePolicySetIndex = workTimePolicySetList.size() - 1;
    }

    public FixWorkTimePolicy getWorkTimePolicy() {
        return workTimePolicy;
    }

    public void setWorkTimePolicy(FixWorkTimePolicy workTimePolicy) {
        this.workTimePolicy = workTimePolicy;
    }

    private FixWorkTimePolicy workTimePolicy;
}
