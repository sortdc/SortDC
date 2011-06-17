package org.sortdc.sortdc.database;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
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
            throw new Exception("Dbname unset");
        }

        Mongo init = new Mongo(this.host, this.port);
        if (this.username != null && this.password != null && !db.authenticate(this.username, this.password.toCharArray())) {
            throw new Exception("Connection denied : incorrect database authentication");
        }
        DB database = init.getDB(this.db_name);

        this.db = database;
        this.init();
    }

    /**
     * Initializes the database : creates tables if they don't exist
     *
     * @throws Exception
     */
    private void init() throws Exception {
        this.addUniqueIndex("documents", "category_id");
        this.addUniqueIndex("words", "name");
    }

    /**
     * Creates a unique index on a field
     *
     * @param collection
     * @param field
     * @throws Exception
     */
    private void addUniqueIndex(String collection, String field) throws Exception {
        DBObject obj = new BasicDBObject();
        obj.put(field, 1);
        DBObject options = new BasicDBObject();
        options.put("unique", true);
        db.getCollection(collection).ensureIndex(obj, options);
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
            category.setId(obj.get("_id").toString());
            categories.add(category);
        }
        cursor.close();
        return categories;
    }

    /**
     * Saves or updates a category
     *
     * @param category
     * @throws Exception
     */
    public synchronized void saveCategory(Category category) throws Exception {
        if (category.getId() == null) {
            throw new Exception("You must set a category's id");
        }
        DBCollection collection = this.db.getCollection("categories");
        DBObject query = new BasicDBObject();
        query.put("_id", category.getId());
        collection.save(query);
    }

    /**
     * Deletes a category given its id
     *
     * @param category_id
     * @throws Exception
     */
    public void deleteCategoryById(String category_id) throws Exception {
        DBCollection collection = this.db.getCollection("categories");
        DBObject query = new BasicDBObject();
        query.put("_id", category_id);
        collection.remove(query);
    }

    /**
     * Finds a document given its id
     *
     * @param id document id
     * @return document matching id
     * @throws Exception
     */
    public Document findDocumentById(String document_id) throws Exception {
        Document document = new Document();

        DBCollection collection = this.db.getCollection("documents");
        DBObject query = new BasicDBObject();
        query.put("_id", document_id);

        DBCursor cursor = collection.find(query).limit(1);

        if (cursor.hasNext()) {
            DBObject current_doc = cursor.next();
            document.setId(current_doc.get("_id").toString());
            document.setCategoryId(current_doc.get("category_id").toString());
            document.setWordsOccurrences((Map<String, Integer>) current_doc.get("words"));
        } else {
            throw new ObjectNotFoundException(ObjectNotFoundException.Type.DOCUMENT);
        }
        cursor.close();
        return document;
    }

    /**
     * Saves a new document or updates an existing one
     *
     * @param document document to save / update
     * @throws Exception
     */
    public synchronized void saveDocument(Document document) throws Exception {
        String category_id = document.getCategoryId();
        if (category_id == null) {
            throw new Exception("You must set a category's id");
        }
        
        String document_id;
        if (document.getId() == null) {
            document_id = UUID.randomUUID().toString();
        } else {
            document_id = document.getId();
            this.deleteDocumentWordsOcurrences(document);
        }

        DBCollection collection = this.db.getCollection("documents");
        DBObject query = new BasicDBObject();
        query.put("category_id", category_id);
        query.put("words", document.getWordsOccurrences());

        if (document_id == null) {
            collection.insert(query);
            document.setId(query.get("_id").toString());
        } else {
            query.put("_id", document_id);
            collection.save(query);
        }

        Set<String> names = new HashSet<String>(document.getWordsOccurrences().keySet());
        List<Word> words = this.findWordsByNames(names);
        Map<String, Integer> occurences = new HashMap<String, Integer>();
        for (Word word : words) {
            names.remove(word.getName());

            occurences = word.getOccurrencesByCategory();

            if (occurences.containsKey(category_id)) {
                occurences.put(category_id, occurences.get(category_id) + document.getWordsOccurrences().get(word.getName()));
            } else {
                occurences.put(category_id, document.getWordsOccurrences().get(word.getName()));
            }
            this.saveWord(word);
        }
        Map<String, Integer> occurences2 = new HashMap<String, Integer>();
        for (String name : names) {
            Word word = new Word();
            word.setName(name);
            occurences2.put(category_id, document.getWordsOccurrences().get(name));
            word.setOccurrencesByCategory(occurences2);
            this.saveWord(word);
        }
    }

    /**
     * Deletes words' occurrences of a document in a category
     *
     * @throws Exception
     */
    private void deleteDocumentWordsOcurrences(Document document) throws Exception {
        Map<String, Integer> occurences = document.getWordsOccurrences();

        for (Map.Entry<String, Integer> doc_occurences : occurences.entrySet()) {
            try {
                Word word = this.findWordByName(doc_occurences.getKey());
                for (Map.Entry<String, Integer> word_occurences : word.getOccurrencesByCategory().entrySet()) {
                    if (word_occurences.getKey().equals(document.getCategoryId())) {
                        word_occurences.setValue(word_occurences.getValue() - doc_occurences.getValue());
                        this.saveWord(word);
                    }
                }
            } catch (ObjectNotFoundException e) {
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
        Document document = this.findDocumentById(document_id);
        this.deleteDocumentWordsOcurrences(document);

        DBCollection collection = this.db.getCollection("documents");
        DBObject query = new BasicDBObject();
        query.put("_id", document_id);
        collection.remove(query);
    }

    /**
     * Finds a word given its id
     *
     * @param id word id
     * @return word matching id
     * @throws Exception
     */
    public Word findWordById(String id) throws Exception {
        return this.findWordByParam("_id", id);
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

        DBCursor cursor = collection.find(query).limit(1);

        if (cursor.hasNext()) {
            DBObject current_doc = cursor.next();
            word.setId(current_doc.get("_id").toString());
            word.setName(current_doc.get("name").toString());
            word.setOccurrencesByCategory((Map<String, Integer>) current_doc.get("occurences"));
        } else {
            throw new ObjectNotFoundException(ObjectNotFoundException.Type.WORD);
        }
        cursor.close();
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
            word.setId(current_word.get("_id").toString());
            word.setName(current_word.get("name").toString());
            word.setOccurrencesByCategory((Map<String, Integer>) current_word.get("occurences"));

            words.add(word);
        }
        cursor.close();
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
        DBObject query = new BasicDBObject();
        query.put("name", word.getName());

        Map<String, Integer> occurences = word.getOccurrencesByCategory();
        DBObject word_occurences = new BasicDBObject();
        for (Map.Entry<String, Integer> cat_occurences : occurences.entrySet()) {
            word_occurences.put(cat_occurences.getKey(), cat_occurences.getValue());
        }
        query.put("occurences", word_occurences);

        if (word.getId() == null) {
            collection.insert(query);
            word.setId(query.get("_id").toString());
        } else {
            query.put("_id", word.getId());
            collection.save(query);
        }
    }
}
