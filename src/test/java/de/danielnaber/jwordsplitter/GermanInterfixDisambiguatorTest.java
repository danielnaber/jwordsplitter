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

import de.danielnaber.jwordsplitter.tools.FastObjectSaver;
import de.danielnaber.jwordsplitter.tools.FileTools;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class GermanInterfixDisambiguatorTest {
    
    @Test
    public void testSmallDict() throws IOException {
        try (InputStream stream = GermanInterfixDisambiguatorTest.class.getResourceAsStream("/de/danielnaber/jwordsplitter/test-de.txt")) {
            Set<String> compoundParts = FileTools.loadFileToSet(stream, "utf-8");
            GermanInterfixDisambiguator disambiguator = new GermanInterfixDisambiguator(compoundParts);
            assertDecompose("Verkehr samt", "Verkehrs, amt", disambiguator);
            // input when hideInfix is false:
            assertDecompose("Sauerstoff flaschen störung s verhalten", "Sauerstoff, flaschen, störungs, verhalten", disambiguator);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFullDict() throws IOException {
        HashSet<String> compoundParts = (HashSet<String>) FastObjectSaver.load("/de/danielnaber/jwordsplitter/wordsGerman.ser");
        GermanInterfixDisambiguator disambiguator = new GermanInterfixDisambiguator(compoundParts);
        assertDecompose("Verkehr samt", "Verkehrs, amt", disambiguator);
        assertDecompose("Sauerstoff flaschen störung s verhalten", "Sauerstoff, flaschen, störungs, verhalten", disambiguator);
    }

    private void assertDecompose(String input, String output, GermanInterfixDisambiguator disambiguator) {
        assertThat(disambiguator.disambiguate(Arrays.asList(input.split(" "))).toString(), is("[" + output + "]"));
    }

}