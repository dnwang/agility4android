package org.pinwheel.agility.net.web;

/**
* @author Paul S. Hawke (paul.hawke@gmail.com)
*         On: 9/14/13 at 8:09 AM
*/
interface WebServerPluginInfo {
    String[] getMimeTypes();

    String[] getIndexFilesForMimeType(String mime);

    WebServerPlugin getWebServerPlugin(String mimeType);
}
