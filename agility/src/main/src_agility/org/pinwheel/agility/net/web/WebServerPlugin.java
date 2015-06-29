package org.pinwheel.agility.net.web;

import java.io.File;
import java.util.Map;


/**
 * @author Paul S. Hawke (paul.hawke@gmail.com)
 *         On: 9/14/13 at 8:09 AM
 */
interface WebServerPlugin {

    void initialize(Map<String, String> commandLineOptions);

    boolean canServeUri(String uri, File rootDir);

    NanoHTTPD.Response serveFile(String uri, Map<String, String> headers, NanoHTTPD.IHTTPSession session, File file, String mimeType);
}
