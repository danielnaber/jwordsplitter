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
	if (wordParts != null) {
		Iterator<String> i = wordParts.iterator();
		ArrayList<WordPart> newParts = new ArrayList<WordPart>();
		while (i.hasNext()) {
			newParts.add(new WordPart(i.next()));
		}
		addExceptionParts(completeWord, newParts);
	} else {
		addExceptionParts(completeWord, null);
	}
    }

    /**
     * @param completeWord the word for which an exception is to be defined (will be considered case-insensitive)
     * @param wordParts the parts in which the word is to be split (use a list with a single element if the word should not be split)
     */
    public void addExceptionParts(String completeWord, List<WordPart> wordParts) {
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
	List<WordPart> parts = splitWordIntoParts(word);
	List result = new ArrayList<String>();
	Iterator<WordPart> i = parts.iterator();
	while (i.hasNext()) {
		String s = i.next().toString();
		result.add(s);
	}
	return result;
    }

    public List<WordPart> splitWordIntoParts(String word) {
        if (word == null) {
            return Collections.emptyList();
        }
	// First of all lowercase the word here
        final WordPart trimmedWord = new WordPart(word.trim());
        final List<WordPart> exceptionSplit = exceptionSplits.getExceptionSplitOrNull(trimmedWord);
        if (exceptionSplit != null) {
            return exceptionSplit;
        }
        final List<WordPart> parts = split(trimmedWord, false);
        if (parts == null) {
            // Return the trimmed word, but not lowercased
            return Collections.singletonList(trimmedWord);
        }
        final List<WordPart> disambiguatedParts = getDisambiguator().disambiguate(parts);
        cleanLeadingAndTrailingHyphens(disambiguatedParts);
        return disambiguatedParts;
    }

    private void cleanLeadingAndTrailingHyphens(List<WordPart> disambiguatedParts) {
        for (int i = 0; i < disambiguatedParts.size(); i++) {
            final WordPart element = disambiguatedParts.get(i);
            if (element.startsWith("-")) {
                disambiguatedParts.set(i, element.substring(1));
            }
            if (element.endsWith("-")) {
                disambiguatedParts.set(i, element.substring(0, element.length() - 1));
            }
        }
    }

    private List<WordPart> split(WordPart word, boolean allowInterfixRemoval) {
        List<WordPart> parts = null;
	final WordPart lcWord = word.toLowerCase();
        final String removableInterfix = findInterfixOrNull(lcWord);
        final WordPart wordWithoutInterfix = removeInterfix(word, removableInterfix);
        final boolean canInterfixBeRemoved = removableInterfix != null && allowInterfixRemoval;
        if (isSimpleWord(word)) {
            parts = Collections.singletonList(word);
        } else if (canInterfixBeRemoved && isSimpleWord(wordWithoutInterfix)) {
            if (hideInterfixCharacters) {
                parts = Arrays.asList(wordWithoutInterfix);
            } else {
                parts = Arrays.asList(wordWithoutInterfix, new WordPart(removableInterfix));
            }
        } else {
            parts = splitFromRight(word);
            if (parts == null && endsWithInterfix(lcWord.toString())) {
                parts = splitFromRight(wordWithoutInterfix);
                if (parts != null) {
                    parts.add(new WordPart(removableInterfix));
                }
            }
        }
        return parts;
    }

    private List<WordPart> splitFromRight(WordPart word) {
        List<WordPart> parts = null;
        for (int i = word.length() - minimumWordLength; i >= minimumWordLength; i--) {
            final WordPart leftPart = word.substring(0, i);
            final WordPart rightPart = word.substring(i);
            if (!strictMode) {
                final List<WordPart> exceptionSplit = getExceptionSplitOrNull(rightPart, leftPart);
                if (exceptionSplit != null) {
                    return exceptionSplit;
                }
            }
            if (isSimpleWord(rightPart)) {
                final List<WordPart> leftPartParts = split(leftPart, true);
                final boolean isLeftPartAWord = leftPartParts != null;
                if (isLeftPartAWord) {
                    parts = new ArrayList<WordPart>(leftPartParts);
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

    private List<WordPart> getExceptionSplitOrNull(WordPart rightPart, WordPart leftPart) {
        final List<WordPart> exceptionSplit = exceptionSplits.getExceptionSplitOrNull(rightPart);
        if (exceptionSplit != null) {
            final List<WordPart> parts = new ArrayList<WordPart>();
            parts.add(leftPart);
            parts.addAll(exceptionSplit);
            return parts;
        }
        final List<WordPart> exceptionSplit2 = exceptionSplits.getExceptionSplitOrNull(leftPart);
        if (exceptionSplit2 != null) {
            final List<WordPart> parts = new ArrayList<WordPart>();
            parts.addAll(exceptionSplit2);
            parts.add(rightPart);
            return parts;
        }
        return null;
    }

    private String findInterfixOrNull(WordPart word) {
        final Collection<String> interfixes = getInterfixCharacters();
        for (String interfix : interfixes) {
            if (word.endsWith(interfix)) {
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

    private WordPart removeInterfix(WordPart word, String interfixOrNull) {
        if (interfixOrNull != null) {
            return word.substring(0, word.length() - interfixOrNull.length());
        }
        return word;
    }

    private boolean isSimpleWord(WordPart part) {
        return part.length() >= minimumWordLength && words.contains(part.toString().toLowerCase());
    }

}
