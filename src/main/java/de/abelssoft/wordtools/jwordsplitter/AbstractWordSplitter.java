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
package de.abelssoft.wordtools.jwordsplitter;

import de.abelssoft.tools.FileTools;

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
 * @author Sven Abels (Abelssoft), Sven@abelssoft.de
 * @author Daniel Naber
 */
public abstract class AbstractWordSplitter {

    private static final String COMMENT_CHAR = "#";
    private static final String DELIMITER_CHAR = "|";

    private Set<String> words = null;
    private boolean hideConnectingCharacters = true;
    private boolean strictMode = false;
    private boolean reverseMode = false;

    private final Map<String,List<String>> exceptionMap = new HashMap<String, List<String>>();

    protected String plainTextDictFile = null;
    protected InputStream plainTextDict = null;

    protected abstract Set<String> getWordList() throws IOException;
    protected abstract int getMinimumWordLength();
    protected abstract Collection<String> getConnectingCharacters();

    /**
     * @param hideConnectingCharacters whether the word parts returned by {@link #splitWord(String)} still contain
     *  the connecting character (a.k.a. interfix)
     * @throws IOException
     */
    public AbstractWordSplitter(boolean hideConnectingCharacters) throws IOException {
        this(hideConnectingCharacters, (String)null);
    }

    /**
     * @param hideConnectingCharacters whether the word parts returned by {@link #splitWord(String)} still contain
     *  the connecting character (a.k.a. interfix)
     * @param  plainTextDictFile a text file with one word per line, to be used instead of the embedded dictionary,
     *                           must be in UTF-8 format
     * @throws IOException
     */
    public AbstractWordSplitter(boolean hideConnectingCharacters, String plainTextDictFile) throws IOException {
        this.hideConnectingCharacters = hideConnectingCharacters;
        this.plainTextDictFile = plainTextDictFile;
        words = getWordList();
    }

    /**
     * @param hideConnectingCharacters whether the word parts returned by {@link #splitWord(String)} still contain
     *  the connecting character (a.k.a. interfix)
     * @param  plainTextDict a stream of a text file with one word per line, to be used instead of the embedded dictionary,
     *                       must be in UTF-8 format
     * @throws IOException
     */
    public AbstractWordSplitter(boolean hideConnectingCharacters, InputStream plainTextDict) throws IOException {
        this.hideConnectingCharacters = hideConnectingCharacters;
        this.plainTextDict = plainTextDict;
        words = getWordList();
    }

    /**
     * @throws IOException
     */
    public AbstractWordSplitter() throws IOException {
        this(true);
    }

    public void setExceptionFile(String filename) throws IOException {
        final InputStream is = AbstractWordSplitter.class.getResourceAsStream(filename);
        try {
            if (is == null) {
                throw new IOException("Cannot locate exception list in JAR: " + filename);
            }
            final String exceptions = FileTools.loadFile(is, "UTF-8");
            final Scanner scanner = new Scanner(exceptions);
            while (scanner.hasNextLine()) {
                final String line = scanner.nextLine().trim();
                if (!line.isEmpty() && !line.startsWith(COMMENT_CHAR)) {
                    final String[] parts = line.split("\\|");
                    final String completeWord = line.replace(DELIMITER_CHAR, "");
                    final List<String> list = new ArrayList<String>(Arrays.asList(parts));
                    exceptionMap.put(completeWord.toLowerCase(), list);
                }
            }
            scanner.close();
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    /**
     * @param completeWord the word for which an exception is to be defined (will be considered case-insensitive)
     * @param wordParts the parts in which the word is to be split (use a list with a single element if the word should not be split)
     */
    public void addException(String completeWord, List<String> wordParts) {
        exceptionMap.put(completeWord.toLowerCase(), wordParts);
    }

    /**
     * When set to true, words will only be split if all parts are words.
     * Otherwise the splitting result might contain parts that are not words.
     * The minimum length of word parts is correctly taken into account only if this is set to true.
     */
    public void setStrictMode(boolean strictMode) {
        this.strictMode = strictMode;
    }

    /**
     * If set to true, words will be split from the end, not from the start. Useful only 
     * to compare both ways of splitting to detect ambiguities.
     */
    public void setReverseMode(boolean reverseMode) {
        this.reverseMode = reverseMode;
    }

    /**
     * Detect if a word exists in the dictionary. Words that are too short are ignored 
     * in order to avoid a fragmentation, which is too strong.
     */
    private boolean isWord(String s) {
        if (s==null)
            return false;
        if (s.trim().length()<getMinimumWordLength())
            return false;
        if (words.contains(s.toLowerCase().trim()))
            return true;
        return false;
    }


    /**
     * Split a compound word into its parts. If the word cannot be split (e.g.
     * because it's unknown or it is not a compound), one part with the
     * word itself is returned.
     *
     * <p>Attention: We don't expect to have any special chars here (!":;,.-_, etc.).
     *
     * @param str a single compound word
     */
    public Collection<String> splitWord(String str) {
        final Collection<String> result=new ArrayList<String>();
        if (str==null)
            return result;
        final String s=str.trim();
        if (s.length()<2)
        {
            result.add(s);
            return result;
        }

        //find a tuple (from left to right):
        Collection<String> tuple = findTuple(s);
        if (tuple ==null && !strictMode)
            tuple =truncateSplit(s);
        if (tuple ==null && !strictMode)
            tuple =truncateSplitReverse(s);
        if (tuple ==null)
            result.add(str);
        else
            result.addAll(tuple);

        return result;
    }


    /**
     * We were not able to split the word...well: Let's try to cut it at its beginning.
     */
    private Collection<String> truncateSplit(String s) {
        //we were not able to split the word...well: Let's try to cut it:
        for (int i=0;i<(s.length()-2);i++) {
            final Collection<String> tmp= findTuple(s.substring(i));
            if (tmp!=null) {
                final Collection<String> tmp2=new ArrayList<String>();
                if (strictMode && !isWord(s.substring(0,i))) {
                    continue;
                }
                tmp2.add(s.substring(0,i));
                tmp2.addAll(tmp);
                return tmp2;
            }
        }
        return null;
    }


    /**
     * We were not able to split the word... well: Let's try to cut it at its end.
     */
    private Collection<String> truncateSplitReverse(String s) {
        //we were not able to split the word...well: Let's try to cut it:
        for (int i=(s.length()-1);i>1;i--) {
            final Collection<String> tmp= findTuple(s.substring(0,i));
            if (tmp!=null) {
                if (strictMode && !isWord(s.substring(i))) {
                    continue;
                }
                tmp.add(s.substring(i));
                return tmp;
            }
        }
        return null;
    }

    /**
     * Removes e.g. 's' at the end of a string.
     */
    private String removeTailingCharacters(String str) {
        final Collection<String> connChars = getConnectingCharacters();
        for (String connChar : connChars) {
            if (str.toUpperCase().endsWith(connChar.toUpperCase())) {
                return str.substring(0, str.length()-connChar.length());
            }
        }
        return str;
    }

    private Collection<String> findTuple(String s) {

        final List<String> exceptionSplit = exceptionMap.get(s.toLowerCase());
        if (exceptionSplit != null) {
            return exceptionSplit;
        }

        if (s.length()<2)
            return null;
        Collection<String> result=new ArrayList<String>();

        final int fromPos;
        if (reverseMode) {
            fromPos = s.length()-1;
        } else {
            fromPos = 0;
        }
        int i = fromPos;
        while (true) {
            if (reverseMode) {
                if (i < 1) {
                    break;
                }
                i--;
            } else {
                if (i >= s.length()) {
                    break;
                }
                i++;
            }
            final String left=s.substring(0, i);
            final String right=s.substring(left.length());
            final String leftCleaned=removeTailingCharacters(left);
            boolean leftIsWord=false;
            if ((isWord(leftCleaned))) {
                if (hideConnectingCharacters)
                    result.add(leftCleaned);
                else
                    result.add(left);
                leftIsWord=true;
            } else if ((isWord(left))) {
                result.add(left);
                leftIsWord=true;
            }
            if (leftIsWord) {
                //look if we can split the right part, too:
                final Collection<String> rightCol= findTuple(right);
                if (rightCol!=null) {
                    result.addAll(rightCol);
                } else {
                    //we cannot split the rest of the word => left was not ok.
                    result=new ArrayList<String>();
                    continue;
                }
                return result;
            }
        }

        final boolean stringIsWord=isWord(s);
        final boolean cleanedStringIsWord=isWord(removeTailingCharacters(s));
        if (!stringIsWord && !cleanedStringIsWord) {
            return null;
        }
        if (hideConnectingCharacters && !stringIsWord) {
            result.add(removeTailingCharacters(s));
        } else {
            result.add(s);
        }
        return result;
    }

}
