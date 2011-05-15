package org.sortdc.sortdc.database;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.sortdc.sortdc.dao.Category;
import org.sortdc.sortdc.dao.Document;
import org.sortdc.sortdc.dao.Word;

public class DatabaseMongo extends Database {

    private DB db;

    public DatabaseMongo() {
        this.setHost("localhost");
        this.setPort(27017);
    }

    /**
     * Establishes connection with database
     *
     * @throws Exception
     */
    public void connect() throws Exception {
        if (this.db_name == null) {
            throw new Exception("Dbname not set");
        }

        Mongo init = new Mongo(this.host, this.port);
        DB database = init.getDB(this.db_name);

        if (this.username != null && this.password != null && !db.authenticate(this.username, this.password.toCharArray())) {
            throw new Exception("Connection denied : incorrect database authentication");
        }

        this.db = database;
    }

    /**
     * Finds all registered categories
     * 
     * @return Categories list
     * @throws Exception
     */
    public List<Category> findAllCategories() throws Exception {
        List<Category> categories = new ArrayList();
        DBCollection collection = this.db.getCollection("categories");
        DBCursor cursor = collection.find();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            Category category = new Category();
            category.setId((String) obj.get("_id"));
            category.setName((String) obj.get("name"));
            categories.add(category);
        }
        return categories;
    }

    /**
     * Saves or updates a category
     *
     * @param category
     * @throws Exception
     */
    public synchronized void saveCategory(Category category) throws Exception {
        DBCollection collection = this.db.getCollection("categories");
        DBObject obj = new BasicDBObject();
        obj.put("name", category.getName());
        if (category.getId() == null) {
            collection.insert(obj);
        } else {
            obj.put("_id", category.getId());
            collection.save(obj);
        }
    }

    /**
     * Deletes a category given its id
     *
     * @param category_id
     * @throws Exception
     */
    public void deleteCategoryById(String category_id) throws Exception {
        this.deleteCategoryByParam("id", category_id);
    }

    /**
     * Deletes a category given its name
     *
     * @param category_id
     * @throws Exception
     */
    public void deleteCategoryByName(String category_name) throws Exception {
        this.deleteCategoryByParam("name", category_name);
    }

    /**
     * Deletes a category by a parameter (id or name)
     *
     * @param param search parameter
     * @param value id or name
     * @throws Exception
     */
    private void deleteCategoryByParam(String param, String value) throws Exception {
        // TODO
    }

    /**
     * Finds a document given its id
     *
     * @param id document id
     * @return document matching id
     * @throws Exception
     */
    public Document findDocumentById(String id) throws Exception {
        return this.findDocumentByParam("id", id);
    }

    /**
     * Finds a document given its name
     *
     * @param name document name
     * @return document matching name
     * @throws Exception
     */
    public Document findDocumentByName(String name) throws Exception {
        return this.findDocumentByParam("name", name);
    }

    /**
     * Finds a document by a parameter (id or name)
     *
     * @param param search parameter
     * @param name document name
     * @return document matching name
     * @throws Exception
     */
    private Document findDocumentByParam(String param, String value) throws Exception {
        Document document = new Document();

        DBCollection collection = this.db.getCollection("documents");
        DBObject query = new BasicDBObject();
        query.put(param, value);

        DBCursor cursor = collection.find(query);

        if (cursor.hasNext()) {
            DBObject current_doc = cursor.next();
            document.setId((String) current_doc.get("_id"));
            document.setName((String) current_doc.get("name"));
            document.setCategoryId((String) current_doc.get("category_id"));
            document.setWordsOccurrences((Map<String, Integer>) current_doc.get("words"));
        } else {
            throw new Exception("Document not found");
        }
        return document;
    }

    /**
     * Saves a new document or updates an existing one
     *
     * @param document document to save / update
     * @throws Exception
     */
    public synchronized void saveDocument(Document document) throws Exception {
        // si le document existe, on supprime les occurences de ses mots dans sa catégorie
        if (document.getId() != null) {
            Document old_document = this.findDocumentById(document.getId());
            this.deleteDocumentWordsOcurrences(old_document);
        }
        DBCollection collection = this.db.getCollection("documents");
        DBObject obj = new BasicDBObject();
        obj.put("name", document.getName());
        obj.put("category_id", document.getCategoryId());
        obj.put("words", document.getWordsOccurrences());

        if (document.getId() == null) {
            collection.insert(obj);
        } else {
            obj.put("_id", document.getId());
            collection.save(obj);
        }

        for (Map.Entry<String, Integer> doc_occurences : document.getWordsOccurrences().entrySet()) {
            Word word = this.findWordByName(doc_occurences.getKey());

            Map<String, Integer> occurences = new HashMap<String, Integer>();
            // si le mot n'existe pas encore dans la collection, on l'ajoute
            if (word.getId() == null) {
                word.setName(doc_occurences.getKey());
                occurences.put(document.getCategoryId(), doc_occurences.getValue());
                word.setOccurrencesByCategory(occurences);
            } else {
                // s'il existe, on gère le nombre d'occurences dans la catégorie
                occurences = word.getOccurrencesByCategory();
                boolean cat_found = false;
                for (Map.Entry<String, Integer> word_occurences : word.getOccurrencesByCategory().entrySet()) {
                    // s'il est déjà présent dans la catégorie, on ajoute le nb d'occurences dans le doc au nb d'occurences dans la catégorie du doc
                    if (word_occurences.getKey().equals(document.getCategoryId())) {
                        word_occurences.setValue(word_occurences.getValue() + doc_occurences.getValue());
                        occurences.put(word_occurences.getKey(), word_occurences.getValue());
                        cat_found = true;
                    }
                }
                // s'il n'est pas présent dans la catégorie, on ajoute l'entrée correspondante
                if (!cat_found) {
                    occurences.put(document.getCategoryId(), doc_occurences.getValue());
                }
                word.setOccurrencesByCategory(occurences);
            }
            this.saveWord(word);
        }
    }

    /**
     * Deletes words' occurrences of a document in a category
     *
     * @throws Exception
     */
    private void deleteDocumentWordsOcurrences(Document document) throws Exception {
        if (document.getId() != null) {
            Map<String, Integer> occurences = document.getWordsOccurrences();

            for (Map.Entry<String, Integer> doc_occurences : occurences.entrySet()) {
                Word word = this.findWordByName(doc_occurences.getKey());
                for (Map.Entry<String, Integer> word_occurences : word.getOccurrencesByCategory().entrySet()) {
                    if (word_occurences.getKey().equals(document.getCategoryId())) {
                        word_occurences.setValue(word_occurences.getValue() - doc_occurences.getValue());
                        this.saveWord(word);
                    }
                }
            }
        }
    }

    /**
     * Deletes a document given its id
     *
     * @param document_id
     * @throws Exception
     */
    public void deleteDocumentById(String document_id) throws Exception {
        this.deleteDocumentByParam("id", document_id);
    }

    /**
     * Deletes a document given its name
     *
     * @param document_name
     * @throws Exception
     */
    public void deleteDocumentByName(String document_name) throws Exception {
        this.deleteDocumentByParam("name", document_name);
    }

    /**
     * Deletes a document by a parameter (id or name)
     *
     * @param param search parameter
     * @param value id or name
     * @throws Exception
     */
    private void deleteDocumentByParam(String param, String value) throws Exception {
        // TODO
    }

    /**
     * Finds a word given its id
     *
     * @param id word id
     * @return word matching id
     * @throws Exception
     */
    public Word findWordById(String id) throws Exception {
        return this.findWordByParam("id", id);
    }

    /**
     * Finds a word given its name
     *
     * @param name word name
     * @return word matching name
     * @throws Exception
     */
    public Word findWordByName(String name) throws Exception {
        return this.findWordByParam("name", name);
    }

    /**
     * Finds a word by a parameter (id or name)
     *
     * @param param search parameter
     * @param name word name
     * @return word matching name
     * @throws Exception
     */
    private Word findWordByParam(String param, String value) throws Exception {
        Word word = new Word();

        DBCollection collection = this.db.getCollection("words");
        DBObject query = new BasicDBObject();
        query.put(param, value);

        DBCursor cursor = collection.find(query);

        if (cursor.hasNext()) {
            DBObject current_doc = cursor.next();
            word.setId((String) current_doc.get("_id"));
            word.setName((String) current_doc.get("name"));
            word.setOccurrencesByCategory((Map<String, Integer>) current_doc.get("occurences"));
        } else {
            throw new Exception("Word not found");
        }
        return word;
    }

    /**
     * Finds a list of words given a set of names
     *
     * @param names set of names
     * @return list of words matching names
     * @throws Exception
     */
    public List<Word> findWordsByNames(Set<String> names) throws Exception {
        List<Word> words = new ArrayList<Word>();

        DBCollection collection = this.db.getCollection("words");
        DBObject query = new BasicDBObject();
        query.put("name", new BasicDBObject("$in", names.toArray()));

        DBCursor cursor = collection.find(query);

        while (cursor.hasNext()) {
            Word word = new Word();
            DBObject current_word = cursor.next();
            word.setId((String) current_word.get("_id"));
            word.setName((String) current_word.get("name"));
            word.setOccurrencesByCategory((Map<String, Integer>) current_word.get("occurences"));

            words.add(word);
        }
        return words;
    }

    /**
     * Saves a new word or update an existing one
     *
     * @param word word to save / update
     * @throws Exception
     */
    public synchronized void saveWord(Word word) throws Exception {
        DBCollection collection = this.db.getCollection("words");
        DBObject obj = new BasicDBObject();
        obj.put("name", word.getName());
        obj.put("occurences", word.getOccurrencesByCategory());

        if (word.getId() == null) {
            collection.insert(obj);
        } else {
            obj.put("_id", word.getId());
            collection.save(obj);
        }
    }
}
