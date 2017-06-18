jWordSplitter Change Log
========================

### 2017-..-.. (4.2)
* Net method `getSubWords()`, that also gets the shortest matches.
  For example, `Sauerstoffflasche` will get `Sauer, stoff, Sauerstoff, flasche`.
  Thanks to github user Tobulus.

### 2015-05-09 (4.1)
* extended the dictionary
* added several exceptions
* constructor `GermanWordSplitter(boolean hideInterfixCharacters)` now
  makes sure the dictionary is read only once
* fixed `getAllSplits()` to properly obey minimum word length

### 2015-04-20 (4.0)
* requires Java 1.7 or later
* moved classes to package `de.danielnaber.jwordsplitter`
* rewrote algorithm to make it simpler, slightly faster and more correct:
  it now always returns the longest match
* default minimum length of the compound parts is now 3, leading
  to much more decompositions
* dictionary update
* internal: binary dictionary not part of repository anymore. The
  source files are in the repository now and everything can be built
  with `build.sh`.

### 2012-09-24 (3.4)
* Important note for users who extend AbstractWordSplitter:
  `getConnectingCharacters()` must return lowercase characters now
* added new constructor:
  `GermanWordSplitter(boolean hideConnectingCharacters, InputStream plainTextDict)`
* added several words to the list of exceptions
* about 40% speedup if you split a lot of words (for many use cases, time of initialization
  will probably still outweigh runtime)
* Fixed an UnsupportedOperationException that could occur when strict mode
  is false (which is the default)
* Fixed the JAR so starting it with `java -jar jWordSplitter-x.y.jar <filename>`
  works again
* Removed `EnglishWordSplitter` as its dictionary was not included anyway. Extend
  `AbstractWordSplitter` if you want to add support for languages other than German.

### 2011-12-16 (3.3)
* renamed `jWordSplitter` in package path to `jwordsplitter` to be in accordance with 
  Java conventions 
* New file `exceptionsGerman.txt` added to JAR that contains special cases
  to overwrite the algorithm. With this we now get a correct split for e.g.
  Klimasünderecke -> Klima + Sünder + Ecke (used to be: Klima + Sünde + Recke)
* new method `addException()` to set the desired splitting for words, without touching 
  the dictionary
* small dictionary cleanup (e.g. removing some three-letter words)

### 2011-02-06 (3.2)
* now built with Maven, no other changes

### 2010-09-18 (3.1)
* fixed a bug: compound parts that ended in "s" caused the splitting not to work

### 2009-10-25 (3.0)
* using generics (i.e. at least Java 1.5 is required now for jWordSplitter)
* AbstractWordSplitter.splitWords(String) is now called AbstractWordSplitter.splitWord(String)
* slightly better handling of German compounds with hyphens
* small extensions to the dictionary 

### 2008-11-09 (2.2)
* in strict mode the minimum length of words is not longer ignored
* major dictionary update: using a smaller dictionary with less words
  but hopefully better quality (it's the one that was used for 
  LanguageTool already)

### 2008-07-01 (2.1)
* now distributed as a ZIP
* simplified output format of TestjWordSplitterGerman
* comes with a larger German dictionary
* ImportTxtFile is now called SerializeDict

### 2007-05-12 (2.0)
* The JAR can now be used directly to split a list of German words:
  `java -jar jWordSplitter.jar <filename>`

### 2007-05-08
* checked in original version from Sven Abels
* removed log4j dependency
* improved exception handling, i.e. exceptions aren't caught and logged but thrown
* code cleanup 
* hardcoded three examples of German compound parts that don't exist as stand-alone 
  words ("miet-", "grenz-", "ess-")
