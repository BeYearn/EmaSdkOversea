package com.emagroup.oversea.example;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.emagroup.oversea.sdk.ComUtils;
import com.emagroup.oversea.sdk.EmaCallBackConst;
import com.emagroup.oversea.sdk.EmaSDKListener;
import com.emagroup.oversea.sdk.EmaSdk;
import com.emagroup.oversea.sdk.ThreadUtil;
import com.emagroup.oversea.sdk.ToastHelper;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btLogin;
    private Button btPay;
    private Button btLogout;
    private Button btShowBar;
    private Button btAccountUpgrade;
    private Button btSwichAccount;
    private Button btUpGameInfo;
    private Button btOpenWebview;
    private Button btEmShare;
    private Button btAgain_init;
    private Button bt_snap_shot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EmaSdk.getInstance().init(this, new EmaSDKListener() {
            @Override
            public void onCallBack(int resultCode, String decr) {
                switch (resultCode) {
                    case EmaCallBackConst.INITSUCCESS://初始化SDK成功回调
                        ToastHelper.toast(MainActivity.this, "sdk初始化成功");
                        break;
                    case EmaCallBackConst.INITFALIED://初始化SDK失败回调
                        ToastHelper.toast(MainActivity.this, "sdk初始化失败");
                        break;
                    case EmaCallBackConst.LOGINSUCCESS://登陆成功回调
                        ToastHelper.toast(MainActivity.this, "登陆成功");
                        break;
                    case EmaCallBackConst.LOGOUTSUCCESS://登出成功回调
                        ToastHelper.toast(MainActivity.this, decr);
                        break;
                    case EmaCallBackConst.LOGINEXPIRED: //登录过期
                        ToastHelper.toast(MainActivity.this, decr);
                        break;
                }
            }
        }, "Y[]D(s<swIB%X~2G");


        btLogin = (Button) findViewById(R.id.bt_login);
        btPay = (Button) findViewById(R.id.bt_pay);
        btLogout = (Button) findViewById(R.id.bt_logout);
        btShowBar = (Button) findViewById(R.id.bt_get_product);
        btAccountUpgrade = (Button) findViewById(R.id.bt_account_upgrade);
        btSwichAccount = (Button) findViewById(R.id.bt_swichaccount);
        btUpGameInfo = (Button) findViewById(R.id.up_game_info);
        btOpenWebview = (Button) findViewById(R.id.bt_open_webview);
        btEmShare = (Button) findViewById(R.id.bt_emshare);
        btAgain_init = (Button) findViewById(R.id.again_init);
        bt_snap_shot = (Button) findViewById(R.id.bt_snap_shot);


        btAgain_init.setOnClickListener(this);
        btLogin.setOnClickListener(this);
        btPay.setOnClickListener(this);
        btLogout.setOnClickListener(this);
        btShowBar.setOnClickListener(this);
        btAccountUpgrade.setOnClickListener(this);
        btSwichAccount.setOnClickListener(this);
        btUpGameInfo.setOnClickListener(this);
        btOpenWebview.setOnClickListener(this);
        btEmShare.setOnClickListener(this);
        bt_snap_shot.setOnClickListener(this);


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
        switch (v.getId()) {
            case R.id.bt_login:


                EmaSdk.getInstance().login();

                break;
            case R.id.bt_logout:
                EmaSdk.getInstance().logout();
                break;

            case R.id.bt_account_upgrade:
                EmaSdk.getInstance().accountUpgrade();
                break;

            case R.id.bt_pay:
                HashMap<String, String> payParams = new HashMap<>();
                payParams.put("server_id", "168000100001");
                payParams.put("quantity", "1");
                payParams.put("product_id", "com.emagroups.wol.40");
                payParams.put("role_id", "test001");
                payParams.put("custom_data", "1;2;528280977428;197568495617;");
                payParams.put("consume_now", "true");  // 是否立刻消耗

                EmaSdk.getInstance().pay(payParams, new EmaSDKListener() {
                    @Override
                    public void onCallBack(int resultCode, String decr) {
                        ToastHelper.toast(MainActivity.this, decr);
                        switch (resultCode) {
                            case EmaCallBackConst.PAYSUCCESS:

                                break;
                            case EmaCallBackConst.PAYFALIED:

                                break;
                            case EmaCallBackConst.PAYCANELI:


                                break;
                        }
                    }
                });
                break;
            case R.id.bt_get_product:

                ThreadUtil.runInSubThread(new Runnable() {
                    @Override
                    public void run() {
                        List<String> productList = EmaSdk.getInstance().getProductList();
                    }
                });
                break;
            case R.id.bt_emshare:
                //ProgressUtil.getInstance(MainActivity.this).openProgressDialog();
                ThreadUtil.runInSubThread(new Runnable() {
                    @Override
                    public void run() {
                        String device_id = ComUtils.getDEVICE_ID(MainActivity.this);
                        ToastHelper.toast(MainActivity.this, "AdId " + device_id);
                    }
                });
                break;
            case R.id.bt_snap_shot:
                /*UserLoginInfo userLoginInfo = EmaUser.getInstance().getUserLoginInfo(MainActivity.this);
                L.e("token", userLoginInfo.getAccessToken());*/

                EmaSdk.getInstance().consumeHad();

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        EmaSdk.getInstance().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EmaSdk.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        EmaSdk.getInstance().onDestory();
        super.onDestroy();
    }

/*    private void rxDemo() {
        Subscription stringObservable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    String result = new HttpRequestor().doGet("https://www.baidu.com/", null);
                    subscriber.onNext(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();

                    }
                });
    }

    private void rxDemo2() {

        Observer<String> observer = new Observer<String>() {
            @Override
            public void onCompleted() {
                Toast.makeText(MainActivity.this, "complete", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onNext(String s) {
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
            }
        };


        Observable<String> stringObservable = Observable.create(new Observable.OnSubscribe<String>() {

            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    String result = new HttpRequestor().doPost("https://www.baidu.com/", null);
                    subscriber.onNext(result);
                    subscriber.onNext("Hi");
                    subscriber.onNext("Aloha");
                    subscriber.onCompleted();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Subscription subscribe = stringObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
        subscribe.unsubscribe();

    }*/
}
