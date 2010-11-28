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

import de.abelssoft.tools.persistence.FastObjectSaver;
import de.abelssoft.wordtools.jWordSplitter.AbstractWordSplitter;

/**
 * This implements an English word splitter.
 * 
 * @author Sven Abels
 */
public class EnglishWordSplitter extends AbstractWordSplitter
{

	private static Set<String> words = null;
	
	public EnglishWordSplitter() throws IOException
	{
		this(true);
	}

	public EnglishWordSplitter(boolean withoutConnectingCharacters) throws IOException
	{
		super(withoutConnectingCharacters);
	}

	@Override
	protected Set<String> getWordList() throws IOException
	{
    if (words == null) {
      words = loadWords();
    }
		return words;
	}

	private static Set<String> loadWords() throws IOException
	{
			return (HashSet<String>)FastObjectSaver.load("/wordsEnglish.ser");
	}

	@Override
	protected int getMinimumWordLength()
	{
		return 3;
	}

	@Override
	protected Collection<String> getConnectingCharacters()
	{
	  return new ArrayList<String>();
	}

}
