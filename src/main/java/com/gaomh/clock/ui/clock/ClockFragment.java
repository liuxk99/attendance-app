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
import com.gaomh.clock.ResHelper;
import com.gaomh.clock.WorkTimePolicySetConfig;
import com.loonggg.lib.alarmmanager.clock.AlarmManagerUtil;
import com.sj.attendance.bl.DateTime;
import com.sj.attendance.bl.FixWorkTimePolicy;
import com.sj.attendance.bl.FlexWorkTimePolicy;
import com.sj.attendance.bl.WorkTimePolicySet;
import com.sj.lib.calander.CalendarFactory;
import com.sj.lib.calander.CalendarUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.sj.attendance.bl.DateTime.timeInMillisByDate;

public class ClockFragment extends Fragment implements View.OnClickListener {
    final String TAG = ClockFragment.class.getSimpleName();

    private ClockViewModel clockViewModel;
    private Button clockGoWorkTime;

    WorkTimePolicySetConfig config = WorkTimePolicySetConfig.getInstance();
    private WorkTimePolicySet workTimePolicySet;
    private List<FixWorkTimePolicy> workTimePolicyList;

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
        initDateInfo(root);

        RadioGroup radioGroup = root.findViewById(R.id.rg_work_time_policy_set);
        radioGroup.setOrientation(LinearLayout.HORIZONTAL);
        if (!workTimePolicyList.isEmpty()) {
            for (FixWorkTimePolicy policy : workTimePolicyList) {
                RadioButton radioButton = new RadioButton(root.getContext());
                radioButton.setTag(policy);
                radioButton.setText(policy.getTitle());
                radioGroup.addView(radioButton);
                if (policy == config.getWorkTimePolicy()) {
                    radioGroup.check(radioButton.getId());
                }
            }
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d(TAG, "onCheckedChanged(" + group + ", " + checkedId + ")");
                int radioButtonId = group.getCheckedRadioButtonId();
                FixWorkTimePolicy workTimePolicy = (FixWorkTimePolicy) group.findViewById(radioButtonId).getTag();
                config.setWorkTimePolicy(workTimePolicy);
                updateWorkTimePolicy(root);
            }
        });

        clockGoWorkTime = root.findViewById(R.id.btn_check_in);
        clockGoWorkTime.setOnClickListener(this);

        TextView workTimePolicyTv = root.findViewById(R.id.tv_work_time_policy_value);
        workTimePolicyTv.setText(workTimePolicySet.getTitle());

        updateWorkTimePolicy(root);

        realCheckInTimeTv = root.findViewById(R.id.tv_real_check_in_value);
        planCheckOutTimeTv = root.findViewById(R.id.tv_plan_check_out_time_value);

        lateTv = root.findViewById(R.id.is_late);
    }

    private void initDateInfo(View root) {
        Calendar calendar = Calendar.getInstance();

        TextView dateInfoTextView = root.findViewById(R.id.tv_date_info);
        if (dateInfoTextView != null) {
            int resId = ResHelper.getInstance().toWeekday(calendar.get(Calendar.DAY_OF_WEEK));
            String weekday = getString(resId);

            String date = String.format(Locale.getDefault(), "%04d/%02d/%02d %s",
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH),
                    weekday);
            dateInfoTextView.setText(date);
        }

        TextView dateCategory = root.findViewById(R.id.tv_date_category);
        if (dateCategory != null) {
            Calendar cal = CalendarUtils.genDate(calendar);
            boolean isWorkDay = CalendarFactory.getInstance().calendarMap.get(cal);
            if (isWorkDay) {
                dateCategory.setText(R.string.text_workday);
                dateCategory.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
            } else {
                dateCategory.setText(R.string.text_restday);
                dateCategory.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            }
        }
    }

    private void updateWorkTimePolicy(View root) {
        FixWorkTimePolicy workTimePolicy = config.getWorkTimePolicy();

        TextView checkInTv = root.findViewById(R.id.tv_work_time_policy_check_in_value);
        checkInTv.setText(DateTime.timeToString(workTimePolicy.getCheckInTime()));

        TextView latestCheckInTv = root.findViewById(R.id.tv_work_time_latest_check_in_label_value);
        TextView checkOutTv = root.findViewById(R.id.tv_work_time_policy_check_out_value);

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
        workTimePolicySet = config.getWorkTimePolicySet();
        workTimePolicyList = workTimePolicySet.getWorkTimePolicyList();

        FixWorkTimePolicy workTimePolicy = config.getWorkTimePolicy();
        if (workTimePolicy == null)
            if (!workTimePolicyList.isEmpty()) {
                config.setWorkTimePolicy(workTimePolicyList.get(0));
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
            case R.id.btn_check_in:
                Date date = new Date();
                realCheckInTimeTv.setText(DateTime.formatTime(date));
                FixWorkTimePolicy workTimePolicy = config.getWorkTimePolicy();
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