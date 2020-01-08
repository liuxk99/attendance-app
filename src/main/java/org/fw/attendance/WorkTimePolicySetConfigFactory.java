package org.fw.attendance;

public class WorkTimePolicySetConfigFactory {
    static WorkTimePolicySetConfig instance;

    public static WorkTimePolicySetConfig getInstance() {
        if (instance == null) {
            instance = new WorkTimePolicySetConfig4A();
        }
        return instance;
    }
}
