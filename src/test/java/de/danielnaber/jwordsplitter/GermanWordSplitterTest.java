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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GermanWordSplitterTest extends BaseTest {

    @Override
    protected String getDictionaryFile() {
        return "/de/danielnaber/jwordsplitter/test-de.txt";
    }

    public void testGetAllSplits() throws IOException {
        try (FileInputStream fis = new FileInputStream(tmpLexiconFile)) {
            splitter = new GermanWordSplitter(true, fis);
            splitter.setStrictMode(false);

            testAllSplits("Klumaflaschenstörung", "[[Klumaflaschen, störung], [Kluma, flaschen, störung]]");
            testAllSplits("Klimaflischenstörung", "[[Klima, flischenstörung], [Klima, flischen, störung], [Klimaflischen, störung]]");
            testAllSplits("Klimaflaschenstirung", "[[Klima, flaschenstirung], [Klima, flasche, nstirung], [Klima, flaschen, stirung]]");

            testAllSplits("Hxusverhaltenflügel", "[[Hxusverhalten, flügel], [Hxus, verhalten, flügel]]");
            testAllSplits("Hausvxrhaltenflügel", "[[Haus, vxrhaltenflügel], [Haus, vxrhalten, flügel], [Hausvxrhalten, flügel]]");
            testAllSplits("Hausverhaltenflüxel", "[[Haus, verhaltenflüxel], [Haus, verhalten, flüxel]]");
            
            testAllSplits("Hauxverhaltensflügel", "[[Hauxverhaltens, flügel], [Haux, verhaltens, flügel]]");
            testAllSplits("Hausverhalxensflügel", "[[Haus, verhalxensflügel], [Haus, verhalxens, flügel], [Hausverhalxens, flügel]]");
            testAllSplits("Hausverhaltensflügex", "[[Haus, verhaltensflügex], [Haus, verhalten, sflügex], [Haus, verhaltens, flügex]]");
            
            testAllSplits("Verhaltens-Flügex", "[[Verhalten, s-Flügex], [Verhaltens, -Flügex]]");
            testAllSplits("Hausverhaltens-Flügex", "[[Haus, verhaltens-Flügex], [Haus, verhalten, s-Flügex], [Haus, verhaltens, -Flügex]]");

            // for debugging:
            //List<AbstractWordSplitter.Split> result = splitter.getAllSplits("Hausverhaltensflügex", true);  // also with false
            //System.out.println(result);
        }
    }

    public void testStreamDictConstructor() throws IOException {
        try (FileInputStream fis = new FileInputStream(tmpLexiconFile)) {
            splitter = new GermanWordSplitter(true, fis);
            baseChecks();
        }
    }

    private void testAllSplits(String input, String expected) {
        List<List<String>> result = splitter.getAllSplits(input);
        assertThat(result.toString(), is(expected));
    }

    public void testFileDictConstructor() throws IOException {
        splitter = new GermanWordSplitter(true, tmpLexiconFile);
        baseChecks();
    }

    public void testFile() throws IOException {
        splitter = new GermanWordSplitter(false, tmpLexiconFile);
        baseChecks();
    }

    private void baseChecks() {
        expect("[xyz]", "xyz");
        expect("[Verhalten]", "Verhalten");
        expect("[Verhalten, störung]", "Verhaltenstörung");
        expect("[Verhaltens, störung]", "Verhaltensstörung");
        expect("[Verhaltenxstörung]", "Verhaltenxstörung");
        expect("[Verhaltens, haus]", "Verhaltenshaus");
        expect("[Verhaltens, haus, störung]", "Verhaltenshausstörung");
        expect("[Abend, haus]", "Abendhaus");
        expect("[Abend, haus, störung]", "Abendhausstörung");
        // just from special test file:
        expect("[Krawehl, pusselsumm]", "Krawehlpusselsumm");
    }

    public void testInterfix() throws IOException {
        splitter = new GermanWordSplitter(true, tmpLexiconFile);
        // 'Störungs' doesn't exist, so the 's' is an interfix:
        expect("[Störung, flügel]", "Störungsflügel");
        expect("[Störung, haus]", "Störungshaus");
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
        expect("[VERHALTENS, STÖRUNG]", "VERHALTENSSTÖRUNG");
        expect("[verhaltens, störung]", "verhaltensstörung");
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
        splitter.setMinimumWordLength(3);
        expect("[Eiverhalten]", "Eiverhalten");
        splitter.setMinimumWordLength(2);
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
        expect("[yzstörung]", "yzstörung");
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

    public void testLongerWordsHideInterfix() throws IOException {
        splitter = new GermanWordSplitter(true, tmpLexiconFile);
        expect("[Sauerstoff, flaschen, störung, verhalten]", "Sauerstoffflaschenstörungsverhalten");
        expect("[Sauerstoff, sauerstoff]", "Sauerstoffsauerstoff");
        expect("[Sauerstoff, sauerstoff, sauerstoff]", "Sauerstoffsauerstoffsauerstoff");
        expect("[Störung, störung]", "Störungsstörung");
        expect("[Störung, störung, störung]", "Störungsstörungsstörung");
    }

    public void testExceptionsWithEmbeddedDict() throws IOException {
        splitter = new GermanWordSplitter(false);
        expect("[Sünder, ecke]", "Sünderecke");
        expect("[Klima, sünde, recke]", "Klimasünderecke");
        expect("[Klima, sünder, recke]", "Klimasünderrecke");
        splitter.setStrictMode(false);
        expect("[Sünder, ecke]", "Sünderecke");
        expect("[Klima, sünder, ecke]", "Klimasünderecke");
        expect("[Klima, sünderrecke]", "Klimasünderrecke");
        // test that some words to *not* get split:
        expect("[Vereinsamen]", "Vereinsamen");
    }

    public void testEmbeddedDict() throws IOException {
        splitter = new GermanWordSplitter(false);
        expect("[Sauerstoff, flasche]", "Sauerstoffflasche");
        expect("[Sauerstoff, lasche]", "Sauerstofflasche");  // not correct (pre-reform spelling)
        expect("[Noten, durchschnitt]", "Notendurchschnitt");
        expect("[Fahrzeug, staus]", "Fahrzeugstaus");
        expect("[Noten, bank, vorsitzenden]", "Notenbankvorsitzenden");
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
        } catch (NullPointerException ignored) {}
    }

}
