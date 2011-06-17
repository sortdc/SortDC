package org.sortdc.sortdc.database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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

public class DatabaseMysql extends Database {

    private Connection connection;

    public DatabaseMysql() {
        this.setHost("localhost");
        this.setPort(3306);
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

        this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + "/" + this.db_name, this.username, this.password);
        this.init();
    }

    /**
     * Initializes the database : creates tables if they don't exist
     * 
     * @throws Exception
     */
    private void init() throws Exception {
        FileReader input = new FileReader("config/mysql_structure.sql");
        BufferedReader br = new BufferedReader(input);
        String line, sql = "";
        while ((line = br.readLine()) != null) {
            sql += line;
        }
        Statement statement = this.connection.createStatement();
        String[] queries = sql.split(";");
        for (String query : queries) {
            if (!query.trim().equals("")) {
                try {
                    statement.executeUpdate(query);
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * Finds all registered categories
     *
     * @return Categories list
     * @throws Exception
     */
    public List<Category> findAllCategories() throws Exception {
        List<Category> categories = new ArrayList<Category>();
        PreparedStatement statement = this.connection.prepareStatement("SELECT id FROM categories");
        ResultSet data = statement.executeQuery();
        while (data.next()) {
            Category category = new Category();
            category.setId(data.getString("id"));
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
        if (category.getId() == null) {
            throw new Exception("You must set a category's id");
        }
        PreparedStatement statement = this.connection.prepareStatement("REPLACE INTO categories (id) VALUES (?)");
        statement.setString(1, category.getId());
        statement.executeUpdate();
    }

    /**
     * Deletes a category given its id
     * 
     * @param category_id
     * @throws Exception
     */
    public void deleteCategoryById(String category_id) throws Exception {
        PreparedStatement statement = this.connection.prepareStatement("DELETE FROM categories WHERE id = ?");
        statement.setString(1, category_id);
        statement.executeUpdate();
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
        PreparedStatement statement;
        ResultSet data;

        statement = this.connection.prepareStatement("SELECT id, category_id FROM documents WHERE id = ? LIMIT 1");
        statement.setString(1, document_id);
        data = statement.executeQuery();
        if (data.next()) {
            document.setId(data.getString("id"));
            document.setCategoryId(data.getString("category_id"));

            Map<String, Integer> words = new HashMap<String, Integer>();
            statement = this.connection.prepareStatement(
                    "SELECT w.name, dw.occurrences "
                    + "FROM documents_words dw "
                    + "INNER JOIN words w ON w.id = dw.word_id "
                    + "WHERE dw.document_id = ?");
            statement.setString(1, document_id);
            data = statement.executeQuery();
            while (data.next()) {
                words.put(data.getString("name"), data.getInt("occurrences"));
            }
            document.setWordsOccurrences(words);

        } else {
            throw new ObjectNotFoundException(ObjectNotFoundException.Type.DOCUMENT);
        }
        return document;
    }

    /**
     * Saves or updates a document
     *
     * @param document
     * @throws Exception
     */
    public synchronized void saveDocument(Document document) throws Exception {
        this.connection.setAutoCommit(false);
        try {
            PreparedStatement statement;
            PreparedStatement statement2;
            ResultSet data;
            
            String category_id = document.getCategoryId();
            if (category_id == null) {
                throw new Exception("You must set a category's id");
            }

            String document_id;
            if (document.getId() == null) {
                document_id = UUID.randomUUID().toString();
            } else {
                document_id = document.getId();
                this.deleteDocumentById(document_id);
            }

            statement = this.connection.prepareStatement("REPLACE INTO documents (id, category_id) VALUES (?, ?)");
            statement.setString(1, document_id);
            statement.setString(2, category_id);
            statement.executeUpdate();

            Set<String> words = new HashSet<String>(document.getWordsOccurrences().keySet());
            Map<String, Integer> words_ids = new HashMap<String, Integer>();
            Map<Integer, String> words_names = new HashMap<Integer, String>();

            statement = this.connection.prepareStatement("SELECT id, name FROM words WHERE name IN (" + this.generateQsForIn(words.size()) + ")");
            int i = 1;
            for (String word_name : words) {
                statement.setString(i++, word_name);
            }
            data = statement.executeQuery();
            while (data.next()) {
                words.remove(data.getString("name"));
                words_ids.put(data.getString("name"), data.getInt("id"));
                words_names.put(data.getInt("id"), data.getString("name"));
            }

            statement = this.connection.prepareStatement("INSERT INTO words (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
            for (String word_name : words) {
                statement.setString(1, word_name);
                statement.executeUpdate();

                data = statement.getGeneratedKeys();
                if (data != null && data.next()) {
                    words_ids.put(word_name, data.getInt(1));
                    words_names.put(data.getInt(1), word_name);
                } else {
                    throw new Exception("Unable to add word: " + word_name);
                }
            }

            statement = this.connection.prepareStatement("INSERT INTO documents_words (document_id, word_id, occurrences) VALUES (?, ?, ?)");
            for (Map.Entry<String, Integer> word : words_ids.entrySet()) {
                if (!document.getWordsOccurrences().containsKey((String) word.getKey())) {
                    System.out.println(document.getWordsOccurrences());
                    System.out.println(word.getKey());
                }
                statement.setString(1, document_id);
                statement.setInt(2, (Integer) word.getValue());
                statement.setInt(3, document.getWordsOccurrences().get((String) word.getKey()));
                statement.executeUpdate();
            }

            statement2 = this.connection.prepareStatement(
                    "UPDATE categories_words "
                    + "SET occurrences = occurrences + ? "
                    + "WHERE word_id = ? AND category_id = ?");
            statement = this.connection.prepareStatement(
                    "SELECT word_id "
                    + "FROM categories_words "
                    + "WHERE word_id IN (" + this.generateQsForIn(words_ids.size()) + ") AND category_id = ?");
            i = 1;
            for (Map.Entry<String, Integer> word : words_ids.entrySet()) {
                statement.setInt(i++, (Integer) word.getValue());
            }
            statement.setString(i, category_id);
            data = statement.executeQuery();
            while (data.next()) {
                int word_id = data.getInt("word_id");
                statement2.setInt(1, document.getWordsOccurrences().get((String) words_names.get(word_id)));
                statement2.setInt(2, word_id);
                statement2.setString(3, category_id);
                statement2.executeUpdate();
                words_names.remove(word_id);
            }

            for (Map.Entry<Integer, String> word : words_names.entrySet()) {
                statement = this.connection.prepareStatement(
                        "INSERT INTO categories_words "
                        + "(word_id, category_id, occurrences) "
                        + "VALUES (?, ?, ?)");
                statement.setInt(1, word.getKey());
                statement.setString(2, category_id);
                statement.setInt(3, document.getWordsOccurrences().get((String) word.getValue()));
                statement.executeUpdate();
            }

            this.connection.commit();
            document.setId(document_id);

        } catch (Exception e) {
            this.connection.rollback();
            throw e;

        } finally {
            this.connection.setAutoCommit(true);
        }
    }

    /**
     * Deletes a document given its id
     *
     * @param document_id
     * @throws Exception
     */
    public synchronized void deleteDocumentById(String document_id) throws Exception {
        PreparedStatement statement2 = this.connection.prepareStatement(
                "UPDATE categories_words "
                + "SET occurrences = GREATEST(0, occurrences - ?) "
                + "WHERE word_id = ? AND category_id = ? "
                + "LIMIT 1");
        PreparedStatement statement = this.connection.prepareStatement(
                "SELECT dw.word_id, dw.occurrences, d.category_id "
                + "FROM documents_words dw "
                + "INNER JOIN documents d ON d.id = dw.document_id "
                + "WHERE d.id = ?");
        statement.setString(1, document_id);
        ResultSet data = statement.executeQuery();
        while (data.next()) {
            statement2.setInt(1, data.getInt("occurrences"));
            statement2.setInt(2, data.getInt("word_id"));
            statement2.setString(3, data.getString("category_id"));
            statement2.executeUpdate();
        }

        statement = this.connection.prepareStatement("DELETE FROM documents WHERE id = ?");
        statement.setString(1, document_id);
        statement.executeUpdate();
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
     * @param value id or name
     * @return word matching name
     * @throws Exception
     */
    private Word findWordByParam(String param, String value) throws Exception {
        Word word = new Word();
        PreparedStatement statement;
        ResultSet data;

        statement = this.connection.prepareStatement("SELECT id, name FROM words WHERE " + param + " = ? LIMIT 1");
        statement.setString(1, value);
        data = statement.executeQuery();
        if (data.next()) {
            int word_id = data.getInt("id");
            word.setId(Integer.toString(word_id));
            word.setName(data.getString("name"));

            Map<String, Integer> occurences = new HashMap<String, Integer>();
            PreparedStatement statement2 = this.connection.prepareStatement("SELECT category_id, occurrences FROM categories_words WHERE word_id = ?");
            statement2.setInt(1, word_id);
            ResultSet data2 = statement2.executeQuery();
            while (data2.next()) {
                occurences.put(data2.getString("category_id"), data2.getInt("occurrences"));
            }
            word.setOccurrencesByCategory(occurences);

        } else {
            throw new ObjectNotFoundException(ObjectNotFoundException.Type.WORD);
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

        PreparedStatement statement;
        ResultSet data;

        statement = this.connection.prepareStatement("SELECT id, name FROM words WHERE name IN (" + this.generateQsForIn(names.size()) + ")");
        int i = 1;
        for (String name : names) {
            statement.setString(i++, name);
        }
        data = statement.executeQuery();
        while (data.next()) {
            Word word = new Word();
            int word_id = data.getInt("id");
            word.setId(Integer.toString(word_id));
            word.setName(data.getString("name"));

            Map<String, Integer> occurences = new HashMap<String, Integer>();
            PreparedStatement statement2 = this.connection.prepareStatement("SELECT category_id, occurrences FROM categories_words WHERE word_id = ?");
            statement2.setInt(1, word_id);
            ResultSet data2 = statement2.executeQuery();
            while (data2.next()) {
                occurences.put(data2.getString("category_id"), data2.getInt("occurrences"));
            }
            word.setOccurrencesByCategory(occurences);
            words.add(word);
        }
        return words;
    }

    /**
     * Prepares a list of "?" for PreparedStatement IN clauses
     * 
     * @param numQs
     * @return
     */
    private String generateQsForIn(int numQs) {
        String items = "";
        for (int i = 0; i < numQs; i++) {
            items += "?";
            if (i < numQs - 1) {
                items += ", ";
            }
        }
        return items;
    }
}
