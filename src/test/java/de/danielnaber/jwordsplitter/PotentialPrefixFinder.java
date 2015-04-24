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

import de.danielnaber.jwordsplitter.tools.FileTools;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Simple program to find potential prefixes like "Miet", "Hilf" that
 * are themselves not nouns.
 */
public class PotentialPrefixFinder {

    private final Set<String> words = TestTools.loadLines(new File("src/main/resources/de/danielnaber/jwordsplitter/languagetool-dict.txt"));
    private final Set<String> knownPrefixes = TestTools.loadLines(new File("src/main/resources/de/danielnaber/jwordsplitter/germanPrefixes.txt"));
    
    private void run(File file) throws IOException {
        Set<String> compounds = FileTools.loadFileToSet(new FileInputStream(file), "utf-8");
        List<String> sortedCompounds = new ArrayList<>(compounds);
        Collections.sort(sortedCompounds);
        Map<String,Integer> prefixCount = new HashMap<>();
        for (String compound : sortedCompounds) {
            String prefix = findPrefixOfSplit(compound);
            if (prefix != null) {
                Integer count = prefixCount.get(prefix);
                if (count != null) {
                    prefixCount.put(prefix, count + 1);
                } else {
                    prefixCount.put(prefix, 1);
                }
            }
        }
        for (Map.Entry<String, Integer> entry : prefixCount.entrySet()) {
            System.out.println(entry.getValue() + " " + entry.getKey());
        }
    }

    @Nullable
    private String findPrefixOfSplit(String word) {
        for (int i = 2; i < word.length() - 2; i++) {
            String left = word.substring(0, i).toLowerCase();
            String right = word.substring(i);
            if (!words.contains(left) && !words.contains(left.replaceAll("s$", "")) && words.contains(right) && !knownPrefixes.contains(left)) {
                return left;
            }
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: " + PotentialPrefixFinder.class.getSimpleName() + " <compoundFile>");
            System.exit(1);
        }
        PotentialPrefixFinder prg = new PotentialPrefixFinder();
        prg.run(new File(args[0]));
    }

}
