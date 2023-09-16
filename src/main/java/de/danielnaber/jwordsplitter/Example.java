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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * A very simple example of how to use jWordSplitter.
 */
final class Example {

    private Example() {}

    public static void main(String[] args) throws IOException {
        GermanWordSplitter splitter = new GermanWordSplitter(true);
        splitter.setMinimumWordLength(3);
        splitter.setStrictMode(true);
        List<String> words = new ArrayList<>();
        if (args.length == 1) {
            Scanner sc = new Scanner(new File(args[0]));
            while (sc.hasNextLine()) {
                words.add(sc.nextLine());
            }
        } else {
            String wordsInput = "Bahnhofsuhr, Bahnhofssanierung";
            words = Arrays.asList(wordsInput.split(",\\s*"));
        }
        for (String word : words) {
            List<String> parts = splitter.splitWord(word);
            System.out.println(String.join(", ", parts));
        }
    }

}
