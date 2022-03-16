/**
 * Description: Assignment 1 - Spellchecking a text file
 * Author: Rudy Liljeberg
 * Date: 3/10/22
 * Bugs: None that I know of.
 * Reflection: This assignment was challenging, both in terms of form
 *             and function. Using tests helped to ensure parts were
 *             running smoothly and definitely cut down on debug time
 *             considerably overall. Nonetheless, a fun but time-consuming
 *             assignment all-in-all.
 */

package a1;

import net.datastructures.ArrayList;
import net.datastructures.PositionalList;

public class Assignment1 {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        displaySpellingErrorsAndStatistics();
    }

    public static void displaySpellingErrorsAndStatistics() {
        PositionalList<String> wordList = Spellchecker.readWordList("jlawler-wordlist.txt");
        ArrayList<String> textFileList = Spellchecker.readTextFile("sampleTextFile.txt");
        int[] stats = new int[5];
        ArrayList<ArrayList<String>> spellcheckedWords = new ArrayList<>();
        int numWordsSpellchecked = displaySpellingErrors(wordList, textFileList, spellcheckedWords, stats);

        System.out.printf("\n# of words spellchecked: %d\n", numWordsSpellchecked);
        System.out.printf("%% of words misspelled: %.1f\n",
                (double) ((double) spellcheckedWords.size() / (double) numWordsSpellchecked) * 100);
        System.out.printf("Average # of suggestions / misspelled word: %.1f\n",
                (double) ((double) stats[4] / (double) spellcheckedWords.size()));
        System.out.printf("Swaps: %d\n", stats[0]);
        System.out.printf("Insertions: %d\n", stats[1]);
        System.out.printf("Deletions: %d\n", stats[2]);
        System.out.printf("Replacements: %d\n", stats[3]);
    }

    
    /** 
     * @param spellcheckedWords
     * @param stats
     * @return int
     */
    public static int displaySpellingErrors(PositionalList<String> wordList, ArrayList<String> textFileList,
            ArrayList<ArrayList<String>> spellcheckedWords, int[] stats) {
        int numWordsSpellchecked = 0;
        boolean isAValidWord;
        for (String str : textFileList) {
            isAValidWord = false;
            for (String currentWord : wordList) {
                if (str.equals(currentWord)) {
                    isAValidWord = true;
                }
            }

            if (str.length() > 1 && !isAValidWord) {
                spellcheckedWords.add(spellcheckedWords.size(), Spellchecker.spellcheck(str, wordList, stats));
                ArrayList<String> currentListOfWords = spellcheckedWords.get(spellcheckedWords.size() - 1);
                System.out.printf("%s - ", currentListOfWords.get(0));
                if (currentListOfWords.size() != 1) {
                    for (int i = 1; i < currentListOfWords.size() - 1; i++) {
                        System.out.printf("%s, ", currentListOfWords.get(i));
                    }
                } else {
                    System.out.printf("No Suggestions\n");
                }

                if (currentListOfWords.size() > 1) {
                    System.out.printf("%s\n", currentListOfWords.get(currentListOfWords.size() - 1));
                }
            }

            numWordsSpellchecked++;
        }

        return numWordsSpellchecked;
    }
}