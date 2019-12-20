package org.fw.attendance;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sj.attendance.bl.WorkTimePolicySet;

public class WorkTimePolicySetEditor extends AppCompatActivity {
    EditText policySetNameEditText;

    RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    WorkTimePolicySet mWorkTimePolicySet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_time_policy_set_editor);

        initViews();
    }

    private void initViews() {
        initData();

        {
            policySetNameEditText = findViewById(R.id.et_work_time_policy_set_name);
            if (mWorkTimePolicySet != null) {
                policySetNameEditText.setText(mWorkTimePolicySet.getTitle());
            }
        }

        {
            recyclerView = findViewById(R.id.my_recycler_view);

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            recyclerView.setHasFixedSize(true);

            // use a linear layout manager
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);

            // specify an adapter (see also next example)
            mAdapter = new WorkTimePolicyAdapter(mWorkTimePolicySet.getWorkTimePolicyList());
            recyclerView.setAdapter(mAdapter);
            recyclerView.addItemDecoration(new MyItemDecoration());
        }
    }

    private void initData() {
        mWorkTimePolicySet = WorkTimePolicySetConfig.getInstance().getWorkTimePolicySet();
    }
}
