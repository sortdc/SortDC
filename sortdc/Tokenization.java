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

    private boolean extract_words = true;
    private boolean apply_stemming = false;
    private boolean extract_bigrams = false;
    private boolean extract_trigrams = false;

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

    public ArrayList extract(String text){
        ArrayList list = new ArrayList();

        String[] words = this.tokenize(text);

        if(this.extract_words)
            list.addAll(Arrays.asList(words));

        if(this.extract_bigrams)
            for(String word : words)
                this.getBigrams(word, list);

        if(this.extract_trigrams)
            for(String word : words)
                this.getTrigrams(word, list);

        return list;
    }

    private String[] tokenize(String text){
        text = text.replaceAll("\n", " ");
        String[] words = text.split(" ");

        return words;
    }

    private void getWordParts(String word, ArrayList list, int parts_length){
        int array_size = (word.length() - parts_length + 1 > 0 ? word.length() - parts_length + 1 : 0);

        for(int i = 0 ; i < array_size ; i++)
            list.add(word.substring(i, i + parts_length));
    }

    private void getBigrams(String word, ArrayList list){
        getWordParts(word, list, 2);
    }

    private void getTrigrams(String word, ArrayList list){
        getWordParts(word, list, 3);
    }

}
