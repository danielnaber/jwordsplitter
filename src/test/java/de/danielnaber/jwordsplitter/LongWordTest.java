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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Print long words that cannot be decomposed - this might indicate
 * a missing entry in the dictionary (not a unit test, but for interactive use).
 */
public class LongWordTest {

    private static final int MIN_LENGTH = 15;

    private final Set<String> words = loadLines(new File("src/main/resources/de/danielnaber/jwordsplitter/languagetool-dict.txt"));
    private final Map<String,Integer> occurrences = loadLowercaseOccurrences(new File("/media/Data/google-ngram/de/1gram-aggregated/all_without_underscore"));

    public void printLongNonCompounds(File file) throws IOException {
        GermanWordSplitter splitter = new GermanWordSplitter(true);
        int i = 0;
        try (Scanner scanner = new Scanner(file, "utf-8")) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                List<String> parts = splitter.splitWord(line);
                boolean uppercase = line.length() > 0 && Character.isUpperCase(line.charAt(0));
                if (uppercase & parts.size() <= 1 && line.length() > MIN_LENGTH) {
                    String source = getOccurrenceInfo(words, splitter, line);
                    System.out.println(i + ". " + line);
                    System.err.println(source);
                    i++;
                }
            }
        }
    }

    private String getOccurrenceInfo(Set<String> words, GermanWordSplitter splitter, String line) {
        String source;
        if (words.contains(line.toLowerCase())) {
            source = " [in list]";
        } else {
            List<List<String>> allSplits = splitter.getAllSplits(line);
            StringBuilder sb = new StringBuilder("\n");
            for (List<String> allSplit : allSplits) {
                for (String s : allSplit) {
                    if (!words.contains(s.toLowerCase())) {
                        sb.append(occurrences.get(s.toLowerCase())).append(" ").append(s).append("\n");
                    }
                }
            }
            source = sb.toString();
        }
        return source;
    }

    private Set<String> loadLines(File file) {
        Set<String> result = new HashSet<>();
        try (Scanner scanner = new Scanner(file, "utf-8")) {
            while (scanner.hasNextLine()) {
                result.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    private Map<String,Integer> loadLowercaseOccurrences(File file) {
        System.err.println("Loading occurrences...");
        Map<String,Integer> result = new HashMap<>();
        try (Scanner scanner = new Scanner(file, "utf-8")) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(" ");  
                result.put(parts[0].toLowerCase(), Integer.valueOf(parts[1]));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.err.println("Loaded " + result.size() + " occurrences");
        return result;
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
