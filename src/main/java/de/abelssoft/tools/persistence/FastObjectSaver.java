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
package de.abelssoft.tools.persistence;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * This stores serializable objects. IMPORTANT: THOSE OBJECTS SHOULD HAVE A serialVersionUID!
 *     private static final long serialVersionUID = 1L;
 */
public class FastObjectSaver
{
  
    private FastObjectSaver() {
      // no public constructor, static methods only
    }

    /**
     * Stores serializable objects. IMPORTANT: THOSE OBJECTS SHOULD HAVE A serialVersionUID!
     *     private static final long serialVersionUID = 1L;
     * @throws IOException 
     */
    public static void saveToFile(String filename, Serializable serializableObject) throws IOException {
      FileOutputStream fos = new FileOutputStream(filename);
      ObjectOutputStream oos = new ObjectOutputStream( fos );
      oos.writeObject( serializableObject );
      oos.close();
    }

    /**
     * Load a serialized dictionary.
     * @throws IOException
     */
    public static synchronized Object load(String filename) throws IOException {
      InputStream is = FastObjectSaver.class.getResourceAsStream(filename);
      if (is == null) {
        throw new IOException("Cannot locate dictionary in JAR: " + filename);
      }
      ObjectInputStream oos = new ObjectInputStream(is);
      try {
        return oos.readObject();
      } catch (ClassNotFoundException e) {
        IOException ioe = new IOException();
        ioe.initCause(e);
        throw ioe;
      }
    }
    
}
