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
package de.abelssoft.wordtools.jwordsplitter.converter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import de.abelssoft.tools.persistence.FastObjectSaver;

/**
 * This imports a txt file and saves it in the jWordSplitter format.
 * 
 * @author Sven Abels
 */
public class SerializeDict
{
	
	private static HashSet<String> getFileContents(String filename) throws IOException
	{
		HashSet<String> lines = new HashSet<String>();
		String line;
		BufferedReader br = new BufferedReader(new FileReader(filename));
		while ((line = br.readLine()) != null) 
		{ 
			lines.add(line);
		}
		br.close();
	  return lines;
	}
	
	/**
	 * Read a text file with one word per line and serialize the
	 * HashSet with all words to a binary file that can be part
	 * of jWordSplitter's JAR.
	 * 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException
	{
	  if (args.length != 2) {
	    System.out.println("Usage: SerializeDict <input> <output>");
	    System.exit(1);
	  }
    System.out.println("Reading " + args[0] + "...");
		HashSet<String> hs = getFileContents(args[0]);
		String outputFile = args[1];
    System.out.println("Saving " + outputFile + "...");
    FastObjectSaver.saveToFile(outputFile, hs);
    System.out.println("Done.");
	}

}
