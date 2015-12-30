package org.pinwheel.agility.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public final class FileUtils {

    private FileUtils() {

    }

    /**
     * Save data from inputStream
     *
     * @param name
     * @param inStream
     * @throws Exception
     */
    public static File save(String name, InputStream inStream) throws Exception {
        File target_file = null;

        boolean isException = false;
        FileOutputStream fout = null;
        File p = new File(name.substring(0, name.lastIndexOf(File.separator)));
        if (!p.exists()) {
            p.mkdirs();
        }
        try {
            fout = new FileOutputStream(name);
            byte[] buf = new byte[1024] ;
            int flag = 0 ;
            while((flag = inStream.read(buf))!=-1){
                fout.write(buf, 0, flag) ;
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

    /**
     * Save data from byte[]
     *
     * @param name
     * @param data
     * @throws Exception
     */
    public static File save(String name, byte[] data) throws Exception {
        File target_file = null;

        boolean isException = false;
        FileOutputStream fout = null;
        File p = new File(name.substring(0, name.lastIndexOf(File.separator)));
        if (!p.exists()) {
            p.mkdirs();
        }
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

    /**
     * Save bitmap
     *
     * @param name
     * @param bitmap
     * @throws Exception
     */
    public static File save(String name, Bitmap bitmap, Bitmap.CompressFormat format) throws Exception {
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

    /**
     * Add date in an old file
     *
     * @param name
     * @param data
     * @throws Exception
     */
    public static void add(String name, byte[] data) throws Exception {
        boolean isException = false;
        FileOutputStream fout = null;
        File p = new File(name.substring(0, name.lastIndexOf(File.separator)));
        if (!p.exists()) {
            p.mkdirs();
        }
        try {
            fout = new FileOutputStream(name, true);
            fout.write(data);
        } catch (Exception e) {
            isException = true;
        } finally {
            if (fout != null) {
                fout.flush();
                fout.close();
                fout = null;
            }
            if (isException) {
                throw new Exception("Add byte exception");
            }
        }
    }

    /**
     * Load string from file
     *
     * @param name
     * @return
     */
    public static String loadString(String name) {
        String data = "";
        try {
            data = streamToString(new FileInputStream(name));
        } catch (Exception e) {
            data = "";
        }
        return data;
    }

    /**
     * Load bitmap from file
     *
     * @param name
     * @return
     */
    public static Bitmap loadBitmap(String name) {
        Bitmap bitmap = null;
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(name);
            bitmap = BitmapFactory.decodeStream(fin);
        } catch (Exception e) {
            try {
                if (fin != null)
                    fin.close();
            } catch (IOException e1) {
            }
            bitmap = null;
        }
        return bitmap;
    }

    /**
     * delete file or directory
     *
     * @param file
     * @return
     */
    public static boolean delete(File file) {
        try {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    delete(f);
                }
                file.delete();
            } else {
                file.delete();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * copy file
     *
     * @param in
     * @param outFile
     * @param isForce
     * @return
     * @throws Exception
     */
    public static boolean copy(InputStream in, File outFile, boolean isForce) throws Exception {
        if (outFile.exists()) {
            if (isForce) {
                delete(outFile);
                copy(in, new FileOutputStream(outFile));
                return true;
            } else
                return false;
        } else {
            copy(in, new FileOutputStream(outFile));
            return true;
        }
    }

    /**
     * search file
     *
     * @param path_str
     * @param file_name_key      [\s\S]*( + file_name_key + )[\s\S]*
     * @param isIgnoreHiddenFile
     * @return
     */
    private List<File> searchFile(String path_str, String file_name_key, boolean isIgnoreHiddenFile) {
        if (TextUtils.isEmpty(path_str) || TextUtils.isEmpty(file_name_key)) {
            return null;
        }
        File path = new File(path_str);
        if (!path.exists() || !path.isDirectory()) {
            return null;
        }
        if (isIgnoreHiddenFile && path.getName().startsWith(".")) {
            return null;
        }
        List<File> match_files = new ArrayList<File>(0);

        File[] files = path.listFiles();
        for (File sub_file : files) {
            if (sub_file.isDirectory()) {
                List<File> result = searchFile(sub_file.getAbsolutePath(), file_name_key, isIgnoreHiddenFile);
                if (result != null && result.size() > 0) {
                    match_files.addAll(result);
                }
            } else {
                String file_name = sub_file.getName();
                if (isIgnoreHiddenFile && file_name.startsWith(".")) {
                    continue;
                }
                if (file_name.matches(file_name_key)) {
                    match_files.add(sub_file);
                }
            }
        }
        return match_files;
    }

    private static void copy(InputStream in, OutputStream out) throws Exception {
        boolean isException = false;
        try {
            byte[] bf = new byte[1024];
            int length;
            while ((length = in.read(bf)) != -1) {
                out.write(bf, 0, length);
            }
        } catch (Exception e) {
            isException = true;
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.flush();
                out.close();
            }
            if (isException) {
                throw new Exception("copy file Exception");
            }
        }
    }

    private static String streamToString(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\r\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}

