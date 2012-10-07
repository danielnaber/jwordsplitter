/**
 * Copyright 2012 Daniel Naber (www.danielnaber.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.abelssoft.wordtools.jwordsplitter;

import junit.framework.TestCase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Scanner;

public class GermanRegressionTest extends TestCase {

    private static final String TEST_FILE = "/de/abelssoft/test-de-large.txt";
    private static final boolean WRITE_FILE = false;

    private File tempFile;

    public void testLargeFile() throws IOException {
        final AbstractWordSplitter splitter = new GermanWordSplitter(true);
        splitter.setStrictMode(true);
        final FileWriter writer = getOutputWriterOrNull();
        final InputStream is = BaseTest.class.getResourceAsStream(TEST_FILE);
        final StringBuilder sb = new StringBuilder();
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
                if (writer != null) {
                    writer.write(result);
                    writer.write("\n");
                }
                if (!line.equals(result)) {
                    sb.append("-");
                    sb.append(line);
                    sb.append("\n");
                    sb.append("+");
                    sb.append(result);
                    sb.append("\n");
                    diffCount++;
                }
            }
            scanner.close();
            if (diffCount > 0 ) {
                final String message = writer != null ?
                        "output can be found at " + tempFile : "set WRITE_FILE to true to write output to a file";
                fail("Found differences between regression data and real result - modify " + TEST_FILE
                        + " to contain the results if they are better than before (" + message + "):\n" + sb.toString());
            }
        } finally {
            if (is != null) is.close();
            if (writer != null) writer.close();
        }
    }

    private FileWriter getOutputWriterOrNull() throws IOException {
        if (WRITE_FILE) {
            tempFile = File.createTempFile(GermanRegressionTest.class.getName(), ".txt");
            return new FileWriter(tempFile);
        }
        return null;
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