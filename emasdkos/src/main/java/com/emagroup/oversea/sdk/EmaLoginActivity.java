package com.emagroup.oversea.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by beyearn on 2018/2/26.
 * <p>
 * 本来用来登陆页面
 * 现在也顺便用来显示升级账户的页面
 */

public class EmaLoginActivity extends Activity implements View.OnClickListener {

    private ResourceManager mResourceManager;
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private FrameLayout mFlBack;
    private TextView mTvClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mResourceManager = ResourceManager.getInstance(this.getApplicationContext());
        setContentView(mResourceManager.getLayoutId("ema_activity_login"));

        setFinishOnTouchOutside(false);
        //getWindow().setGravity(Gravity.CENTER);

        mWebView = (WebView) findViewById(mResourceManager.getViewId("wv_login"));
        mProgressBar = (ProgressBar) findViewById(mResourceManager.getViewId("wv_progressbar"));
        mFlBack = (FrameLayout) findViewById(mResourceManager.getViewId("fl_back"));
        mTvClose = (TextView) findViewById(mResourceManager.getViewId("tv_close"));
        mFlBack.setOnClickListener(this);
        mTvClose.setOnClickListener(this);

        Intent intent = getIntent();
        String loginUrl = intent.getStringExtra("webUrl");
        L.e("webUrl", loginUrl);
        boolean showCloseView = intent.getBooleanExtra("showCloseView", false);

        initView(showCloseView);
        mWebView.loadUrl(loginUrl);

        final Window window = getWindow();
        setHideVirtualKey(window);
        window.getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                setHideVirtualKey(window);
            }
        });
    }

    public void setHideVirtualKey(Window window) {
        //保持布局状态
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | //布局位于状态栏下方
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | //全屏
                View.SYSTEM_UI_FLAG_FULLSCREEN | //隐藏导航栏
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= 19) {
            uiOptions |= 0x00001000;
        } else {
            uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }
        window.getDecorView().setSystemUiVisibility(uiOptions);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == mResourceManager.getViewId("fl_back")) {
            mWebView.goBack();
        } else if (viewId == mResourceManager.getViewId("tv_close")) {
            this.finish();
        }
    }


    /**
     * 与js交互时用到的方法对象，在js里直接调用
     */
    public class JavaScriptinterface {
        Context context;

        public JavaScriptinterface(Context c) {
            context = c;
        }

        @JavascriptInterface
        public void close(String num) {
            ToastHelper.toast(context, "密码修改成功");
        }

        @JavascriptInterface
        public void ema_didLogin(String data) {
            L.e("didLogin", data);
            EmaUser.getInstance().setLoginInfo(EmaLoginActivity.this.getApplicationContext(), data);
            EmaLoginActivity.this.finish();
        }
    }


    private void initView(boolean showCloseView) {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new JavaScriptinterface(this), "webview");
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        //mWebView.getSettings().setTextZoom(75);
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);//设置是否允许通过file url加载的Javascript可以访问其他的源，包括其他的文件和http,https等其他的源
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //Log.e("aaaa", "22222");
                if (mWebView.canGoBack()) {
                    mFlBack.setVisibility(View.VISIBLE);
                } else {
                    mFlBack.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //Log.e("aaaa", "11111");
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    if (View.VISIBLE != mProgressBar.getVisibility()) {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                    mProgressBar.setProgress(newProgress);
                }
            }
        });

        if (showCloseView) {
            mTvClose.setVisibility(View.VISIBLE);
        } else {
            mTvClose.setVisibility(View.GONE);
        }

    }


    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        CookieSyncManager.createInstance(this);
        CookieManager.getInstance().removeAllCookie();
        super.onDestroy();
    }
}
