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
/*
 * Created on 09.07.2008
 */
package de.abelssoft.wordtools.jwordsplitter.impl;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import de.abelssoft.tools.FileTools;
import junit.framework.TestCase;

/**
 * Test cases for the German word splitter.
 * @author Daniel Naber
 */
public class GermanWordSplitterTest extends TestCase {
  
  private static final String TEST_FILE = "test-de.txt";

  private File tmpLexiconFile;
  private GermanWordSplitter splitter;

  @Override
  public void setUp() throws IOException {
    final InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(TEST_FILE);
    try {
      if (is == null) {
        throw new RuntimeException("Could not load " + TEST_FILE + " from classpath");
      }
      final String lexicon = FileTools.loadFile(is, "utf-8");
      tmpLexiconFile = File.createTempFile("jwordsplitter-junit", ".txt");
      final FileOutputStream fos = new FileOutputStream(tmpLexiconFile);
      final OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
      try {
        osw.write(lexicon);
      } finally {
        osw.close();
        fos.close();
      }
    } finally {
      if (is != null) { is.close(); }
    }
  }

  @Override
  public void tearDown() {
    if (tmpLexiconFile != null) {
      tmpLexiconFile.delete();
    }
  }

  public void test() throws IOException {
    splitter = new GermanWordSplitter(true, tmpLexiconFile.getAbsolutePath());
    splitter.setStrictMode(true);
    strictModeBaseChecks();
  }

  public void testInputStream() throws IOException {
    FileInputStream fis = new FileInputStream(tmpLexiconFile);
    try {
      splitter = new GermanWordSplitter(true, fis);
      splitter.setStrictMode(true);
      strictModeBaseChecks();
    } finally {
      fis.close();
    }
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

  public void testBug() throws IOException {
    splitter = new GermanWordSplitter(true, tmpLexiconFile.getAbsolutePath());
    splitter.setStrictMode(false);
    expect("[Wirts, tiers, pezies]", "Wirtstierspezies");
    splitter.setStrictMode(true);
    expect("[Wirtstierspezies]", "Wirtstierspezies");
  }

  public void testReverse() throws IOException {
    splitter = new GermanWordSplitter(true, tmpLexiconFile.getAbsolutePath());
    splitter.setStrictMode(true);
    // don't use 'Sünderecke' as that is listed in the exception list:
    expect("[Xünde, recke]", "Xünderecke");
    splitter.setReverseMode(true);
    expect("[Xünder, ecke]", "Xünderecke");
  }
    
  public void testWithHyphen() throws IOException {
    splitter = new GermanWordSplitter(true, tmpLexiconFile.getAbsolutePath());
    splitter.setStrictMode(true);
    expect("[Verhalten, Störung]", "Verhaltens-Störung");
    expect("[Sauerstoff, Flasche]", "Sauerstoff-Flasche");
    
    expect("[Sauerstoff-Foobar]", "Sauerstoff-Foobar");
    splitter.setStrictMode(false);
    expect("[Sauerstoff, Foobar]", "Sauerstoff-Foobar");
    // no term known -> not split at all despite hyphen:
    expect("[Blahbar-Foobar]", "Blahbar-Foobar");
  }

  public void testWrongCase() throws IOException {
    splitter = new GermanWordSplitter(true, tmpLexiconFile.getAbsolutePath());
    splitter.setStrictMode(true);
    // words with wrong case are also split up:
    expect("[VERHALTEN, STÖRUNG]", "VERHALTENSSTÖRUNG");
    expect("[verhalten, störung]", "verhaltensstörung");
  }
  
  public void testWithConnectionCharacter() throws IOException {
    splitter = new GermanWordSplitter(false, tmpLexiconFile.getAbsolutePath());
    splitter.setStrictMode(true);
    expect("[xyz]", "xyz");
    expect("[Verhalten]", "Verhalten");
    expect("[Verhalten, störung]", "Verhaltenstörung");
    expect("[Verhaltens, störung]", "Verhaltensstörung");   // now with "s"
    expect("[Verhaltenxstörung]", "Verhaltenxstörung");
    expect("[Verhaltenfoobar]", "Verhaltenfoobar");
  }

  public void testTooShortWords() throws IOException {
    splitter = new GermanWordSplitter(false, tmpLexiconFile.getAbsolutePath());
    splitter.setStrictMode(true);
    // too short to be split (default min word length: 4)
    expect("[Verhaltenei]", "Verhaltenei");
    expect("[Eiverhalten]", "Eiverhalten");
    splitter.setMinimumWordLength(3);
    expect("[Eiverhalten]", "Eiverhalten");
    splitter.setMinimumWordLength(2);
    expect("[Ei, verhalten]", "Eiverhalten");
  }
  
  public void testNonStrictMode() throws IOException {
    splitter = new GermanWordSplitter(false, tmpLexiconFile.getAbsolutePath());
    splitter.setStrictMode(false);
    expect("[xyz]", "xyz");
    expect("[Verhalten]", "Verhalten");
    expect("[Verhalten, störung]", "Verhaltenstörung");
    expect("[Verhaltens, störung]", "Verhaltensstörung");
    // now split because of non-strict mode:
    expect("[Verhaltenx, störung]", "Verhaltenxstörung");
    expect("[xyz, störung]", "xyzstörung");
    expect("[Verhalten, xyz]", "Verhaltenxyz");
    expect("[Verhaltens, xyz]", "Verhaltensxyz");
    expect("[Verhaltens, xyz]", "Verhaltensxyz");
  }

  public void testLongerWords() throws IOException {
    splitter = new GermanWordSplitter(false, tmpLexiconFile.getAbsolutePath());
    splitter.setStrictMode(true);
    expect("[Sauerstoff, flaschen, störungs, verhalten]", "Sauerstoffflaschenstörungsverhalten");
    expect("[Sauerstoff, sauerstoff]", "Sauerstoffsauerstoff");
    expect("[Sauerstoff, sauerstoff, sauerstoff]", "Sauerstoffsauerstoffsauerstoff");
    expect("[Störungs, störung]", "Störungsstörung");
    expect("[Störungs, störungs, störung]", "Störungsstörungsstörung");
  }

  // TODO: case is not always correct...
  public void testExceptions() throws IOException {
    splitter = new GermanWordSplitter(false);
    splitter.setStrictMode(true);
    expect("[Sünder, ecke]", "Sünderecke");
    expect("[Klima, Sünder, ecke]", "Klimasünderecke");
    expect("[Klima, sünder, recke]", "Klimasünderrecke");
    splitter.setStrictMode(false);
    expect("[Sünder, ecke]", "Sünderecke");
    expect("[Klima, Sünder, ecke]", "Klimasünderecke");
    expect("[Klima, sünder, recke]", "Klimasünderrecke");
    // test that some words to *not* get split:
    expect("[Vereinsamen]", "Vereinsamen");
  }

  // TODO: Sauerstoffflasche vs Sauerstofflasche; upper vs lower case

  public void testNoCompounds() throws IOException {
    splitter = new GermanWordSplitter(false, tmpLexiconFile.getAbsolutePath());
    splitter.setStrictMode(true);
    // Kotflügel, Kot, and Flügel in the dictionary so don't split: 
    expect("[Kotflügel]", "Kotflügel");
  }
  
  public void testSpecialCases() throws IOException {
    splitter = new GermanWordSplitter(false, tmpLexiconFile.getAbsolutePath());
    expect("[]", null);
    expect("[]", "");
    expect("[]", "\t");
    expect("[]", "   ");
  }

  public void testExceptionsAddedViaApi() throws IOException {
    splitter = new GermanWordSplitter(false, tmpLexiconFile.getAbsolutePath());
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

  private void expect(String expected, String input) {
    final Collection<String> result = splitter.splitWord(input);
    assertEquals(expected, result.toString());
  }

}
