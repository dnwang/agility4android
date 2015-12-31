package org.pinwheel.demo4agility.entity.field;

import android.util.Log;
import org.pinwheel.agility.field.Ignore;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class InjectStruct {

    public String arg0;
    int arg1;
    protected float arg2;
    private boolean arg3;
    private long arg4;
    private short arg5;
    private char arg10;

    @Ignore
    private double arg6;

    private FieldSession1 arg7;
    private FieldSession3 arg8;

//    private List<FieldSession3> arg9;

    public InjectStruct() {
        Log.e("------------------", "InjectStruct()");
//        arg2 = 1.0f;
//        arg3 = true;
//        arg6 = 0.12313;
//        arg1 = 111;
//        arg4 = 222;
//        arg5 = 333;
//        arg0 = "im field";
//
//        arg7 = new FieldSession1();
//
//        arg9 = Arrays.asList(new FieldSession3(), new FieldSession3());
    }

    public static class FieldSession1 {

        public String arg0;
        private int arg1;

        private FieldSession2 arg2;

        // 无构造
        public FieldSession1(FieldSession2 arg2) {
            Log.e("------------------", "FieldSession1(FieldSession2 arg2)");
//            arg2 = new FieldSession2();
//            arg1 = 111;
//            arg0 = "im session1";
        }

    }

    public static class FieldSession2 {

        public String arg0;
        private int arg1;

//        public FieldSession2() {
//            Log.e("------------------", "FieldSession2()");
//            arg1 = 111;
//            arg0 = "im session2";
//        }

    }

    public static class FieldSession3 {

        public String arg0;
        private int arg1;
        private FieldSession1 arg2;

        // 多构造
//        public FieldSession3() {
//            arg2 = new FieldSession1();
//            arg1 = 3;
//            arg0 = "im session3";
//        }

        public FieldSession3(FieldSession1 session1, String arg0, int arg1) {
            Log.e("------------------", "FieldSession3(FieldSession1 session1, String arg0, int arg1)");
            arg2 = session1;
        }

//        public FieldSession3(String arg0) {
//            Log.e("------------------", "FieldSession3(String arg0)");
//            this.arg0 = arg0;
//        }

//        public FieldSession3(int arg1) {
//            Log.e("------------------", "FieldSession3(int arg1)");
//            this.arg1 = arg1;
//        }

    }

}
