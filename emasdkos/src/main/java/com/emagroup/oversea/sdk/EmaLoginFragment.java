package com.emagroup.oversea.sdk;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by beyearn on 2017/7/3.
 *
 * 由于googlesdk 面临多种回调 onacticityresult等 暂不用fragment
 *
 * 暂废！
 *
 *
 */

public class EmaLoginFragment extends Fragment implements View.OnClickListener {


    private ResourceManager mResourceManager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mResourceManager = ResourceManager.getInstance(context.getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        int fragmentLayoutId = mResourceManager.getLayoutId("ema_fragment_login");
        View frameLayout = inflater.inflate(fragmentLayoutId, container, false);

        View bt_gl_login = frameLayout.findViewById(mResourceManager.getViewId("bt_gl_login"));
        bt_gl_login.setOnClickListener(this);

        return frameLayout;
    }


    private void signIn() {
    }


    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == mResourceManager.getViewId("bt_gl_login")) {
            signIn();
        } else {

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
