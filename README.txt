jWordSplitter 3.5-dev (2012-xx-yy)
Copyright 2004-2007 Sven Abels
Copyright 2007-2013 Daniel Naber
See LICENSE.txt for license information.
Homepage: http://www.danielnaber.de/jwordsplitter

This Java library can split German words into their smallest parts (atoms).
For example "Erhebungsfehler" will be split into "erhebung" and "fehler".
This is especially beneficial for German words but it can work with
all languages, as long as a dictionary and a class extending AbstractWordSplitter
is provided. So far, only German is supported and a German dictionary is included
in the JAR.

To split a list of words (one word per line), use this command:
java -jar jwordsplitter-x.y.jar <filename>

To export the German dictionary from the JAR file, use this command:
java -cp jwordsplitter-x.y.jar de.danielnaber.jwordsplitter.converter.ExportDict /de/danielnaber/jwordsplitter/wordsGerman.ser

To serialize a text dictionary (one word per line) to a binary format
so it can be used by jWordSplitter, use this command:
java -cp jwordsplitter-x.y.jar de.danielnaber.jwordsplitter.converter.SerializeDict textDict textDict.ser

TODO:
  -keep the plain text dict in git, not the binary one; build binary only for release
  -build the dictionary from Morphy data
	-extend list of exceptions (exceptionsGerman.txt, GermanInterfixDisambiguator.java)
	-API: return null if a word isn't known at all, return the word itself
	 if it is not a compound?
