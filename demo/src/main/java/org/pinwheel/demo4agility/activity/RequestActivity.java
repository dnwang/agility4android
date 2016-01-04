package org.pinwheel.demo4agility.activity;

import android.view.View;

import org.pinwheel.agility.net.Request;
import org.pinwheel.agility.net.VolleyRequestHelper;

public class RequestActivity extends AbsTestActivity {

    @Override
    protected void onInitInCreate() {
        VolleyRequestHelper.init(this);
        VolleyRequestHelper.debug = true;
    }

    @Override
    protected View getContentView() {
        return null;
    }

    @Override
    protected void doSomethingAfterCreated() {
        showLog(true);
//        for (int index = 0; index < 10; index++) {
//            request();
//        }
//
//        for (int index = 0; index < 10; index++) {
//            request2();
//        }

        request3();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VolleyRequestHelper.release();
    }

    private void request2() {
        String requestTag = "request2";
        Request api = new Request.Builder().url("http://www.baidu.com").keepSingle(true).create();
        VolleyRequestHelper.doPost(api, new VolleyRequestHelper.OnHandleTagRequestAdapter<String>(requestTag) {
            @Override
            public void onError(Exception e, Object tag) {
                String log = "<" + tag.toString() + "> --> " + "Error\n";
                log += e.getMessage();
                logout(log);
            }

            @Override
            public void onSuccess(String obj, Object tag) {
                String log = "<" + tag.toString() + "> --> " + "OK\n";
                log += obj.toString();
                logout(log);
            }
        });
    }

    private void request3() {
        String url = "http://www.baidu.com";
        String requestTag = "request3";
        Request api = new Request.Builder().url(url).keepSingle(true).create();
        VolleyRequestHelper.doGet(api, new VolleyRequestHelper.OnHandleTagRequestAdapter<String>(requestTag) {
            @Override
            public void onError(Exception e, Object tag) {
                String log = "<" + tag.toString() + "> --> " + "Error\n";
                log += e.getMessage();
                logout(log);
            }

            @Override
            public void onSuccess(String obj, Object tag) {
                String log = "<" + tag.toString() + "> --> " + "OK\n";
                log += obj.toString();
                logout(log);
            }
        });
    }

}