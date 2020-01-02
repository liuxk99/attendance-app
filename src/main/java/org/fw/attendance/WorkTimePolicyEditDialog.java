package org.fw.attendance;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.sj.attendance.bl.TimeUtils;

public class WorkTimePolicyEditDialog extends AlertDialog implements View.OnClickListener {
    private final String TAG = WorkTimePolicyEditDialog.class.getSimpleName();
    private TextView checkInTextView;
    private TextView latestCheckInTextView;
    private TextView checkOutTextView;

    protected WorkTimePolicyEditDialog(Context context) {
        super(context);
    }

    protected WorkTimePolicyEditDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.work_time_policy_editor_dialog);

        EditText titleEditText = findViewById(R.id.ed_policy_title);

        Button checkInButton = findViewById(R.id.btn_modify_time_check_in);
        checkInButton.setOnClickListener(this);
        checkInTextView = findViewById(R.id.tv_check_in_value);

        Button latestCheckInButton = findViewById(R.id.btn_modify_time_latest_check_in);
        latestCheckInButton.setOnClickListener(this);
        latestCheckInTextView = findViewById(R.id.tv_latest_check_in_value);

        Button checkOutButton = findViewById(R.id.btn_modify_time_check_out);
        checkOutButton.setOnClickListener(this);
        checkOutTextView = findViewById(R.id.tv_check_out_value);

        updateView();
    }

    private void updateView() {
        checkInTextView.setText(TimeUtils.formatRefTime(checkInTime));
        latestCheckInTextView.setText(TimeUtils.formatRefTime(latestCheckInTime));
        checkOutTextView.setText(TimeUtils.formatRefTime(checkOutTime));
    }

    protected WorkTimePolicyEditDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_modify_time_check_in: {
                onClickTime(new CheckInTimePickerListener(), checkInTime);
            }
            break;
            case R.id.btn_modify_time_latest_check_in: {
                onClickTime(new LatestCheckInTimePickerListener(), latestCheckInTime);
            }
            break;
            case R.id.btn_modify_time_check_out: {
                onClickTime(new CheckOutTimePickerListener(), checkOutTime);
            }
            break;
            default:
                break;
        }
    }

    public class CheckOutTimePickerListener implements TimePickerDialog.OnTimeSetListener {
        public void onTimeSet(TimePicker view, int hour, int minute) {
            Log.i(TAG, "CheckOutTimePickerListener#onTimeSet(" + hour + ", " + minute + ")");

            long time = TimeUtils.compoundTime(hour, minute);
            if (time != checkOutTime) {
                checkOutTime = time;
                checkOutTextView.setText(TimeUtils.formatRefTime(checkOutTime));
            }
        }
    }

    public class LatestCheckInTimePickerListener implements TimePickerDialog.OnTimeSetListener {
        public void onTimeSet(TimePicker view, int hour, int minute) {
            Log.i(TAG, "LatestCheckInTimePickerListener#onTimeSet(" + hour + ", " + minute + ")");

            long time = TimeUtils.compoundTime(hour, minute);
            if (time != latestCheckInTime) {
                latestCheckInTime = time;
                latestCheckInTextView.setText(TimeUtils.formatRefTime(latestCheckInTime));
            }
        }
    }

    public class CheckInTimePickerListener implements TimePickerDialog.OnTimeSetListener {
        public void onTimeSet(TimePicker view, int hour, int minute) {
            Log.i(TAG, "CheckInTimePickerListener#onTimeSet(" + hour + ", " + minute + ")");

            long time = TimeUtils.compoundTime(hour, minute);
            if (time != checkInTime) {
                checkInTime = time;
                checkInTextView.setText(TimeUtils.formatRefTime(checkInTime));
            }
        }
    }

    private long checkInTime = TimeUtils.compoundTime(TimeUtils.DEF_CHECK_IN_HOUR, TimeUtils.DEF_CHECK_OUT_MIN);
    private long latestCheckInTime = TimeUtils.compoundTime(TimeUtils.DEF_LATEST_CHECK_IN_HOUR, TimeUtils.DEF_CHECK_OUT_MIN);
    private long checkOutTime = TimeUtils.compoundTime(TimeUtils.DEF_CHECK_OUT_HOUR, TimeUtils.DEF_CHECK_OUT_MIN);

    private void onClickTime(TimePickerDialog.OnTimeSetListener listener, long refTime) {
        LatestCheckInTimePickerListener checkInTimePickerListener = new LatestCheckInTimePickerListener();

        int hour = TimeUtils.getHour(refTime);
        int min = TimeUtils.getMinute(refTime);
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                listener, hour,
                min,
                true);
        timePickerDialog.show();
    }
}
