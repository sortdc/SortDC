package org.sortdc.sortdc;

import java.util.List;

public class Classifier {

    Tokenization tokenization;
    String lang;

    /**
     * Instance of Classifier
     *
     * @param tokenization Instance of Tokenization
     */
    public Classifier(Tokenization tokenization) {
        this.tokenization = tokenization;
    }

    /**
     * Sets texts language (for stemming)
     *
     * @param lang
     */
    public void setLang(String lang) {
        this.lang = lang;
    }

    /**
     * Assigns a new text to an existing category
     *
     * @param text Text
     * @param category_id Category's id
     */
    public void train(String text, String category_id) {
        List<String> words;
        if (this.lang == null) {
            words = tokenization.extract(text);
        } else {
            words = tokenization.extract(text, this.lang);
        }

        // TODO
        System.out.println(words);
    }
}
