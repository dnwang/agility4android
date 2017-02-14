package org.pinwheel.sample.entity;

import java.util.ArrayList;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved.
 *
 * @author Json2Class
 */
public class AutoJsonStruct {
    public Struct0 secret_keys;
    public Struct5 api_encrypt;
    public Struct8 result;

    /**
     * Temporary class name, create by Json2Class.
     */
    public static class Struct0 {
        public ArrayList<Struct2> l;
    }

    /**
     * Temporary class name, create by Json2Class.
     */
    public static class Struct1 {
        public ArrayList<Struct6> i;
    }

    /**
     * Temporary class name, create by Json2Class.
     */
    public static class Struct2 {
        public int decode_type;
        public Struct4 il;
    }

    /**
     * Temporary class name, create by Json2Class.
     */
    public static class Struct3 {
        public int sign;
        public String request_encrypt_key;
        public String response_encrypt_key;
    }

    /**
     * Temporary class name, create by Json2Class.
     */
    public static class Struct4 {
        public ArrayList<Struct3> i;
    }

    /**
     * Temporary class name, create by Json2Class.
     */
    public static class Struct5 {
        public Struct1 il;
    }

    /**
     * Temporary class name, create by Json2Class.
     */
    public static class Struct6 {
        public String sign;
        public String request_encrypt_mode;
        public String response_encrypt_mode;
    }

    /**
     * Temporary class name, create by Json2Class.
     */
    public static class Struct8 {
        public int state;
        public String reason;
    }

}