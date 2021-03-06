package org.fw.attendance;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.sj.attendance.bl.FlexWorkTimePolicy;
import com.sj.attendance.bl.StockWorkTime;
import com.sj.time.DateTimeUtils;

public class WorkTimePolicyEditDialog extends AlertDialog implements View.OnClickListener {
    private final String TAG = WorkTimePolicyEditDialog.class.getSimpleName();

    private EditText nameEditText;
    private EditText shortNameEditText;
    private TextView checkInTextView;
    private TextView latestCheckInTextView;
    private TextView checkOutTextView;

    WorkTimePolicyEditDialog(Context context) {
        super(context);
    }

    WorkTimePolicyEditDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.work_time_policy_editor_dialog);

        nameEditText = findViewById(R.id.ed_policy_name);
        shortNameEditText = findViewById(R.id.ed_policy_short_name);

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
        checkInTextView.setText(DateTimeUtils.formatRefTime(checkInTime));
        latestCheckInTextView.setText(DateTimeUtils.formatRefTime(latestCheckInTime));
        checkOutTextView.setText(DateTimeUtils.formatRefTime(checkOutTime));
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

            long time = DateTimeUtils.compoundTime(hour, minute);
            if (time != checkOutTime) {
                checkOutTime = time;
                checkOutTextView.setText(DateTimeUtils.formatRefTime(checkOutTime));
            }
        }
    }

    public class LatestCheckInTimePickerListener implements TimePickerDialog.OnTimeSetListener {
        public void onTimeSet(TimePicker view, int hour, int minute) {
            Log.i(TAG, "LatestCheckInTimePickerListener#onTimeSet(" + hour + ", " + minute + ")");

            long time = DateTimeUtils.compoundTime(hour, minute);
            if (time != latestCheckInTime) {
                latestCheckInTime = time;
                latestCheckInTextView.setText(DateTimeUtils.formatRefTime(latestCheckInTime));
            }
        }
    }

    public class CheckInTimePickerListener implements TimePickerDialog.OnTimeSetListener {
        public void onTimeSet(TimePicker view, int hour, int minute) {
            Log.i(TAG, "CheckInTimePickerListener#onTimeSet(" + hour + ", " + minute + ")");

            long time = DateTimeUtils.compoundTime(hour, minute);
            if (time != checkInTime) {
                checkInTime = time;
                checkInTextView.setText(DateTimeUtils.formatRefTime(checkInTime));
            }
        }
    }

    private long checkInTime = DateTimeUtils.compoundTime(StockWorkTime.DEF_CHECK_IN_HOUR, StockWorkTime.DEF_CHECK_OUT_MIN);
    private long latestCheckInTime = DateTimeUtils.compoundTime(StockWorkTime.DEF_LATEST_CHECK_IN_HOUR, StockWorkTime.DEF_CHECK_OUT_MIN);
    private long checkOutTime = DateTimeUtils.compoundTime(StockWorkTime.DEF_CHECK_OUT_HOUR, StockWorkTime.DEF_CHECK_OUT_MIN);

    private void onClickTime(TimePickerDialog.OnTimeSetListener listener, long refTime) {
        LatestCheckInTimePickerListener checkInTimePickerListener = new LatestCheckInTimePickerListener();

        int hour = DateTimeUtils.getHour(refTime);
        int min = DateTimeUtils.getMinute(refTime);
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                listener, hour,
                min,
                true);
        timePickerDialog.show();
    }

    FlexWorkTimePolicy getPolicy() {
        Editable name = nameEditText.getText();
        Editable shortName = shortNameEditText.getText();
        return new FlexWorkTimePolicy(name.toString(), shortName.toString(), checkInTime, latestCheckInTime, checkOutTime);
    }
}
