package org.pinwheel.agility.net.parser;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public final class BitmapParser implements IResponseParser<Bitmap> {

    private Bitmap result;
    private String file_name;
    private Bitmap.CompressFormat format;

    public BitmapParser() {
        this(null, null);
    }

    public BitmapParser(String file_name, Bitmap.CompressFormat format) {
        this.file_name = file_name;
        this.format = format;
    }

    @Override
    public void parse(InputStream inStream) throws Exception {
        result = BitmapFactory.decodeStream(inStream);
        saveBitmap2File(result);
    }

    @Override
    public void parse(byte[] dataBytes) throws Exception {
        result = BitmapFactory.decodeByteArray(dataBytes, 0, dataBytes.length);
        saveBitmap2File(result);
    }

    @Override
    public void parse(String dataString) throws Exception {

    }

    @Override
    public Bitmap getResult() {
        return result;
    }

    private void saveBitmap2File(Bitmap bitmap) throws Exception {
        if (!TextUtils.isEmpty(file_name)) {
            Bitmap.CompressFormat bitmap_format;
            if (format == null) {
                bitmap_format = Bitmap.CompressFormat.JPEG;
            } else {
                bitmap_format = format;
            }
            save(file_name, bitmap, bitmap_format);
        }
    }

    private File save(String name, Bitmap bitmap, Bitmap.CompressFormat format) throws Exception {
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
