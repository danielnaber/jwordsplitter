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
package de.danielnaber.wordtools.jwordsplitter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

/**
 * Test cases for the German word splitter.
 */
public class GermanWordSplitterTest extends BaseTest {

    @Override
    protected String getDictionaryFile() {
        return "/de/danielnaber/test-de.txt";
    }

    @Override
    public void setUp() throws IOException {
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void test() throws IOException {
        splitter = new GermanWordSplitter(true, tmpLexiconFile);
        strictModeBaseChecks();
    }

    public void testFile() throws IOException {
        splitter = new GermanWordSplitter(true, tmpLexiconFile);
        strictModeBaseChecks();
    }

    private void strictModeBaseChecks() {
        expect("[xyz]", "xyz");
        expect("[Verhalten]", "Verhalten");
        expect("[Verhalten, störung]", "Verhaltenstörung");
        expect("[Verhalten, störung]", "Verhaltensstörung");
        expect("[Verhaltenxstörung]", "Verhaltenxstörung");
        expect("[Verhalten, haus]", "Verhaltenshaus");
        expect("[Verhalten, haus, störung]", "Verhaltenshausstörung");
        expect("[Abend, haus]", "Abendhaus");
        expect("[Abend, haus, störung]", "Abendhausstörung");
        // just from special test file:
        expect("[Krawehl, pusselsumm]", "Krawehlpusselsumm");
    }

    public void testStrictMode() throws IOException {
        splitter = new GermanWordSplitter(true, tmpLexiconFile);
        expect("[Wirtstierspezies]", "Wirtstierspezies");
        splitter.setStrictMode(false);
        expect("[Wirts, tiers, pezies]", "Wirtstierspezies");  // Wirtstier is in exception file
    }

    public void testWithHyphen() throws IOException {
        splitter = new GermanWordSplitter(true, tmpLexiconFile);
        expect("[Verhalten, Störung]", "Verhaltens-Störung");
        expect("[Sauerstoff, Flasche]", "Sauerstoff-Flasche");

        expect("[Sauerstoff-Foobar]", "Sauerstoff-Foobar");
        splitter.setStrictMode(false);
        expect("[Sauerstoff, Foobar]", "Sauerstoff-Foobar");
        expect("[Foobar, Sauerstoff]", "Foobar-Sauerstoff");
        // no term known -> not split at all despite hyphen:
        expect("[Blahbar-Foobar]", "Blahbar-Foobar");
    }

    public void testWithWhitespace() throws IOException {
        splitter = new GermanWordSplitter(true, tmpLexiconFile);
        expect("[Verhalten, Störung]", "  Verhaltens-Störung\t ");
    }

    public void testWrongCase() throws IOException {
        splitter = new GermanWordSplitter(true, tmpLexiconFile);
        // words with wrong case are also split up:
        expect("[VERHALTEN, STÖRUNG]", "VERHALTENSSTÖRUNG");
        expect("[verhalten, störung]", "verhaltensstörung");
    }

    public void testWithConnectionCharacter() throws IOException {
        splitter = new GermanWordSplitter(false, tmpLexiconFile);
        expect("[xyz]", "xyz");
        expect("[Verhalten]", "Verhalten");
        expect("[Verhalten, störung]", "Verhaltenstörung");
        expect("[Verhaltens, störung]", "Verhaltensstörung");   // now with "s"
        expect("[Verhaltenxstörung]", "Verhaltenxstörung");
        expect("[Verhaltenfoobar]", "Verhaltenfoobar");
    }

    public void testTooShortWords() throws IOException {
        splitter = new GermanWordSplitter(false, tmpLexiconFile);
        // too short to be split (default min word length: 4)
        expect("[Verhaltenei]", "Verhaltenei");
        expect("[Eiverhalten]", "Eiverhalten");
        ((GermanWordSplitter)splitter).setMinimumWordLength(3);
        expect("[Eiverhalten]", "Eiverhalten");
        ((GermanWordSplitter)splitter).setMinimumWordLength(2);
        expect("[Ei, verhalten]", "Eiverhalten");
    }

    public void testNonStrictMode() throws IOException {
        splitter = new GermanWordSplitter(false, tmpLexiconFile);
        splitter.setStrictMode(false);
        expect("[xyz]", "xyz");
        expect("[Verhalten]", "Verhalten");
        expect("[Verhalten, störung]", "Verhaltenstörung");
        expect("[Verhalten, sstörung]", "Verhaltensstörung");   // not correct, but hey - it's non-strict mode
        // now split because of non-strict mode:
        expect("[Verhalten, xstörung]", "Verhaltenxstörung");
        expect("[Verhalten, sxyz]", "Verhaltensxyz");
        expect("[Verhalten, sxyz]", "Verhaltensxyz");
        expect("[xyzstörung]", "xyzstörung");
        splitter.setMinimumWordLength(3);
        expect("[xyz, störung]", "xyzstörung");
        expect("[Verhalten, xyz]", "Verhaltenxyz");
    }

    public void testLongerWords() throws IOException {
        splitter = new GermanWordSplitter(false, tmpLexiconFile);
        expect("[Sauerstoff, flaschen, störungs, verhalten]", "Sauerstoffflaschenstörungsverhalten");
        expect("[Sauerstoff, sauerstoff]", "Sauerstoffsauerstoff");
        expect("[Sauerstoff, sauerstoff, sauerstoff]", "Sauerstoffsauerstoffsauerstoff");
        expect("[Störungs, störung]", "Störungsstörung");
        expect("[Störungs, störungs, störung]", "Störungsstörungsstörung");
    }

    public void testExceptionsWithEmbeddedDict() throws IOException {
        splitter = new GermanWordSplitter(false);
        expect("[Sünder, ecke]", "Sünderecke");
        expect("[Klima, sünde, recke]", "Klimasünderecke");
        expect("[Klima, sünder, recke]", "Klimasünderrecke");
        splitter.setStrictMode(false);
        expect("[Sünder, ecke]", "Sünderecke");
        expect("[klima, Sünder, ecke]", "Klimasünderecke");    // not correct, but hey - it's non-strict mode
        expect("[Klima, sünderrecke]", "Klimasünderrecke");  // not correct, but hey - it's non-strict mode
        // test that some words to *not* get split:
        expect("[Vereinsamen]", "Vereinsamen");
    }

    public void testEmbeddedDict() throws IOException {
        splitter = new GermanWordSplitter(false);
        expect("[Sauerstoff, flasche]", "Sauerstoffflasche");
        expect("[Sauerstoff, lasche]", "Sauerstofflasche");  // not correct (pre-reform spelling)
        expect("[Noten, durchschnitt]", "Notendurchschnitt");
        expect("[Fahrzeug, staus]", "Fahrzeugstaus");
        expect("[Noten, bank, vorsitzenden]", "Notenbankvorsitzenden");   // TODO: still no longest match!
    }

    public void testNoCompounds() throws IOException {
        splitter = new GermanWordSplitter(false, tmpLexiconFile);
        // Kotflügel, Kot, and Flügel in the dictionary so don't split:
        expect("[Kotflügel]", "Kotflügel");
    }

    public void testSpecialCases() throws IOException {
        splitter = new GermanWordSplitter(false, tmpLexiconFile);
        expect("[]", null);
        expect("[]", "");
        expect("[]", "\t");
        expect("[]", "   ");
    }

    public void testExceptionsAddedViaApi() throws IOException {
        splitter = new GermanWordSplitter(false, tmpLexiconFile);
        expect("[Verhaltens, störung]", "Verhaltensstörung");
        splitter.addException("Verhaltensstörung", Arrays.asList("Verhaltensstörung"));
        expect("[Verhaltensstörung]", "Verhaltensstörung");
        splitter.addException("Verhaltensstörung", Arrays.asList("Ver", "halten", "Störung"));  // will override the old mapping
        expect("[Ver, halten, Störung]", "Verhaltensstörung");

        splitter.addException("Verhaltensstörung", Collections.<String>emptyList());
        expect("[]", "Verhaltensstörung");    // not sure if this makes sense...
        splitter.addException("Verhaltensstörung", null);    // resets to original behaviour
        expect("[Verhaltens, störung]", "Verhaltensstörung");
        try {
            splitter.addException(null, Arrays.asList("Verhaltensstörung"));
            fail();
        } catch (NullPointerException expected) {}
    }

}
