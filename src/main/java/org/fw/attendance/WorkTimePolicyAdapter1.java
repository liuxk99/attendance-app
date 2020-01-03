package org.fw.attendance;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.sj.attendance.bl.FixWorkTimePolicy;

import java.util.ArrayList;
import java.util.List;

public class WorkTimePolicyAdapter1 extends RecyclerView.Adapter<WorkTimePolicyAdapter1.PolicyViewHolder> {
    private List<FixWorkTimePolicy> dataSet = new ArrayList<>();

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class PolicyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView policyTitleTv;
        private TextView checkInTextView;
        private TextView checkOutTextView;

        PolicyViewHolder(ConstraintLayout linearLayout,
                         TextView policyTitleTv,
                         TextView checkInTextView,
                         TextView checkOutTextView) {
            super(linearLayout);
            this.policyTitleTv = policyTitleTv;
            this.checkInTextView = checkInTextView;
            this.checkOutTextView = checkOutTextView;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    void updateData(List<FixWorkTimePolicy> workTimePolicyList) {
        dataSet.clear();
        dataSet.addAll(workTimePolicyList);

        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PolicyViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        // create a new view
        ConstraintLayout policyLayout = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.work_time_policy_item_1, parent, false);
        TextView titleTextView = policyLayout.findViewById(R.id.tv_policy_set_title_value);
        TextView checkInTextView = policyLayout.findViewById(R.id.tv_policy_check_in_value);
        TextView checkOutTextView = policyLayout.findViewById(R.id.tv_policy_check_out_value);
        return new PolicyViewHolder(policyLayout, titleTextView, checkInTextView, checkOutTextView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(PolicyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        FixWorkTimePolicy workTimePolicy = dataSet.get(position);

        holder.policyTitleTv.setText(workTimePolicy.getShortName());
        holder.checkInTextView.setText(workTimePolicy.toCheckIn());
        holder.checkOutTextView.setText(workTimePolicy.toCheckOut());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}