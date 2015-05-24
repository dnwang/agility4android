package org.pinwheel.agility.net.parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public final class FileParser implements IResponseParser<File> {

    private File result;
    private String file_name;

    public FileParser(String file_name) {
        this.file_name = file_name;
    }

    @Override
    public void parse(InputStream inStream) throws Exception {
        result = save(file_name, inStream);
    }

    @Override
    public void parse(byte[] dataBytes) throws Exception {
        result = save(file_name, dataBytes);
    }

    @Override
    public void parse(String dataString) throws Exception {
        result = save(file_name, dataString.getBytes());
    }

    @Override
    public File getResult() {
        return result;
    }

    private File save(String name, InputStream inStream) throws Exception {
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
            byte[] buf = new byte[1024];
            int flag = 0;
            while ((flag = inStream.read(buf)) != -1) {
                fout.write(buf, 0, flag);
            }
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
                throw new Exception("save stream exception");
            }
        }
        return target_file;
    }


    private File save(String name, byte[] data) throws Exception {
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
            fout.write(data);

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
                throw new Exception("save byte exception");
            }
        }
        return target_file;
    }

}
