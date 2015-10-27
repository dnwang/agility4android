package org.pinwheel.agility.cache;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
final class StringEntity extends CacheEntity<String> {

    public StringEntity() {
        super();
    }

    public StringEntity(String string) {
        super(string);
    }

    @Override
    protected int sizeOf() {
        if (obj == null) {
            return 0;
        }
        return obj.getBytes().length;
    }

    @Override
    protected void decodeFrom(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
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
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        obj = sb.toString();
    }

    @Override
    protected InputStream getInputStream() {
        if (obj == null) {
            return null;
        }
        return new ByteArrayInputStream(get().getBytes());
    }

}
