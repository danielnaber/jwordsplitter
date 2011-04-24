/**
 * Copyright 2011 Daniel Naber
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
package de.abelssoft.tools;

import de.abelssoft.wordtools.jWordSplitter.impl.GermanWordSplitter;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Scanner;

/**
 * Command line tool that takes a list of compounds and prints those that have
 * an ambiguous decomposition.
 * 
 * @author Daniel Naber
 */
public class AmbiguityFinder {
    
    private AmbiguityFinder() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: " + AmbiguityFinder.class.getSimpleName() + " <filename>");
            System.exit(1);
        }
        final GermanWordSplitter splitter = new GermanWordSplitter();
        splitter.setStrictMode(true);
        final Scanner scanner = new Scanner(new File(args[0]));
        try {
            while (scanner.hasNextLine()) {
                final String line = scanner.nextLine();
                splitter.setReverseMode(false);
                final Collection<String> split1 = splitter.splitWord(line.trim());
                splitter.setReverseMode(true);
                final Collection<String> split2 = splitter.splitWord(line.trim());
                if (!split1.equals(split2)) {
                    System.out.println(line + ": " + split1 + " <-> " + split2);
                }
            }            
        } finally {
            scanner.close();
        }
    }
}
