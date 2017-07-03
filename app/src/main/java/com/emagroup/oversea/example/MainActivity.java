package com.emagroup.oversea.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.emagroup.oversea.sdk.EmaCallBackConst;
import com.emagroup.oversea.sdk.EmaSDKListener;
import com.emagroup.oversea.sdk.EmaSdk;
import com.emagroup.oversea.sdk.HttpRequestor;
import com.emagroup.oversea.sdk.ToastHelper;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private SignInButton btLogin;
    private Button btPay;
    private Button btLogout;
    private Button btShowBar;
    private Button btHideBar;
    private Button btSwichAccount;
    private Button btUpGameInfo;
    private Button btOpenWebview;
    private Button btEmShare;
    private Button btAgain_init;
    private Button bt_snap_shot;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EmaSdk.getInstance().init(this, new EmaSDKListener() {
            @Override
            public void onCallBack(int resultCode, String decr) {
                switch (resultCode){
                    case EmaCallBackConst.INITSUCCESS://初始化SDK成功回调
                        ToastHelper.toast(MainActivity.this, "sdk初始化成功");
                        break;
                    case EmaCallBackConst.INITFALIED://初始化SDK失败回调
                        ToastHelper.toast(MainActivity.this, "sdk初始化失败");
                        break;
                    case EmaCallBackConst.LOGINSUCCESS://登陆成功回调
                        ToastHelper.toast(MainActivity.this, "登陆成功");
                        break;
                    case EmaCallBackConst.LOGINCANELL://登陆取消回调
                        break;
                }
            }
        });


        btLogin = (SignInButton) findViewById(R.id.bt_login);
        btPay = (Button) findViewById(R.id.bt_pay);
        btLogout = (Button) findViewById(R.id.bt_logout);
        btShowBar = (Button) findViewById(R.id.bt_showbar);
        btHideBar = (Button) findViewById(R.id.bt_hidebar);
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
        btHideBar.setOnClickListener(this);
        btSwichAccount.setOnClickListener(this);
        btUpGameInfo.setOnClickListener(this);
        btOpenWebview.setOnClickListener(this);
        btEmShare.setOnClickListener(this);
        bt_snap_shot.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_login:


                EmaSdk.getInstance().login();

                break;
        }
    }






















    private void rxDemo() {
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

    }
}
