package org.fw.attendance;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sj.attendance.bl.FixWorkTimePolicy;

import java.util.List;

public class WorkTimePolicySetView extends ConstraintLayout {
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private WorkTimePolicyAdapter1 adapter;

    public WorkTimePolicySetView(Context context) {
        super(context);
        createRecyclerView(context);
    }

    private void createRecyclerView(Context context) {
        if (recyclerView == null) {
            recyclerView = new RecyclerView(context);
            initViews();
        }
    }

    public WorkTimePolicySetView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        createRecyclerView(context);
    }

    public WorkTimePolicySetView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createRecyclerView(context);
    }

    private void initViews() {
        if (recyclerView != null) {
            addView(recyclerView);
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            recyclerView.setHasFixedSize(true);

            // use a linear layout manager
            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);

            // specify an adapter (see also next example)
            adapter = new WorkTimePolicyAdapter1();
            recyclerView.setAdapter(adapter);
//            recyclerView.addItemDecoration(new MyItemDecoration());
        }
    }

    public void setPolicySetList(List<FixWorkTimePolicy> workTimePolicyList) {
        if (adapter != null) {
            adapter.updateData(workTimePolicyList);
        }
    }
}
