/**
 * Copyright 2015 Daniel Naber (www.danielnaber.de)
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
package de.danielnaber.jwordsplitter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Print long words that cannot be decomposed - this might indicate
 * a missing entry in the dictionary (not a unit test, but for interactive use).
 */
public class LongWordTest {

    private static final int MIN_LENGTH = 15;

    public void printLongNonCompounds(File file) throws IOException {
        GermanWordSplitter splitter = new GermanWordSplitter(true);
        int i = 0;
        try (Scanner scanner = new Scanner(file, "utf-8")) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                List<String> parts = splitter.splitWord(line);
                boolean uppercase = line.length() > 0 && Character.isUpperCase(line.charAt(0));
                if (uppercase & parts.size() <= 1 && line.length() > MIN_LENGTH) {
                    System.out.println(i + ". " + line);
                    i++;
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: " + LongWordTest.class.getName() + " <file>");
            System.exit(1);
        }
        LongWordTest prg = new LongWordTest();
        prg.printLongNonCompounds(new File(args[0]));
    }

}
