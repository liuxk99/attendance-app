package com.gaomh.clock.ui.clock;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;


import com.gaomh.clock.R;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.sj.attendance.bl.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ClockFragment extends Fragment implements View.OnClickListener {

    private ClockViewModel clockViewModel;
    private Button clockGoWorkTime;
    private FixWorkTimePolicy time;
    private TextView goToWorkText;
    private TextView backWorkText;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        clockViewModel =
                ViewModelProviders.of(this).get(ClockViewModel.class);
        View root = inflater.inflate(R.layout.fragment_clock, container, false);
        init(root);
        return root;
    }

    private void init(View root) {
        clockGoWorkTime = root.findViewById(R.id.clock_button_go_to_work);
        goToWorkText = root.findViewById(R.id.clock_text_work_time);
        backWorkText = root.findViewById(R.id.clock_text_back_work_time);
        clockGoWorkTime.setOnClickListener(this);
        time = new StockFixWorkTimeFullDay();
    }

    private long getRealTime() {
        Date date = new Date();
        long nowTime = date.getHours() * DateTime.HOUR + date.getMinutes() * DateTime.MINUTE + date.getSeconds() * DateTime.SECOND;
        return nowTime;

    }

    private String goToWorkTime() {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = dateformat.format(System.currentTimeMillis());
        return dateStr;
    }

    private String backWorkTime() {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String dateStr = dateformat.format(System.currentTimeMillis() + 9 * DateTime.HOUR);
        return dateStr;
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
                boolean late = time.isLate(getRealTime());

                if (late) {
                    Toast.makeText(getActivity(), "恭喜你，迟到了。", Toast.LENGTH_SHORT).show();
                }
                goToWorkText.setText(goToWorkTime());
                backWorkText.setText(backWorkTime());
                //6.0权限处理
                Acp.getInstance(getActivity()).request(new AcpOptions.Builder().setPermissions(
                        Manifest.permission.SET_ALARM).build(), new AcpListener() {
                    @Override public void onGranted() {
                        createAlarm("下班了",20,00);
                    }


                    @Override public void onDenied(List<String> permissions) {

                    }
                });

                break;
        }
    }
}