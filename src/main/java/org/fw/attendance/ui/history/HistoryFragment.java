package org.fw.attendance.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sj.attendance.bl.CheckRecord;
import org.fw.attendance.Attendance;

import org.fw.attendance.CheckInOutAdapter;
import org.fw.attendance.MyItemDecoration;
import org.fw.attendance.R;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private List<CheckRecord> checkRecordList = new ArrayList<>();
    private CheckInOutAdapter adapter;

    private HistoryViewModel historyViewModel;
    private TextView historyTextView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        historyViewModel =
                ViewModelProviders.of(this).get(HistoryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_history, container, false);
        historyTextView = root.findViewById(R.id.tv_history);
        historyViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                historyTextView.setText(s);
            }
        });

        initViews(root);

        initData();
        return root;
    }

    private void initData() {
        checkRecordList = Attendance.getInstance().getCheckRecordList();
        if (!checkRecordList.isEmpty()) {
            historyTextView.setVisibility(View.GONE);
        }
        adapter.updateData(checkRecordList);
    }

    private void initViews(View root) {
        RecyclerView checkRecordRecyclerView = root.findViewById(R.id.rv_check_records);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        checkRecordRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        checkRecordRecyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        adapter = new CheckInOutAdapter(checkRecordList);
        checkRecordRecyclerView.setAdapter(adapter);
        checkRecordRecyclerView.addItemDecoration(new MyItemDecoration());
    }

}