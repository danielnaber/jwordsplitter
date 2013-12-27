/**
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

    private ExceptionSplits exceptionSplits;
    private boolean hideInterfixCharacters = true;
    private boolean strictMode = true;
    private int minimumWordLength = getDefaultMinimumWordLength();

    /**
     * Create a word splitter that uses the embedded dictionary.
     *
     * @param hideInterfixCharacters whether the word parts returned by {@link #splitWord(String)} still contain
     *  the connecting character (a.k.a. interfix)
     * @throws IOException
     */
    public AbstractWordSplitter(boolean hideInterfixCharacters) throws IOException {
        this.hideInterfixCharacters = hideInterfixCharacters;
        words = getWordList();
    }

    /**
     * @param hideInterfixCharacters whether the word parts returned by {@link #splitWord(String)} still contain
     *  the connecting character (a.k.a. interfix)
     * @param  plainTextDict a stream of a text file with one word per line, to be used instead of the embedded dictionary,
     *                       must be in UTF-8 format
     * @throws IOException
     */
    public AbstractWordSplitter(boolean hideInterfixCharacters, InputStream plainTextDict) throws IOException {
        this.hideInterfixCharacters = hideInterfixCharacters;
        words = getWordList(plainTextDict);
    }

    /**
     * @param hideInterfixCharacters whether the word parts returned by {@link #splitWord(String)} still contain
     *  the connecting character (a.k.a. interfix)
     * @param  plainTextDict a stream of a text file with one word per line, to be used instead of the embedded dictionary,
     *                       must be in UTF-8 format
     * @throws IOException
     */
    public AbstractWordSplitter(boolean hideInterfixCharacters, File plainTextDict) throws IOException {
        this.hideInterfixCharacters = hideInterfixCharacters;
        words = getWordList(plainTextDict);
    }

    private Set<String> getWordList(File file) throws IOException {
        final FileInputStream fis = new FileInputStream(file);
        try {
            return getWordList(fis);
        } finally {
            fis.close();
        }
    }

    public void setMinimumWordLength(int minimumWordLength) {
        this.minimumWordLength = minimumWordLength;
    }

    /**
     * @param filename UTF-8 encoded file with exceptions in the classpath, one exception per line, using pipe as delimiter.
     *   Example: <tt>Pilot|sendung</tt>
     * @throws IOException
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
     * The minimum length of word parts is correctly taken into account only if this is set to true.
     */
    public void setStrictMode(boolean strictMode) {
        this.strictMode = strictMode;
    }

    public List<String> splitWord(String word) {
        if (word == null) {
            return Collections.emptyList();
        }
        final String trimmedWord = word.trim();
        final List<String> exceptionSplit = exceptionSplits.getExceptionSplitOrNull(trimmedWord);
        if (exceptionSplit != null) {
            return exceptionSplit;
        }
        final List<String> parts = split(trimmedWord, false);
        if (parts == null) {
            return Collections.singletonList(trimmedWord);
        }
        final List<String> disambiguatedParts = getDisambiguator().disambiguate(parts);
        cleanLeadingAndTrailingHyphens(disambiguatedParts);
        return disambiguatedParts;
    }

    private void cleanLeadingAndTrailingHyphens(List<String> disambiguatedParts) {
        for (int i = 0; i < disambiguatedParts.size(); i++) {
            final String element = disambiguatedParts.get(i);
            if (element.startsWith("-")) {
                disambiguatedParts.set(i, element.substring(1));
            }
            if (element.endsWith("-")) {
                disambiguatedParts.set(i, element.substring(0, element.length() - 1));
            }
        }
    }

    private List<String> split(String word, boolean allowInterfixRemoval) {
        List<String> parts;
        final String lcWord = word.toLowerCase();
        final String removableInterfix = findInterfixOrNull(lcWord);
        final String wordWithoutInterfix = removeInterfix(word, removableInterfix);
        final boolean canInterfixBeRemoved = removableInterfix != null && allowInterfixRemoval;
        if (isSimpleWord(word)) {
            parts = Collections.singletonList(word);
        } else if (canInterfixBeRemoved && isSimpleWord(wordWithoutInterfix)) {
            if (hideInterfixCharacters) {
                parts = Arrays.asList(wordWithoutInterfix);
            } else {
                parts = Arrays.asList(wordWithoutInterfix, removableInterfix);
            }
        } else {
            parts = splitFromRight(word);
            if (parts == null && endsWithInterfix(lcWord)) {
                parts = splitFromRight(wordWithoutInterfix);
                if (parts != null) {
                    parts.add(removableInterfix);
                }
            }
        }
        return parts;
    }

    private List<String> splitFromRight(String word) {
        List<String> parts = null;
        for (int i = word.length() - minimumWordLength; i >= minimumWordLength; i--) {
            final String leftPart = word.substring(0, i);
            final String rightPart = word.substring(i);
            //System.out.println(word  + " -> " + leftPart + " + " + rightPart);
            if (!strictMode) {
                final List<String> exceptionSplit = getExceptionSplitOrNull(rightPart, leftPart);
                if (exceptionSplit != null) {
                    return exceptionSplit;
                }
            }
            if (isSimpleWord(rightPart)) {
                final List<String> leftPartParts = split(leftPart, true);
                final boolean isLeftPartAWord = leftPartParts != null;
                if (isLeftPartAWord) {
                    parts = new ArrayList<String>(leftPartParts);
                    parts.add(rightPart);
                } else if (!strictMode) {
                    parts = Arrays.asList(leftPart, rightPart);
                }
            } else if (!strictMode) {
                if (isSimpleWord(leftPart)) {
                    parts = Arrays.asList(leftPart, rightPart);
                }
            }
        }
        return parts;
    }

    private List<String> getExceptionSplitOrNull(String rightPart, String leftPart) {
        final List<String> exceptionSplit = exceptionSplits.getExceptionSplitOrNull(rightPart);
        if (exceptionSplit != null) {
            final List<String> parts = new ArrayList<String>();
            parts.add(leftPart);
            parts.addAll(exceptionSplit);
            return parts;
        }
        final List<String> exceptionSplit2 = exceptionSplits.getExceptionSplitOrNull(leftPart);
        if (exceptionSplit2 != null) {
            final List<String> parts = new ArrayList<String>();
            parts.addAll(exceptionSplit2);
            parts.add(rightPart);
            return parts;
        }
        return null;
    }

    private String findInterfixOrNull(String word) {
        final Collection<String> interfixes = getInterfixCharacters();
        final String lcWord = word.toLowerCase();
        for (String interfix : interfixes) {
            if (lcWord.endsWith(interfix)) {
                return interfix;
            }
        }
        return null;
    }

    private boolean endsWithInterfix(String word) {
        final Collection<String> interfixes = getInterfixCharacters();
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
