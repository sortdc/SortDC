package org.sortdc.sortdc.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.sortdc.sortdc.dao.Category;
import org.sortdc.sortdc.dao.Document;
import org.sortdc.sortdc.dao.Word;

public class DatabaseMysql extends Database {

    private static Database instance;
    private Connection connection;

    private DatabaseMysql() {
    }

    /**
     * Creates a unique instance of DatabaseMysql (Singleton)
     *
     * @return Instance of DatabaseMysql
     */
    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new DatabaseMysql();
        }
        return instance;
    }

    /**
     * Establishes connection with database
     *
     * @throws Exception
     */
    public void connect() throws Exception {
        this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + "/" + this.db_name, this.username, this.password);
    }

    /**
     * Finds all registered categories
     *
     * @return Categories list
     * @throws Exception
     */
    public List<Category> findAllCategories() throws Exception {
        List<Category> categories = new ArrayList<Category>();
        PreparedStatement statement = this.connection.prepareStatement("SELECT id, name FROM categories");
        ResultSet data = statement.executeQuery();
        while (data.next()) {
            Category category = new Category();
            category.setId(data.getString("id"));
            category.setName(data.getString("name"));
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
            PreparedStatement statement = this.connection.prepareStatement("INSERT INTO categories (name) VALUES (?)");
            statement.setString(2, category.getName());
            statement.execute();
            ResultSet data = statement.getGeneratedKeys();
            if (data != null && data.next()) {
                category.setId(Integer.toString(data.getInt(1)));
            } else {
                throw new Exception("Unable to add category");
            }
        } else {
            PreparedStatement statement = this.connection.prepareStatement("UPDATE categories SET name = ? WHERE id = ?");
            statement.setString(1, category.getName());
            statement.setInt(2, Integer.parseInt(category.getId()));
            statement.execute();
            if (statement.getUpdateCount() == 0) {
                throw new Exception("Category not found");
            }
        }
    }

    public Document findDocumentById(String id) throws Exception {
        // TODO
        return null;
    }

    public Document findDocumentByName(String name) throws Exception {
        // TODO
        return null;
    }

    /**
     * Saves or updates a document
     *
     * @param document
     * @throws Exception
     */
    public synchronized void saveDocument(Document document) throws Exception {
        Integer new_id = null;
        this.connection.setAutoCommit(false);
        try {
            PreparedStatement statement;
            ResultSet data;
            int document_id = Integer.parseInt(document.getId());
            int category_id = Integer.parseInt(document.getCategoryId());

            if (document.getId() == null) {
                statement = this.connection.prepareStatement("INSERT INTO documents (name, category_id) VALUES (?, ?)");
                statement.setString(1, document.getName());
                statement.setInt(2, category_id);
                statement.execute();

                data = statement.getGeneratedKeys();
                if (data != null && data.next()) {
                    new_id = data.getInt(1);
                } else {
                    throw new Exception("Unable to add category");
                }

            } else {

                this.deleteDocumentWordsOccurences(document_id);

                statement = this.connection.prepareStatement("UPDATE documents SET name = ?, category_id = ? WHERE id = ?");
                statement.setString(1, document.getName());
                statement.setInt(2, Integer.parseInt(document.getCategoryId()));
                statement.setInt(3, Integer.parseInt(document.getId()));
                statement.execute();
                if (statement.getUpdateCount() == 0) {
                    throw new Exception("Document not found");
                }
            }

            Set<String> words = document.getWordsOccurrences().keySet();
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

            statement = this.connection.prepareStatement("INSERT INTO words (name) VALUES (?)");
            for (String word_name : words) {
                statement.setString(1, word_name);
                statement.execute();

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
                statement.setInt(1, document_id);
                statement.setInt(2, (Integer) word.getValue());
                statement.setInt(3, document.getWordsOccurrences().get((String) word.getKey()));
                statement.execute();
            }

            statement = this.connection.prepareStatement(
                    "SELECT word_id "
                    + "FROM words_categories "
                    + "WHERE word_id IN (" + this.generateQsForIn(words_ids.size()) + ") AND category_id = ?");
            i = 1;
            for (Map.Entry<String, Integer> word : words_ids.entrySet()) {
                statement.setInt(i++, (Integer) word.getValue());
            }
            statement.setInt(i, category_id);
            data = statement.executeQuery();
            while (data.next()) {
                int word_id = data.getInt("word_id");
                statement = this.connection.prepareStatement(
                        "UPDATE words_categories "
                        + "SET occurrences = occurrences + ? "
                        + "WHERE word_id = ? AND category_id = ?");
                statement.setInt(1, document.getWordsOccurrences().get((String) words_names.get(word_id)));
                statement.setInt(2, word_id);
                statement.setInt(3, category_id);
                statement.execute();
                words_names.remove(word_id);
            }

            for (Map.Entry<Integer, String> word : words_names.entrySet()) {
                statement = this.connection.prepareStatement(
                        "INSERT INTO words_categories "
                        + "(word_id, category_id, occurrences) "
                        + "VALUES (?, ?, ?)");
                statement.setInt(1, word.getKey());
                statement.setInt(2, category_id);
                statement.setInt(3, document.getWordsOccurrences().get((String) word.getValue()));
                statement.execute();
            }

            this.connection.commit();
            if (new_id != null) {
                document.setId(Integer.toString(new_id));
            }

        } catch (Exception e) {
            this.connection.rollback();
            throw e;

        } finally {
            this.connection.setAutoCommit(true);
        }
    }

    /**
     * Deletes words' occurrences of a document
     * 
     * @param document_id
     * @param category_id
     * @throws Exception
     */
    private void deleteDocumentWordsOccurences(int document_id) throws Exception {
        PreparedStatement statement;
        ResultSet data;
        PreparedStatement statement2 = this.connection.prepareStatement(
                "UPDATE words_categories "
                + "SET occurrences = GREATEST(0, occurrences - ?) "
                + "WHERE word_id = ? AND category_id = ? "
                + "LIMIT 1");
        statement = this.connection.prepareStatement(
                "SELECT dw.word_id, dw.occurrences, d.category_id "
                + "FROM documents_words dw"
                + "INNER JOIN documents d ON d.id = dw.document_id");
        data = statement.executeQuery();
        while (data.next()) {
            statement2.setInt(1, data.getInt("occurrences"));
            statement2.setInt(2, data.getInt("word_id"));
            statement2.setInt(3, data.getInt("category_id"));
            statement2.execute();
        }

        statement = this.connection.prepareStatement("DELETE FROM words_categories WHERE occurrences = 0");
        statement.execute();

        statement = this.connection.prepareStatement("DELETE FROM documents_words WHERE document_id = ?");
        statement.setInt(1, document_id);
        statement.execute();
    }

    public Word findWordById(String id) throws Exception {
        // TODO
        return null;
    }

    public Word findWordByName(String name) throws Exception {
        // TODO
        return null;
    }

    public List<Word> findWordByNames(Set<String> names) throws Exception {
        // TODO
        return null;
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
