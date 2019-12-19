package org.fw.attendance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;


import com.sj.attendance.bl.DateTime;
import com.sj.attendance.bl.FixWorkTimePolicy;
import com.sj.attendance.bl.FlexWorkTimePolicy;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<FixWorkTimePolicy> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final Group latestCheckInLayout;

        // each data item is just a string in this case
        private TextView titleTv;
        private TextView checkInTextView;
        private TextView latestCheckInTextView;
        private TextView checkOutTextView;

        MyViewHolder(ConstraintLayout linearLayout, TextView titleTv, TextView checkInTextView, TextView checkOutTextView, TextView latestCheckInTextView, Group latestCheckInLayout) {
            super(linearLayout);
            this.titleTv = titleTv;
            this.checkInTextView = checkInTextView;
            this.checkOutTextView = checkOutTextView;
            if (latestCheckInTextView != null) {
                this.latestCheckInTextView = latestCheckInTextView;
            }
            this.latestCheckInLayout = latestCheckInLayout;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<FixWorkTimePolicy> workTimePolicyList) {
        mDataset = workTimePolicyList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        ConstraintLayout policyLayout = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.work_time_policy_set_item, parent, false);
        TextView titleTextView = policyLayout.findViewById(R.id.tv_policy_title_value);
        TextView checkInTextView = policyLayout.findViewById(R.id.tv_check_in_value);
        TextView latestCheckInTextView = policyLayout.findViewById(R.id.tv_latest_check_in_value);
        TextView checkOutTextView = policyLayout.findViewById(R.id.tv_check_out_value);
        Group latestCheckInGroup = policyLayout.findViewById(R.id.group_latest_check_in);
        return new MyViewHolder(policyLayout, titleTextView, checkInTextView, checkOutTextView, latestCheckInTextView, latestCheckInGroup);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        FixWorkTimePolicy workTimePolicy = mDataset.get(position);

        holder.titleTv.setText(workTimePolicy.getTitle());
        if (workTimePolicy instanceof FlexWorkTimePolicy) {
            holder.latestCheckInLayout.setVisibility(View.VISIBLE);
            {
                FlexWorkTimePolicy policy = (FlexWorkTimePolicy) workTimePolicy;
                long time = DateTime.compoundTime(policy.getLatestCheckInTime());
                holder.latestCheckInTextView.setText(DateTime.formatTime(time));
            }
        } else {
            holder.latestCheckInLayout.setVisibility(View.GONE);
        }
        {
            long checkInTime = workTimePolicy.getCheckInTime();
            long duration = workTimePolicy.getDuration();
            holder.checkInTextView.setText(DateTime.formatTime(DateTime.compoundTime(checkInTime)));
            holder.checkOutTextView.setText(DateTime.formatRefTime(checkInTime + duration));
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}