package org.pinwheel.demo4agility.field;

import org.pinwheel.agility.field.Ignore;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved
 *
 * @author dnwang
 */
public class DemoEntity {

    public int i_aa = 1111;
    private int i_bb = 2222;
    protected int i_cc = 333;
    private Integer i_dd = new Integer(444);

    public String s_aa = "aaaaa";
    private String s_bb = "bbbbb";
    protected String s_cc = "ccccc";

    public boolean b_aa = true;
    private boolean b_bb = false;
    protected boolean b_cc = false;
    private Boolean b_dd = new Boolean(true);

    private double d_aa = 1.11;
    @Ignore
    private double d_bb = 2.22;

    private TestStruct struct = new TestStruct();

    public static class TestStruct {
        public String struct = "in_struct";

        @Override
        public String toString() {
            return struct;
        }
    }

}
