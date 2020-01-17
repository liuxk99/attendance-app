package org.fw.attendance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;

import com.sj.attendance.bl.FixWorkTimePolicy;
import com.sj.attendance.bl.FlexWorkTimePolicy;
import com.sj.time.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

public class WorkTimePolicyAdapter2 extends RecyclerView.Adapter<WorkTimePolicyAdapter2.MyViewHolder> {
    private List<FixWorkTimePolicy> dataSet = new ArrayList<>();

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final Group latestCheckInLayout;

        // each data item is just a string in this case
        private TextView nameTextView;
        private final TextView shortNameTextView;

        private TextView checkInTextView;
        private TextView latestCheckInTextView;
        private TextView checkOutTextView;

        MyViewHolder(ConstraintLayout linearLayout,
                     TextView nameTextView, TextView shortNameTextView,
                     TextView checkInTextView,
                     TextView checkOutTextView,
                     TextView latestCheckInTextView,
                     Group latestCheckInLayout) {
            super(linearLayout);
            this.nameTextView = nameTextView;
            this.shortNameTextView = shortNameTextView;
            this.checkInTextView = checkInTextView;
            this.checkOutTextView = checkOutTextView;
            if (latestCheckInTextView != null) {
                this.latestCheckInTextView = latestCheckInTextView;
            }
            this.latestCheckInLayout = latestCheckInLayout;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    WorkTimePolicyAdapter2(List<FixWorkTimePolicy> workTimePolicyList) {
        updateData(workTimePolicyList);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public WorkTimePolicyAdapter2.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                  int viewType) {
        // create a new view
        ConstraintLayout policyLayout = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.work_time_policy_item_2, parent, false);
        TextView nameTextView = policyLayout.findViewById(R.id.tv_policy_name_value);
        TextView shortNameTextView = policyLayout.findViewById(R.id.tv_policy_short_name_value);
        TextView checkInTextView = policyLayout.findViewById(R.id.tv_check_in_value);
        TextView latestCheckInTextView = policyLayout.findViewById(R.id.tv_latest_check_in_value);
        TextView checkOutTextView = policyLayout.findViewById(R.id.tv_check_out_value);
        Group latestCheckInGroup = policyLayout.findViewById(R.id.group_latest_check_in);
        return new MyViewHolder(policyLayout, nameTextView, shortNameTextView,
                checkInTextView, checkOutTextView, latestCheckInTextView,
                latestCheckInGroup);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        FixWorkTimePolicy workTimePolicy = dataSet.get(position);

        holder.nameTextView.setText(workTimePolicy.getName());
        holder.shortNameTextView.setText(workTimePolicy.getShortName());
        if (workTimePolicy instanceof FlexWorkTimePolicy) {
            holder.latestCheckInLayout.setVisibility(View.VISIBLE);
            {
                FlexWorkTimePolicy policy = (FlexWorkTimePolicy) workTimePolicy;
                long time = DateTimeUtils.compoundTime(policy.getLatestCheckInTime());
                holder.latestCheckInTextView.setText(DateTimeUtils.formatTime(time));
            }
        } else {
            holder.latestCheckInLayout.setVisibility(View.GONE);
        }
        {
            long checkInTime = workTimePolicy.getCheckInTime();
            long duration = workTimePolicy.getDuration();
            holder.checkInTextView.setText(DateTimeUtils.formatTime(DateTimeUtils.compoundTime(checkInTime)));
            holder.checkOutTextView.setText(DateTimeUtils.formatRefTime(checkInTime + duration));
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    void updateData(List<FixWorkTimePolicy> workTimePolicyList) {
        dataSet.clear();
        dataSet.addAll(workTimePolicyList);

        notifyDataSetChanged();
    }
}