package org.pinwheel.agility.net.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class StringParser implements IResponseParser<String> {

    private String result;

    @Override
    public void parse(InputStream inStream) throws Exception {
        result = streamToString(inStream);
    }

    @Override
    public void parse(byte[] dataBytes) throws Exception {
        result = new String(dataBytes, "UTF-8");
    }

    @Override
    public void parse(String dataString) throws Exception {
        result = dataString;
    }

    @Override
    public String getResult() {
        return result;
    }

    private String streamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
