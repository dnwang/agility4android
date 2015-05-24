package org.pinwheel.agility.util;

import java.io.DataOutputStream;
import java.io.IOException;

public final class Root implements Runnable {

    private Process process;
    private DataOutputStream dataOutputStream;
    private RootPermissionListener rootPermissionListener;

    public Root(RootPermissionListener rootPermissionListener) {
        this.rootPermissionListener = rootPermissionListener;
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            process = Runtime.getRuntime().exec("su");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            rootPermissionListener.isRoot(true);
        } catch (IOException e) {
            rootPermissionListener.isRoot(false);
            e.printStackTrace();
        }
    }

    public void root(String cmd) {
        try {
            dataOutputStream.writeBytes(cmd + "\n");
            dataOutputStream.flush();
        } catch (IOException e) {
            rootPermissionListener.isRoot(false);
            e.printStackTrace();
        }
    }

    public void recycle() {
        try {
            dataOutputStream.flush();
            dataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        process.destroy();
    }

    public interface RootPermissionListener {
        void isRoot(boolean root);
    }

}
