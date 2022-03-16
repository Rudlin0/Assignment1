package a1;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.Collator;
import java.util.Scanner;

import org.junit.Test;

import net.datastructures.ArrayList;
import net.datastructures.LinkedPositionalList;
import net.datastructures.PositionalList;

public class Spellchecker {

    /**
     * Reads in a list of words from a file to be checked against.
     * 
     * @param filename name of the file being read in.
     * @return LinkedPositionalList<String> list of words read in.
     */
    public static LinkedPositionalList<String> readWordList(String filename) {
        Scanner input = null;
        LinkedPositionalList<String> wordList = new LinkedPositionalList<>();
        try {
            input = new Scanner(new File(filename));
            while (input.hasNext()) {
                wordList.addLast(input.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.printf("Exception: FileNotFound\n");
        } finally {
            input.close();
        }

        return wordList;
    }

    /**
     * Tests readWordList() for valid output.
     */
    @Test
    public void testReadWordList() {
        assertEquals("a", readWordList("/Users/Rudy/CS271/Assignment1/jlawler-wordlist.txt").first().getElement());
    }

    /**
     * Reads in a text file of words to check if they are misspelled.
     * 
     * @param filename name of the file being read in.
     * @return ArrayList<String> List of words in the file.
     */
    public static ArrayList<String> readTextFile(String filename) {
        Scanner input = null;
        ArrayList<String> textFileWords = new ArrayList<>();
        int currentIndex = 0;
        try {
            input = new Scanner(new File(filename)).useDelimiter("[^a-zA-Z]+");
            while (input.hasNext()) {
                textFileWords.add(currentIndex++, input.next());
            }
        } catch (FileNotFoundException e) {
            System.out.printf("Exception: FileNotFound\n");
        } finally {
            input.close();
        }

        return textFileWords;
    }

    /**
     * Tests readTextFile() for valid output.
     */
    @Test
    public void testReadTextFile() {
        assertEquals("Onec", readTextFile("/Users/Rudy/CS271/Assignment1/sampleTextFile.txt").get(0));
    }

    /**
     * Checks how a word is misspelled and gives suggestions.
     * 
     * @param word     Word to be checked for misspelling.
     * @param wordList Dictionary of words to be referenced against.
     * @param stats    int array tracking # of insertions, deletions, swaps,
     *                 replacements, as well as # of suggestions overall.
     * @return ArrayList<String> List consisting of initial word, as well as
     *         suggested replacement words.
     */
    public static ArrayList<String> spellcheck(String word, PositionalList<String> wordList, int[] stats) {

        ArrayList<String> wordAndRelated = new ArrayList<>();

        wordAndRelated.add(0, word.toLowerCase());

        stats[0] += swapAdjacentLetters(wordList, wordAndRelated, stats);

        stats[1] += deleteSingleLetter(wordList, wordAndRelated, stats);

        stats[2] += insertExtraLetter(wordList, wordAndRelated, stats);

        stats[3] += replaceSingleLetter(wordList, wordAndRelated, stats);

        String originalWord = wordAndRelated.remove(0);
        Collator comparingStrings = Collator.getInstance();
        for (int i = 0; i < wordAndRelated.size(); i++) {
            for (int j = i + 1; j < wordAndRelated.size(); j++) {
                if (comparingStrings.compare(wordAndRelated.get(i), wordAndRelated.get(j)) > 0) {
                    String movedWord = wordAndRelated.get(i);
                    wordAndRelated.set(i, wordAndRelated.get(j));
                    wordAndRelated.set(j, movedWord);
                }
            }
        }

        wordAndRelated.add(0, originalWord);

        return wordAndRelated;
    }

    /**
     * Swaps adjacent letters to check if resulting word is valid or not.
     * 
     * @param wordList       Dictionary of words to be referenced against.
     * @param wordAndRelated List consisting of initial word and any suggestions
     *                       made up to this point.
     * @param stats          int array tracking # of insertions, deletions, swaps,
     *                       replacements, as well as # of suggestions overall.
     * @return int # of successful swaps.
     */
    public static int swapAdjacentLetters(PositionalList<String> wordList,
            ArrayList<String> wordAndRelated, int[] stats) {
        String originalWord = wordAndRelated.get(0);
        int swaps = 0;

        for (String word : wordList) {
            char[] wordArray = word.toCharArray();
            String newWord;
            boolean isAlreadyInList;
            for (int i = 1; i < wordArray.length; i++) {
                isAlreadyInList = false;
                wordArray = word.toCharArray();
                char swappedChar = wordArray[i];
                wordArray[i] = wordArray[i - 1];
                wordArray[i - 1] = swappedChar;
                newWord = new String(wordArray);

                for (int k = 1; k < wordAndRelated.size(); k++) {
                    if (wordAndRelated.get(k).equals(newWord)) {
                        isAlreadyInList = true;
                    }
                }

                if (newWord.equals(originalWord) && !isAlreadyInList) {
                    wordAndRelated.add(wordAndRelated.size(), word);
                    swaps++;
                    stats[4]++;
                }
            }
        }

        return swaps;
    }

    /**
     * Tests swapAdjacentLetters() for valid output.
     */
    @Test
    public void testSwapAdjacentLetters() {
        ArrayList<String> wordAndRelated = new ArrayList<>();
        wordAndRelated.add(0, "onec");
        int[] stats = new int[5];
        swapAdjacentLetters(readWordList("/Users/Rudy/CS271/Assignment1/jlawler-wordlist.txt"), wordAndRelated, stats);
        assertEquals("once", wordAndRelated.get(1));
    }

    /**
     * Inserts additional letters into word to check if result is valid or not.
     * 
     * @param wordList       Dictionary of words to be referenced against.
     * @param wordAndRelated List consisting of initial word and any suggestions
     *                       made up to this point.
     * @param stats          int array tracking # of insertions, deletions, swaps,
     *                       replacements, as well as # of suggestions overall.
     * @return int # of successful insertions.
     */
    public static int insertExtraLetter(PositionalList<String> wordList, ArrayList<String> wordAndRelated,
            int[] stats) {
        String originalWord = wordAndRelated.get(0);
        int insertions = 0;

        for (String word : wordList) {
            ArrayList<Character> wordArray = new ArrayList<>();

            for (int i = 0; i < originalWord.length(); i++) {
                wordArray.add(wordArray.size(), originalWord.charAt(i));
            }

            boolean isAlreadyInList;

            for (int i = 0; i <= wordArray.size(); i++) {
                for (int j = 0; j < 26; j++) {
                    isAlreadyInList = false;
                    wordArray.add(i, (char) (97 + j));
                    StringBuilder builder = new StringBuilder(wordArray.size());

                    for (Character ch : wordArray) {
                        builder.append(ch);
                    }

                    String newWord = builder.toString();

                    for (int k = 1; k < wordAndRelated.size(); k++) {
                        if (wordAndRelated.get(k).equals(newWord)) {
                            isAlreadyInList = true;
                        }
                    }

                    if (newWord.equals(word) && !isAlreadyInList) {
                        wordAndRelated.add(wordAndRelated.size(), word);
                        insertions++;
                        stats[4]++;
                    }

                    wordArray.remove(i);
                }
            }
        }

        return insertions;
    }

    /**
     * Tests insertExtraLetter() for valid output.
     */
    @Test
    public void testInsertExtraLetter() {
        ArrayList<String> wordAndRelated = new ArrayList<>();
        wordAndRelated.add(0, "onec");
        int[] stats = new int[5];
        insertExtraLetter(readWordList("/Users/Rudy/CS271/Assignment1/wordlisttest.txt"), wordAndRelated, stats);
        assertEquals("donec", wordAndRelated.get(1));
    }

    /**
     * Removes letters from word to check if result is valid or not.
     * 
     * @param wordList       Dictionary of words to be referenced against.
     * @param wordAndRelated List consisting of initial word and any suggestions
     *                       made up to this point.
     * @param stats          int array tracking # of insertions, deletions, swaps,
     *                       replacements, as well as # of suggestions overall.
     * @return int # of successful deletions.
     */
    public static int deleteSingleLetter(PositionalList<String> wordList, ArrayList<String> wordAndRelated,
            int[] stats) {
        String originalWord = wordAndRelated.get(0);
        int deletions = 0;

        for (String word : wordList) {
            ArrayList<Character> wordArray = new ArrayList<>();

            for (int i = 0; i < originalWord.length(); i++) {
                wordArray.add(wordArray.size(), originalWord.charAt(i));
            }

            boolean isAlreadyInList;

            for (int i = 0; i < originalWord.length(); i++) {
                isAlreadyInList = false;
                char removedChar = wordArray.remove(i);
                StringBuilder builder = new StringBuilder(wordArray.size());

                for (Character ch : wordArray) {
                    builder.append(ch);
                }

                String newWord = builder.toString();

                for (int k = 1; k < wordAndRelated.size(); k++) {
                    if (wordAndRelated.get(k).equals(newWord)) {
                        isAlreadyInList = true;
                    }
                }

                if (newWord.equals(word) && !isAlreadyInList) {
                    wordAndRelated.add(wordAndRelated.size(), word);
                    deletions++;
                    stats[4]++;
                }

                wordArray.add(i, removedChar);
            }
        }

        return deletions;
    }

    /**
     * Tests deleteSingleLetter() for valid output.
     */
    @Test
    public void testDeleteSingleLetter() {
        ArrayList<String> wordAndRelated = new ArrayList<>();
        wordAndRelated.add(0, "onec");
        int[] stats = new int[5];
        deleteSingleLetter(readWordList("/Users/Rudy/CS271/Assignment1/wordlisttest.txt"), wordAndRelated, stats);
        assertEquals("one", wordAndRelated.get(2));
    }

    /**
     * Replaces letters in word to check if result is valid or not.
     * 
     * @param wordList       Dictionary of words to be referenced against.
     * @param wordAndRelated List consisting of initial word and any suggestions
     *                       made up to this point.
     * @param stats          int array tracking # of insertions, deletions, swaps,
     *                       replacements, as well as # of suggestions overall.
     * @return int # of successful replacements.
     */
    public static int replaceSingleLetter(PositionalList<String> wordList, ArrayList<String> wordAndRelated,
            int[] stats) {
        String originalWord = wordAndRelated.get(0);
        int replacements = 0;
        for (String word : wordList) {
            ArrayList<Character> wordArray = new ArrayList<>();

            for (int i = 0; i < word.length(); i++) {
                wordArray.add(wordArray.size(), word.charAt(i));
            }

            boolean isAlreadyInList;

            for (int i = 0; i < word.length(); i++) {
                for (int j = 0; j < 26; j++) {
                    isAlreadyInList = false;
                    Character replacedCharacter = wordArray.get(i);
                    wordArray.set(i, (char) (97 + j));
                    StringBuilder builder = new StringBuilder(wordArray.size());

                    for (Character ch : wordArray) {
                        builder.append(ch);
                    }

                    String newWord = builder.toString();

                    for (int k = 1; k < wordAndRelated.size(); k++) {
                        if (wordAndRelated.get(k).equals(newWord)) {
                            isAlreadyInList = true;
                        }
                    }

                    if (newWord.equals(originalWord) && !isAlreadyInList) {
                        wordAndRelated.add(wordAndRelated.size(), word);
                        replacements++;
                        stats[4]++;
                    }

                    wordArray.set(i, replacedCharacter);
                }
            }
        }

        return replacements;
    }

    /**
     * Tests replaceSingleLetter() for valid output.
     */
    @Test
    public void testReplaceSingleLetter() {
        ArrayList<String> wordAndRelated = new ArrayList<>();
        wordAndRelated.add(0, "onec");
        int[] stats = new int[5];
        replaceSingleLetter(readWordList("/Users/Rudy/CS271/Assignment1/wordlisttest.txt"), wordAndRelated, stats);
        assertEquals("ones", wordAndRelated.get(1));
    }
}
