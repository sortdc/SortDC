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
     * Get a parameter
     *
     * @param parameter Name of the parameter
     * @return Value of the parameter
     */
    public Object get(String parameter) {
        return this.parameters.get(parameter);
    }

    /**
     * Set a parameter's value
     * 
     * @param name Name of the parameter
     * @param value Value of the parameter
     */
    public void set(String name, Object value) {
        this.parameters.put(name, value);
    }
}
