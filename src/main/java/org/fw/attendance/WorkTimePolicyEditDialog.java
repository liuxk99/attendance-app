package org.fw.attendance;

import android.app.AlertDialog;
import android.content.Context;

public class WorkTimePolicyEditDialog extends AlertDialog {
    protected WorkTimePolicyEditDialog(Context context) {
        super(context);
    }

    protected WorkTimePolicyEditDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected WorkTimePolicyEditDialog(Context context, int themeResId) {
        super(context, themeResId);
    }
}
