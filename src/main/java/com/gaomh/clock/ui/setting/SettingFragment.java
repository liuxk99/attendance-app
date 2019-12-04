package com.gaomh.clock.ui.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.gaomh.clock.R;
import com.gaomh.clock.WorkTimePolicySetConfig;
import com.sj.attendance.bl.WorkTimePolicySet;

import java.util.List;

public class SettingFragment extends Fragment {

    private SettingViewModel settingViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingViewModel =
                ViewModelProviders.of(this).get(SettingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_setting, container, false);

        initViews(root);
        return root;
    }

    private void initViews(View root) {
        initData();
        initPoliciesView(root);
    }

    private void initData() {

    }

    private void initPoliciesView(View root) {
        Spinner spinner = root.findViewById(R.id.policies_spinner);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getContext(), android.R.layout.simple_spinner_item);
        List<WorkTimePolicySet> workTimePolicySetList = WorkTimePolicySetConfig.getInstance().getWorkTimePolicySetList();
        for (WorkTimePolicySet workTimePolicySet : workTimePolicySetList) {
            adapter.add(workTimePolicySet.getTitle());
        }
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

}