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

    private static final Set<String> wordsRequiringInfixS = new HashSet<>();
    
    private final Set<String> dictionary;

    static {
        wordsRequiringInfixS.add("Verhalten");
    }

    GermanInterfixDisambiguator(Set<String> dictionary) {
        this.dictionary = dictionary;
    }

    // TODO:
    // ecke, recke ...
    // stube vs. tube
    // tau vs. stau

    List<String> disambiguate(List<String> parts) {
        List<String> newParts = new ArrayList<>(parts);
        int lastPartIdx = parts.size() - 1;
        if (parts.size() > 1) {
            String lastPart = parts.get(lastPartIdx);
            if (lastPart.equals("samt") || lastPart.equals("samts") || lastPart.equals("samtes")) {
                // Verkehr+s+amt = Verkehrs+amt
                newParts.set(lastPartIdx - 1, parts.get(lastPartIdx - 1) + "s");
                newParts.set(lastPartIdx, lastPart.replaceFirst("^s", ""));
                return newParts;
            }
        }
        for (int i = newParts.size() - 1; i >= 2; i--) {
            String part = newParts.get(i);
            String prevPart = newParts.get(i - 1);
            String prevPrevPart = newParts.get(i - 2);
            if (prevPart.equals("s")) {
                boolean partIsWord = isWord("s" + part);
                if (!partIsWord || wordsRequiringInfixS.contains(prevPrevPart)) {
                    // Schönheit+s+tempel = Schönheits-tempel
                    newParts.set(i - 2, prevPrevPart + "s");
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
