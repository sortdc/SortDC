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
    private List<Integer> ngrams_words = null;
    private List<Integer> ngrams_chars = null;
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
     * ngrams_chars parameter setter.
     * Indicates the n letters words parts to extract
     *
     * @param ngrams_chars list of ngrams to extract
     */
    public void setNgramsChars(List<Integer> ngrams_chars) {
        this.ngrams_chars = ngrams_chars;
    }

    /**
     * ngrams_words parameter setter.
     * Indicates the n words parts of the text to extract
     *
     * @param ngrams_chars list of words ngrams to extract
     */
    public void setNgramsWords(List<Integer> ngrams_words) {
        this.ngrams_words = ngrams_words;
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

    /**
     * Determines a list of words to exclude from tokenization
     *
     * @param stopWords list of words to ignore
     */
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
        List<String> tokens = new ArrayList<String>();

        String[] words = this.tokenize(text);

        if (this.extract_words) {
            tokens.addAll(Arrays.asList(words));
            this.deleteSmallWords(tokens);
            this.deleteStopWords(tokens);
            if (this.apply_stemming) {
                this.applyStemming(tokens);
            }
        }

        if (this.ngrams_words != null) {
            for (Integer n : this.ngrams_words) {
                tokens.addAll(this.getWordsNGrams(words, n));
            }
        }
        if (this.ngrams_chars != null) {
            for (Integer n : this.ngrams_chars) {
                tokens.addAll(this.getCharsNGrams(words, n));
            }
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
     * Cuts words into n letters parts
     *
     * @param words an array in wich words are stored
     * @param n grams length
     * @return list of n letters parts of each words
     */
    private List<String> getCharsNGrams(String[] words, int n) {
        List<String> grams = new ArrayList<String>();

        for (String w : words) {
            int array_size = (w.length() - n + 1 > 0 ? w.length() - n + 1 : 0);

            for (int i = 0; i < array_size; i++) {
                grams.add(w.substring(i, i + n));
            }
        }

        return grams;
    }

    /**
     * Cuts a text into n words parts
     *
     * @param words an array in wich words are stored
     * @param n grams length
     * @return list of n words parts of the text
     */
    private List<String> getWordsNGrams(String[] words, int n) {
        List<String> grams = new ArrayList<String>();

        for (int i = 0; i < words.length - n + 1; i++) {
            String gram = words[i];
            for (int k = 1; k < n; k++) {
                gram += " " + words[i + k];
            }
            grams.add(gram);
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
        Map<String, Integer> occurrences = new HashMap<String, Integer>();
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
