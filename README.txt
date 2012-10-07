jWordSplitter 3.5-dev (2012-xx-yy)
Copyright 2004-2007 Sven Abels, Copyright 2007-2012 Daniel Naber, see LICENSE.txt for license information.
Homepage: http://www.danielnaber.de/jwordsplitter

TODO: move resources to sub package

This Java library can split words into their smallest parts (atoms).
For example (in German) "Erhebungsfehler" will be split into "erhebung" and
"fehler". This is especially beneficial for German words but it can work with 
all languages, as long as a dictionary and a class extending AbstractWordSplitter
is provided. So far, only German is supported and a German dictionary is included
in the JAR.

jWordSplitter requires about 7MB RAM (that is, you can write a simple Java program
that uses jWordSplitter and run it with option -Xmx7M).

To split a list of words (one word per line), use this command:
java -jar jWordSplitter.jar <filename>

To export the German dictionary from the JAR file, use this command:
java -cp jWordSplitter.jar de.abelssoft.wordtools.jwordsplitter.converter.ExportDict /wordsGerman.ser

To serialize a text dictionary (one word per line) to a binary format
so it can be used by jWordSplitter, use this command:
java -cp jWordSplitter.jar de.abelssoft.wordtools.jwordsplitter.converter.SerializeDict textDict textDict.ser

TODO:
	-extend list of exceptions (exceptionsGerman.txt)
	-further decrease JAR size by removing compounds from the dictionary?
	-API: return null if a word isn't known at all, return the word itself
	 if it is not a compound?
