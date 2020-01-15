package org.fw.attendance.ui.me;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sj.attendance.bl.ConfigPersist;
import com.sj.attendance.bl.WorkTimePolicySetConfig;

import org.fw.attendance.ConfigPersist4A;
import org.fw.attendance.MyItemDecoration;
import org.fw.attendance.R;
import org.fw.attendance.WorkTimePolicySetAdapter;

import org.fw.attendance.WorkTimePolicySetEditor;

public class MeFragment extends Fragment {

    private MeViewModel meViewModel;
    private RecyclerView workTimePolicySetListView;
    private LinearLayoutManager layoutManager;
    private WorkTimePolicySetAdapter mAdapter;
    private Button addPolicySetButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        meViewModel =
                ViewModelProviders.of(this).get(MeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_me, container, false);

        initViews(root);
        return root;
    }

    private void initViews(View root) {
        initData();
        initPolicySetListView(root);

        initAddPolicySetButton(root);
    }

    private void initAddPolicySetButton(View root) {
        addPolicySetButton = root.findViewById(R.id.btn_add_policy_set);
        if (addPolicySetButton != null){
            addPolicySetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(WorkTimePolicySetEditor.ACTION.ADD);
                    intent.setClass(MeFragment.this.getActivity(), WorkTimePolicySetEditor.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void initData() {

    }

    private void initPolicySetListView(View root) {
        workTimePolicySetListView = root.findViewById(R.id.rv_policy_set_list);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        workTimePolicySetListView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        workTimePolicySetListView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        WorkTimePolicySetConfig config = ConfigPersist4A.getInstance().getWorkTimePolicySetConfig();
        mAdapter = new WorkTimePolicySetAdapter(config.getPolicySetList());
        workTimePolicySetListView.setAdapter(mAdapter);
        workTimePolicySetListView.addItemDecoration(new MyItemDecoration());
    }
}