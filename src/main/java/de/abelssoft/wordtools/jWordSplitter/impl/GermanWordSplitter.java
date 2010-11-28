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
package de.abelssoft.wordtools.jWordSplitter.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.abelssoft.tools.FileTools;
import de.abelssoft.tools.persistence.FastObjectSaver;
import de.abelssoft.wordtools.jWordSplitter.AbstractWordSplitter;

/**
 * This implements a German word splitter.
 * 
 * @author Sven Abels
 */
public class GermanWordSplitter extends AbstractWordSplitter
{

  private static final String SERIALIZED_DICT = "/wordsGerman.ser";   // dict inside the JAR

  private Set<String> words = null;

  private int minimumWordLength = 4;
	
	public GermanWordSplitter() throws IOException
	{
		this(true);
	}

	public GermanWordSplitter(boolean hideConnectingCharacters, String plainTextDictFile) throws IOException
	{
		super(hideConnectingCharacters, plainTextDictFile);
	}

	public GermanWordSplitter(boolean hideConnectingCharacters) throws IOException
	{
	  super(hideConnectingCharacters);
	}

	@Override
	protected Set<String> getWordList() throws IOException
	{
    if (words == null) {
      words = loadWords();
    }
		return words;
	}
	
	private Set<String> loadWords() throws IOException
	{
	  if (plainTextDictFile != null) {
	    return FileTools.loadFile(plainTextDictFile);
	  } else {
	    return (HashSet<String>)FastObjectSaver.load(SERIALIZED_DICT);
	  }
	}
	
	@Override
	protected int getMinimumWordLength()
	{
		return minimumWordLength ;
	}

	public void setMinimumWordLength(int minimumWordLength)
  {
    this.minimumWordLength = minimumWordLength;
  }

	@Override
	protected Collection<String> getConnectingCharacters()
	{
	  Collection<String> connChars = new ArrayList<String>();
	  // combination of the characters below:
    connChars.add("s-");
    // connection characters ("Fugenelemente"):
	  connChars.add("s");
	  connChars.add("-");
		return connChars;
	}

}
