package org.sortdc.sortdc;

import java.util.Map;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Config config = Config.getInstance();
        try {
            config.loadFile("config/config.yaml");
            config.applyLogConfig();
        } catch (Exception e) {
            Log.getInstance().add(e);
        }

        Log.getInstance().add("Starting...");

        try {
            Map<String, Classifier> classifiers = config.getClassifiers();
            Classifier classifier = classifiers.get("languages");
            //classifier.train("text1", "Bonjour je fais des confitures", "lolizator");
            classifier.train("text2", "Sortdc ça roxe du poney", "poutrage");
            System.out.println(classifier.categorize("les confitures sont bonnes"));
        } catch (Exception e) {
            Log.getInstance().add(e);
        }
    }
}
