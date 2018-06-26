package de.unibi.citec.clf.btl.xml;


import static org.junit.Assert.fail;

/**
 * Utility functions for testing.
 *
 * @author jwienke
 */
public class TestUtils {

    public static String makeTestFileName(String filename) {

        String root = System.getProperty("testresources");

        if (root == null) {
            return TestUtils.class.getClassLoader().getResource(filename).getPath();
            //fail("Property testresources not set.");
        }

        return root + "/" + filename;

    }

}
