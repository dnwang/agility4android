package org.pinwheel.agility.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.HashSet;
import java.util.Set;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public enum EventManager {

    INSTANCE;

    private Handler eventHandler;

    EventManager() {
        eventHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                final Event event = (null == msg.obj) ? null : (Event) msg.obj;
                if (null == event || null == event.action) {
                    return;
                }
                for (EventReceiver receiver : receiverSet) {
                    receiver.onReceive(event);
                }
            }
        };
    }

    public void post(Event event) {
        if (null == event || null == event.action) {
            return;
        }
        Message.obtain(eventHandler, 0, event).sendToTarget();
    }

    public void postEmpty(String action) {
        post(new Event(action));
    }

    private Set<EventReceiver> receiverSet = new HashSet<>();

    public void register(EventReceiver object) {
        receiverSet.add(object);
    }

    public void unregister(EventReceiver object) {
        receiverSet.remove(object);
    }

    public interface EventReceiver {
        void onReceive(Event event);
    }

    public static class Event {
        private String action;
        private Object content;

        public Event(String action, Object obj) {
            this.action = action;
            this.content = obj;
        }

        public Event(String action) {
            this.action = action;
        }

        public Object getContent() {
            return content;
        }

        public String getAction() {
            return action;
        }
    }

}
