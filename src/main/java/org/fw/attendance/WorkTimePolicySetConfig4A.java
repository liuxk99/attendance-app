package org.fw.attendance;

import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.uuid.Generators;
import com.sj.attendance.bl.FixWorkTimePolicy;
import com.sj.attendance.bl.FlexWorkTimePolicy;
import com.sj.attendance.bl.WorkTimePolicySet;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class WorkTimePolicySetConfig4A extends WorkTimePolicySetConfig {
    public static final String SELECTED_POLICY = "selected";
    private Context context;

    private static final String SP_WORK_TIME_CONFIG = "work-time-config";
    private static final String NODE_POLICY_SET_LIST = "policy-set-list";
    public static final String NODE_WORK_TIME_POLICY_LIST = "workTimePolicyList";
    private static final String NODE_POLICY_LIST = "policy-list";
    private SharedPreferences configSp;

    public List<FixWorkTimePolicy> getWorkTimePolicyList() {
        return workTimePolicyList;
    }

    void initialize(Context context) {
        this.context = context;
        if (context != null) {
            configSp = context.getSharedPreferences(SP_WORK_TIME_CONFIG, Context.MODE_PRIVATE);
        }
    }

    void load() {
        loadPolicyList();
        loadPolicySetList();
        loadSelectedPolicy();
    }

    private void loadSelectedPolicy() {
        String uuid = configSp.getString(SELECTED_POLICY, "");
        if (!uuid.isEmpty()) {
            setWorkTimePolicy(workTimePolicyMap.get(uuid));
        }
    }

    private void loadPolicyList() {
        Set<String> policyUuidSet = configSp.getStringSet(NODE_POLICY_LIST, null);
        if (policyUuidSet != null) {
            for (String uuidStr : policyUuidSet) {
                FixWorkTimePolicy policy = loadPolicy(uuidStr);
                workTimePolicyList.add(policy);
                workTimePolicyMap.put(uuidStr, policy);
            }
        }
    }

    private FixWorkTimePolicy loadPolicy(String uuid) {
        FixWorkTimePolicy policy = null;
        {
            SharedPreferences sp = context.getSharedPreferences(uuid, Context.MODE_PRIVATE);
            if (sp != null) {
                String defStr = "";
                String name = sp.getString("name", defStr);
                String shortName = sp.getString("shortName", defStr);
                int type = sp.getInt("type", 0);
                long checkIn = sp.getLong("checkIn", -1L);
                long checkOut = sp.getLong("checkOut", -1L);
                if (type == 1) {
                    long latestCheckIn = sp.getLong("latestCheckIn", -1L);

                    policy = new FlexWorkTimePolicy(name, shortName, checkIn, checkOut - checkIn, latestCheckIn);
                } else {
                    policy = new FixWorkTimePolicy(name, shortName, checkIn, checkOut - checkIn);
                }
            }
        }
        return policy;
    }

    private void loadPolicySetList() {
        Set<String> policySetUuidSet = configSp.getStringSet(NODE_POLICY_SET_LIST, null);
        if (policySetUuidSet != null) {
            for (String uuid : policySetUuidSet) {
                WorkTimePolicySet policySet = loadPolicySet(uuid);
                workTimePolicySetList.add(policySet);
            }
        }

    }

    private WorkTimePolicySet loadPolicySet(String uuid) {
        WorkTimePolicySet policySet = null;
        {
            SharedPreferences sp = context.getSharedPreferences(uuid, Context.MODE_PRIVATE);
            if (sp != null) {
                String defStr = "";
                String name = sp.getString("name", defStr);
                policySet = new WorkTimePolicySet(name);

                Set<String> uuidSet = sp.getStringSet(NODE_WORK_TIME_POLICY_LIST, null);
                if (uuidSet != null) {
                    for (String policyUuid : uuidSet) {
                        FixWorkTimePolicy policy = workTimePolicyMap.get(policyUuid);
                        policySet.addPolicy(policy);
                    }
                }
            }
        }
        return policySet;
    }

    void save() {
        saveWorkTimePolicyList();
        saveWorkTimePolicySetList();
        saveSelectedPolicy();
    }

    private void saveSelectedPolicy() {
        FixWorkTimePolicy policy = getWorkTimePolicy();
        UUID uuid = policy.getUuid();
        if (uuid != null) {
            configSp.edit().putString(SELECTED_POLICY, uuid.toString()).apply();
        }
    }

    public void saveWorkTimePolicySetList() {
        Set<String> uuidSet = new HashSet<>();
        {
            for (WorkTimePolicySet policySet : workTimePolicySetList) {
                UUID uuid = policySet.getUuid();
                if (uuid == null) {
                    uuid = Generators.timeBasedGenerator().generate();
                    policySet.setUuid(uuid);
                }

                if (uuid != null) {
                    uuidSet.add(uuid.toString());
                    saveWorkTimePolicySet(policySet);
                }
            }
        }
        configSp.edit().putStringSet(NODE_POLICY_SET_LIST, uuidSet).apply();
    }

    private void saveWorkTimePolicySet(WorkTimePolicySet policySet) {
        UUID uuid = policySet.getUuid();
        if (uuid != null) {
            SharedPreferences sp = context.getSharedPreferences(uuid.toString(), Context.MODE_PRIVATE);
            sp.edit().putString("name", policySet.getName()).apply();
            Set<String> policyList = new HashSet<>();
            for (FixWorkTimePolicy policy : policySet.getWorkTimePolicyList()) {
                policyList.add(policy.getUuid().toString());
            }
            sp.edit().putStringSet(NODE_WORK_TIME_POLICY_LIST, policyList).apply();
        }
    }

    public void saveWorkTimePolicyList() {
        Set<String> uuidSet = new HashSet<>();

        for (FixWorkTimePolicy policy : workTimePolicyList) {
            saveWorkTimePolicy(policy);
            UUID uuid = policy.getUuid();
            if (uuid != null) {
                uuidSet.add(uuid.toString());
            }
        }

        configSp.edit().putStringSet(NODE_POLICY_LIST, uuidSet).apply();
    }

    private void saveWorkTimePolicy(FixWorkTimePolicy policy) {
        {
            UUID uuid = policy.getUuid();
            if (uuid == null) {
                uuid = Generators.timeBasedGenerator().generate();
                policy.setUuid(uuid);
            }

            SharedPreferences.Editor editor = context.getSharedPreferences(policy.getUuid().toString(),
                    Context.MODE_PRIVATE).edit();
            if (editor != null) {
                editor.putString("name", policy.getName()).apply();
                editor.putString("shortName", policy.getShortName()).apply();
                int type = 0;
                if (policy instanceof FlexWorkTimePolicy) {
                    type = 1;
                    editor.putLong("latestCheckIn", ((FlexWorkTimePolicy) policy).getLatestCheckInTime());
                }
                editor.putInt("type", type).apply();
                editor.putLong("checkIn", policy.getCheckInTime()).apply();
                editor.putLong("checkOut", policy.getCheckOutTime()).apply();
            }
        }
    }
}
