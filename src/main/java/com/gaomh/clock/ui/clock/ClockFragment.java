package com.gaomh.clock.ui.clock;

import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.gaomh.clock.R;
import com.loonggg.lib.alarmmanager.clock.AlarmManagerUtil;
import com.sj.attendance.bl.DateTime;
import com.sj.attendance.bl.FixWorkTimePolicy;
import com.sj.attendance.bl.WorkTimePolicyFactory;

import java.util.Date;
import java.util.List;

public class ClockFragment extends Fragment implements View.OnClickListener {

    private ClockViewModel clockViewModel;
    private Button clockGoWorkTime;

    private FixWorkTimePolicy workTimePolicy;
    private List<FixWorkTimePolicy> workTimePolicyList;

    private TextView realCheckInTimeTv;
    private TextView planCheckOutTimeTv;
    private TextView lateTv;
    private RadioGroup workTimePolicyGroup;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        initData();

        clockViewModel =
                ViewModelProviders.of(this).get(ClockViewModel.class);
        View root = inflater.inflate(R.layout.fragment_clock, container, false);
        initView(root);
        return root;
    }

    private void initView(View root) {
        initPoliciesView(root);

        clockGoWorkTime = root.findViewById(R.id.clock_button_go_to_work);
        clockGoWorkTime.setOnClickListener(this);

        TextView checkInTv = root.findViewById(R.id.work_time_checkin);
        checkInTv.setText(DateTime.timeToString(workTimePolicy.getCheckInTime()));

        TextView checkOutTv = root.findViewById(R.id.work_time_checkout);
        checkOutTv.setText(DateTime.timeToString(workTimePolicy.getCheckOutTime()));

        realCheckInTimeTv = root.findViewById(R.id.real_check_in_time);
        planCheckOutTimeTv = root.findViewById(R.id.plan_check_out_time);

        lateTv = root.findViewById(R.id.is_late);
    }

    private void initPoliciesView(View root) {
        Spinner spinner = (Spinner) root.findViewById(R.id.policies_spinner);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getContext(), android.R.layout.simple_spinner_item);
        for (FixWorkTimePolicy policy : workTimePolicyList) {
            adapter.add(policy.getName());
        }
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    private void initData() {
        workTimePolicyList = WorkTimePolicyFactory.createPolicies();
        workTimePolicy = workTimePolicyList.get(0);
    }

    public void createAlarm(String message, int hour, int minutes) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_MESSAGE, message)
                .putExtra(AlarmClock.EXTRA_HOUR, hour)
                .putExtra(AlarmClock.EXTRA_MINUTES, minutes)
                .putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clock_button_go_to_work:
                Date date = new Date();
                realCheckInTimeTv.setText(DateTime.formatTime(date));

                boolean late = workTimePolicy.isLate(DateTime.timeInMillisByDate(date));
                if (late) {
                    Toast.makeText(getActivity(), R.string.late, Toast.LENGTH_SHORT).show();
                    lateTv.setText(R.string.late);
                }

                long dayInMillis = DateTime.dayInMillisByDate(date);
                // 预计下班时间
                long planCheckOutTime = dayInMillis + workTimePolicy.getCheckOutTime();

                planCheckOutTimeTv.setText(DateTime.formatTime(planCheckOutTime));
                AlarmManagerUtil.setAlarm(getActivity(), 0, planCheckOutTime, 0, 0, "该下班啦！！！！", 1);

                break;
        }
    }
}