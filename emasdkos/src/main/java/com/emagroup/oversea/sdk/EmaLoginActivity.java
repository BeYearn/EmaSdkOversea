package com.emagroup.oversea.sdk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * Created by beyearn on 2018/2/26.
 */

public class EmaLoginActivity extends AppCompatActivity {

    private ResourceManager mResourceManager;
    private WebView mWebView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResourceManager = ResourceManager.getInstance(this.getApplicationContext());
        setContentView(mResourceManager.getLayoutId("ema_activity_login"));

        mWebView = (WebView) findViewById(mResourceManager.getViewId("wv_login"));
        mProgressBar = (ProgressBar) findViewById(mResourceManager.getViewId("wv_progressbar"));


        Intent intent = getIntent();
        String loginUrl = intent.getStringExtra("loginUrl");
        L.e("loginurl", loginUrl);

        initView();

        mWebView.loadUrl(loginUrl);

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
            EmaUser.getInstance().doUserResult(EmaLoginActivity.this.getApplicationContext(), data);
        }
    }


    private void initView() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new JavaScriptinterface(this), "webview");
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        mWebView.getSettings().setBuiltInZoomControls(true);
        //mWebView.getSettings().setTextZoom(75);
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);//设置是否允许通过file url加载的Javascript可以访问其他的源，包括其他的文件和http,https等其他的源
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    if (View.INVISIBLE == mProgressBar.getVisibility()) {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                    mProgressBar.setProgress(newProgress);
                }
            }
        });


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
