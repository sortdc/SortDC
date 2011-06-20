package org.sortdc.sortdc;

import java.util.ArrayList;
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

    /**
     * Returns classifier's id
     * 
     * @return 
     */
    public String getId() {
        return this.id;
    }

    /**
     * Inserts or updates a document
     *
     * @param document
     * @throws Exception
     */
    public void saveDocument(Document document) throws Exception {
        if (!this.categories.containsKey(document.getCategoryId())) {
            this.addCategory(document.getCategoryId());
        }
        this.database.saveDocument(document);
    }

    /**
     * For each category, determines the probability that the given text belongs to it
     *
     * @param tokens_occurrences Tokens occurences
     * @return Probabilities by category
     * @throws Exception
     */
    public Map<String, Float> categorize(Map<String, Integer> tokens_occurrences) throws Exception {
        Set<String> tokens_set = new HashSet<String>(tokens_occurrences.keySet());
        List<Token> tokens = this.database.findTokensByNames(tokens_set);
        Map<String, Float> categories_prob = new HashMap<String, Float>();

        int nb_tokens = 0;
        for (Token token : tokens) {
            if (tokens_occurrences.containsKey(token.getName())) {
                nb_tokens += tokens_occurrences.get(token.getName());
            }
        }

        for (Category category : this.categories.values()) {
            Float category_prob = 0f;
            for (Token token : tokens) {
                if (!tokens_occurrences.containsKey(token.getName()) || !token.getOccurrencesByCategory().containsKey(category.getId())) {
                    continue;
                }
                category_prob +=
                        new Float(tokens_occurrences.get(token.getName())
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
     * Extracts tokens from possibly multiple sources: plain text, HTML, tokens list
     * 
     * @param text
     * @param html
     * @param tokens_occurrences
     * @return
     * @throws Exception 
     */
    public Map<String, Integer> extractTokens(String text, String html, Map<String, Integer> tokens_occurrences) throws Exception {
        List<String> tokens = new ArrayList<String>();
        if (text != null) {
            tokens.addAll(this.tokenization.extract(text));
        }
        if (html != null) {
            tokens.addAll(this.tokenization.extractFromHTML(html));
        }
        Map<String, Integer> tokens_occurrences2 = this.tokenization.getOccurrences(tokens);
        if (tokens_occurrences == null) {
            return tokens_occurrences2;
        } else {
            return this.tokenization.mergeOccurrences(tokens_occurrences, tokens_occurrences2);
        }
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

    public Tokenization getTokenization() {
        return this.tokenization;
    }
}
