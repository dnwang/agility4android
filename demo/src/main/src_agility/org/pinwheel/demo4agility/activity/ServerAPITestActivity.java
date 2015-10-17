package org.pinwheel.demo4agility.activity;

import android.view.View;

import com.google.gson.Gson;

import org.pinwheel.agility.net.Request;
import org.pinwheel.agility.net.RequestManager;
import org.pinwheel.agility.net.parser.GsonParser;
import org.pinwheel.demo4agility.entity.ECMobileEntity;

import java.io.Serializable;

/**
 * 版权所有 (C), 2014 <br>
 * <br>
 * All rights reserved
 *
 * @author dnwang
 * @date 2015/3/12 23:02
 * @description
 */
public class ServerAPITestActivity extends AbsTestActivity {
    private static final String TAG = ServerAPITestActivity.class.getSimpleName();

    private static final String SERVER_URL = "http://172.31.12.59/ecmobile/?url=";

    private static final String ORDER_AFFIRMRECEIVED = "/order/affirmReceived";
    private static final String SHOPHELP = "/shopHelp";
    private static final String ORDER_EXPRESS = "/order/express";
    private static final String CART_DELETE = "/cart/delete";
    private static final String CART_UPDATE = "/cart/update";
    private static final String CART_CREATE = "/cart/create";
    private static final String PRICE_RANGE = "/price_range";
    private static final String GOODS = "/goods";
    private static final String ARTICLE = "/article";
    private static final String CART_LIST = "/cart/list";
    private static final String ADDRESS_LIST = "/address/list";
    private static final String ADDRESS_SETDEFAULT = "/address/setDefault";
    private static final String USER_COLLECT_CREATE = "/user/collect/create";
    private static final String FLOW_DONE = "/flow/done";
    private static final String BRAND = "/brand";
    private static final String ORDER_PAY = "/order/pay";
    private static final String HOME_CATEGORY = "/home/category";
    private static final String USER_COLLECT_DELETE = "/user/collect/delete";
    private static final String VALIDATE_INTEGRAL = "/validate/integral";
    private static final String HOME_DATA = "/home/data";
    private static final String ADDRESS_ADD = "/address/add";
    private static final String USER_SIGNIN = "/user/signin";
    private static final String USER_SIGNUP = "/user/signup";
    private static final String GOODS_DESC = "/goods/desc";
    private static final String USER_INFO = "/user/info";
    private static final String ADDRESS_DELETE = "/address/delete";
    private static final String USER_SIGNUPFIELDS = "/user/signupFields";
    private static final String SEARCHKEYWORDS = "/searchKeywords";
    private static final String ADDRESS_UPDATE = "/address/update";
    private static final String ADDRESS_INFO = "/address/info";
    private static final String VALIDATE_BONUS = "/validate/bonus";
    private static final String REGION = "/region";
    private static final String USER_COLLECT_LIST = "/user/collect/list";
    private static final String ORDER_LIST = "/order/list";
    private static final String CONFIG = "/config";
    private static final String ORDER_CANCLE = "/order/cancel";
    private static final String COMMENTS = "/comments";
    private static final String SEARCH = "/search";
    private static final String CATEGORY = "/category";
    private static final String FLOW_CHECKORDER = "/flow/checkOrder";

    @Override
    protected void onInitInCreate() {
        RequestManager.init(getBaseContext());
        RequestManager.debug = true;
    }

    @Override
    protected View getContentView() {
        return null;
    }

    @Override
    protected void doTest() {
        doRequest();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RequestManager.release();
    }

    private void doRequest() {
        SigninParams params = new SigninParams();
        params.name = "wdnonly";
        params.password = "1990219";
        Request request = new Request.Builder().url(SERVER_URL + USER_SIGNIN).addParam("json", new Gson().toJson(params)).create();
        logout(request.getUrlByMethod());
        RequestManager.doPost(
                request,
                new GsonParser(Response.class),
                new RequestManager.OnRequestListener<Response>() {
                    @Override
                    public void onError(Exception e) {
                        logout(e.getMessage());
                    }

                    @Override
                    public void onSuccess(Response obj) {
                        logout(obj.toString());
                    }
                });

        RequestManager.doPost(request, new RequestManager.OnRequestListener<String>() {
            @Override
            public void onError(Exception e) {
                logout(e.getMessage());
            }

            @Override
            public void onSuccess(String obj) {
                logout(obj);
            }
        });

    }

    private class SigninParams {
        //json:{"password":"1990219","name":"wdnonly"}
        String name;
        String password;
    }

    public static class Response implements Serializable {
        public Data data;
    }

    public static class Data implements Serializable {
        public ECMobileEntity.Session session;
        public ECMobileEntity.User user;
    }

}
