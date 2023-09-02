#!/bin/sh
# Build the binary dictionary, then build the artifact

RESOURCES=src/main/resources/de/danielnaber/jwordsplitter
BIN_FILE=$RESOURCES/wordsGerman.ser
ALL_WORDS=$RESOURCES/all-words.txt

echo "Removing $BIN_FILE"
rm $BIN_FILE
echo "Removing $RESOURCES/all-words.txt"
rm $ALL_WORDS

mvn clean package -DskipTests
grep -v -f $RESOURCES/removals.txt $RESOURCES/languagetool-dict.txt | cat - $RESOURCES/additions.txt $RESOURCES/germanPrefixes.txt | grep -v "^#" >$ALL_WORDS
java -cp target/jwordsplitter-*-SNAPSHOT.jar de.danielnaber.jwordsplitter.converter.SerializeDict $ALL_WORDS $BIN_FILE

mvn clean package
echo "Writing new file to: $BIN_FILE"
echo -n "Result: "
ls -l $BIN_FILE

echo -n "Total lines: "
wc -l $ALL_WORDS
rm $ALL_WORDS
