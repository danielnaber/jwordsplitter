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

import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GermanFullDictWordSplitterTest {

    @Test
    public void testSplit() throws IOException {
        GermanWordSplitter splitter = new GermanWordSplitter(true);
        assertThat(splitter.splitWord("Hausrichten").toString(), is("[Hausrichten]"));  // 'richten' is in removals.txt
        assertThat(splitter.splitWord("Krankenhaus").toString(), is("[Kranken, haus]"));
        assertThat(splitter.splitWord("Krankenhauses").toString(), is("[Kranken, hauses]"));
        assertThat(splitter.splitWord("Junggeselle").toString(), is("[Junggeselle]"));
        assertThat(splitter.splitWord("Junggesellen").toString(), is("[Junggesellen]"));
        assertThat(splitter.splitWord("Bahnhof").toString(), is("[Bahnhof]"));
        assertThat(splitter.splitWord("Bahnhofs").toString(), is("[Bahnhofs]"));
        assertThat(splitter.splitWord("Nicht-Angriffspakt").toString(), is("[Nicht, Angriffs, pakt]"));
        assertThat(splitter.splitWord("Nicht-Mitgliedern").toString(), is("[Nicht, Mitgliedern]"));
        assertThat(splitter.splitWord("Zwei-Jahres-Programm").toString(), is("[Zwei, Jahre, Programm]"));
        assertThat(splitter.splitWord("Bildabtastgerät").toString(), is("[Bild, abtast, gerät]"));
        // some items from exceptionsGerman.txt:
        assertThat(splitter.splitWord("Schwerpunktsachsen").toString(), is("[Schwerpunkts, achsen]"));
        assertThat(splitter.splitWord("Bahnsteig").toString(), is("[Bahnsteig]"));
        assertThat(splitter.splitWord("Gewebekulturzelle").toString(), is("[Gewebe, kultur, zelle]"));
        assertThat(splitter.splitWord("Bruttorentenniveau").toString(), is("[Brutto, renten, niveau]"));
        assertThat(splitter.splitWord("Bruttorentenniveaus").toString(), is("[Brutto, renten, niveaus]"));
    }

    @Ignore("use to manually clean up exceptionsGerman.txt")
    @Test
    public void testExceptions() throws IOException {
        GermanWordSplitter splitter = new GermanWordSplitter(true);
        String filename = "src/main/resources/de/danielnaber/jwordsplitter/exceptionsGerman.txt.copy";
        try (Scanner s = new Scanner(new File(filename))) {
            int fail = 0;
            int okay = 0;
            while (s.hasNextLine()) {
                String line = s.nextLine();
                if (line.startsWith("#")) {
                    System.out.println(line);
                    continue;
                }
                String input = line.replace("|" , "");
                String expected = "[" + line.replace("|", ", ") + "]";
                String result = splitter.splitWord(input).toString();
                if (!result.equals(expected)) {
                    //System.out.println(input + " -> " + result + ", expected " + expected);
                    System.out.println(line);
                    fail++;
                } else {
                    //System.out.println("OK: " + input + " -> " + result);
                    okay++;
                }
            }
            System.out.println("Okay: " + okay);
            System.out.println("Fail: " + fail);
        }
    }

}
