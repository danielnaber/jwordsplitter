/*
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
package de.danielnaber.jwordsplitter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * This class can split compound words into their smallest parts (atoms). For example "Erhebungsfehler"
 * will be split into "erhebung" and "fehler", if "erhebung" and "fehler" are in the dictionary
 * and "erhebungsfehler" is not. Thus how words are split only depends on the contents of
 * the dictionary. A dictionary for German is included.
 *
 * <p>This is especially useful for German words but it will work with all languages.
 * The order of the words in the collection will be identical to their appearance in the
 * connected word. It's good to provide a large dictionary.
 *
 * <p>Please note: We don't expect to have any special chars here (!":;,.-_, etc.). Only a set of
 * characters and only one word.
 *
 * @author Daniel Naber
 */
public abstract class AbstractWordSplitter {

    protected abstract Set<String> getWordList(InputStream stream) throws IOException;
    protected abstract Set<String> getWordList() throws IOException;
    protected abstract GermanInterfixDisambiguator getDisambiguator();
    protected abstract int getDefaultMinimumWordLength();
    /** Interfix elements in lowercase, e.g. at least "s" for German. */
    protected abstract Collection<String> getInterfixCharacters();

    protected Set<String> words = null;

    private final boolean hideInterfixCharacters;

    private ExceptionSplits exceptionSplits = new ExceptionSplits();
    private boolean strictMode = true;
    private int minimumWordLength = getDefaultMinimumWordLength();

    /**
     * Create a word splitter that uses the embedded dictionary.
     *
     * @param hideInterfixCharacters whether the word parts returned by {@link #splitWord(String)} still contain
     *  the connecting character (a.k.a. interfix)
     */
    public AbstractWordSplitter(boolean hideInterfixCharacters) throws IOException {
        this.hideInterfixCharacters = hideInterfixCharacters;
        words = getWordList();
    }

    /**
     * @param hideInterfixCharacters whether the word parts returned by {@link #splitWord(String)} still contain
     *  the connecting character (a.k.a. interfix)
     * @param plainTextDict a stream of a text file with one word per line, to be used instead of the embedded dictionary,
     *                       must be in UTF-8 format
     */
    public AbstractWordSplitter(boolean hideInterfixCharacters, InputStream plainTextDict) throws IOException {
        this.hideInterfixCharacters = hideInterfixCharacters;
        words = getWordList(plainTextDict);
    }

    /**
     * @param hideInterfixCharacters whether the word parts returned by {@link #splitWord(String)} still contain
     *  the connecting character (a.k.a. interfix)
     * @param plainTextDict a stream of a text file with one word per line, to be used instead of the embedded dictionary,
     *                       must be in UTF-8 format
     */
    public AbstractWordSplitter(boolean hideInterfixCharacters, File plainTextDict) throws IOException {
        this.hideInterfixCharacters = hideInterfixCharacters;
        words = getWordList(plainTextDict);
    }

    /**
     * @param hideInterfixCharacters whether the word parts returned by {@link #splitWord(String)} still contain
     *  the connecting character (a.k.a. interfix)
     * @param words the compound part words
     * @since 4.1
     */
    public AbstractWordSplitter(boolean hideInterfixCharacters, Set<String> words) throws IOException {
        this.hideInterfixCharacters = hideInterfixCharacters;
        this.words = words;
    }

    private Set<String> getWordList(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            return getWordList(fis);
        }
    }

    public void setMinimumWordLength(int minimumWordLength) {
        this.minimumWordLength = minimumWordLength;
    }

    /**
     * @param filename UTF-8 encoded file with exceptions in the classpath, one exception per line, using pipe as delimiter.
     *   Example: <tt>Pilot|sendung</tt>
     */
    public void setExceptionFile(String filename) throws IOException {
        exceptionSplits = new ExceptionSplits(filename);
    }

    /**
     * @param completeWord the word for which an exception is to be defined (will be considered case-insensitive)
     * @param wordParts the parts in which the word is to be split (use a list with a single element if the word should not be split)
     */
    public void addException(String completeWord, List<String> wordParts) {
        exceptionSplits.addSplit(completeWord.toLowerCase(), wordParts);
    }

    /**
     * When set to true, words will only be split if all parts are words.
     * Otherwise the splitting result might contain parts that are not words.
     */
    public void setStrictMode(boolean strictMode) {
        this.strictMode = strictMode;
    }

    /**
     * Experimental: Split a word with unknown parts, typically because one part
     * has a typo. This could be used to split three-part compounds where one
     * part has a typo (the caller is then responsible for making useful corrections
     * out of these parts). Results are returned in no specific order.
     * @since 4.0
     */
    public List<List<String>> getAllSplits(String word) {
        try {
            List<List<String>> result1 = getAllSplits(word, true);
            List<List<String>> result2 = getAllSplits(word, false);
            List<List<String>> result = new ArrayList<>(result1);
            for (List<String> split : result2) {
                if (!result.contains(split)) {
                    result.add(split);
                }
            }
            return result;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    List<List<String>> getAllSplits(String word, boolean fromLeft) throws InterruptedException {
        List<List<String>> result = new ArrayList<>();
        int start = fromLeft ? minimumWordLength : word.length() - minimumWordLength;
        for (int i = start; isLoopEnd(fromLeft, i, word);) {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            String left = word.substring(0, i);
            String right = word.substring(i, word.length());
            String relevantWord = fromLeft ? left : right;
            boolean isSimpleWord = isSimpleWord(relevantWord);
            //System.out.println(i + ". " + left + " " + right + " -> " + relevantWord + " " + (isSimpleWord ? "***" : ""));
            if (isSimpleWord) {
                result.add(Arrays.asList(left, right));
                List<List<String>> otherSplits = getAllSplits(fromLeft ? right : left);
                if (otherSplits.size() > 0) {
                    for (List<String> otherSplit : otherSplits) {
                        List<String> sub = new ArrayList<>();
                        if (fromLeft) {
                            sub.add(left);
                            sub.addAll(otherSplit);
                        } else {
                            sub.addAll(otherSplit);
                            sub.add(right);
                        }
                        result.add(new ArrayList<>(sub));
                    }
                }
            }
            i = fromLeft ? i + 1 : i - 1;
        }
        return result;
    }

    private boolean isLoopEnd(boolean fromLeft, int i, String word) {
        if (fromLeft) {
            return i < word.length() - minimumWordLength;
        } else {
            return i > minimumWordLength;
        }
    }

    /**
     * @since 4.2
     */
    public List<String> getSubWords(String word) {
        return splitWord(word, true);
    }

    public List<String> splitWord(String word) {
        return splitWord(word, false);
    }

    /**
     * @return a list of compound parts, with one element (the input word itself) if the input
     *   could not be split; returns an empty list if the input is {@code null}
     * @since 4.2
     */
    public List<String> splitWord(String word, boolean collectSubwords) {
        if (word == null) {
            return Collections.emptyList();
        }
        String trimmedWord = word.trim();
        List<String> exceptionSplit = exceptionSplits.getExceptionSplitOrNull(trimmedWord);
        if (exceptionSplit != null) {
            return exceptionSplit;
        }
        List<String> parts = split(trimmedWord, false, collectSubwords);
        if (parts == null) {
            return Collections.singletonList(trimmedWord);
        }
        List<String> disambiguatedParts = getDisambiguator().disambiguate(parts);
        cleanLeadingAndTrailingHyphens(disambiguatedParts);
        return disambiguatedParts;
    }

    private void cleanLeadingAndTrailingHyphens(List<String> disambiguatedParts) {
        for (int i = 0; i < disambiguatedParts.size(); i++) {
            String element = disambiguatedParts.get(i);
            if (element.startsWith("-")) {
                disambiguatedParts.set(i, element.substring(1));
            }
            if (element.endsWith("-")) {
                disambiguatedParts.set(i, element.substring(0, element.length() - 1));
            }
        }
    }

    private List<String> split(String word, boolean allowInterfixRemoval, boolean collectSubwords) {
        List<String> parts = exceptionSplits.getExceptionSplitOrNull(word);
        if (parts != null) {
            return parts;
        }
        String lcWord = word.toLowerCase();
        String removableInterfix = findInterfixOrNull(lcWord);
        String wordWithoutInterfix = removeInterfix(word, removableInterfix);
        boolean canInterfixBeRemoved = removableInterfix != null && allowInterfixRemoval;

        if (isSimpleWord(word) && !collectSubwords) {
            parts = Collections.singletonList(word);
        } else if (canInterfixBeRemoved && isSimpleWord(wordWithoutInterfix)) {
            if (hideInterfixCharacters) {
                parts = Arrays.asList(wordWithoutInterfix);
            } else {
                parts = Arrays.asList(wordWithoutInterfix, removableInterfix);
            }
        } else {
            parts = splitFromRight(word, collectSubwords);

            if (parts == null && isSimpleWord(word)) {
                parts = new ArrayList<>();
                parts.add(word);
            } else if (parts != null && isSimpleWord(word) && !parts.contains(word)) {
                parts.add(word);
            }

            if (parts == null && endsWithInterfix(lcWord)) {
                parts = splitFromRight(wordWithoutInterfix, collectSubwords);
                if (parts != null && !hideInterfixCharacters) {
                    parts.add(removableInterfix);
                }
            }
        }
        return parts;
    }

    private List<String> splitFromRight(String word, boolean collectSubwords) {
        List<String> parts = exceptionSplits.getExceptionSplitOrNull(word);
        if (parts != null) {
            return parts;
        }

        for (int i = word.length() - minimumWordLength; i >= minimumWordLength; i--) {
            String leftPart = word.substring(0, i);
            String rightPart = word.substring(i);
            //System.out.println(word  + " -> " + leftPart + " + " + rightPart);
            if (!strictMode) {
                List<String> exceptionSplit = getExceptionSplitOrNull(rightPart, leftPart);
                if (exceptionSplit != null) {
                    return exceptionSplit;
                }
            }
            if (isSimpleWord(rightPart)) {
                List<String> leftPartParts = split(leftPart, true, collectSubwords);
                boolean isLeftPartAWord = leftPartParts != null;
                if (isLeftPartAWord) {
                    if (collectSubwords) {
                        if (parts == null) {
                            parts = new ArrayList<>();
                        }
                        for (String leftPartPart : leftPartParts) {
                            if (!parts.contains(leftPartPart)) {
                                parts.add(leftPartPart);
                            }
                        }
                        if (!parts.contains(rightPart)) {
                            parts.add(rightPart);
                        }
                        List<String> rightPartExceptions = exceptionSplits.getExceptionSplitOrNull(rightPart);
                        if (rightPartExceptions != null) {
                            for (String exception : rightPartExceptions) {
                                if (!parts.contains(exception)) {
                                    parts.add(exception);
                                }
                            }
                        }
                    } else {
                        parts = new ArrayList<>(leftPartParts);
                        parts.add(rightPart);
                    }
                } else if (!strictMode) {
                    parts = new ArrayList<>();
                    parts.add(leftPart);
                    parts.add(rightPart);
                }
            } else if (!strictMode) {
                if (isSimpleWord(leftPart)) {
                    parts = new ArrayList<>();
                    parts.add(leftPart);
                    parts.add(rightPart);
                }
            }
        }
        return parts;
    }

    private List<String> getExceptionSplitOrNull(String rightPart, String leftPart) {
        List<String> exceptionSplit = exceptionSplits.getExceptionSplitOrNull(rightPart);
        if (exceptionSplit != null) {
            List<String> parts = new ArrayList<>();
            parts.add(leftPart);
            parts.addAll(exceptionSplit);
            return parts;
        }
        List<String> exceptionSplit2 = exceptionSplits.getExceptionSplitOrNull(leftPart);
        if (exceptionSplit2 != null) {
            List<String> parts = new ArrayList<>();
            parts.addAll(exceptionSplit2);
            parts.add(rightPart);
            return parts;
        }
        return null;
    }

    private String findInterfixOrNull(String word) {
        Collection<String> interfixes = getInterfixCharacters();
        String lcWord = word.toLowerCase();
        for (String interfix : interfixes) {
            if (lcWord.endsWith(interfix)) {
                return interfix;
            }
        }
        return null;
    }

    private boolean endsWithInterfix(String word) {
        Collection<String> interfixes = getInterfixCharacters();
        for (String interfix : interfixes) {
            if (word.endsWith(interfix)) {
                return true;
            }
        }
        return false;
    }

    private String removeInterfix(String word, String interfixOrNull) {
        if (interfixOrNull != null) {
            return word.substring(0, word.length() - interfixOrNull.length());
        }
        return word;
    }

    private boolean isSimpleWord(String part) {
        return part.length() >= minimumWordLength && words.contains(part.toLowerCase());
    }

}
