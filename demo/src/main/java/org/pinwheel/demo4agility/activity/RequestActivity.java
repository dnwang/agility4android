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
    protected void doTest() {
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
        String url = "http://182.138.101.48:5001/nn_live/nn_x64/aWQ9Y2N0djEmdXJsX2MxPTIwMDAmbm5fYWs9MDEwNjM4MDgxMGFmZjVkNGJhZTZkMzMxYmEzYTA3YjI2NiZudHRsPTMmbnBpcHM9MTgyLjEzOC4xMDEuNDg6NTEwMSZuY21zaWQ9MTAwMDAxJm5ncz01NTFjZWZjYzAwMDU1NDkzOWUwYjAxYzk2N2IxNDFjYiZubl9jcD1udWxsJm5uX3VzZXJfaWQ9WVlIRDAwMDAwNzgxJm5uX2RheT0yMDE1MDQwMiZubl9iZWdpbj0xMjAwMDAmbm5fdGltZV9sZW49MzYwMCZuZHY9MS4wLjAuMC4yLlNDLUpHUy1BUEhPTkUuMC4wX1JlbGVhc2UmbmVhPSUyNm5uX2RheSUzZDIwMTUwNDAyJTI2bm5fYmVnaW4lM2QxMjAwMDAlMjZubl90aW1lX2xlbiUzZDM2MDAmbmVzPTU4N2I2ZmNkYjNjMjYzNTM5YTE0MTJkNWFkMTk4Yzdj/cctv1.m3u8";
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