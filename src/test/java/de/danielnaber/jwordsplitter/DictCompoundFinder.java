/* LanguageTool, a natural language style checker 
 * Copyright (C) 2015 Daniel Naber (http://www.danielnaber.de)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package de.danielnaber.jwordsplitter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * Possible solution for https://github.com/danielnaber/jwordsplitter/issues/6,
 * prints new values for the removals.txt file.
 */
class DictCompoundFinder {

    private final GermanWordSplitter splitter;
    private final Set<String> words = load(new File("src/main/resources/de/danielnaber/jwordsplitter/languagetool-dict.txt"));
    // words that are needed to split compounds which we remove, these may thus not be removed themselves:
    private final Set<String> protectedWords = new HashSet<>();
    
    private int removeCount = 0;

    DictCompoundFinder() throws IOException {
        splitter = new GermanWordSplitter(true);
    }

    private void run() throws IOException {
        for (String word : words) {
            isIgnorableCompound(word);
        }
    }

    private Set<String> load(File file) throws FileNotFoundException {
        Set<String> result = new HashSet<>();
        try (Scanner scanner = new Scanner(file, "utf-8")) {
            while (scanner.hasNextLine()) {
                result.add(scanner.nextLine());
            }
        }
        return result;
    }
    
    private void isIgnorableCompound(String word) {
        List<List<String>> splits = splitter.getAllSplits(word);
        for (List<String> split : splits) {
            if (!protectedWords.contains(word) && words.containsAll(split)) {
                //System.out.println(splitCount + " " +  removeCount + ". REMOVE: " + word + " ("+split+")");
                System.out.println("^" + word + "$");
                protectedWords.addAll(split);
                removeCount++;
                break;
            }
        }
    }

    public static void main(String[] args) throws IOException {
        DictCompoundFinder prg = new DictCompoundFinder();
        prg.run();
    }
}
