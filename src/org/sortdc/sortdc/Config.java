package org.sortdc.sortdc;

import com.esotericsoftware.yamlbeans.YamlReader;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.sortdc.sortdc.database.Database;
import org.sortdc.sortdc.database.DatabaseMongo;
import org.sortdc.sortdc.database.DatabaseMysql;

public class Config {

    private static Config instance;
    private Map parameters;
    public static final String LOG = "log";
    public static final String LOG_VERBOSE = "verbose";
    public static final String LOG_FILEPATH = "filepath";
    public static final String DATABASE = "database";
    public static final String DATABASE_DBMS = "dbms";
    public static final String DATABASE_HOST = "host";
    public static final String DATABASE_PORT = "port";
    public static final String DATABASE_DBNAME = "dbname";
    public static final String DATABASE_USERNAME = "username";
    public static final String DATABASE_PASSWORD = "password";
    public static final String DATABASE_INSTANCE = "instance";
    public static final String CLASSIFIERS_LIST = "classifiers";
    public static final String CLASSIFIER_LANG = "lang";
    public static final String CLASSIFIER_WORDS = "words";
    public static final String CLASSIFIER_WORDS_MIN_LENGTH = "words_min_length";
    public static final String CLASSIFIER_STEMMING = "stemming";
    public static final String CLASSIFIER_BIGRAMS = "bigrams";
    public static final String CLASSIFIER_TRIGRAMS = "trigrams";
    public static final String CLASSIFIER_STOPWORDS = "stopwords";
    public static final String CLASSIFIER_STOPWORDS_FILEPATH = "stopwords_file";

    private Config() {
    }

    /**
     * Creates a unique instance of Config (Singleton)
     *
     * @return Instance of Config
     */
    public static synchronized Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    /**
     * Loads configuration parameters from a config file
     *
     * @param filePath Path to the config file
     * @throws Exception
     */
    public void loadFile(String filePath) throws Exception {
        YamlReader reader = new YamlReader(new FileReader(filePath));
        Object params = reader.read();
        if (params instanceof Map) {
            this.parameters = (Map) params;
        } else {
            throw new Exception("Invalid config file");
        }

        this.loadStopWords();
    }

    /**
     * Loads stopwords for each classifier from stopwords files
     *
     * @throws Exception
     */
    private void loadStopWords() throws Exception {
        List<Map> classifiers = (List) this.get(CLASSIFIERS_LIST);
        if (classifiers != null) {
            for (Map classifier : classifiers) {
                String stopwordsFilepath = (String) classifier.get(CLASSIFIER_STOPWORDS_FILEPATH);
                if (stopwordsFilepath == null) {
                    continue;
                }
                List<String> stopWords = new ArrayList();
                try {
                    InputStream is = new FileInputStream(stopwordsFilepath);
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String line;
                    while ((line = br.readLine()) != null) {
                        stopWords.add(line.trim());
                    }
                    br.close();
                } catch (IOException e) {
                    Log.getInstance().add(e.getMessage());
                }
                if (stopWords.size() != 0 || classifier.get(CLASSIFIER_STOPWORDS) == null) {
                    classifier.put(CLASSIFIER_STOPWORDS, stopWords);
                }
            }
        }
    }

    /**
     * Gets a parameter
     *
     * @param parameter Name of the parameter
     * @return Value of the parameter
     */
    public Object get(String parameter) {
        return this.parameters.get(parameter);
    }

    /**
     * Sets a parameter's value
     * 
     * @param name Name of the parameter
     * @param value Value of the parameter
     */
    public void set(String name, Object value) {
        this.parameters.put(name, value);
    }

    /**
     * Converts a config parameter to a boolean
     *
     * @param value
     * @return
     */
    public boolean paramIsTrue(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            return ((String) value).toLowerCase().equals("true")
                    || ((String) value).toLowerCase().equals("yes")
                    || ((String) value).equals("1");
        }
        return false;
    }

    /**
     * Converts a config parameter to an integer
     *
     * @param value
     * @return
     * @throws Exception
     */
    public Integer paramToInt(Object value) throws Exception {
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof String) {
            return Integer.parseInt((String) value);
        }
        throw new Exception("Invalid number format: " + value);
    }

    /**
     * Converts a config parameter to a String
     *
     * @param value
     * @return
     * @throws Exception
     */
    public String paramToString(Object value) throws Exception {
        if (value instanceof String) {
            return (String) value;
        }
        return value.toString();
    }

    /**
     * Converts a config parameter to a List
     *
     * @param value
     * @return
     * @throws Exception
     */
    public List paramToList(Object value) throws Exception {
        if (value instanceof List) {
            return (List) value;
        }
        return null;
    }

    /**
     * Converts a config parameter to a List
     *
     * @param value
     * @return
     * @throws Exception
     */
    public Map paramToMap(Object value) throws Exception {
        if (value instanceof Map) {
            return (Map) value;
        }
        return null;
    }

    /**
     * Applies config for logging system
     *
     * @throws Exception
     */
    public void applyLogConfig() throws Exception {
        Map logConfig = this.paramToMap(this.get(LOG));
        if (logConfig == null) {
            return;
        }
        Log log = Log.getInstance();

        if (logConfig.containsKey(LOG_VERBOSE)) {
            log.setVerbose(this.paramIsTrue(logConfig.get(LOG_VERBOSE)));
        }
        if (logConfig.containsKey(LOG_FILEPATH)) {
            log.setFilepath(this.paramToString(logConfig.get(LOG_FILEPATH)));
        }
    }

    /**
     * Creates an instance of Classifier and configures it
     *
     * @param id Classifier's id
     * @return Classifier instance
     * @throws Exception
     */
    public Classifier getClassifier(int id) throws Exception {
        List classifiers = this.paramToList(this.get(Config.CLASSIFIERS_LIST));
        if (classifiers == null || id >= classifiers.size()) {
            throw new Exception("Classifier not found");
        }

        Map classifierConfig = this.paramToMap(classifiers.get(id));
        if (classifierConfig == null) {
            throw new Exception("Invalid classifier config");
        }

        Tokenization tokenization = new Tokenization();
        if (classifierConfig.containsKey(CLASSIFIER_WORDS)) {
            tokenization.setExtractWords(this.paramIsTrue(classifierConfig.get(CLASSIFIER_WORDS)));
        }
        if (classifierConfig.containsKey(CLASSIFIER_STEMMING)) {
            if (this.paramIsTrue(classifierConfig.get(CLASSIFIER_STEMMING))) {
                if (classifierConfig.containsKey(CLASSIFIER_LANG)) {
                    tokenization.enableStemming(this.paramToString(classifierConfig.get(CLASSIFIER_LANG)));
                } else {
                    throw new Exception("You must define a language for stemming");
                }
            } else {
                tokenization.disableStemming();
            }
        }
        if (classifierConfig.containsKey(CLASSIFIER_WORDS_MIN_LENGTH)) {
            tokenization.setWordsMinLength(this.paramToInt(classifierConfig.get(CLASSIFIER_WORDS_MIN_LENGTH)));
        }
        if (classifierConfig.containsKey(CLASSIFIER_BIGRAMS)) {
            tokenization.setExtractBigrams(this.paramIsTrue(classifierConfig.get(CLASSIFIER_BIGRAMS)));
        }
        if (classifierConfig.containsKey(CLASSIFIER_TRIGRAMS)) {
            tokenization.setExtractTrigrams(this.paramIsTrue(classifierConfig.get(CLASSIFIER_TRIGRAMS)));
        }
        if (classifierConfig.containsKey(CLASSIFIER_STOPWORDS)) {
            tokenization.setStopWords(this.paramToList(classifierConfig.get(CLASSIFIER_STOPWORDS)));
        }

        Classifier classifier = new Classifier(tokenization);

        return classifier;
    }

    /**
     * Creates an instance of Database if doesn't exist, else returns existing instance
     *
     * @return
     * @throws Exception
     */
    public Database getDatabase() throws Exception {
        Map databaseConfig = this.paramToMap(this.parameters.get(DATABASE));
        if (databaseConfig == null) {
            throw new Exception("Database config not found");
        }

        if (databaseConfig.containsKey(DATABASE_INSTANCE) && databaseConfig.get(DATABASE_INSTANCE) instanceof Database) {
            return (Database) databaseConfig.get(DATABASE_INSTANCE);
        }

        Database database = null;
        String dbms = this.paramToString(databaseConfig.get(DATABASE_DBMS)).toLowerCase();

        if (dbms.equals("mongodb")) {
            database = DatabaseMongo.getInstance();
        } else if (dbms.equals("mysql")) {
            database = DatabaseMysql.getInstance();
        } else {
            throw new Exception("Unsupported DBMS: " + dbms);
        }

        database.setHost(this.paramToString(databaseConfig.get(DATABASE_HOST)));
        database.setPort(this.paramToInt(databaseConfig.get(DATABASE_PORT)));
        database.setDbName(this.paramToString(databaseConfig.get(DATABASE_DBNAME)));
        if (databaseConfig.containsKey(DATABASE_USERNAME) && databaseConfig.containsKey(DATABASE_PASSWORD)) {
            database.setUsername(this.paramToString(databaseConfig.get(DATABASE_USERNAME)));
            database.setPassword(this.paramToString(databaseConfig.get(DATABASE_PASSWORD)));
        }
        database.connect();

        databaseConfig.put(DATABASE_INSTANCE, database);
        return database;
    }
}
