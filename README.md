jWordSplitter 4.0-dev (2015-xx-yy)
==================================

Copyright 2004-2007 Sven Abels  
Copyright 2007-2013 Daniel Naber  
Homepage: http://www.danielnaber.de/jwordsplitter

This Java library can split German words into their smallest parts (atoms).
For example "Erhebungsfehler" will be split into "erhebung" and "fehler".
This is especially beneficial for German words but it can work with
all languages, as long as a dictionary and a class extending `AbstractWordSplitter`
is provided. So far, only German is supported and a German dictionary is included
in the JAR.

#### Usage

To split a list of words (one word per line), use this command:

    java -jar jwordsplitter-x.y.jar <filename>

#### Data Import and Export

To export the German dictionary from the JAR file, use this command:

    java -cp jwordsplitter-x.y.jar de.danielnaber.jwordsplitter.converter.ExportDict /de/danielnaber/jwordsplitter/wordsGerman.ser

To serialize a text dictionary (one word per line) to a binary format
so it can be used by jWordSplitter, use this command:

    java -cp jwordsplitter-x.y.jar de.danielnaber.jwordsplitter.converter.SerializeDict <textDict> <output>

The binary format used is simply the standard Java object serialization.

#### Building

Use `build.sh` to create the JAR. It will build the internal binary dictionary
from the plain text files in `resources` and then run the required mvn commands.

#### Changelog

See [CHANGES.md](https://github.com/danielnaber/jwordsplitter/blob/master/CHANGES.md).
If you need the old project history (for example to access tags that got lost when
moving to git), check it out from SVN at http://sourceforge.net/p/jwordsplitter/code/HEAD/tree/

#### License

The source code part of this project is licensed under [Apache License, Version 2.0](https://github.com/danielnaber/jwordsplitter/blob/master/LICENSE.txt).
The integrated dictionary (`wordsGerman.ser`) is a subset of
[Morphy](http://www.wolfganglezius.de/doku.php?id=cl:morphy) with additions from
[LanguageTool](https://languagetool.org) and licensed under
[Creative Commons Attribution-Share Alike 4.0](http://creativecommons.org/licenses/by-sa/4.0/).
