package org.pinwheel.demo4agility.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public final class WeatherEntity implements Serializable {

    //http://wthrcdn.etouch.cn/weather_mini?citykey=101010100

    String desc;
    String status;
    String aqi;
    String city;
    Data data;

    public static class Data implements Serializable {
        String wendu;
        String ganmao;
        ArrayList<Forecast> forecast;
    }

    public static class Forecast implements Serializable {
        String fengxiang;
        String fengli;
        String high;
        String type;
        String low;
        String date;
    }

}
