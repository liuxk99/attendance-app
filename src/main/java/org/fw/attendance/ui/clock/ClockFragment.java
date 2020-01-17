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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.loonggg.lib.alarmmanager.clock.AlarmManagerUtil;
import com.sj.attendance.bl.CheckRecord;
import com.sj.attendance.bl.FixWorkTimePolicy;
import com.sj.attendance.bl.WorkTimePolicySet;
import com.sj.attendance.bl.WorkTimePolicySetConfig;
import com.sj.days.CalendarFactory;
import com.sj.time.CalendarUtils;
import com.sj.time.DateDub;
import com.sj.time.DateObserver;
import com.sj.time.DateStore;
import com.sj.time.DateTimeUtils;

import org.fw.attendance.Attendance;
import org.fw.attendance.CheckInOutAdapter;
import org.fw.attendance.ConfigPersist4A;
import org.fw.attendance.DateStore4A;
import org.fw.attendance.MyItemDecoration;
import org.fw.attendance.R;
import org.fw.attendance.ResHelper;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClockFragment extends Fragment implements View.OnClickListener {
    final String TAG = ClockFragment.class.getSimpleName();
    private SharedPreferences sp;
    private List<CheckRecord> infoList = new ArrayList<>();
    private CheckRecord todayRecord;
    private CheckInOutAdapter adapter;

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

    private TextView realCheckInTimeTv;
    private TextView realCheckOutTimeTv;
    private TextView planCheckOutTimeTv;
    private TextView checkInIssueTv;
    private TextView checkOutIssueTv;
    private int normalTextColor;
    private int issueTextColor;

    private Date checkInSnapshot;
    private DateStore checkInSnapshotStore;
    private DateDub realCheckInDub;

    private Date checkOutSnapshot;
    private DateStore checkOutSnapshotStore;
    private DateDub realCheckOutDub;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        initData();

        issueTextColor = getContext().getResources().getColor(android.R.color.holo_red_dark);
        clockViewModel =
                ViewModelProviders.of(this).get(ClockViewModel.class);
        root = inflater.inflate(R.layout.fragment_clock, container, false);
        initView(root);

        LoadDates(root);

        WorkTimePolicySetConfig config = ConfigPersist4A.getInstance().getWorkTimePolicySetConfig();
        CheckRecord checkRecord = Attendance.getInstance().findCheckRecord(config.getPolicy(), new Date());
        if (checkRecord != null) {
            todayRecord = checkRecord;
        } else {
            todayRecord = new CheckRecord(config.getPolicySet().getName(), realCheckInDub.getDate(), realCheckOutDub.getDate(), config.getPolicy());
        }
        infoList.add(todayRecord);
        adapter.updateData(infoList);

        return root;
    }

    private void initView(final View root) {
        initDayInfo(root);

        RadioGroup radioGroup = root.findViewById(R.id.rg_work_time_policy_set);
        radioGroup.setOrientation(LinearLayout.HORIZONTAL);

        WorkTimePolicySet policySet = ConfigPersist4A.getInstance().getWorkTimePolicySetConfig().getPolicySet();
        if (!policySet.getPolicyList().isEmpty()) {
            for (FixWorkTimePolicy policy : policySet.getPolicyList()) {
                RadioButton radioButton = new RadioButton(root.getContext());
                radioButton.setTag(policy);
                radioButton.setText(policy.getShortName());
                radioGroup.addView(radioButton);
                if (policy == policySet.getPolicy()) {
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
//                config.setPolicy(workTimePolicy);
                updateWorkTimePolicy(root);
            }
        });

        TextView policyTextView = root.findViewById(R.id.tv_work_time_policy_value);
        policyTextView.setText(policySet.getName());

        updateWorkTimePolicy(root);

        {
            realCheckInTimeTv = root.findViewById(R.id.tv_real_check_in_value);
            Button checkInButton = root.findViewById(R.id.btn_check_in_snapshot);
            checkInButton.setOnClickListener(this);
        }
        planCheckOutTimeTv = root.findViewById(R.id.tv_plan_check_out_value);
        {
            realCheckOutTimeTv = root.findViewById(R.id.tv_real_check_out_value);
            Button checkOutButton = root.findViewById(R.id.btn_check_out_snapshot);
            checkOutButton.setOnClickListener(this);
        }
        checkInIssueTv = root.findViewById(R.id.tv_check_in_issue_value);
        normalTextColor = checkInIssueTv.getCurrentTextColor();
        checkOutIssueTv = root.findViewById(R.id.tv_check_out_issue_value);

        {
            RecyclerView checkRecordRecyclerView = root.findViewById(R.id.rv_check_in_out_records);
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
//            checkRecordRecyclerView.setHasFixedSize(true);

            // use a linear layout manager
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            checkRecordRecyclerView.setLayoutManager(layoutManager);

            // specify an adapter (see also next example)
            adapter = new CheckInOutAdapter(infoList);
            checkRecordRecyclerView.setAdapter(adapter);
            checkRecordRecyclerView.addItemDecoration(new MyItemDecoration());
        }
    }

    private void LoadDates(View root) {
        sp = getContext().getSharedPreferences("check in-out", Context.MODE_PRIVATE);

        loadCheckInDate(root);
        loadCheckOutDate(root);
    }

    private void loadCheckOutDate(View root) {
        DateStore4A realCheckOutStore = new DateStore4A(sp, "realCheckOut");
        realCheckOutDub = new DateDub();
        realCheckOutDub.setStore(realCheckOutStore);
        realCheckOutDub.addObserver(new RealCheckOutDateObserver());

        checkOutSnapshotStore = new DateStore4A(sp, "checkOutSnapshot");
        try {
            checkOutSnapshot = checkOutSnapshotStore.load();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (checkOutSnapshot != null) {
            onClickCheckOut(root);
        }

        Date realCheckOut = checkOutSnapshot;
        if (realCheckOutDub.getDate() != null) {
            realCheckOut = realCheckOutDub.getDate();
        }
        realCheckOutDub.setDate(realCheckOut);
    }

    private void loadCheckInDate(View root) {
        realCheckInDub = new DateDub();
        realCheckInDub.setStore(new DateStore4A(sp, "realCheckIn"));
        realCheckInDub.addObserver(new RealCheckInDateObserver());

        checkInSnapshotStore = new DateStore4A(sp, "checkInSnapshot");
        try {
            checkInSnapshot = checkInSnapshotStore.load();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (checkInSnapshot != null) {
            onClickCheckIn(root);
        }

        Date realCheckIn = checkInSnapshot;
        if (realCheckInDub.getDate() != null) {
            realCheckIn = realCheckInDub.getDate();
        }
        realCheckInDub.setDate(realCheckIn);
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
            Calendar cal = CalendarUtils.getDate(calendar);
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
        FixWorkTimePolicy workTimePolicy = ConfigPersist4A.getInstance().getWorkTimePolicySetConfig().getPolicy();

        TextView checkInTv = root.findViewById(R.id.tv_policy_check_in_value);
        checkInTv.setText(workTimePolicy.toCheckIn());

        TextView checkOuTv = root.findViewById(R.id.tv_policy_check_out_value);
        checkOuTv.setText(workTimePolicy.toCheckOut());
    }

    private void initData() {
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
            Date realCheckInDate = (Date) checkInSnapshot.clone();
            realCheckInDate.setHours(hour);
            realCheckInDate.setMinutes(minute);

            realCheckInDub.setDate(realCheckInDate);
        }
    }

    /*
     * 实现TimePickerDialog.OnTimeSetListener接口，处理新的时间输入。
     */
    public class CheckOutTimePickerListener implements TimePickerDialog.OnTimeSetListener {
        public void onTimeSet(TimePicker view, int hour, int minute) {
            Date date = (Date) checkOutSnapshot.clone();
            date.setHours(hour);
            date.setMinutes(minute);

            realCheckOutDub.setDate(date);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_check_in_snapshot: {
                Date date = new Date();
                {
                    checkInSnapshot = (Date) date.clone();
                    checkInSnapshotStore.save(checkInSnapshot);
                    onClickCheckIn(v.getRootView());
                }

                realCheckInDub.setDate(date);
            }
            break;

            case R.id.btn_real_check_in: {
                Date realCheckInDate = realCheckInDub.getDate();
                CheckInTimePickerListener checkInTimePickerListener = new CheckInTimePickerListener();
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        checkInTimePickerListener, realCheckInDate.getHours(),
                        realCheckInDate.getMinutes(),
                        true);
                timePickerDialog.show();
            }
            break;

            case R.id.btn_check_out_snapshot: {
                Date date = new Date();
                {
                    checkOutSnapshot = (Date) date.clone();
                    checkOutSnapshotStore.save(checkOutSnapshot);
                    onClickCheckOut(v.getRootView());
                }

                realCheckOutDub.setDate(date);
            }
            break;

            case R.id.btn_real_check_out: {
                Date realCheckOutDate = realCheckOutDub.getDate();
                CheckOutTimePickerListener checkOutListener = new CheckOutTimePickerListener();
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        checkOutListener, realCheckOutDate.getHours(),
                        realCheckOutDate.getMinutes(),
                        true);
                timePickerDialog.show();
            }
            break;
        }
    }

    private void saveDate(String dateStr, Date date) {
        Log.i(TAG, "saveDate(" + dateStr + ", " + date + ")");
        sp.edit().putString(dateStr, DateTimeUtils.toISO8601(date)).apply();
    }

    private void onClickCheckOut(View root) {
        // 打开修改时间对话框
        Button v = root.findViewById(R.id.btn_check_out_snapshot);
        Button button = root.findViewById(R.id.btn_real_check_out);
        button.setOnClickListener(this);

        v.setVisibility(View.GONE);
        button.setVisibility(View.VISIBLE);

        adapter.notifyDataSetChanged();
    }

    private void onClickCheckIn(View root) {
        if (root != null) {
            Button checkInButton = root.findViewById(R.id.btn_check_in_snapshot);
            Button modifyCheckInButton = root.findViewById(R.id.btn_real_check_in);
            modifyCheckInButton.setOnClickListener(this);

            modifyCheckInButton.setVisibility(View.VISIBLE);
            checkInButton.setVisibility(View.GONE);

            adapter.notifyDataSetChanged();
        }
    }

    class RealCheckInDateObserver implements DateObserver {

        @Override
        public void onDateChanged(Date checkInDate) {
            Log.i(TAG, "RealCheckInDateObserver.onDateChanged(" + checkInDate + ")");
            if (checkInDate != null) {

                realCheckInTimeTv.setText(DateTimeUtils.formatTime(checkInDate));
                if (todayRecord != null) {
                    todayRecord.realCheckInTime = checkInDate;
                    adapter.notifyDataSetChanged();
                }

                FixWorkTimePolicy policy = ConfigPersist4A.getInstance().getWorkTimePolicySetConfig().getPolicy();
                boolean late = policy.isLate(checkInDate);
                if (late) {
                    Toast.makeText(ClockFragment.this.getActivity(), R.string.late, Toast.LENGTH_SHORT).show();

                    checkInIssueTv.setTextColor(issueTextColor);
                    checkInIssueTv.setText(R.string.late);
                } else {
                    checkInIssueTv.setTextColor(normalTextColor);
                    checkInIssueTv.setText(R.string.normal);
                }

                // 预计下班时间
                long planCheckOutTime = policy.getPlanCheckOutTime(checkInDate);
                planCheckOutTimeTv.setText(DateTimeUtils.formatTime(planCheckOutTime));
                AlarmManagerUtil.setAlarm(ClockFragment.this.getContext(), 0, planCheckOutTime, 0, 0, getString(R.string.checkout_time_up), 1);
            }
        }
    }

    private class RealCheckOutDateObserver implements DateObserver {
        @Override
        public void onDateChanged(Date date) {
            Log.i(TAG, "RealCheckOutDateObserver.onDateChanged(" + date + ")");

            if (date != null) {
                if (todayRecord != null) {
                    todayRecord.realCheckOutTime = date;

                    Attendance.getInstance().save(todayRecord);
                    adapter.notifyDataSetChanged();
                }

                realCheckOutTimeTv.setText(DateTimeUtils.formatTime(date));
                FixWorkTimePolicy workTimePolicy = ConfigPersist4A.getInstance().getWorkTimePolicySetConfig().getPolicy();
                boolean earlyLeave = workTimePolicy.isEarlyLeave(date);
                if (earlyLeave) {
                    Toast.makeText(getActivity(), R.string.early_leave, Toast.LENGTH_SHORT).show();

                    checkOutIssueTv.setTextColor(issueTextColor);
                    checkOutIssueTv.setText(R.string.early_leave);
                } else {
                    checkOutIssueTv.setTextColor(normalTextColor);
                    checkOutIssueTv.setText(R.string.normal);
                }
            }
        }
    }
}