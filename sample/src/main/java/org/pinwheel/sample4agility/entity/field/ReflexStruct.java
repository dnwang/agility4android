package org.pinwheel.sample4agility.entity.field;

import org.pinwheel.agility.util.FieldUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class ReflexStruct {

    public String arg0;
    int arg1;
    protected float arg2;
    private boolean arg3;
    private long arg4;
    private short arg5;

    @FieldUtils.Ignore
    private double arg6;

    private FieldSession1 arg7;
    private FieldSession3 arg8; // test null

    private List<FieldSession3> arg9;

    public ReflexStruct() {
        arg2 = 1.0f;
        arg3 = true;
        arg6 = 0.12313;
        arg1 = 111;
        arg4 = 222;
        arg5 = 333;
        arg0 = "im field";

        arg7 = new FieldSession1();

        arg9 = Arrays.asList(new FieldSession3(), new FieldSession3());
    }

    public static class FieldSession1 {

        public String arg0;
        private int arg1;

        private FieldSession2 arg2;

        public FieldSession1() {
            arg2 = new FieldSession2();
            arg1 = 111;
            arg0 = "im session1";
        }

    }

    public static class FieldSession2 {

        public String arg0;
        private int arg1;

        public FieldSession2() {
            arg1 = 111;
            arg0 = "im session2";
        }

    }

    public static class FieldSession3 {

        public String arg0;
        private int arg1;
        private FieldSession1 arg2;

        public FieldSession3() {
            arg2 = new FieldSession1();
            arg1 = 3;
            arg0 = "im session3";
        }

    }

}
