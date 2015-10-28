package org.pinwheel.agility.net.parser;

import java.io.InputStream;

/**
 * Copyright (C), 2015 <br>
 * <br>
 * All rights reserved <br>
 * <br>
 *
 * @author dnwang
 */
public interface IDataParser<T> {

    /**
     * @param inStream
     * @throws Exception
     */
    public void parse(InputStream inStream) throws Exception;

    /**
     * @param dataBytes
     * @throws Exception
     */
    public void parse(byte[] dataBytes) throws Exception;

    /**
     * @param dataString
     * @throws Exception
     */
    public void parse(String dataString) throws Exception;

    /**
     * Get parser result
     *
     * @return T
     */
    public T getResult();

}
