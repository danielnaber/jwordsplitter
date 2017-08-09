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
package de.danielnaber.jwordsplitter;

import de.danielnaber.jwordsplitter.tools.FileTools;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Exceptions for splitting loaded from a file.
 */
class ExceptionSplits {

    private static final String COMMENT_CHAR = "#";
    private static final String DELIMITER_CHAR = "|";

    private final Map<String,List<String>> exceptionMap = new HashMap<>();

    /**
     * Create an empty list of exceptions.
     */
    ExceptionSplits() {
    }
    
    ExceptionSplits(String filename) throws IOException {
        try (InputStream is = AbstractWordSplitter.class.getResourceAsStream(filename)) {
            if (is == null) {
                throw new IOException("Cannot locate exception list in class path: " + filename);
            }
            String exceptions = FileTools.loadFile(is, "UTF-8");
            try (Scanner scanner = new Scanner(exceptions)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (!line.isEmpty() && !line.startsWith(COMMENT_CHAR)) {
                        String[] parts = line.replace("/NS", "").split("\\|");
                        String completeWord = line.replace(DELIMITER_CHAR, "");
                        List<String> list = new ArrayList<>(Arrays.asList(parts));
                        if (completeWord.contains("/")) {
                            if (completeWord.endsWith("/NS")) {
                                String realWord = completeWord.replace("/NS", "").toLowerCase();
                                exceptionMap.put(realWord, list);
                                exceptionMap.put(realWord + "n", addToLastPart(list, "n"));
                                exceptionMap.put(realWord + "s", addToLastPart(list, "s"));
                            } else {
                                throw new RuntimeException("Unknown suffix in line: " + line);
                            }
                        } else {
                            exceptionMap.put(completeWord.toLowerCase(), list);
                        }
                    }
                }
            }
        }
    }

    private List<String> addToLastPart(List<String> list, String suffix) {
        List<String> result = new ArrayList<>(list);
        int lastPos = result.size() - 1;
        result.set(lastPos, result.get(lastPos) + suffix);
        return result;
    }

    List<String> getExceptionSplitOrNull(String word) {
        String lcWord = word.toLowerCase();
        List<String> result = exceptionMap.get(lcWord);
        if (result != null) {
            // The following code will only get executed if an exception split is encountered
            String check = join(result, "");
            if (lcWord.equals(check.toLowerCase())) {
                // The recombined, lowercased split-word is equal to the lowercase original word
                // Generate the pieces by splitting the original word with the same string lengths
                // as the splitted word. This will preserve the case of the original word
                result = splitEqually(result, word);
            }
        }
        return result;
    }

    protected List<String> splitEqually(List<String> splitted, String original) {
        List<String> list = new ArrayList<>();
        int offset = 0;
        for (String s : splitted) {
            int length = s.length();
            list.add(original.substring(offset, offset+length));
            offset += length;
        }
        return list;
    }

    protected String join(List<String> elements, String separator) {
        StringBuilder builder = new StringBuilder();
        Iterator<String> iter = elements.iterator();
        if (iter.hasNext()) {
            builder.append(iter.next());
            while (iter.hasNext()) {
                builder.append(separator).append(iter.next());
            }
        }
        return builder.toString();
    }

    void addSplit(String word, List<String> wordParts) {
        exceptionMap.put(word.toLowerCase(), wordParts);
    }
}
