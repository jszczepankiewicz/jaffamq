package org.jaffamq.test;

import java.io.File;

/**
 * Test utilities around IO.
 */
public class IOTestHelper {

    /**
     * Returns temp dir name (without creation) inside TEMP dir that can be used for testing.
     * @return the value of unique temp dir
     */
    public static String getTempDataDir() {
        String tmpDir = System.getProperty("java.io.tmpdir");
        tmpDir = tmpDir.endsWith(File.separator) ? tmpDir : tmpDir.concat(File.separator);
        return tmpDir + "torpidomq.tst" + File.separator + System.currentTimeMillis();
    }
}
