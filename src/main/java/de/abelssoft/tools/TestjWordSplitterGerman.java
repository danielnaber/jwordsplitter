/**
 * Copyright 2004-2007 Sven Abels
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
package de.abelssoft.tools;

import de.abelssoft.wordtools.jwordsplitter.AbstractWordSplitter;
import de.abelssoft.wordtools.jwordsplitter.GermanWordSplitter;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Simple command-line tool for decomposing German compound words.
 *
 * @author Daniel Naber
 */
public class TestjWordSplitterGerman {

    public static void main(String[] args) throws IOException {
        final long startTime = System.currentTimeMillis();
        if (args.length < 1 || args.length > 4 || args[0].equals("-h") || args[0].equals("--help")) {
            usage();
        }
        int argCount = 0;
        String plainDict = null;
        boolean hideGlueChars = true;
        while (argCount < args.length) {
            if (args[argCount].equals("-h")) {
                usage();
            } else if (args[argCount].equals("-d")) {
                plainDict = args[++argCount];
            } else if (args[argCount].equals("-f")) {
                hideGlueChars = false;
            }
            argCount++;
        }
        final String inputFile = args[argCount-1];
        final AbstractWordSplitter wordSplitter;
        if (plainDict != null) {
            wordSplitter = new GermanWordSplitter(hideGlueChars, new File(plainDict));
        } else {
            wordSplitter = new GermanWordSplitter(hideGlueChars);
        }
        wordSplitter.setStrictMode(true);
        final Scanner scanner = new Scanner(new File(inputFile));
        try {
            while (scanner.hasNext()) {
                final String token = scanner.next();
                final Collection<String> col = wordSplitter.splitWord(token);
                for (Iterator<String> it = col.iterator(); it.hasNext();) {
                    System.out.print(it.next());
                    if (it.hasNext()) {
                        System.out.print(", ");
                    }
                }
                System.out.println();
            }
        } finally {
            scanner.close();
        }
        System.err.println("Time: " + (System.currentTimeMillis()-startTime)+ "ms");
    }

    private static void usage() {
        System.out.println("Usage: TestjWordSplitterGerman [-f] [-d dictionary] <file>");
        System.out.println("    <file> Textdatei mit zu zerlegenden Wörtern");
        System.out.println("    -f  Fugenelemente mit ausgeben");
        System.out.println("    -d  Wortliste mit potenziellen Komposita-Teilen (statt der integrierten)");
        System.exit(1);
    }

}
