package de.abelssoft.wordtools.jwordsplitter.impl;

import de.abelssoft.wordtools.jwordsplitter.AbstractWordSplitter;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Scanner;

public class GermanRegressionTest extends TestCase {

    private static final String TEST_FILE = "/test-de-large.txt";

    public void testLargeFile() throws IOException {
        final AbstractWordSplitter splitter = new GermanWordSplitter(true);
        splitter.setStrictMode(true);
        final InputStream is = BaseTest.class.getResourceAsStream(TEST_FILE);
        try {
            if (is == null) {
                throw new RuntimeException("Could not load " + TEST_FILE + " from classpath");
            }
            int diffCount = 0;
            final Scanner scanner = new Scanner(is);
            while (scanner.hasNextLine()) {
                final String line = scanner.nextLine();
                final String input = line.replace(", ", "");
                final String result = join(splitter.splitWord(input));
                if (!line.equals(result)) {
                    System.out.println("-" + line);
                    System.out.println("+" + result);
                    diffCount++;
                }
            }
            scanner.close();
            if (diffCount > 0 ) {
                fail("Found differences between regression data and real result (see above) - modify " + TEST_FILE
                        + " to contain the results if they are better than before");
            }
        } finally {
            if (is != null) is.close();
        }
    }
    
    private String join(Collection<String> list) {
        final StringBuilder sb = new StringBuilder();
        int i = 0;
        for (String item : list) {
            if (i++ > 0) {
                sb.append(", ");
            }
            sb.append(item);
        }
        return sb.toString();
    }

}
