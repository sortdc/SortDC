package org.sortdc.sortdc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import org.tartarus.snowball.SnowballStemmer;
import java.util.List;

/**
 *
 * @author ronan
 */
public class Tokenization {

    private boolean extract_words = true;
    private boolean apply_stemming = false;
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
     * apply_stemming parameter setter.
     * If set to true, extract() method will stem each word.
     * /!\ needs extract_word parameter to be set to true as well.
     *
     * @param set
     */
    public void setApplyStemming(boolean set) {
        this.apply_stemming = set;
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
     * If set to true, extract() method will ad each text trigrams to the returned list.
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

    public void setStopWordsFile(String filePath) {
        this.stopWords = new ArrayList();
        try {
            InputStream is = new FileInputStream(filePath);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                this.stopWords.add(line.trim());
            }
            br.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Analizes a text and returns a list words and tokens depending on instance parameters.
     *
     * @param text text to analize
     * @param lang text language
     * @return list of text words/bigrams/trigrams
     */
    public List<String> extract(String text, String lang) {
        List<String> list = new ArrayList();

        String[] words = this.tokenize(text);

        if (this.extract_bigrams) {
            for (String word : words) {
                this.getBigrams(word, list);
            }
        }

        if (this.extract_trigrams) {
            for (String word : words) {
                this.getTrigrams(word, list);
            }
        }

        if (this.extract_words) {
            if (this.apply_stemming && !lang.equals("")) {
                this.applyStemming(words, lang);
            }
            list.addAll(Arrays.asList(words));
        }

        this.deleteStopWords(list);

        this.deleteSmallWords(list);

        return list;
    }

    /**
     * Calls extract() method with language unspecified.
     *
     * @param text text to analize
     * @return list of text words/bigrams/trigrams
     */
    public List<String> extract(String text) {
        return this.extract(text, "");
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
     * Separates a word into 2 letters tokens and add them to a list.
     * Uses getWordParts() method.
     *
     * @param word word to analize
     * @param list list in which tokens will be added
     */
    private void getBigrams(String word, List<String> list) {
        getWordParts(word, list, 2);
    }

    /**
     * Separates a word into 3 letters tokens and add them to a list.
     * Uses getWordParts() method.
     *
     * @param word word to analize
     * @param list list in which tokens will be added
     */
    private void getTrigrams(String word, List<String> list) {
        getWordParts(word, list, 3);
    }

    /**
     * Reduces words to their roots.
     *
     * @param words list of words to be stemmed
     * @param lang language used for stemming
     */
    private void applyStemming(String[] words, String lang) {
        try {
            Class stemClass = Class.forName("org.tartarus.snowball.ext." + lang + "Stemmer");
            SnowballStemmer stemmer = (SnowballStemmer) stemClass.newInstance();
            for (int i = 0; i < words.length; i++) {
                stemmer.setCurrent(words[i]);
                stemmer.stem();
                words[i] = stemmer.getCurrent();
            }
        } catch (Exception e) {
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
}
