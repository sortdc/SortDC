package org.sortdc.sortdc;

import java.util.Arrays;
import java.util.List;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
            Tokenization tokenization = new Tokenization();
            tokenization.setExtractWords(true);
            tokenization.enableStemming("french");
            tokenization.setNgramsWords(Arrays.asList(2, 3));
            tokenization.setNgramsChars(Arrays.asList(2, 3));
            tokenization.setWordsMinLength(3);
            tokenization.setStopWords(Arrays.asList("fait", "avec"));
            List<String> extract = tokenization.extract("Hello world!\nGrand-mère fait des confitures avec application.");
            for(String s : extract){
                System.out.println(s);
            }
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        /*Config config = Config.getInstance();
        try {
            config.loadFile("config/config.yaml");
            config.applyLogConfig();
        } catch (Exception e) {
            Log.getInstance().add(e.getMessage());
        }

        Log.getInstance().add("Starting...");

        try {
            Classifier classifier = config.getClassifier(0);
            classifier.train("text1", "Bonjour je fais des confitures", "lolizator");
        } catch (Exception e) {
            Log.getInstance().add(e.getMessage());
        }*/
    }
}
