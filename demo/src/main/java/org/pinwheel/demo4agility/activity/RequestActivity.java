package org.pinwheel.demo4agility.activity;

import android.view.View;

import org.pinwheel.agility.net.Request;
import org.pinwheel.agility.net.RequestManager;
import org.pinwheel.agility.net.parser.GsonParser;
import org.pinwheel.demo4agility.entity.DBStruct;

public class RequestActivity extends AbsTestActivity {

    @Override
    protected void onInitInCreate() {
        RequestManager.init(this);
        RequestManager.debug = true;
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
        RequestManager.release();
    }

    private void request() {
        String requestTag = "request";
        Request api = new Request("http://182.138.101.48:9090/cyyy/nn_cms/nn_cms_view/cy_v2/n1_a.php");
        api.addParam("nns_output_type", "json");
        api.setKeepSingle(true);

        RequestManager.doGet(api, new GsonParser(DBStruct.class), new RequestManager.OnHandleTagRequestAdapter<DBStruct>(requestTag) {
            @Override
            public void onError(Exception e, Object tag) {
                String log = "<" + tag.toString() + "> --> " + "Error\n";
                log += e.getMessage();
                logout(log);
            }

            @Override
            public void onSuccess(DBStruct obj, Object tag) {
                String log = "<" + tag.toString() + "> --> " + "OK\n";
                log += obj.toString();
                logout(log);
            }
        });
    }

    private void request2() {
        String requestTag = "request2";
        Request api = new Request("http://www.baidu.com");
        api.setKeepSingle(true);
        RequestManager.doPost(api, new RequestManager.OnHandleTagRequestAdapter<String>(requestTag) {
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
        Request api = new Request(url);
        api.setKeepSingle(true);
        RequestManager.doGet(api, new RequestManager.OnHandleTagRequestAdapter<String>(requestTag) {
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