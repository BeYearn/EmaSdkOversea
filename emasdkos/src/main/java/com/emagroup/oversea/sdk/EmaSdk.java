package com.emagroup.oversea.sdk;

/**
 * Created by beyearn on 2017/5/24.
 */

public class EmaSdk {

    private Object object = new Object();
    private EmaSdk instance;

    private EmaSdk() {
    }

    public EmaSdk getInstance() {
        if (null == instance) {
            synchronized (object) {
                if (null == instance) {
                    instance = new EmaSdk();
                }
            }
        }
        return instance;
    }

    public void init(){




    }





    public void login(){




    }



}
