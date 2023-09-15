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

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

/**
 * A singleton that returns always the same words of the embedded dictionary.
 * @since 4.1
 */
public final class EmbeddedGermanDictionary {

  private static final String DICT = "/de/danielnaber/jwordsplitter/wordsGerman.txt";   // dict inside the JAR

  private static Set<String> words;

  private EmbeddedGermanDictionary() {
  }

  public static synchronized Set<String> getWords() {
    if (words == null) {
      words = new HashSet<>();
      //long t = System.currentTimeMillis();
      try (InputStream is = new BufferedInputStream(EmbeddedGermanDictionary.class.getResourceAsStream(DICT));
           InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
           BufferedReader br = new BufferedReader(isr)
      ) {
        String line;
        while ((line = br.readLine()) != null) {
          if (!line.startsWith("#")) {
            words.add(line.trim().toLowerCase());
          }
        }
        //long t2 = System.currentTimeMillis();
        //System.out.println("Loading time: " + (t2-t) + "ms");
      } catch (IOException e) {
        throw new RuntimeException("Could not load " + DICT, e);
      }
    }
    return words;
  }

}
