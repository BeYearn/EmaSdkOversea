package com.emagroup.oversea.sdk;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by beyearn on 2018/3/1.
 */

public class EmaProgress extends ProgressDialog {


    public EmaProgress(Context context) {
        super(context);
    }

    public EmaProgress(Context context, int theme) {
        super(context, theme);
    }
}
