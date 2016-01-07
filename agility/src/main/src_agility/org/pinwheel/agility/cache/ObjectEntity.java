package org.pinwheel.agility.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class ObjectEntity<T> implements Serializable {

    private T obj;
    private int size;

    public ObjectEntity() {
        this.obj = null;
    }

    protected int sizeOf() {
        return size;
    }

    protected InputStream getInputStream() {
        if (obj == null) {
            return null;
        }
        ByteArrayInputStream inputStream = null;
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);
            objectOutputStream.writeObject(obj);
            byte[] bytes = byteOutputStream.toByteArray();
            this.size = bytes.length;
            objectOutputStream.flush();
            objectOutputStream.close();
            inputStream = new ByteArrayInputStream(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                byteOutputStream.flush();
                byteOutputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return inputStream;
    }

    public T get() {
        return obj;
    }

    protected final void setObj(T obj) {
        this.obj = obj;
    }

    public void decodeFrom(InputStream inputStream) {
        if (inputStream == null) {
            return;
        }
        ObjectInputStream objectInputStream = null;
        try {
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int size = 0;
            while ((size = inputStream.read(buff)) != -1) {
                byteOutputStream.write(buff, 0, size);
            }
            buff = byteOutputStream.toByteArray();
            this.size = buff.length;
            objectInputStream = new ObjectInputStream(new ByteArrayInputStream(buff));
            setObj((T) objectInputStream.readObject());

            byteOutputStream.flush();
            byteOutputStream.close();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        } finally {
            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            try {
                inputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void decodeFrom(byte[] bytes) {

    }

    public void decodeFrom(T obj) {
        if (obj == null) {
            return;
        }
        ByteArrayOutputStream byteOutStream = null;
        ObjectInputStream objInStream = null;
        try {
            byteOutStream = new ByteArrayOutputStream();
            ObjectOutputStream objOutStream = new ObjectOutputStream(byteOutStream);
            objOutStream.writeObject(obj);
            byte[] bytes = byteOutStream.toByteArray();
            this.size = bytes.length;
            objInStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
            setObj((T) objInStream.readObject());

            objOutStream.flush();
            objOutStream.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (byteOutStream != null) {
                try {
                    byteOutStream.flush();
                    byteOutStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (objInStream != null) {
                try {
                    objInStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
