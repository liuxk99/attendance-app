package org.fw.attendance;

import com.sj.attendance.bl.FixWorkTimePolicy;
import com.sj.attendance.bl.WorkTimePolicyFactory;
import com.sj.attendance.bl.WorkTimePolicySet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sj.attendance.bl.WorkTimePolicyFactory.createFixPolicyAM;
import static com.sj.attendance.bl.WorkTimePolicyFactory.createFixPolicyFD;
import static com.sj.attendance.bl.WorkTimePolicyFactory.createFixPolicyPM;

public class WorkTimePolicySetConfig {
    protected static List<WorkTimePolicySet> workTimePolicySetList = new ArrayList<>();

    protected List<FixWorkTimePolicy> workTimePolicyList = new ArrayList<>();
    protected Map<String, FixWorkTimePolicy> workTimePolicyMap = new HashMap<>();

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

    protected WorkTimePolicySetConfig() {
    }

    public void generateDef() {
        FixWorkTimePolicy fixPolicyFD = createFixPolicyFD();
        FixWorkTimePolicy fixPolicyAM = createFixPolicyAM();
        FixWorkTimePolicy fixPolicyPM = createFixPolicyPM();

        FixWorkTimePolicy flexPolicyFD = WorkTimePolicyFactory.createFlexPolicyFD();
        FixWorkTimePolicy flexPolicyAM = WorkTimePolicyFactory.createFlexPolicyAM();

        workTimePolicyList.add(fixPolicyFD);
        workTimePolicyList.add(fixPolicyAM);
        workTimePolicyList.add(fixPolicyPM);

        workTimePolicyList.add(flexPolicyFD);
        workTimePolicyList.add(flexPolicyAM);

        WorkTimePolicySet fixWorkTimePolicySet = new WorkTimePolicySet("XX集团-固定工时");
        {
            fixWorkTimePolicySet.addPolicy(fixPolicyFD);
            fixWorkTimePolicySet.addPolicy(fixPolicyAM);
            fixWorkTimePolicySet.addPolicy(fixPolicyPM);
        }

        WorkTimePolicySet flexWorkTimePolicySet = new WorkTimePolicySet("XX集团-弹性工时");
        {
            flexWorkTimePolicySet.addPolicy(flexPolicyFD);
            flexWorkTimePolicySet.addPolicy(flexPolicyAM);
            flexWorkTimePolicySet.addPolicy(fixPolicyPM);
        }

        workTimePolicySetList.add(fixWorkTimePolicySet);
        workTimePolicySetList.add(flexWorkTimePolicySet);

//        workTimePolicySetIndex = 0;
        workTimePolicySetIndex = workTimePolicySetList.size() - 1;
        setWorkTimePolicy(flexPolicyFD);
    }

    public FixWorkTimePolicy getWorkTimePolicy() {
        return workTimePolicy;
    }

    public void setWorkTimePolicy(FixWorkTimePolicy workTimePolicy) {
        this.workTimePolicy = workTimePolicy;
    }

    private FixWorkTimePolicy workTimePolicy;
}
