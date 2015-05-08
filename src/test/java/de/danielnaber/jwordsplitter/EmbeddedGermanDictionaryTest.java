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

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class EmbeddedGermanDictionaryTest {
  
  @Test
  public void testDict() {
    Set<String> words = EmbeddedGermanDictionary.getWords();
    assertTrue("Got only " + words.size() + " words", words.size() > 50_000);
  }

}
