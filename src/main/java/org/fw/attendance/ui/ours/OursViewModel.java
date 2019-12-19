package org.fw.attendance.ui.ours;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OursViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public OursViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is ours fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
