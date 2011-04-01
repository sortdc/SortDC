/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sortdc.sortdc;

import java.util.List;

/**
 *
 * @author skreo
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Tokenization tokenization = new Tokenization();
        //tokenization.setExtractWords(false);
        tokenization.setApplyStemming(true);
        tokenization.setWordsMinLength(3);
        //tokenization.setExtractBigrams(true);
        //tokenization.setExtractTrigrams(true);
        tokenization.setStopWordsFile("config/stopwords.txt");

        List test = tokenization.extract("Bonjour tout le monde les applications de classification sont jolies éè à ö légèrement. Porte-monnaies! Hello World =)\nbonjour!", "french");

        for (int i = 0; i < test.size(); i++) {
            System.out.println(test.get(i));
        }
    }
}
