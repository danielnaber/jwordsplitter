/**
 * Copyright 2004-2007 Sven Abels
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
package de.danielnaber.jwordsplitter.tools;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * This stores serializable objects. IMPORTANT: THOSE OBJECTS SHOULD HAVE A serialVersionUID:
 * <br/><br/>
 * <code>private static final long serialVersionUID = 1L;</code>
 */
public class FastObjectSaver {

    private FastObjectSaver() {
        // no public constructor, static methods only
    }

    /**
     * Stores serializable objects. IMPORTANT: THOSE OBJECTS SHOULD HAVE A serialVersionUID!
     *     private static final long serialVersionUID = 1L;
     * @throws IOException
     */
    public static void saveToFile(String filename, Serializable serializableObject) throws IOException {
        final FileOutputStream fos = new FileOutputStream(filename);
        final ObjectOutputStream oos = new ObjectOutputStream(fos);
        try {
            oos.writeObject(serializableObject);
        } finally {
            oos.close();
        }
    }

    /**
     * Load a serialized dictionary.
     * @throws IOException
     */
    public static synchronized Object load(String filename) throws IOException {
        final InputStream is = FastObjectSaver.class.getResourceAsStream(filename);
        if (is == null) {
            throw new IOException("Cannot find dictionary in class path: " + filename);
        }
        final ObjectInputStream oos = new ObjectInputStream(is);
        try {
            return oos.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Could not read data from " + filename, e);
        }
    }

}
