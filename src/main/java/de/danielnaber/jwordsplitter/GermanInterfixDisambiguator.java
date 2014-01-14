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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Tries to resolve ambiguities, e.g. "Urlaubsorte" could be
 * Urlaub+s+Orte but also Urlaub+Sorte (our dictionary doesn't know
 * which words require an interfix character).
 */
class GermanInterfixDisambiguator {

    private static final Set<String> wordsRequiringInfixS = new HashSet<String>();
    private final Set<String> dictionary;

    static {
        wordsRequiringInfixS.add("Verhalten");
    }

    GermanInterfixDisambiguator(Set<String> dictionary) {
        this.dictionary = dictionary;
    }

    // TODO:
    // ecke, recke ... amt, samt
    // stube vs. tube
    // tau vs. stau

    List<WordPart> disambiguate(List<WordPart> parts) {
        final List<WordPart> newParts = new ArrayList<WordPart>(parts);
        for (int i = newParts.size() - 1; i >= 2; i--) {
            final WordPart part = newParts.get(i);
            final WordPart prevPart = newParts.get(i - 1);
            final WordPart prevPrevPart = newParts.get(i - 2);
            if (prevPart.equals("s")) {
                final boolean partIsWord = isWord("s" + part);
                if (partIsWord && !wordsRequiringInfixS.contains(prevPrevPart)) {
                    // Wein+s+orte = Wein-sorte
                    part.eatLeft(1);
                    newParts.remove(i - 1);   // remove infix
                } else {
                    // Schönheit+s+tempel = Schönheits-tempel
                    prevPrevPart.eatRight(1);
                    newParts.remove(i - 1);   // remove infix
                }
            }
        }
        return newParts;
    }

    private boolean isWord(String word) {
        return dictionary.contains(word.toLowerCase());
    }
}
