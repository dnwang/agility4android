package org.pinwheel.agility.util.callback;

/**
 * Copyright (C), 2016 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 * @version 27/09/2016,22:34
 * @see
 */
public interface Function2<R, T, K> {

    R call(T obj0, K obj1);

}
