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

public class Config {

    private static Config instance;
    private Map parameters;
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
        this.parameters = (Map) reader.read();

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
                    System.out.println(e.getMessage());
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
     * @return boolean value
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
     * @return integer value
     */
    public Integer paramToInt(Object value) {
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof String) {
            return Integer.parseInt((String) value);
        }
        return null;
    }

    /**
     * Creates an instance of Classifier and configures it
     *
     * @param id Classifier's id
     * @return Classifier instance
     * @throws Exception
     */
    public Classifier getClassifier(int id) throws Exception {
        List classifiers = (List) this.get(Config.CLASSIFIERS_LIST);
        if (id >= classifiers.size()) {
            throw new Exception("Classifier not found");
        }

        Map classifierConfig = (Map) classifiers.get(id);

        Tokenization tokenization = new Tokenization();
        if (classifierConfig.containsKey(CLASSIFIER_WORDS)) {
            tokenization.setExtractWords(this.paramIsTrue(classifierConfig.get(CLASSIFIER_WORDS)));
        }
        if (classifierConfig.containsKey(CLASSIFIER_STEMMING)) {
            tokenization.setApplyStemming(this.paramIsTrue(classifierConfig.get(CLASSIFIER_STEMMING)));
        }
        if (classifierConfig.containsKey(CLASSIFIER_WORDS_MIN_LENGTH)) {
            Integer min_length = this.paramToInt(classifierConfig.get(CLASSIFIER_WORDS_MIN_LENGTH));
            if (min_length != null) {
                tokenization.setWordsMinLength(min_length);
            }
        }
        if (classifierConfig.containsKey(CLASSIFIER_BIGRAMS)) {
            tokenization.setExtractBigrams(this.paramIsTrue(classifierConfig.get(CLASSIFIER_BIGRAMS)));
        }
        if (classifierConfig.containsKey(CLASSIFIER_TRIGRAMS)) {
            tokenization.setExtractTrigrams(this.paramIsTrue(classifierConfig.get(CLASSIFIER_TRIGRAMS)));
        }
        if (classifierConfig.containsKey(CLASSIFIER_STOPWORDS)) {
            if (classifierConfig.get(CLASSIFIER_STOPWORDS) instanceof List) {
                tokenization.setStopWords((List<String>) classifierConfig.get(CLASSIFIER_STOPWORDS));
            }
        }

        Classifier classifier = new Classifier(tokenization);


        if (classifierConfig.containsKey(CLASSIFIER_LANG)) {
            if (classifierConfig.get(CLASSIFIER_LANG) instanceof String) {
                classifier.setLang((String) classifierConfig.get(CLASSIFIER_LANG));
            }
        }
        return classifier;
    }
}
