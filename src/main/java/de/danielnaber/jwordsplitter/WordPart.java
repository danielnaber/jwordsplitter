package de.danielnaber.jwordsplitter;

/**
 * Copyright 2013 Bernhard Kraft (kraft(at)webconsulting.at)
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

import java.lang.String;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * This class represents a splitted word part. It does not only contain
 * the splitted word and the original word but also the start/end offset
 * for the splitted word in the original.
 *
 * @author Bernhard Kraft
 */
public class WordPart {
	protected String original = null;

	protected int beginIndex = -1;
	protected int endIndex = -1;
	
	public WordPart(String original) {
		this.original = original;
		this.beginIndex = 0;
		this.endIndex = original.length();
	}

	public WordPart(String original, int beginIndex, int endIndex) {
		if (beginIndex < 0) {
			throw new IndexOutOfBoundsException();
		}
		if (endIndex > original.length()) {
			throw new IndexOutOfBoundsException();
		}
		if (beginIndex > endIndex) {
			throw new IndexOutOfBoundsException();
		}
		this.original = original;
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
	}

	public WordPart(String original, int beginIndex) {
		this(original, beginIndex, original.length());
	}


	public WordPart(WordPart part, int beginIndex, int endIndex) {
		if (beginIndex < 0) {
			throw new IndexOutOfBoundsException();
		}
		if (endIndex > part.length()) {
			throw new IndexOutOfBoundsException();
		}
		if (beginIndex > endIndex) {
			throw new IndexOutOfBoundsException();
		}
		this.original = part.getOriginal();
		this.beginIndex = part.getBeginIndex() + beginIndex;
		this.endIndex = part.getBeginIndex() + endIndex;
	}

	public WordPart(WordPart part, int beginIndex) {
		this(part, beginIndex, part.length());
	}

	public WordPart(WordPart part) {
		this(part, 0, part.length());
	}

	/*
	 * @override
	 */
	public String toString() {
		return original.substring(beginIndex, endIndex);
	}	

	public String debug() {
		return "{\"" + original + "\" (" + beginIndex + ", " + endIndex + "): \""+toString()+"\"}";
	}

	public String getOriginal() {
		return original;
	}

	public int getBeginIndex() {
		return beginIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public int length() {
		return endIndex-beginIndex;
	}

	public WordPart toLowerCase() {
		return new WordPart(original.toLowerCase(), beginIndex, endIndex);
	}

	public WordPart substring(int beginIndex, int endIndex) {
		return new WordPart(this, beginIndex, endIndex);
	}

	public WordPart substring(int beginIndex) {
		return new WordPart(this, beginIndex);
	}

	public List<WordPart> split(String regex) {
		Pattern p = Pattern.compile(regex);
		String current = toString();
		int len = current.length();
		Matcher m = p.matcher(current);
		ArrayList <WordPart> result = new ArrayList<WordPart>();
		int lastEnd = 0;
		while (m.find()) {
			int startMatch = m.start();
			if (startMatch > lastEnd) {
				result.add(substring(lastEnd, startMatch));
			}
			lastEnd = m.end();
		}
		if (lastEnd < len) {
			result.add(substring(lastEnd, len));
		}
		return result;
	}

	protected void stripInOriginal(String regex) {
		Pattern p = Pattern.compile(regex);
		int len = original.length();
		Matcher m = p.matcher(original);
		int removed = 0;
		int lastEnd = 0;
		int wordBeginIndex = beginIndex;
		int wordEndIndex = endIndex;
		String result = "";
		while (m.find()) {
			int startMatch = m.start();
			int endMatch = m.end();
			if (startMatch > lastEnd) {
				result = result.concat(original.substring(lastEnd, startMatch));
				removed = endMatch - startMatch;
				// B ... current part beginIndex
				// E ... current part endIndex
				// s ... separator startMatch
				// e ... separator endMatch

				// a ... any character
				// W ... current word character
				// S ... Separator being split
				// s ... Separator being split and part of current word character
				if (wordBeginIndex >= endMatch) {
					// Word after separator
					// aaaaSSaaaaWWWWWWaaaaa
					//     s e   B     E
					// B > e
					beginIndex  -= removed;
					endIndex  -= removed;
					// aaaaaaaaWWWWWWaaaaa
					//         B     E
				} else if ((wordBeginIndex >= startMatch) && (wordEndIndex > endMatch)) {
					// Word start overlap
					// aaaaSSssWWWWWaaaaa
					//     s B e    E
					// B > s // E > e
					beginIndex  -= (beginIndex-startMatch);
					endIndex  -= removed;
					// aaaaWWWWWaaaaa
					//     B    E
				} else if ((wordBeginIndex >= startMatch) && (wordEndIndex <= endMatch)) {
					// Whole word included
					// aaaaSSssssssSSSaaaaa
					//     s B     E  e
					// B > s // E < e
					beginIndex  -= (wordBeginIndex-startMatch);
					// Whole part removed. 0-String
					endIndex = beginIndex;
					// aaaaaaaaa
					//     B
					//     E
				} else if (wordEndIndex > startMatch) {
					// Word end overlap
					// aaaaaWWWWssSSSSaaaa
					//      B   s E   e
					// E > s
					endIndex -= (wordEndIndex-startMatch);
					// aaaaaWWWWaaaa
					//      B   E
				} else {
					// Word before separator
					// aaaaaWWWWaaaaaaSSaaaa
					// aaaaaWWWWaaaaaaaaaa
				}
			}

/*

*/
			lastEnd = endMatch;
		}
		if (lastEnd < len) {
			result = result.concat(original.substring(lastEnd, len));
		}
		original = result;
	}

	public List<WordPart> exceptionSplit(String regex) {
		List<WordPart> pieces = split(regex);
		Iterator<WordPart> i = pieces.iterator();
//		ArrayList<WordPart> result = new ArrayList<WordPart>();
		while (i.hasNext()) {
			WordPart p = i.next();
			p.stripInOriginal(regex);
		}
		return pieces;
//		return result;
	}

	public boolean startsWith(String begin) {
		return toString().startsWith(begin);
	}

	public boolean endsWith(String end) {
		return toString().endsWith(end);
	}

	public boolean equals(String comp) {
		return toString().equals(comp);
	}

	public void eatLeft(int length) {
		beginIndex -= length;
		if (beginIndex < 0) {
			beginIndex = 0;
		}
	}

	public void eatRight(int length) {
		endIndex += length;
		if (endIndex > original.length()) {
			endIndex = original.length();
		}
	}


}

