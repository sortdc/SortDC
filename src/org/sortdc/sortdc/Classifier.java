package org.sortdc.sortdc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.sortdc.sortdc.dao.Category;
import org.sortdc.sortdc.dao.Document;

public class Classifier {

    Tokenization tokenization;

    /**
     * Instance of Classifier
     *
     * @param tokenization Instance of Tokenization
     */
    public Classifier(Tokenization tokenization) {
        this.tokenization = tokenization;
    }

    /**
     * Assigns a new text to an existing category
     *
     * @param text Text
     * @param category_id Category's id
     */
    public void train(String id, String text, String category_id) {
        List<String> words = tokenization.extract(text);
        Map<String, Integer> occurrences = this.tokenization.getOccurrences(words);

        Document doc = new Document();
        doc.setName(id);
        doc.setCategoryId(category_id);
        doc.setOccurrences(occurrences);
        doc.save();
    }

    /**
     * Determines the category of a new text
     *
     * @param text Text
     */
    public Category categorize(String text) {
        List<String> words = tokenization.extract(text);
        int nb_words = words.size();
        Map<String, Integer> occurrences = this.tokenization.getOccurrences(words);
        Map<String, Float> probabilities = new HashMap();

        Iterator it = occurrences.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            
            // TODO
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
        }
        // TODO
    }
}
