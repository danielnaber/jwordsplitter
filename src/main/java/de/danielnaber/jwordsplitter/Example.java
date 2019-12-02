/*
 * Copyright 2009 Daniel Naber
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

import java.io.IOException;
import java.util.List;

/**
 * A very simple example of how to use jWordSplitter.
 *
 * @author Daniel Naber
 */
final class Example {

    private Example() {}

    public static void main(String[] args) throws IOException {
        GermanWordSplitter splitter = new GermanWordSplitter(true);
        splitter.setMinimumWordLength(3);
        splitter.setStrictMode(true);
        String wordsInput = "Bahnhofsuhr, Bahnhofssanierung";
        String[] words = wordsInput.split(",\\s*");
        for (String word : words) {
            List<String> parts = splitter.splitWord(word);
            System.out.println(parts);
        }
    }

}
