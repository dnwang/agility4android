package org.pinwheel.demo4agility.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class EventBusActivity extends Activity {

    private static EventBus eventBus = new EventBus();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new View1(getBaseContext()));

        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(2000l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                eventBus.post("Im from eventbus");
            }
        }.start();
    }

    public static class View1 extends TextView{

        public View1(Context context) {
            super(context);
            setGravity(Gravity.CENTER);
        }

        @Subscribe
        public void onMessage(final String msg){
            post(new Runnable() {
                @Override
                public void run() {
                    setText(msg);
                }
            });
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            eventBus.register(this);
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            // need to unregister !!
            eventBus.unregister(this);
        }
    }

}
