package org.pinwheel.demo4agility.entity;

import java.io.Serializable;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public class ECMobileEntity {

    public static class User implements Serializable {
        public String id;
        public String name;
        public String rank_name;
        public int rank_level;
        public String collection_num;
        public String email;
    }

    public static class Session implements Serializable {
        public String uid;
        public String sid;
    }

}
