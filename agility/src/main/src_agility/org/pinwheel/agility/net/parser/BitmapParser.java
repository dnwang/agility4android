package org.pinwheel.agility.net.parser;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class BitmapParser implements IDataParser<Bitmap> {
    private final static String TAG = BitmapParser.class.getSimpleName();

    private Bitmap result;
    private String fileName;
    private Bitmap.CompressFormat format;

    private OnParseAdapter listener;

    public BitmapParser(File fileName, Bitmap.CompressFormat format) {
        this(fileName.getAbsolutePath(), format);
    }

    public BitmapParser(String fileName, Bitmap.CompressFormat format) {
        this.fileName = fileName;
        this.format = format;
    }

    @Override
    public void parse(InputStream inStream) throws Exception {
        if (listener != null) {
            listener.dispatchOnProgress(0, -1);
        }

        result = BitmapFactory.decodeStream(inStream);

        saveBitmap2File(result);

        if (listener != null) {
            listener.dispatchOnComplete();
        }
    }

    @Override
    public void parse(byte[] dataBytes) throws Exception {
        if (listener != null) {
            listener.dispatchOnProgress(0, dataBytes == null ? -1 : dataBytes.length);
        }

        result = BitmapFactory.decodeByteArray(dataBytes, 0, dataBytes.length);

        if (listener != null) {
            listener.dispatchOnProgress(dataBytes.length, dataBytes.length);
        }

        saveBitmap2File(result);

        if (listener != null) {
            listener.dispatchOnComplete();
        }
    }

    @Override
    public void parse(String dataString) throws Exception {
        if (listener != null) {
            listener.dispatchOnProgress(-1, -1);
        }

        if (debug) {
            Log.e(TAG, TAG + " not support !");
        }

        if (listener != null) {
            listener.dispatchOnComplete();
        }
    }

    @Override
    public Bitmap getResult() {
        return result;
    }

    @Override
    public void release() {
        if (result != null && result.isRecycled()) {
            result.recycle();
        }
        fileName = null;
        format = null;
        listener = null;
    }

    @Override
    public void setOnParseAdapter(OnParseAdapter listener) {
        this.listener = listener;
    }

    protected final void saveBitmap2File(Bitmap bitmap) throws Exception {
        if (!TextUtils.isEmpty(fileName)) {
            Bitmap.CompressFormat bitmap_format;
            if (format == null) {
                bitmap_format = Bitmap.CompressFormat.JPEG;
            } else {
                bitmap_format = format;
            }
            save(fileName, bitmap, bitmap_format);
        }
    }

    protected final File save(String name, Bitmap bitmap, Bitmap.CompressFormat format) throws Exception {
        File target_file = null;

        boolean isException = false;
        FileOutputStream fout = null;
        //检测目录是否存在
        File p = new File(name.substring(0, name.lastIndexOf(File.separator)));
        if (!p.exists()) {
            p.mkdirs();
        }
        //写入 文件
        try {
            fout = new FileOutputStream(name);
            bitmap.compress(format, 100, fout);
            target_file = new File(name);
        } catch (Exception e) {
            isException = true;
        } finally {
            if (fout != null) {
                fout.flush();
                fout.close();
                fout = null;
            }
            if (isException) {
                throw new Exception("save bitmap exception");
            }
        }
        return target_file;
    }

}
