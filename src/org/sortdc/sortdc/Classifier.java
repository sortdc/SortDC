package org.sortdc.sortdc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.sortdc.sortdc.dao.Category;
import org.sortdc.sortdc.dao.Document;
import org.sortdc.sortdc.dao.Token;
import org.sortdc.sortdc.database.Database;
import org.sortdc.sortdc.database.ObjectNotFoundException;

public class Classifier {

    private String id;
    private Tokenization tokenization;
    private Database database;
    private Map<String, Category> categories;

    /**
     * Instance of Classifier
     *
     * @param tokenization Instance of Tokenization
     */
    public Classifier(String id, Tokenization tokenization, Database database) throws Exception {
        this.id = id;
        this.tokenization = tokenization;
        this.database = database;
        this.loadCategories();
    }

    public String getId() {
        return this.id;
    }

    /**
     * Assigns a new text to an existing category
     *
     * @param text Text
     * @param category_id Category's id
     * @throws Exception
     */
    public Document train(String document_id, String text, String category_id) throws Exception {
        Document document;
        try {
            if (document_id == null) {
                throw new Exception("Document id missing");
            }
            document = this.database.findDocumentById(document_id);
        } catch (Exception e) {
            document = new Document();
            document.setId(document_id);
        }
        return this.train(document, text, category_id);
    }

    public Document train(Document document, String text, String category_id) throws Exception {
        if (!this.categories.containsKey(category_id)) {
            this.addCategory(category_id);
        }
        document.setCategoryId(category_id);
        return this.train(document, text);
    }

    public Document train(Document document, String text) throws Exception {
        List<String> tokens = tokenization.extract(text);
        Map<String, Integer> occurrences = this.tokenization.getOccurrences(tokens);
        document.setTokensOccurrences(occurrences);
        this.database.saveDocument(document);
        return document;
    }

    /**
     * For each category, determines the probability that the given text belongs to it
     *
     * @param text Text
     * @return Probabilities by category
     * @throws Exception
     */
    public Map<String, Float> categorize(String text) throws Exception {
        List<String> text_tokens = tokenization.extract(text);
        Set<String> tokens_set = new HashSet<String>(text_tokens);
        Map<String, Integer> doc_tokens_occurrences = this.tokenization.getOccurrences(text_tokens);
        List<Token> tokens = this.database.findTokensByNames(tokens_set);
        Map<String, Float> categories_prob = new HashMap<String, Float>();

        int nb_tokens = 0;
        for (Token token : tokens) {
            if (doc_tokens_occurrences.containsKey(token.getName())) {
                nb_tokens += doc_tokens_occurrences.get(token.getName());
            }
        }

        for (Category category : this.categories.values()) {
            Float category_prob = 0f;
            for (Token token : tokens) {
                if (!doc_tokens_occurrences.containsKey(token.getName()) || !token.getOccurrencesByCategory().containsKey(category.getId())) {
                    continue;
                }
                category_prob +=
                        new Float(doc_tokens_occurrences.get(token.getName())
                        * token.getOccurrencesByCategory().get(category.getId()))
                        / new Float(token.getOccurrences());
            }
            if (nb_tokens != 0) {
                category_prob /= nb_tokens;
            }
            categories_prob.put(category.getId(), category_prob);
        }
        return categories_prob;
    }

    /**
     * Creates a category
     *
     * @param id Category's unique id
     * @throws Exception
     */
    public void addCategory(String id) throws Exception {
        Category category = new Category();
        category.setId(id);
        this.database.saveCategory(category);
        this.categories.put(id, category);
    }

    /**
     * Loads all categories and stores it in this.categories
     * 
     * @throws Exception
     */
    private synchronized void loadCategories() throws Exception {
        this.categories = new HashMap<String, Category>();
        List<Category> categories_list = this.database.findAllCategories();
        for (Category category : categories_list) {
            this.categories.put(category.getId(), category);
        }
    }

    /**
     * Returns all categories
     * 
     * @return 
     */
    public Map<String, Category> getCategories() {
        return this.categories;
    }

    public Category getCategory(String category_id) throws ObjectNotFoundException {
        if (!this.categories.containsKey(category_id)) {
            throw new ObjectNotFoundException(ObjectNotFoundException.Type.CATEGORY);
        }
        return this.categories.get(category_id);
    }

    /**
     * Finds a document given its id
     *
     * @param document_id document id
     * @return document matching id
     * @throws ObjectNotFoundException or Exception
     */
    public Document findDocumentById(String document_id) throws Exception {
        return this.database.findDocumentById(document_id);
    }
}
