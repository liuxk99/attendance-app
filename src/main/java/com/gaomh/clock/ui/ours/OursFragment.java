package com.gaomh.clock.ui.ours;

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

import com.gaomh.clock.R;

public class OursFragment extends Fragment {

    private OursViewModel oursViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        oursViewModel =
                ViewModelProviders.of(this).get(OursViewModel.class);
        View root = inflater.inflate(R.layout.fragment_ours, container, false);
        final TextView textView = root.findViewById(R.id.text_ours);
        oursViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
