package com.emagroup.oversea.sdk;

/**
 * Created by beyearn on 2017/5/24.
 */

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by xialo on 2016/6/28.
 * <p>
 * Observable和Subject是两个“生产”实体，Observer和Subscriber是两个“消费”实体。
 */
public class RxBus {
    private static volatile RxBus defaultInstance;
    // 主题
    private final Subject bus;

    // PublishSubject只会把在订阅发生的时间点之后来自原始Observable的数据发射给观察者
    public RxBus() {
        bus = new SerializedSubject<>(PublishSubject.create());
    }

    // 单例RxBus
    public static RxBus getDefault() {
        if (defaultInstance == null) {
            synchronized (RxBus.class) {
                if (defaultInstance == null) {
                    defaultInstance = new RxBus();
                }
            }
        }
        return defaultInstance;
    }

    /**
     * 提供了一个新的事件,单一类型
     *
     * @param o 事件数据
     */
    public void post(Object o) {
        bus.onNext(o);
    }

    /**
     * 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
     *
     * @param eventType 事件类型
     * @param <T>
     * @return
     */
    public <T> Observable<T> toObservable(Class<T> eventType) {
        return bus.ofType(eventType);
    }

    /**
     * 提供了一个新的事件,根据code进行分发
     *
     * @param code 事件code
     * @param o
     */
    public void post(int code, Object o) {
        bus.onNext(new BusMessage(code, o));

    }


    /**
     * 根据传递的code和 eventType 类型返回特定类型(eventType)的 被观察者
     *
     * @param code      事件code
     * @param eventType 事件类型
     * @param <T>
     * @return
     */
    public <T> Observable<T> toObservable(final int code, final Class<T> eventType) {
        return bus.ofType(BusMessage.class)
                .filter(new Func1<BusMessage, Boolean>() {
                    @Override
                    public Boolean call(BusMessage o) {
                        //过滤code和eventType都相同的事件
                        return o.getCode() == code && eventType.isInstance(o.getObject());
                    }
                }).map(new Func1<BusMessage, Object>() {
                    @Override
                    public Object call(BusMessage o) {
                        return o.getObject();
                    }
                }).cast(eventType);
    }



/*
    //注册观察者
    Subscription subscription =RxBus.getDefault()
            .toObservable(100,String.class)
            .subscribe(new Action1<String>() {
                @Override
                public void call(String s) {
                    Log.e("CM", s);
                }
            });


    //发送事件或消息:
    RxBus.getDefault().post(100, "123456");




    最后，一定要记得在生命周期结束的地方取消订阅事件，防止RxJava可能会引起的内存泄漏问题。
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!subscription .isUnsubscribed()) {
            subscription .unsubscribe();
        }
    }



    */

}