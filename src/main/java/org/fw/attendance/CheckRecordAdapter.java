package org.fw.attendance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.sj.attendance.bl.TimeUtils;

import java.util.List;

public class CheckRecordAdapter extends RecyclerView.Adapter<CheckRecordAdapter.CheckInOutRecordViewHolder> {
    private List<CheckRecordInfo> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class CheckInOutRecordViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView policySetName;
        private TextView policyName;
        private TextView checkInTime;
        private TextView realCheckInTime;
        private TextView checkInIssue;
        private TextView planCheckOutTime;
        private TextView realCheckOutTime;
        private TextView checkOutIssue;

        public CheckInOutRecordViewHolder(@NonNull View itemView,
                                          TextView policySetName,
                                          TextView policyName,
                                          TextView checkInTime,
                                          TextView realCheckInTime,
                                          TextView checkInIssue,
                                          TextView planCheckOutTime,
                                          TextView realCheckOutTime,
                                          TextView checkOutIssue) {
            super(itemView);
            this.policySetName = policySetName;
            this.policyName = policyName;
            this.checkInTime = checkInTime;
            this.realCheckInTime = realCheckInTime;
            this.checkInIssue = checkInIssue;
            this.planCheckOutTime = planCheckOutTime;
            this.realCheckOutTime = realCheckOutTime;
            this.checkOutIssue = checkOutIssue;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CheckRecordAdapter(List<CheckRecordInfo> workTimePolicyList) {
        mDataset = workTimePolicyList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CheckInOutRecordViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        ConstraintLayout layout = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.check_in_out_record_item, parent, false);

        TextView workTimePolicySetTitle = layout.findViewById(R.id.holder_tv_work_time_policy_set_title);
        TextView workTimePolicyTitle = layout.findViewById(R.id.holder_tv_work_time_policy_title);
        TextView checkInTime = layout.findViewById(R.id.holder_tv_check_in_time);
        TextView realCheckInTime = layout.findViewById(R.id.holder_tv_real_check_in_time);
        TextView checkInIssue = layout.findViewById(R.id.holder_tv_check_in_issue);
        TextView planCheckOutTime = layout.findViewById(R.id.holder_tv_plan_check_out_time);
        TextView realCheckOutTime = layout.findViewById(R.id.holder_tv_real_check_out_time);
        TextView checkOutIssue = layout.findViewById(R.id.holder_tv_check_out_issue);
        return new CheckInOutRecordViewHolder(layout,
                workTimePolicySetTitle,
                workTimePolicyTitle,
                checkInTime,
                realCheckInTime,
                checkInIssue,
                planCheckOutTime,
                realCheckOutTime,
                checkOutIssue);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CheckInOutRecordViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        CheckRecordInfo checkRecordInfo = mDataset.get(position);

        holder.policySetName.setText(checkRecordInfo.policySet.getName());
        holder.policyName.setText(checkRecordInfo.policy.getShortName());

        holder.checkInTime.setText(checkRecordInfo.policy.toCheckIn());
        if (checkRecordInfo.realCheckInTime != null) {
            holder.realCheckInTime.setText(TimeUtils.formatTime(checkRecordInfo.realCheckInTime));
        } else {
            holder.realCheckInTime.setText(R.string.time_placeholder);
        }
        holder.checkInIssue.setText(R.string.normal);

        holder.planCheckOutTime.setText(checkRecordInfo.policy.toCheckOut());
        if (checkRecordInfo.realCheckOutTime != null) {
            holder.realCheckOutTime.setText(TimeUtils.formatTime(checkRecordInfo.realCheckOutTime));
        } else {
            holder.realCheckOutTime.setText(R.string.time_placeholder);
        }
        holder.checkOutIssue.setText(R.string.normal);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}