package org.fw.attendance;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sj.attendance.bl.FixWorkTimePolicy;
import com.sj.attendance.bl.FlexWorkTimePolicy;
import com.sj.attendance.bl.TimeUtils;
import com.sj.attendance.bl.WorkTimePolicySet;

public class WorkTimePolicySetEditor extends AppCompatActivity {
    private final String TAG = WorkTimePolicySetEditor.class.getSimpleName();

    private Button addPolicyButton;

    public class ACTION {
        public static final String ADD = "ACTION_ADD";
    }

    EditText policySetNameEditText;

    RecyclerView recyclerView;
    WorkTimePolicyAdapter2 adapter;
    RecyclerView.LayoutManager layoutManager;
    WorkTimePolicySet workTimePolicySet;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_time_policy_set_editor);

        Intent i = getIntent();
        if (ACTION.ADD.equals(i.getAction())) {
            workTimePolicySet = new WorkTimePolicySet(getString(R.string.no_title));
//            workTimePolicySet.addPolicy(new FixWorkTimePolicy(getString(R.string.dummy_policy_name),
//                    getString(R.string.dummy_policy_short_name),
//                    TimeUtils.AM_09, TimeUtils.PM_05 - TimeUtils.AM_09));
        } else {
            initData();
        }
        initViews();
    }

    private void initViews() {
        {
            policySetNameEditText = findViewById(R.id.et_work_time_policy_set_name);
            if (workTimePolicySet != null) {
                policySetNameEditText.setText(workTimePolicySet.getTitle());
            }
        }

        {
            recyclerView = findViewById(R.id.rv_policy_set);

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            recyclerView.setHasFixedSize(true);

            // use a linear layout manager
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);

            // specify an adapter (see also next example)
            adapter = new WorkTimePolicyAdapter2(workTimePolicySet.getWorkTimePolicyList());
            recyclerView.setAdapter(adapter);
            recyclerView.addItemDecoration(new MyItemDecoration());
        }

        addPolicyButton = findViewById(R.id.btn_add_policy);
        addPolicyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.equals(addPolicyButton)) {
                    final WorkTimePolicyEditDialog policyEditDialog = new WorkTimePolicyEditDialog(
                            WorkTimePolicySetEditor.this);
                    policyEditDialog.setTitle(R.string.policy_editor_title);
                    policyEditDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            FlexWorkTimePolicy policy = policyEditDialog.getPolicy();
                            Log.i(TAG, "" + policy);

                            if (!TextUtils.isEmpty(policy.getName())
                                    && !TextUtils.isEmpty(policy.getShortName())){
                                workTimePolicySet.addPolicy(policy);
                                adapter.updateData(workTimePolicySet.getWorkTimePolicyList());
                            }
                        }
                    });
                    policyEditDialog.show();
                }
            }
        });
    }

    private void initData() {
        workTimePolicySet = WorkTimePolicySetConfig.getInstance().getWorkTimePolicySet();
    }
}
