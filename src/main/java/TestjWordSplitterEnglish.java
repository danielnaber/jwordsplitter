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

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import de.abelssoft.wordtools.jwordsplitter.AbstractWordSplitter;
import de.abelssoft.wordtools.jwordsplitter.impl.EnglishWordSplitter;

/**
 * WARNING: Please note that English is worse than the German version right now since 
 * the dictionary is quite small.
 *
 * @author Sven Abels
 */
public class TestjWordSplitterEnglish {

    public static void main(String[] args) throws IOException {
        System.out.println("Loading Dictionary...");
        AbstractWordSplitter ws = new EnglishWordSplitter(true);
        System.out.println("done. Now analyzing...");

        String s;
        s = "containership2005";
        //some other words:
        //s="summertime";
        //s="sunflower";
        //doghouse, containership, ecosystem, eyeglasses, handshake, handwriting, houseboat, goldfish, fireplace

        Collection<String> col = ws.splitWord(s.toLowerCase());
        System.out.println("done. Contains fragments: " + col.size());

        for (Iterator<String> it = col.iterator(); it.hasNext();) {
            System.out.println("-" + it.next());
        }
    }

}
