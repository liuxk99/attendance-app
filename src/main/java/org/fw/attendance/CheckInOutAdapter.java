package org.fw.attendance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.sj.attendance.bl.CheckRecord;
import com.sj.attendance.bl.FixWorkTimePolicy;
import com.sj.time.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

public class CheckInOutAdapter extends RecyclerView.Adapter<CheckInOutAdapter.CheckInOutRecordViewHolder> {
    private List<CheckRecord> dataSet = new ArrayList<>();

    public void updateData(List<CheckRecord> checkRecordList) {
        dataSet.clear();
        dataSet.addAll(checkRecordList);

        notifyDataSetChanged();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class CheckInOutRecordViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView policySetName;
        private TextView policyName;
        private TextView date;
        private TextView checkInTime;
        private TextView realCheckInTime;
        private TextView checkInIssue;
        private TextView planCheckOutTime;
        private TextView realCheckOutTime;
        private TextView checkOutIssue;

        public CheckInOutRecordViewHolder(@NonNull View itemView,
                                          TextView policySetName,
                                          TextView policyName,
                                          TextView date,
                                          TextView checkInTime,
                                          TextView realCheckInTime,
                                          TextView checkInIssue,
                                          TextView planCheckOutTime,
                                          TextView realCheckOutTime,
                                          TextView checkOutIssue) {
            super(itemView);
            this.policySetName = policySetName;
            this.policyName = policyName;
            this.date = date;
            this.checkInTime = checkInTime;
            this.realCheckInTime = realCheckInTime;
            this.checkInIssue = checkInIssue;
            this.planCheckOutTime = planCheckOutTime;
            this.realCheckOutTime = realCheckOutTime;
            this.checkOutIssue = checkOutIssue;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CheckInOutAdapter(List<CheckRecord> workTimePolicyList) {
        updateData(workTimePolicyList);
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
        TextView date = layout.findViewById(R.id.holder_tv_date);
        TextView checkInTime = layout.findViewById(R.id.holder_tv_check_in_time);
        TextView realCheckInTime = layout.findViewById(R.id.holder_tv_real_check_in_time);
        TextView checkInIssue = layout.findViewById(R.id.holder_tv_check_in_issue);
        TextView planCheckOutTime = layout.findViewById(R.id.holder_tv_plan_check_out_time);
        TextView realCheckOutTime = layout.findViewById(R.id.holder_tv_real_check_out_time);
        TextView checkOutIssue = layout.findViewById(R.id.holder_tv_check_out_issue);
        return new CheckInOutRecordViewHolder(layout,
                workTimePolicySetTitle,
                workTimePolicyTitle,
                date,
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
        CheckRecord checkRecord = dataSet.get(position);

        holder.policySetName.setText(checkRecord.policySetName);

        FixWorkTimePolicy policy = checkRecord.policy;
        if (policy != null) {
            holder.policyName.setText(policy.getShortName());
            holder.checkInTime.setText(policy.toCheckIn());
            holder.planCheckOutTime.setText(policy.toCheckOut());
        }

        holder.date.setText(DateTimeUtils.formatDate(checkRecord.realCheckInTime));
        if (checkRecord.realCheckInTime != null) {
            holder.realCheckInTime.setText(DateTimeUtils.formatTime(checkRecord.realCheckInTime));
        } else {
            holder.realCheckInTime.setText(R.string.time_placeholder);
        }

        int resId = checkRecord.isLate() ? R.string.late : R.string.normal;
        holder.checkInIssue.setText(resId);

        if (checkRecord.realCheckOutTime != null) {
            holder.realCheckOutTime.setText(DateTimeUtils.formatTime(checkRecord.realCheckOutTime));
        } else {
            holder.realCheckOutTime.setText(R.string.time_placeholder);
        }
        resId = checkRecord.isEarlyLeave() ? R.string.early_leave : R.string.normal;
        holder.checkOutIssue.setText(resId);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}