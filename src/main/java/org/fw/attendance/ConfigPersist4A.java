package org.fw.attendance;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sj.attendance.bl.ConfigPersist;
import com.sj.attendance.bl.FixWorkTimePolicy;
import com.sj.attendance.bl.FlexWorkTimePolicy;
import com.sj.attendance.bl.PolicyDeserializerAdapter;
import com.sj.attendance.bl.WorkTimePolicySet;
import com.sj.attendance.bl.WorkTimePolicySetConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigPersist4A implements ConfigPersist {
    private final String TAG = getClass().getSimpleName();

    public static ConfigPersist4A getInstance() {
        if (instance == null){
            instance = new ConfigPersist4A();
        }
        return instance;
    }
    private static ConfigPersist4A instance;
    public static WorkTimePolicySetConfig workTimePolicySetConfig;

    private final String CONF_JSON = "conf.json";

    private File configFile;
    private Gson gsonTo;

    public void initialize(Context context) {
        gsonTo = new Gson();
        if (context != null) {
            configFile = new File(context.getFilesDir(), CONF_JSON);
        }
    }

    public WorkTimePolicySetConfig load() {
        WorkTimePolicySetConfig config = null;

        // from JSON file
        if (configFile.exists()) {
            try {
                FileReader fileReader = new FileReader(configFile);
                config = load(fileReader);
                workTimePolicySetConfig = config;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return config;
    }

    @Override
    public boolean save(WorkTimePolicySetConfig config) {
        // 1. Java object to JSON file
        FileWriter writer = null;
        try {
            writer = new FileWriter(configFile);
            save(writer, config);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void save(FileWriter writer, WorkTimePolicySetConfig config) {
        gsonTo.toJson(config, writer);
    }

    private WorkTimePolicySetConfig load(FileReader fileReader) {
        WorkTimePolicySetConfig config;
        {
            PolicyDeserializerAdapter deserializer = new PolicyDeserializerAdapter(FixWorkTimePolicy.TAG);

            // registering each Type into the Deserializer's HashMap (key-value pair),
            // where the key (String) must be carried by the object (you can find it in the BaseClass,
            // called "clazz")
            deserializer.registerClassType(FlexWorkTimePolicy.class.getSimpleName(), FlexWorkTimePolicy.class);
            deserializer.registerClassType(FixWorkTimePolicy.class.getSimpleName(), FixWorkTimePolicy.class);
            Gson gsonFrom = new GsonBuilder().registerTypeAdapter(FixWorkTimePolicy.class, deserializer).create();

            config = gsonFrom.fromJson(fileReader, WorkTimePolicySetConfig.class);

            int policySetIndex = config.getPolicySetIndex();
            config.setPolicySet(config.getPolicySetList().get(policySetIndex));
            for (WorkTimePolicySet policySet : config.getPolicySetList()) {
                int policyIndex = policySet.getIndex();
                policySet.setPolicy(policySet.getPolicyList().get(policyIndex));
            }
        }
        return config;
    }
}
