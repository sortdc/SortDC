package org.sortdc.sortdc;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.tartarus.snowball.SnowballStemmer;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;

public class Tokenization {

    private boolean extract_words = true;
    private boolean apply_stemming = false;
    private String lang;
    private List<Integer> ngrams_words = null;
    private List<Integer> ngrams_chars = null;
    private int words_min_length = 2;
    private int tokens_max_length = 50;
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
     * tokens_max_length parameter setter.
     * Indicates the maximum length of tokens returned in the extract() method list.
     *
     * @param length
     */
    public void setTokensMaxLength(int length) {
        this.tokens_max_length = length;
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
     * Analizes a text and returns a list of tokens depending on instance parameters.
     *
     * @param text text to analize
     * @return list of text tokens (words, bigrams, trigrams...)
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
        this.truncateBigTokens(tokens);

        return tokens;
    }

    /**
     * Analizes a html content and returns a list of tokens depending on instance parameters.
     *
     * @param html html content to analize
     * @return list of text tokens (words, bigrams, trigrams...)
     * @throws Exception
     */
    public List<String> extractFromHTML(String html) throws Exception {
        String text = Jsoup.parse(html).text();
        return this.extract(text);
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
        return text.split("[^a-z0-9]+");
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

    /**
     * Truncates too big tokens
     *
     * @param tokens tokens list to analize
     */
    private void truncateBigTokens(List<String> tokens) {
        for (int i = tokens.size() - 1; i >= 0; i--) {
            if (tokens.get(i).length() > this.tokens_max_length) {
                tokens.set(i, tokens.get(i).substring(0, this.tokens_max_length));
            }
        }
    }

    public Map<String, Integer> getOccurrences(List<String> tokens) {
        Map<String, Integer> occurrences = new HashMap<String, Integer>();
        for (String token : tokens) {
            if (occurrences.containsKey(token)) {
                occurrences.put(token, ((int) occurrences.get(token)) + 1);
            } else {
                occurrences.put(token, 1);
            }
        }
        return occurrences;
    }

    public Map<String, Integer> mergeOccurrences(Map<String, Integer>... tokens_lists) {
        Map<String, Integer> tokens_merged = new HashMap<String, Integer>();
        if (tokens_lists.length == 0) {
            return tokens_merged;
        }
        for (Map<String, Integer> tokens : tokens_lists) {
            for (Map.Entry<String, Integer> token : tokens.entrySet()) {
                if (tokens_merged.containsKey(token.getKey())) {
                    tokens_merged.put(token.getKey(), tokens_merged.get(token.getKey()) + token.getValue());
                } else {
                    tokens_merged.put(token.getKey(), token.getValue());
                }
            }
        }
        return tokens_merged;
    }
}
