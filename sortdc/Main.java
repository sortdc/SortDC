/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sortdc;

import org.tartarus.snowball.*;

/**
 *
 * @author skreo
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            Class stemClass = Class.forName("org.tartarus.snowball.ext.frenchStemmer");
            SnowballStemmer stemmer = (SnowballStemmer) stemClass.newInstance();

            String sentence = "Les roses rouges sont belles. arrestation anticonstitutionnellement";
            String[] words = sentence.toLowerCase().split("[^a-z]+");

            for(String word : words){
                stemmer.setCurrent(word);
                stemmer.stem();
                System.out.println(stemmer.getCurrent());
            }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

}
