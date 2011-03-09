/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sortdc;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author ronan
 */
public class Tokenization {

    boolean extract_words = true;
    boolean apply_stemming = false;
    boolean extract_bigrams = false;
    boolean extract_trigrams = false;

    public void setExctractWords(boolean set){
        this.extract_words = set;
    }

    public void setApplyStemming(boolean set){
        this.apply_stemming = set;
    }

    public void setExctractBigrams(boolean set){
        this.extract_bigrams = set;
    }

    public void setExctractTrigrams(boolean set){
        this.extract_trigrams = set;
    }

    public ArrayList extract(String text){
        ArrayList list = new ArrayList();

        String[] words = this.tokenize(text);

        if(this.extract_words)
            list.addAll(Arrays.asList(words));

        if(this.extract_bigrams){
            for(String word : words){
                String[] bigrams = this.getBigrams(word);
                list.addAll(Arrays.asList(bigrams));
            }
        }

        if(this.extract_trigrams){
            for(String word : words){
                String[] trigrams = this.getTrigrams(word);
                list.addAll(Arrays.asList(trigrams));
            }
        }

        return list;
    }

    private String[] tokenize(String text){
        return text.split(" ");
    }

    private String[] getWordParts(String word, int parts_length){
        int array_size = (word.length() - parts_length + 1 > 0 ? word.length() - parts_length + 1 : 0);
        String[] word_parts = new String[array_size];

        for(int i = 0 ; i < array_size ; i++)
            word_parts[i] = word.substring(i, i + parts_length);

        return word_parts;
    }

    private String[] getBigrams(String word){
        return getWordParts(word, 2);
    }

    private String[] getTrigrams(String word){
        return getWordParts(word, 3);
    }

}
