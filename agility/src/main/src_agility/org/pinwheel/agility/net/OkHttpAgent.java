package org.pinwheel.agility.net;

import android.util.Log;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.pinwheel.agility.net.parser.IDataParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class OkHttpAgent implements HttpClientAgent {
    private static final String TAG = OkHttpAgent.class.getSimpleName();

    private static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");

    private OkHttpClient client;
    private ExecutorService executor;

    public OkHttpAgent() {
        client = new OkHttpClient();
        executor = Executors.newCachedThreadPool();
    }

    protected com.squareup.okhttp.Request convert(Request request) {
        com.squareup.okhttp.Request.Builder requestBuilder = new com.squareup.okhttp.Request.Builder();
        // convert body
        RequestBody requestBody = null;
        if (request.getBody() != null) {
            requestBody = RequestBody.create(MEDIA_TYPE_MARKDOWN, request.getBody());
        }
        // method + body
        requestBuilder.method(request.getMethod(), requestBody);
        // url
        requestBuilder.url(request.getUrlByMethod());
        // tag
        Object tag = request.getTag();
        if (tag != null) {
            if (request.isKeepSingle()) {
                cancel(tag);
            }
            requestBuilder.tag(tag);
        }
        // header
        requestBuilder.headers(Headers.of(request.getHeaders()));

        return requestBuilder.build();
    }

    public Response execute(Request request) throws IOException {
        if (client == null || request == null) {
            throw new IOException("client or request must not null !");
        }
        com.squareup.okhttp.Request okHttpRequest = convert(request);
        // set connect time out
        client.setConnectTimeout(request.getTimeout(), TimeUnit.SECONDS);
        return client.newCall(okHttpRequest).execute();
    }

    @Override
    public void enqueue(final Request request) {
        if (client == null || request == null) {
            if (debug) {
                Log.e(TAG, "client or request must not null !");
            }
            return;
        }

        final OnRequestAdapter callback = request.getRequestListener();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    if (callback.onRequest(request)) {
                        // no need handle continue
                        return;
                    }
                }
                // get response
                Response response = null;
                try {
                    response = execute(request);
                    if (callback != null) {
                        if (callback.onResponse(response)) {
                            // no need handle continue
                            return;
                        }
                    }
                    if (response.code() != HttpURLConnection.HTTP_OK) {
                        throw new IllegalStateException("Response code: " + response.code() + "; message: " + response.message());
                    }
                } catch (Exception e) {
                    if (debug) {
                        e.printStackTrace();
                    }
                    if (callback != null) {
                        callback.dispatchOnError(e);
                    }
                    // break; request error
                    return;
                }
                // parse
                IDataParser parser = request.getResponseParser();
                if (parser == null) {
                    if (callback != null) {
                        callback.dispatchOnSuccess(null);
                    }
                    // break; no need parse
                    return;
                }
                try {
                    parser.parse(response.body().byteStream());
                    if (callback != null) {
                        callback.dispatchOnSuccess(parser.getResult());
                    }
                } catch (Exception e) {
                    if (debug) {
                        e.printStackTrace();
                    }
                    if (callback != null) {
                        callback.dispatchOnError(e);
                    }
                } finally {
                }
            }
        });
    }

    @Override
    public void parallelExecute(Request... requests) {
        if (client == null || requests == null || requests.length == 0) {
            return;
        }
        for (Request request : requests) {
            enqueue(request);
        }
    }

    @Override
    public void cancel(Object... tags) {
        if (client == null || tags == null || tags.length == 0) {
            return;
        }
        for (Object tag : tags) {
            client.cancel(tag);
        }
    }

    @Override
    public void release() {
        executor.shutdown();
        executor = null;
//        client.cancel();
        client = null;
    }

}
