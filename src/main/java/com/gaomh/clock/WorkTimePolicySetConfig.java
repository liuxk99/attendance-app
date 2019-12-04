package com.gaomh.clock;

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
        return workTimePolicySetList.get(workTimePolicyIndex);
    }

    public void setWorkTimePolicyIndex(int workTimePolicyIndex) {
        if (workTimePolicyIndex != this.workTimePolicyIndex) {
            this.workTimePolicyIndex = workTimePolicyIndex;
        }
    }

    private int workTimePolicyIndex = 0;

    static public WorkTimePolicySetConfig getInstance() {
        if (workTimePolicySetConfig == null) {
            workTimePolicySetConfig = new WorkTimePolicySetConfig();
        }
        return workTimePolicySetConfig;
    }

    private WorkTimePolicySetConfig() {
        workTimePolicySetList = WorkTimePolicyFactory.createWorkTimePolicySetList();
//        workTimePolicyIndex = 0;
        workTimePolicyIndex = workTimePolicySetList.size() - 1;
    }
}
