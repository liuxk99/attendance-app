package org.fw.attendance.ui.clock;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.loonggg.lib.alarmmanager.clock.AlarmManagerUtil;
import com.sj.attendance.bl.FixWorkTimePolicy;
import com.sj.attendance.bl.FlexWorkTimePolicy;
import com.sj.attendance.bl.TimeUtils;
import com.sj.attendance.bl.WorkTimePolicySet;
import com.sj.lib.calander.CalendarFactory;
import com.sj.lib.calander.CalendarUtils;

import org.fw.attendance.R;
import org.fw.attendance.ResHelper;
import org.fw.attendance.WorkTimePolicySetConfig;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClockFragment extends Fragment implements View.OnClickListener {
    final String TAG = ClockFragment.class.getSimpleName();
    private SharedPreferences sp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreate(" + savedInstanceState + ")");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.i(TAG, "onSaveInstanceState(" + outState + ")");
        super.onSaveInstanceState(outState);
    }

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
    private Date modifiedCheckInDate;

    private Date realCheckOutDate;
    private Date modifiedCheckOutDate;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        initData();

        issueTextColor = getContext().getResources().getColor(android.R.color.holo_red_dark);
        clockViewModel =
                ViewModelProviders.of(this).get(ClockViewModel.class);
        root = inflater.inflate(R.layout.fragment_clock, container, false);
        initView(root);

        sp = getContext().getSharedPreferences("check in-out", Context.MODE_PRIVATE);
        LoadDates();
        return root;
    }

    private void initView(final View root) {
        initDayInfo(root);

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

    private void LoadDates() {
        Date now = new Date();

        String realCheckInDateStr = sp.getString("realCheckInDate", "");
        if (!realCheckInDateStr.isEmpty()) {
            try {
                Date date = TimeUtils.fromISO8601(realCheckInDateStr);
                if (TimeUtils.isSameDay(date, now)) {
                    this.realCheckInDate = date;
                    onClickCheckIn(root);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        String modifiedCheckInDateStr = sp.getString("modifiedCheckInDate", "");
        if (!modifiedCheckInDateStr.isEmpty()) {
            try {
                Date date = TimeUtils.fromISO8601(modifiedCheckInDateStr);
                if (TimeUtils.isSameDay(date, now)) {
                    this.modifiedCheckInDate = date;
                    onModifyCheckIn(modifiedCheckInDate);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        String realCheckOutDateStr = sp.getString("realCheckOutDate", "");
        if (!realCheckOutDateStr.isEmpty()) {
            try {
                Date date = TimeUtils.fromISO8601(realCheckOutDateStr);
                if (TimeUtils.isSameDay(date, now)) {
                    realCheckOutDate = date;
                    onClickCheckOut(root);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        String modifiedCheckOutDateStr = sp.getString("modifiedCheckOutDate", "");
        if (!modifiedCheckOutDateStr.isEmpty()) {
            try {
                Date date = TimeUtils.fromISO8601(modifiedCheckOutDateStr);
                if (TimeUtils.isSameDay(date, now)) {
                    this.modifiedCheckOutDate = date;

                    onModifyCheckOut(modifiedCheckOutDate);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void initDayInfo(View root) {
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
        checkInTv.setText(workTimePolicy.toCheckIn());

        TextView checkOuTv = root.findViewById(R.id.tv_work_time_policy_check_out_value);
        checkOuTv.setText(workTimePolicy.toCheckOut());
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
     * 实现TimePickerDialog.OnTimeSetListener接口，处理新的时间输入。
     */
    public class CheckInTimePickerListener implements TimePickerDialog.OnTimeSetListener {
        public void onTimeSet(TimePicker view, int hour, int minute) {
            modifiedCheckInDate = (Date) realCheckInDate.clone();
            modifiedCheckInDate.setHours(hour);
            modifiedCheckInDate.setMinutes(minute);

            saveDate("modifiedCheckInDate", modifiedCheckInDate);
            onModifyCheckIn(modifiedCheckInDate);
        }
    }

    /*
     * 实现TimePickerDialog.OnTimeSetListener接口，处理新的时间输入。
     */
    public class CheckOutTimePickerListener implements TimePickerDialog.OnTimeSetListener {
        public void onTimeSet(TimePicker view, int hour, int minute) {
            modifiedCheckOutDate = (Date) realCheckOutDate.clone();
            modifiedCheckOutDate.setHours(hour);
            modifiedCheckOutDate.setMinutes(minute);

            saveDate("modifiedCheckOutDate", modifiedCheckOutDate);
            onModifyCheckOut(modifiedCheckOutDate);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_check_in: {
                Date date = new Date();
                realCheckInDate = (Date) date.clone();
                saveDate("realCheckInDate", realCheckInDate);

                modifiedCheckInDate = (Date) date.clone();
                saveDate("modifiedCheckInDate", modifiedCheckInDate);

                onModifyCheckIn(modifiedCheckInDate);
                onClickCheckIn(v.getRootView());
            }
            break;

            case R.id.btn_check_in_modify: {
                CheckInTimePickerListener checkInTimePickerListener = new CheckInTimePickerListener();
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        checkInTimePickerListener, modifiedCheckInDate.getHours(),
                        modifiedCheckInDate.getMinutes(),
                        true);
                timePickerDialog.show();
            }
            break;

            case R.id.btn_check_out: {
                Date date = new Date();
                realCheckOutDate = (Date) date.clone();
                saveDate("realCheckOutDate", realCheckOutDate);

                modifiedCheckOutDate = (Date) realCheckOutDate.clone();
                saveDate("modifiedCheckOutDate", modifiedCheckOutDate);

                onModifyCheckOut(modifiedCheckOutDate);
                onClickCheckOut(v.getRootView());
            }
            break;

            case R.id.btn_check_out_modify: {
                CheckOutTimePickerListener checkOutListener = new CheckOutTimePickerListener();
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        checkOutListener, modifiedCheckOutDate.getHours(),
                        modifiedCheckOutDate.getMinutes(),
                        true);
                timePickerDialog.show();
            }
            break;
        }
    }

    private void saveDate(String dateStr, Date date) {
        Log.i(TAG, "saveDate(" + dateStr + ", " + date + ")");
        sp.edit().putString(dateStr, TimeUtils.toISO8601(date)).apply();
    }

    private void onClickCheckOut(View root) {
        // 打开修改时间对话框
        Button v = root.findViewById(R.id.btn_check_out);
        Button button = root.findViewById(R.id.btn_check_out_modify);
        button.setOnClickListener(this);

        v.setVisibility(View.GONE);
        button.setVisibility(View.VISIBLE);
    }

    private void onClickCheckIn(View root) {
        // 打开修改时间对话框。
        Button checkInButton = root.findViewById(R.id.btn_check_in);
        Button modifyCheckInButton = root.findViewById(R.id.btn_check_in_modify);
        modifyCheckInButton.setOnClickListener(this);

        modifyCheckInButton.setVisibility(View.VISIBLE);
        checkInButton.setVisibility(View.GONE);
    }

    private void onModifyCheckOut(Date checkOutDate) {
        Log.i(TAG, "onModifyCheckOut(" + checkOutDate + ")");

        realCheckOutTimeTv.setText(TimeUtils.formatTime(checkOutDate));
        FixWorkTimePolicy workTimePolicy = config.getWorkTimePolicy();
        boolean earlyLeave = workTimePolicy.isEarlyLeave(checkOutDate);
        if (earlyLeave) {
            Toast.makeText(getActivity(), R.string.early_leave, Toast.LENGTH_SHORT).show();

            checkOutIssueTv.setTextColor(issueTextColor);
            checkOutIssueTv.setText(R.string.early_leave);
        } else {
            checkOutIssueTv.setTextColor(normalTextColor);
            checkOutIssueTv.setText(R.string.normal);
        }
    }

    private void onModifyCheckIn(Date checkInDate) {
        realCheckInTimeTv.setText(TimeUtils.formatTime(checkInDate));

        FixWorkTimePolicy workTimePolicy = config.getWorkTimePolicy();
        boolean late = workTimePolicy.isLate(checkInDate);
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
            ((FlexWorkTimePolicy) workTimePolicy).setRealCheckInTime(checkInDate);
        }
        planCheckOutTime = TimeUtils.getDayDate(checkInDate) + workTimePolicy.getCheckOutTime();
        planCheckOutTimeTv.setText(TimeUtils.formatTime(planCheckOutTime));
        AlarmManagerUtil.setAlarm(this.getContext(), 0, planCheckOutTime, 0, 0, getString(R.string.checkout_time_up), 1);
    }
}