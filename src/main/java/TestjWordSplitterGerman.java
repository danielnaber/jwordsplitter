/**
 * Copyright 2004-2007 Sven Abels
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import de.abelssoft.wordtools.jwordsplitter.AbstractWordSplitter;
import de.abelssoft.wordtools.jwordsplitter.impl.GermanWordSplitter;

/**
 * Simple command-line tool for decomposing German compound words.
 *
 * @author Daniel Naber
 */
public class TestjWordSplitterGerman {

    public static void main(String[] args) throws IOException {
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
        AbstractWordSplitter ws = new GermanWordSplitter(hideGlueChars, plainDict);
        ws.setStrictMode(true);
        BufferedReader br = new BufferedReader(new FileReader(inputFile));
        String line;
        while ((line = br.readLine()) != null) {
            Collection<String> col = ws.splitWord(line);
            for (Iterator<String> it = col.iterator(); it.hasNext();) {
                System.out.print(it.next());
                if (it.hasNext())
                    System.out.print(", ");
            }
            System.out.println();
        }
        br.close();
    }

    private static void usage() {
        System.out.println("Usage: TestjWordSplitterGerman [-f] [-d dictionary] <file>");
        System.out.println("    -f  Fugenelemente mit ausgeben");
        System.out.println("    -d  Wortliste mit potentiellen Komposita-Teilen (statt der integrierten)");
        System.exit(1);
    }

}
