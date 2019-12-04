package com.gaomh.clock.ui.clock;

import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.gaomh.clock.R;
import com.gaomh.clock.WorkTimePolicySetConfig;
import com.loonggg.lib.alarmmanager.clock.AlarmManagerUtil;
import com.sj.attendance.bl.DateTime;
import com.sj.attendance.bl.FixWorkTimePolicy;
import com.sj.attendance.bl.FlexWorkTimePolicy;
import com.sj.attendance.bl.WorkTimePolicySet;

import java.util.Date;
import java.util.List;

import static com.sj.attendance.bl.DateTime.timeInMillisByDate;

public class ClockFragment extends Fragment implements View.OnClickListener {
    final String TAG = ClockFragment.class.getSimpleName();

    private ClockViewModel clockViewModel;
    private Button clockGoWorkTime;

    private WorkTimePolicySet workTimePolicySet;

    private FixWorkTimePolicy workTimePolicy;
    private List<FixWorkTimePolicy> workTimePolicyList;
    private int workTimePolicyIndex = -1;

    private TextView realCheckInTimeTv;
    private TextView planCheckOutTimeTv;
    private TextView lateTv;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        initData();

        clockViewModel =
                ViewModelProviders.of(this).get(ClockViewModel.class);
        root = inflater.inflate(R.layout.fragment_clock, container, false);
        initView(root);
        return root;
    }

    private void initView(final View root) {
        RadioGroup radioGroup = root.findViewById(R.id.rg_work_time_policy_set);
        if (!workTimePolicyList.isEmpty()) {
            for (FixWorkTimePolicy workTimePolicy : workTimePolicyList) {
                RadioButton radioButton = new RadioButton(root.getContext());
                radioButton.setTag(workTimePolicy);
                radioButton.setText(workTimePolicy.getTitle());
                radioGroup.addView(radioButton);
                if (workTimePolicy == workTimePolicyList.get(workTimePolicyIndex)) {
                    radioGroup.check(radioButton.getId());
                }
            }
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d(TAG, "onCheckedChanged(" + group + ", " + checkedId + ")");
                ClockFragment.this.workTimePolicyIndex = checkedId - 1;
                if (0 <= ClockFragment.this.workTimePolicyIndex
                        && ClockFragment.this.workTimePolicyIndex < ClockFragment.this.workTimePolicyList.size()) {
                    workTimePolicy = workTimePolicyList.get(workTimePolicyIndex);
                    updateWorkTimePolicy(root);
                }
            }
        });

        clockGoWorkTime = root.findViewById(R.id.clock_button_go_to_work);
        clockGoWorkTime.setOnClickListener(this);

        TextView workTimePolicyTv = root.findViewById(R.id.tv_work_time_policy);
        workTimePolicyTv.setText(workTimePolicySet.getTitle());

        updateWorkTimePolicy(root);

        realCheckInTimeTv = root.findViewById(R.id.real_check_in_time);
        planCheckOutTimeTv = root.findViewById(R.id.plan_check_out_time);

        lateTv = root.findViewById(R.id.is_late);
    }

    private void updateWorkTimePolicy(View root) {
        TextView checkInTv = root.findViewById(R.id.work_time_checkin);
        checkInTv.setText(DateTime.timeToString(workTimePolicy.getCheckInTime()));

        TextView latestCheckInTv = root.findViewById(R.id.work_time_latest_checkin);
        TextView checkOutTv = root.findViewById(R.id.work_time_checkout);

        if (workTimePolicy instanceof FlexWorkTimePolicy) {
            latestCheckInTv.setText(DateTime.timeToString(((FlexWorkTimePolicy) workTimePolicy).getLatestCheckInTime()));

            latestCheckInTv.setVisibility(View.VISIBLE);
            checkOutTv.setVisibility(View.GONE);
        } else {
            checkOutTv.setText(DateTime.timeToString(workTimePolicy.getCheckOutTime()));

            latestCheckInTv.setVisibility(View.GONE);
            checkOutTv.setVisibility(View.VISIBLE);
        }
    }

    private void initData() {
        workTimePolicySet = WorkTimePolicySetConfig.getInstance().getWorkTimePolicySet();
        workTimePolicyList = workTimePolicySet.getWorkTimePolicyList();
        if (!workTimePolicyList.isEmpty()) {
            workTimePolicyIndex = 0;
            workTimePolicy = workTimePolicyList.get(workTimePolicyIndex);
        }
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

                boolean late = workTimePolicy.isLate(timeInMillisByDate(date));
                if (late) {
                    Toast.makeText(getActivity(), R.string.late, Toast.LENGTH_SHORT).show();
                    lateTv.setText(R.string.late);
                }

                // 预计下班时间
                long planCheckOutTime = 0L;
                if (workTimePolicy instanceof FlexWorkTimePolicy) {
                    ((FlexWorkTimePolicy) workTimePolicy).setRealCheckInTime(date.getTime());
                    planCheckOutTime = workTimePolicy.getCheckOutTime();
                } else {
                    planCheckOutTime = DateTime.dayInMillisByDate(date) + workTimePolicy.getCheckOutTime();
                }
                planCheckOutTimeTv.setText(DateTime.formatTime(planCheckOutTime));
                AlarmManagerUtil.setAlarm(v.getContext(), 0, planCheckOutTime, 0, 0, "该下班啦！！！！", 1);

                break;
        }
    }
}