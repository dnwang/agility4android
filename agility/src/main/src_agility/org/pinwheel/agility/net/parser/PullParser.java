package org.pinwheel.agility.net.parser;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.io.StringReader;
import java.util.Map;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public abstract class PullParser<T> implements IDataParser<T> {
    private static final String TAG = PullParser.class.getSimpleName();

    private XmlPullParser parser;

    protected PullParser() {
        this.parser = Xml.newPullParser();
    }

    @Override
    public final void parse(InputStream inStream) throws Exception {
        this.parser.setInput(inStream, "UTF-8");
        this.parse();
    }

    @Override
    public final void parse(String dataString) throws Exception {
        this.parser.setInput(new StringReader(dataString));
        this.parse();
    }

    @Override
    public final void parse(byte[] dataBytes) throws Exception {
        this.parser.setInput(new StringReader(new String(dataBytes, "UTF-8")));
        this.parse();
    }

    /**
     * Save attribute to map
     *
     * @param container
     * @param parser
     */
    protected final void saveAttribute2Map(Map<String, String> container, XmlPullParser parser) {
        int size = parser.getAttributeCount();
        for (int i = 0; i < size; i++) {
            container.put(parser.getAttributeName(i), parser.getAttributeValue(i));
        }
    }

    private void parse() throws Exception {
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tag = parser.getName();
            this.onParse(tag, eventType, parser);
            eventType = parser.next();
        }
    }

    protected abstract void onParse(String tag, int eventType, XmlPullParser parser) throws Exception;

    @Override
    public void setOnParseAdapter(OnParseAdapter listener) {
        if (debug) {
            Log.e(TAG, TAG + " not support !");
        }
    }

    @Override
    public void release() {
        parser = null;
    }

}
