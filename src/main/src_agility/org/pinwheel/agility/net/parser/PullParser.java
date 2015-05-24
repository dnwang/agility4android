package org.pinwheel.agility.net.parser;

import java.io.InputStream;
import java.io.StringReader;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import android.util.Xml;

public abstract class PullParser<T> implements IResponseParser<T> {

	private final XmlPullParser mParser;

	protected PullParser() {
		this.mParser = Xml.newPullParser();
	}

	@Override
	public final void parse(InputStream inStream) throws Exception {
		this.mParser.setInput(inStream, "UTF-8");
		this.parse();
	}

	@Override
	public final void parse(String dataString) throws Exception {
		this.mParser.setInput(new StringReader(dataString));
		this.parse();
	}
	
	@Override
	public final void parse(byte[] dataBytes) throws Exception {
		this.mParser.setInput(new StringReader(new String(dataBytes, "UTF-8")));
		this.parse();
	}

    /**
     * Save attribute to map
     * @param container
     * @param parser
     */
    protected final void saveAttribute2Map(Map<String, String> container, XmlPullParser parser){
        int size = parser.getAttributeCount();
        for (int i = 0; i < size; i++) {
            container.put(parser.getAttributeName(i), parser.getAttributeValue(i));
        }
    }

	private void parse() throws Exception {
		int eventType = mParser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			String tag = mParser.getName();
			this.onParse(tag, eventType, mParser);
			eventType = mParser.next();
		}
	}

	protected abstract void onParse(String tag, int eventType, XmlPullParser parser) throws Exception;

}
