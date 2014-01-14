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

    private final Map<String,List<WordPart>> exceptionMap = new HashMap<String, List<WordPart>>();

    ExceptionSplits(String filename) throws IOException {
        final InputStream is = AbstractWordSplitter.class.getResourceAsStream(filename);
        try {
            if (is == null) {
                throw new IOException("Cannot locate exception list in class path: " + filename);
            }
            final String exceptions = FileTools.loadFile(is, "UTF-8");
            final Scanner scanner = new Scanner(exceptions);
            while (scanner.hasNextLine()) {
                final String line = scanner.nextLine().trim();
                if (!line.isEmpty() && !line.startsWith(COMMENT_CHAR)) {
                    final WordPart wpLine = new WordPart(line);
                    final List<WordPart> parts = wpLine.exceptionSplit("\\|");
                    final String completeWord = line.replace(DELIMITER_CHAR, "");
                    exceptionMap.put(completeWord.toLowerCase(), parts);

                }
            }
            scanner.close();
        } finally {
            if (is != null) is.close();
        }
    }

    List<WordPart> getExceptionSplitOrNull(WordPart word) {
        WordPart lcWord = word.toLowerCase();
        List<WordPart> result = exceptionMap.get(lcWord.toString());
        if (result != null) {
            // The following code will only get executed if an exception split is encountered
            WordPart check = join(result, "");
            if (lcWord.toString().equals(check.toLowerCase().toString())) {
                // The recombined, lowercased split-word (string) is equal to the lowercase original word
                // Generate the pieces by splitting the original word with the same string lengths
                // as the splitted word. This will preserve the case of the original word
                result = splitEqually(result, word);
            }
        }
        return result;
    }

    protected List<WordPart> splitEqually(List<WordPart> splitted, WordPart original) {
        List<WordPart> list = new ArrayList<WordPart>();
        Iterator<WordPart> iter = splitted.iterator();
        int offset = 0;

        while (iter.hasNext()) {
            int length = iter.next().length();
            list.add(original.substring(offset, offset+length));
            offset += length;
        }
        return list;
    }

    protected WordPart join(List<WordPart> elements, String separator) {
        StringBuilder builder = new StringBuilder();
        Iterator<WordPart> iter = elements.iterator();

        if (iter.hasNext()) {
            builder.append(iter.next().toString());
            while (iter.hasNext()) {
                builder.append(separator).append(iter.next().toString());
            }
        }

        return new WordPart(builder.toString());
    }

    void addSplit(String word, List<WordPart> wordParts) {
        exceptionMap.put(word.toLowerCase(), wordParts);
    }
}
