package org.fw.attendance.ui.clock;

import android.app.TimePickerDialog;
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
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import org.fw.attendance.R;
import org.fw.attendance.ResHelper;
import org.fw.attendance.WorkTimePolicySetConfig;
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

    private View root;
    private ClockViewModel clockViewModel;

    WorkTimePolicySetConfig config = WorkTimePolicySetConfig.getInstance();
    private WorkTimePolicySet workTimePolicySet;
    private List<FixWorkTimePolicy> workTimePolicyList;

    private TextView realCheckInTimeTv;
    private TextView realCheckOutTimeTv;
    private TextView planCheckOutTimeTv;
    private TextView checkInIssueTv;
    private TextView checkOutIssueTv;
    private int normalTextColor;
    private int issueTextColor;
    private Date realCheckInDate;
    private Date secondCheckInDate;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        initData();

        issueTextColor = getContext().getResources().getColor(android.R.color.holo_red_dark);
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

        TextView workTimePolicyTv = root.findViewById(R.id.tv_work_time_policy_value);
        workTimePolicyTv.setText(workTimePolicySet.getTitle());

        updateWorkTimePolicy(root);

        {
            realCheckInTimeTv = root.findViewById(R.id.tv_real_check_in_value);
            Button checkInButton = root.findViewById(R.id.btn_check_in);
            checkInButton.setOnClickListener(this);
        }
        planCheckOutTimeTv = root.findViewById(R.id.tv_plan_check_out_value);
        {
            realCheckOutTimeTv = root.findViewById(R.id.tv_real_check_out_value);
            Button checkOutButton = root.findViewById(R.id.btn_check_out);
            checkOutButton.setOnClickListener(this);
        }
        checkInIssueTv = root.findViewById(R.id.tv_check_in_issue_value);
        normalTextColor = checkInIssueTv.getCurrentTextColor();
        checkOutIssueTv = root.findViewById(R.id.tv_check_out_issue_value);
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

    /*
     * Function 自定义MyTimePickerDialog类，用于实现TimePickerDialog.OnTimeSetListener接口，当点击时间设置对话框中的“设置”按钮时触发该接口方法
     */
    public class MyTimePickerDialog implements TimePickerDialog.OnTimeSetListener {
        public void onTimeSet(TimePicker view, int hour, int minute) {
            secondCheckInDate = (Date) realCheckInDate.clone();
            secondCheckInDate.setHours(hour);
            secondCheckInDate.setMinutes(minute);

            onCheckIn(secondCheckInDate);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_check_in: {
                Date date = new Date();
                realCheckInDate = (Date) date.clone();
                secondCheckInDate = (Date) realCheckInDate.clone();
                onCheckIn(date);

                { // 打开修改时间对话框。
                    v.setVisibility(View.GONE);
                    Button button = v.getRootView().findViewById(R.id.btn_check_in_modify);
                    button.setVisibility(View.VISIBLE);
                    button.setOnClickListener(this);
                }

//                realCheckInDate = date;
            }
            break;

            case R.id.btn_check_in_modify: {
                MyTimePickerDialog myTimePickerDialog = new MyTimePickerDialog();
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        myTimePickerDialog, secondCheckInDate.getHours(),
                        secondCheckInDate.getMinutes(),
                        true);
                timePickerDialog.show();
            }
            break;

            case R.id.btn_check_out: {
                Date date = new Date();
                realCheckOutTimeTv.setText(DateTime.formatTime(date));
                FixWorkTimePolicy workTimePolicy = config.getWorkTimePolicy();
                boolean earlyLeave = workTimePolicy.isEarlyLeave(timeInMillisByDate(date));
                if (earlyLeave) {
                    Toast.makeText(getActivity(), R.string.early_leave, Toast.LENGTH_SHORT).show();

                    checkOutIssueTv.setTextColor(issueTextColor);
                    checkOutIssueTv.setText(R.string.early_leave);
                } else {
                    checkOutIssueTv.setTextColor(normalTextColor);
                }
            }
            break;
        }
    }

    private void onCheckIn(Date date) {
        realCheckInTimeTv.setText(DateTime.formatTime(date));

        FixWorkTimePolicy workTimePolicy = config.getWorkTimePolicy();
        boolean late = workTimePolicy.isLate(timeInMillisByDate(date));
        if (late) {
            Toast.makeText(getActivity(), R.string.late, Toast.LENGTH_SHORT).show();

            checkInIssueTv.setTextColor(issueTextColor);
            checkInIssueTv.setText(R.string.late);
        } else {
            checkInIssueTv.setTextColor(normalTextColor);
            checkInIssueTv.setText(R.string.normal);
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
        AlarmManagerUtil.setAlarm(this.getContext(), 0, planCheckOutTime, 0, 0, "该下班啦！！！！", 1);
    }
}