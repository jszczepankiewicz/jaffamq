package org.jaffamq.test;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: urwisy
 * Date: 29.11.13
 * Time: 20:43
 * To change this template use File | Settings | File Templates.
 */
public class IOTestHelper {

    public static String getTempDataDir() {
        String tmpDir = System.getProperty("java.io.tmpdir");
        tmpDir = tmpDir.endsWith(File.separator) ? tmpDir : tmpDir.concat(File.separator);
        return tmpDir + "torpidomq.tst" + File.separator + System.currentTimeMillis();
    }
}
