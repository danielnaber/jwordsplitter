/*
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

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A singleton that returns always the same words of the embedded dictionary.
 * @since 4.1
 */
public final class EmbeddedGermanDictionary {

  private static final String SERIALIZED_DICT = "/de/danielnaber/jwordsplitter/wordsGerman.ser";   // dict inside the JAR

  private static Set<String> words;

  private EmbeddedGermanDictionary() {
  }

  public static synchronized Set<String> getWords() {
    if (words == null) {
      try {
        words = Collections.unmodifiableSet((HashSet<String>)FastObjectSaver.load(SERIALIZED_DICT));
      } catch (IOException e) {
        throw new RuntimeException("Could not load " + SERIALIZED_DICT, e);
      }
    }
    return words;
  }

}
