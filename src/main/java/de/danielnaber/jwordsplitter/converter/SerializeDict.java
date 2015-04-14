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
package de.danielnaber.jwordsplitter.converter;

import de.danielnaber.jwordsplitter.tools.FastObjectSaver;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;

/**
 * This imports a plain text file and saves it in the jWordSplitter serialization format.
 *
 * @author Sven Abels
 */
public final class SerializeDict {

  private SerializeDict() {
  }

  private static HashSet<String> getFileContents(File file) throws IOException {
        final HashSet<String> lines = new HashSet<String>();
        final Scanner scanner = new Scanner(file);
        try {
            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine());
            }
        } finally {
            scanner.close();
        }
        return lines;
    }

    /**
     * Read a text file with one word per line and serialize the
     * HashSet with all words to a binary file that can be part
     * of jWordSplitter's JAR.
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Usage: " + SerializeDict.class.getSimpleName() + " <input> <output>");
            System.exit(1);
        }
        System.out.println("Reading " + args[0] + "...");
        final HashSet<String> wordSet = getFileContents(new File(args[0]));
        final File outputFile = new File(args[1]);
        System.out.println("Saving " + outputFile + "...");
        FastObjectSaver.saveToFile(outputFile, wordSet);
        System.out.println("Done.");
    }

}
