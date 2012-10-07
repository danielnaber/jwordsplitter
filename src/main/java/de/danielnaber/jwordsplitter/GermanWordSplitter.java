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

import de.danielnaber.tools.FileTools;
import de.danielnaber.tools.FastObjectSaver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 */
public class GermanWordSplitter extends AbstractWordSplitter {

    private static final String SERIALIZED_DICT = "/de/danielnaber/jwordsplitter/wordsGerman.ser";   // dict inside the JAR
    private static final String EXCEPTION_DICT = "/de/danielnaber/jwordsplitter/exceptionsGerman.txt";   // dict inside the JAR
    /** Interfixes = Fugenelemente */
    private static final Collection<String> INTERFIXES = Arrays.asList(
            "s-",  // combination of the characters below
            "s",
            "-");

    private GermanInterfixDisambiguator disambiguator;

    public GermanWordSplitter(boolean hideInterfixCharacters) throws IOException {
        super(hideInterfixCharacters);
        init();
    }

    public GermanWordSplitter(boolean hideInterfixCharacters, InputStream plainTextDict) throws IOException {
        super(hideInterfixCharacters, plainTextDict);
        init();
    }

    public GermanWordSplitter(boolean hideInterfixCharacters, File plainTextDict) throws IOException {
        super(hideInterfixCharacters, plainTextDict);
        init();
    }

    private void init() throws IOException {
        disambiguator = new GermanInterfixDisambiguator(getWordList());
        setExceptionFile(EXCEPTION_DICT);
    }

    @Override
    protected Set<String> getWordList(InputStream stream) throws IOException {
        return FileTools.loadFileToSet(stream, "utf-8");
    }

    @Override
    protected Set<String> getWordList() throws IOException {
        // TODO: make sure this doesn't get called more than once!
        return (HashSet<String>) FastObjectSaver.load(SERIALIZED_DICT);
    }

    @Override
    protected GermanInterfixDisambiguator getDisambiguator() {
        return disambiguator;
    }

    @Override
    protected int getDefaultMinimumWordLength() {
        return 4;
    }

    @Override
    protected Collection<String> getInterfixCharacters() {
        return INTERFIXES;
    }

}
