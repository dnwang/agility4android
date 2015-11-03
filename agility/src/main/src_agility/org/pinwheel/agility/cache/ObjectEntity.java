package org.pinwheel.agility.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
final class ObjectEntity extends CacheEntity<Object> {

    public ObjectEntity() {
        super();
    }

    public ObjectEntity(Object obj) {
        super(obj);
    }

    /**
     * Size of object
     *
     * @return a default value
     */
    @Deprecated
    @Override
    protected int sizeOf() {
        if (obj == null) {
            return 0;
        }
        return 65536;
    }

    @Override
    protected void decodeFrom(InputStream inputStream) {
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(inputStream);
            obj = objectInputStream.readObject();
        } catch (ClassNotFoundException | IOException e) {
            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    @Override
    protected InputStream getInputStream() {
        if (obj == null) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            try {
                byteArrayOutputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        }
    }

}
