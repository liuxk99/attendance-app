package org.fw.attendance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.sj.attendance.bl.WorkTimePolicySet;

import java.util.List;

public class WorkTimePolicySetAdapter extends RecyclerView.Adapter<WorkTimePolicySetAdapter.MyViewHolder> {
    private List<WorkTimePolicySet> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView policyTitleTv;
        private WorkTimePolicySetView workTimePolicySetView;

        MyViewHolder(ConstraintLayout linearLayout,
                     TextView policyTitleTv, WorkTimePolicySetView workTimePolicySetView) {
            super(linearLayout);
            this.policyTitleTv = policyTitleTv;
            this.workTimePolicySetView = workTimePolicySetView;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public WorkTimePolicySetAdapter(List<WorkTimePolicySet> workTimePolicyList) {
        mDataset = workTimePolicyList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public WorkTimePolicySetAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                    int viewType) {
        // create a new view
        ConstraintLayout policyLayout = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.work_time_policy_set_item, parent, false);
        TextView titleTextView = policyLayout.findViewById(R.id.tv_policy_set_title_value);
        WorkTimePolicySetView workTimePolicySetView = null;
        View view = policyLayout.findViewById(R.id.rv_policy_set_view);
        if (view instanceof  WorkTimePolicySetView) {
            workTimePolicySetView = (WorkTimePolicySetView) view;
        }
        return new MyViewHolder(policyLayout, titleTextView, workTimePolicySetView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        WorkTimePolicySet policySet = mDataset.get(position);

        holder.policyTitleTv.setText(policySet.getTitle());
        if (holder.workTimePolicySetView != null) {
            holder.workTimePolicySetView.setPolicySetList(policySet.getWorkTimePolicyList());
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}