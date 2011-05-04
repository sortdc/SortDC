package org.sortdc.sortdc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        List<String> stopWords = new ArrayList();
        try {
            InputStream is = new FileInputStream("config/stopwords.txt");
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

        Tokenization tokenization = new Tokenization();
        //tokenization.setExtractWords(false);
        tokenization.setApplyStemming(true);
        tokenization.setWordsMinLength(3);
        //tokenization.setExtractBigrams(true);
        //tokenization.setExtractTrigrams(true);
        tokenization.setStopWords(stopWords);

        List test = tokenization.extract("Bonjour tout le monde les applications de classification sont jolies éè à ö légèrement. Porte-monnaies! Hello World =)\nbonjour!", "french");

        /*for (int i = 0; i < test.size(); i++) {
        System.out.println(test.get(i));
        }*/

        try {
            Config config = Config.getInstance();
            config.loadFile("config/config.yaml");

            Map db = (Map) config.get("database");

            System.out.println(db.get("host"));
            System.out.println(db.get("port"));
            System.out.println(db.get("dbname"));
            System.out.println(db.get("username"));
            System.out.println(db.get("password"));

            List instances = (List) config.get("instances");

            System.out.println(instances);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}