jWordSplitter 4.1
=================

Copyright 2004-2007 Sven Abels  
Copyright 2007-2015 Daniel Naber  
Source code licensed under Apache License, Version 2.0 (see below)

This Java library can split German compound words into smaller parts.
For example "Erhebungsfehler" will be split into "Erhebung" and "fehler".
This is especially useful for German words but it can work with
all languages, as long as a dictionary and a class extending `AbstractWordSplitter`
is provided. So far, only German is supported and a German dictionary is included
in the JAR. Even though it will work for some adjectives (e.g. "knallgelb" -> knall + gelb)
and verbs (e.g. "zurückrudern" -> zurück + rudern) it works best for nouns.

Alternatives to this library might be [compound-splitter](https://github.com/dweiss/compound-splitter)
or [Lucene's](http://lucene.apache.org/core/) `DictionaryCompoundWordTokenFilter`.
You might also be interested in this [German morphology dictionary](http://www.danielnaber.de/morphologie/).

#### Usage from Java

Use this dependency or [download the JAR here](http://search.maven.org/remotecontent?filepath=de/danielnaber/jwordsplitter/4.1/jwordsplitter-4.1.jar):

```xml
<dependency>
    <groupId>de.danielnaber</groupId>
    <artifactId>jwordsplitter</artifactId>
    <version>4.1</version>
</dependency>
```

Example usage:

```java
AbstractWordSplitter splitter = new GermanWordSplitter(true);
List<String> parts = splitter.splitWord("Versuchsreihe");
System.out.println(parts);    // prints: [Versuchs, reihe]
```

#### Usage from Command Line

To split a list of words (one word per line), use this command:

    java -jar jwordsplitter-x.y.jar <filename>

#### Data Import and Export

To export the German dictionary from the JAR file, use this command:

    java -cp jwordsplitter-x.y.jar de.danielnaber.jwordsplitter.converter.ExportDict /de/danielnaber/jwordsplitter/wordsGerman.ser

To serialize a text dictionary (one word per line) to a binary format
so it can be used by jWordSplitter, use this command:

    java -cp jwordsplitter-x.y.jar de.danielnaber.jwordsplitter.converter.SerializeDict <textDict> <output>

The binary format used is simply the standard Java object serialization.

#### Notes about the algorithm

* The algorithm is very simple and knows almost nothing about language: it tries to split
  the word and checks if one part is actually a word, i.e. whether it occurs in the dictionary.
  If that's the case, the same procedure is recursively applied to the remaining part of the
  word. If not, the position at which the word is split is moved by one character. Due to
  the way the algorithm works it doesn't matter if the input words are nouns, verbs, or
  adjective compounds (as long as they are in the dictionary).
* The length of the word to be split is not limited. For example, jWordSplitter can split
  the famous "Donaudampfschifffahrtskapitän" (Donau, dampf, schiff, fahrt, kapitän).
* To improve results, you will need to tune the contents of the dictionary or
  add exceptions using `GermanWordSplitter.addException()`.
* The dictionary also needs to contain inflected forms (plural, genitive etc.). German has
  some compound parts like "Miet" (in Mietwohnung, Mietverhältnis) or "Wohn" (Wohnraum,
  Wohnrecht) that are not words on their own and yet need to be part of the dictionary.
* The algorithm knows about the German interfix character "s" (als known as linking element),
  as in "Verlag**s**haus", but it will also happily split wrong or uncommon words like "Verlaghaus".
* The algorithm can work with other languages too. Extend class `AbstractWordSplitter` in order
  to add support for a new language.

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
