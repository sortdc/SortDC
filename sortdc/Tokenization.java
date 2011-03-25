/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sortdc;

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

    public void setExtractWords(boolean set){
        this.extract_words = set;
    }

    public void setApplyStemming(boolean set){
        this.apply_stemming = set;
    }

    public void setExtractBigrams(boolean set){
        this.extract_bigrams = set;
    }

    public void setExtractTrigrams(boolean set){
        this.extract_trigrams = set;
    }

    public void setWordsMinLength(int length){
        this.words_min_length = length;
    }

    public List<String> extract(String text, String lang){
        List<String> list = new ArrayList();

        String[] words = this.tokenize(text);

        if(this.extract_bigrams)
            for(String word : words)
                this.getBigrams(word, list);

        if(this.extract_trigrams)
            for(String word : words)
                this.getTrigrams(word, list);

        if(this.extract_words){
            if(this.apply_stemming && !lang.equals(""))
                this.applyStemming(words, lang);
            list.addAll(Arrays.asList(words));
        }

        this.deleteSmallWords(list);

        return list;
    }

    public List<String> extract(String text){
        return this.extract(text, "");
    }

    private String[] tokenize(String text){
        text = this.removeAccents(text);
        text = text.toLowerCase();
        return text.split("[^a-z0-9\\-]+");
    }

    private String removeAccents(String text){
        return Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", "");
    }

    private void getWordParts(String word, List<String> list, int parts_length){
        int array_size = (word.length() - parts_length + 1 > 0 ? word.length() - parts_length + 1 : 0);

        for(int i = 0 ; i < array_size ; i++)
            list.add(word.substring(i, i + parts_length));
    }

    private void getBigrams(String word, List<String> list){
        getWordParts(word, list, 2);
    }

    private void getTrigrams(String word, List<String> list){
        getWordParts(word, list, 3);
    }

    private void applyStemming(String[] words, String lang){
        try {
            Class stemClass = Class.forName("org.tartarus.snowball.ext."+lang+"Stemmer");
            SnowballStemmer stemmer = (SnowballStemmer) stemClass.newInstance();
            for(int i=0; i < words.length; i++){
                stemmer.setCurrent(words[i]);
                stemmer.stem();
                words[i] = stemmer.getCurrent();
            }
        }catch(Exception e){
        }
    }

    private void deleteSmallWords(List<String> words){
        for(int i = words.size()-1 ; i >= 0 ; i--){
            if(words.get(i).length() < this.words_min_length)
                words.remove(i);
        }
    }
}
