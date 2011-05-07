package org.sortdc.sortdc;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.tartarus.snowball.SnowballStemmer;
import java.util.List;
import java.util.Map;

public class Tokenization {

    private boolean extract_words = true;
    private boolean apply_stemming = false;
    private String lang;
    private boolean extract_bigrams = false;
    private boolean extract_trigrams = false;
    private int words_min_length = 2;
    private List<String> stopWords;

    /**
     * extract_words parameter setter.
     * If set to true, extract() method will add each word of the text to the returned list.
     *
     * @param set
     */
    public void setExtractWords(boolean set) {
        this.extract_words = set;
    }

    /**
     * Enables stemming and sets language
     *
     * @param lang
     * @throws Exception
     */
    public void enableStemming(String lang) throws Exception {
        try {
            Class.forName("org.tartarus.snowball.ext." + lang + "Stemmer");
        } catch (ClassNotFoundException e) {
            throw new Exception("Language not recognized: " + lang);
        }
        this.lang = lang;
        this.apply_stemming = true;
    }

    /**
     * Disables stemming
     */
    public void disableStemming() {
        this.apply_stemming = false;
    }

    /**
     * extract_bigrams parameter setter.
     * If set to true, extract() method will add each text bigrams to the returned list.
     *
     * @param set
     */
    public void setExtractBigrams(boolean set) {
        this.extract_bigrams = set;
    }

    /**
     * extract_trigrams setter.
     * If set to true, extract() method will add each text trigrams to the returned list.
     * 
     * @param set
     */
    public void setExtractTrigrams(boolean set) {
        this.extract_trigrams = set;
    }

    /**
     * words_min_length parameter setter.
     * Indicates the minimal length of words returned in the extract() method list.
     *
     * @param length
     */
    public void setWordsMinLength(int length) {
        this.words_min_length = length;
    }

    public void setStopWords(List<String> stopWords) {
        this.stopWords = stopWords;
    }

    /**
     * Analizes a text and returns a list of words and tokens depending on instance parameters.
     *
     * @param text text to analize
     * @param lang text language
     * @return list of text words/bigrams/trigrams
     * @throws Exception
     */
    public List<String> extract(String text) throws Exception {
        List<String> tokens = new ArrayList();

        String[] words = this.tokenize(text);

        if (this.extract_words) {
            tokens.addAll(Arrays.asList(words));
            this.deleteSmallWords(tokens);
            this.deleteStopWords(tokens);
            if (this.apply_stemming) {
                this.applyStemming(tokens);
            }
        }

        if (this.extract_bigrams) {
            tokens.addAll(this.getBigrams(words));
        }

        if (this.extract_trigrams) {
            tokens.addAll(this.getTrigrams(words));
        }

        return tokens;
    }

    /**
     * Treats and separates text words.
     *
     * @param text text to analize
     * @return separated words list
     */
    private String[] tokenize(String text) {
        text = this.removeAccents(text);
        text = text.toLowerCase();
        return text.split("[^a-z0-9\\-]+");
    }

    /**
     * Removes accent from a text.
     *
     * @param text text to analize
     * @return text without accents
     */
    private String removeAccents(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", "");
    }

    /**
     * Separates a word into x letters tokens and add them to a list.
     *
     * @param word word to analize
     * @param list list in which tokens will be added
     * @param parts_length tokens length
     */
    private void getWordParts(String word, List<String> list, int parts_length) {
        int array_size = (word.length() - parts_length + 1 > 0 ? word.length() - parts_length + 1 : 0);

        for (int i = 0; i < array_size; i++) {
            list.add(word.substring(i, i + parts_length));
        }
    }

    /**
     * Separates words in a list of words into 2 letters tokens and returns them
     * Uses getWordParts() method.
     *
     * @param words words to analize
     */
    private List<String> getBigrams(String[] words) {
        List<String> grams = new ArrayList();
        for (String word : words) {
            this.getWordParts(word, grams, 2);
        }
        return grams;
    }

    /**
     * Separates words in a list of words into 3 letters tokens and returns them
     * Uses getWordParts() method.
     *
     * @param words words to analize
     */
    private List<String> getTrigrams(String[] words) {
        List<String> grams = new ArrayList();
        for (String word : words) {
            this.getWordParts(word, grams, 3);
        }
        return grams;
    }

    /**
     * Reduces words to their roots.
     *
     * @param words list of words to be stemmed
     * @param lang language used for stemming
     * @throws Exception
     */
    private void applyStemming(List<String> words) throws Exception {
        Class stemClass = Class.forName("org.tartarus.snowball.ext." + this.lang + "Stemmer");
        SnowballStemmer stemmer = (SnowballStemmer) stemClass.newInstance();
        for (int i = 0; i < words.size(); i++) {
            stemmer.setCurrent(words.get(i));
            stemmer.stem();
            words.set(i, stemmer.getCurrent());
        }
    }

    /**
     * Deletes x letters words from the list returned by extract() method.
     * "x" corresponds to words_min_length parameter.
     *
     * @param words words list to analize
     */
    private void deleteSmallWords(List<String> words) {
        for (int i = words.size() - 1; i >= 0; i--) {
            if (words.get(i).length() < this.words_min_length) {
                words.remove(i);
            }
        }
    }

    /**
     * Deletes stop words
     *
     * @param words words list to analize
     */
    private void deleteStopWords(List<String> words) {
        if (this.stopWords == null) {
            return;
        }
        for (int i = words.size() - 1; i >= 0; i--) {
            if (this.stopWords.contains(words.get(i))) {
                words.remove(i);
            }
        }
    }

    public Map<String, Integer> getOccurrences(List<String> words) {
        Map<String, Integer> occurrences = new HashMap();
        for (String word : words) {
            if (occurrences.containsKey(word)) {
                occurrences.put(word, ((int) occurrences.get(word)) + 1);
            } else {
                occurrences.put(word, 1);
            }
        }
        return occurrences;
    }
}
