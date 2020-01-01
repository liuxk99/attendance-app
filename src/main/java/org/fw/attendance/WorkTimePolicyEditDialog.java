package org.fw.attendance;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import com.sj.attendance.bl.TimeUtils;

public class WorkTimePolicyEditDialog extends AlertDialog implements View.OnClickListener {
    final String TAG = WorkTimePolicyEditDialog.class.getSimpleName();

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

        Button checkInButton = findViewById(R.id.btn_modify_time_check_in);
        checkInButton.setOnClickListener(this);
        Button latestCheckInButton = findViewById(R.id.btn_modify_time_latest_check_in);
        latestCheckInButton.setOnClickListener(this);
        Button checkOutButton = findViewById(R.id.btn_modify_time_check_out);
        checkOutButton.setOnClickListener(this);
    }

    protected WorkTimePolicyEditDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_modify_time_check_in: {
                onClickCheckIn();
            }
            break;
            case R.id.btn_modify_time_latest_check_in: {
                onClickLatestCheckIn();
            }
            break;
            case R.id.btn_modify_time_check_out: {
                onClickCheckOut();
            }
            break;
            default:
                break;
        }
    }

    /*
     * 实现TimePickerDialog.OnTimeSetListener接口，处理新的时间输入。
     */
    public class CheckOutTimePickerListener implements TimePickerDialog.OnTimeSetListener {
        public void onTimeSet(TimePicker view, int hour, int minute) {
            Log.i(TAG, "CheckOutTimePickerListener#onTimeSet(" + hour + ", " + minute + ")");
        }
    }

    private void onClickCheckOut() {
        CheckInTimePickerListener checkInTimePickerListener = new CheckInTimePickerListener();
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                checkInTimePickerListener, 18,
                0,
                true);
        timePickerDialog.show();
    }

    /*
     * 实现TimePickerDialog.OnTimeSetListener接口，处理新的时间输入。
     */
    public class LatestCheckInTimePickerListener implements TimePickerDialog.OnTimeSetListener {
        public void onTimeSet(TimePicker view, int hour, int minute) {
            Log.i(TAG, "LatestCheckInTimePickerListener#onTimeSet(" + hour + ", " + minute + ")");
        }
    }

    private void onClickLatestCheckIn() {
        CheckInTimePickerListener checkInTimePickerListener = new CheckInTimePickerListener();
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                checkInTimePickerListener, 10,
                0,
                true);
        timePickerDialog.show();
    }

    public class CheckInTimePickerListener implements TimePickerDialog.OnTimeSetListener {
        public void onTimeSet(TimePicker view, int hour, int minute) {
            Log.i(TAG, "CheckInTimePickerListener#onTimeSet(" + hour + ", " + minute + ")");
        }
    }

    private void onClickCheckIn() {
        CheckInTimePickerListener checkInTimePickerListener = new CheckInTimePickerListener();
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                checkInTimePickerListener, 9,
                0,
                true);
        timePickerDialog.show();

    }
}
