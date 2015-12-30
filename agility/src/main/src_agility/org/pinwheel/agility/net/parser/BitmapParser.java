package org.pinwheel.agility.net.parser;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

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
public class BitmapParser extends DataParserAdapter<Bitmap> {

    private Bitmap result;
    private String fileName;
    private Bitmap.CompressFormat format;

    public BitmapParser(File fileName, Bitmap.CompressFormat format) {
        this(fileName.getAbsolutePath(), format);
    }

    public BitmapParser(String fileName, Bitmap.CompressFormat format) {
        this.fileName = fileName;
        this.format = format;
    }

    @Override
    public void parse(InputStream inStream) throws Exception {
        dispatchOnProgress(0, -1);

        result = BitmapFactory.decodeStream(inStream);

        saveBitmap2File(result);

        dispatchOnComplete();
    }

    @Override
    public void parse(byte[] dataBytes) throws Exception {
        dispatchOnProgress(0, dataBytes == null ? -1 : dataBytes.length);

        result = BitmapFactory.decodeByteArray(dataBytes, 0, dataBytes.length);

        dispatchOnProgress(dataBytes.length, dataBytes.length);

        saveBitmap2File(result);

        dispatchOnComplete();
    }

    @Override
    public void parse(String dataString) throws Exception {
        dispatchOnProgress(-1, -1);
        dispatchOnComplete();
    }

    @Override
    public Bitmap getResult() {
        return result;
    }

    @Override
    public void release() {
        super.release();
        if (result != null && result.isRecycled()) {
            result.recycle();
        }
        fileName = null;
        format = null;
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
        File p = new File(name.substring(0, name.lastIndexOf(File.separator)));
        if (!p.exists()) {
            p.mkdirs();
        }
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
