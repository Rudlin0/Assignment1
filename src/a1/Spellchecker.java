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
     * @param filename
     * @return LinkedPositionalList<String>
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

    @Test
    public void testReadWordList() {
        assertEquals("a", readWordList("/Users/Rudy/CS271/Assignment1/jlawler-wordlist.txt").first().getElement());
    }

    /**
     * @param filename
     * @return ArrayList<String>
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

    @Test
    public void testReadTextFile() {
        assertEquals("Onec", readTextFile("/Users/Rudy/CS271/Assignment1/sampleTextFile.txt").get(0));
    }

    /**
     * @param word
     * @param wordList
     * @param stats
     * @return ArrayList<String>
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
     * @param wordList
     * @param wordAndRelated
     * @param stats
     * @return int
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

    @Test
    public void testDidSwapAdjacentLetters() {
        ArrayList<String> wordAndRelated = new ArrayList<>();
        wordAndRelated.add(0, "onec");
        int[] stats = new int[5];
        swapAdjacentLetters(readWordList("/Users/Rudy/CS271/Assignment1/jlawler-wordlist.txt"), wordAndRelated, stats);
        assertEquals("once", wordAndRelated.get(1));
    }

    /**
     * @param wordList
     * @param wordAndRelated
     * @param stats
     * @return int
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

    @Test
    public void testInsertExtraLetter() {
        ArrayList<String> wordAndRelated = new ArrayList<>();
        wordAndRelated.add(0, "onec");
        int[] stats = new int[5];
        insertExtraLetter(readWordList("/Users/Rudy/CS271/Assignment1/wordlisttest.txt"), wordAndRelated, stats);
        assertEquals("donec", wordAndRelated.get(1));
    }

    /**
     * @param wordList
     * @param wordAndRelated
     * @param stats
     * @return int
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

    @Test
    public void testDeleteSingleLetter() {
        ArrayList<String> wordAndRelated = new ArrayList<>();
        wordAndRelated.add(0, "onec");
        int[] stats = new int[5];
        deleteSingleLetter(readWordList("/Users/Rudy/CS271/Assignment1/wordlisttest.txt"), wordAndRelated, stats);
        assertEquals("one", wordAndRelated.get(2));
    }

    /**
     * @param wordList
     * @param wordAndRelated
     * @param stats
     * @return int
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

    @Test
    public void testReplaceSingleLetter() {
        ArrayList<String> wordAndRelated = new ArrayList<>();
        wordAndRelated.add(0, "onec");
        int[] stats = new int[5];
        replaceSingleLetter(readWordList("/Users/Rudy/CS271/Assignment1/wordlisttest.txt"), wordAndRelated, stats);
        assertEquals("ones", wordAndRelated.get(1));
    }
}
