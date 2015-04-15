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

import java.io.*;

/**
 * This stores serializable objects. IMPORTANT: THOSE OBJECTS SHOULD HAVE A serialVersionUID:
 * <br/><br/>
 * <code>private static final long serialVersionUID = 1L;</code>
 */
public final class FastObjectSaver {

    private FastObjectSaver() {
        // no public constructor, static methods only
    }

    /**
     * Stores serializable objects. IMPORTANT: THOSE OBJECTS SHOULD HAVE A serialVersionUID!
     *     private static final long serialVersionUID = 1L;
     */
    public static void saveToFile(File file, Serializable serializableObject) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(serializableObject);
        }
    }

    /**
     * Load a serialized dictionary.
     * @param filenameInClassPath a plain text dictionary file in the classpath
     */
    public static Object load(String filenameInClassPath) throws IOException {
        InputStream is = FastObjectSaver.class.getResourceAsStream(filenameInClassPath);
        if (is == null) {
            throw new IOException("Cannot find dictionary in class path: " + filenameInClassPath);
        }
        try (ObjectInputStream oos = new ObjectInputStream(is)) {
            return oos.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Could not read data from " + filenameInClassPath, e);
        }
    }

}
