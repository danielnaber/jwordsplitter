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
package de.danielnaber.wordtools.jwordsplitter.converter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import de.danielnaber.tools.FastObjectSaver;

/**
 * This dumps the contents of the given *.ser file in the JAR to stdout.
 *
 * @author Sven Abels
 */
public class ExportDict {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: " + ExportDict.class.getSimpleName() + " <file in JAR>");
            System.out.println("  for example: ExportDict /de/danielnaber/wordsGerman.ser");
            System.exit(1);
        }
        final String filename = args[0];
        final Object obj = FastObjectSaver.load(filename);
        final Set<String> words = (HashSet<String>) obj;
        for (String word : words) {
            System.out.println(word);
        }
    }

}
